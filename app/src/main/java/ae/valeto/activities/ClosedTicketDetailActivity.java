package ae.valeto.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.MediaScannerConnection;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.gson.Gson;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.nabinbhandari.android.permissions.PermissionHandler;
import com.nabinbhandari.android.permissions.Permissions;
import com.shashank.sony.fancytoastlib.FancyToast;

import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import ae.valeto.R;
import ae.valeto.base.BaseActivity;
import ae.valeto.databinding.ActivityActiveTicketBinding;
import ae.valeto.databinding.ActivityClosedTicketDetailBinding;
import ae.valeto.models.MyTicket;
import ae.valeto.util.AppManager;
import ae.valeto.util.Constants;
import ae.valeto.util.Functions;
import okhttp3.ResponseBody;
import okqapps.com.tagslayout.TagItem;
import okqapps.com.tagslayout.TagTextSize;
import okqapps.com.tagslayout.UnSelectedTagTheme;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ClosedTicketDetailActivity extends BaseActivity implements View.OnClickListener {

    private ActivityClosedTicketDetailBinding binding;

    private MyTicket myTicket;
    private int ticketId;
    private Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityClosedTicketDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        makeStatusbarTransperant();

        Bundle bundle = getIntent().getExtras();
        if (bundle !=null){
            ticketId = bundle.getInt("ticket_id");
        }

        getSingleUserTicket(true, ticketId);

        binding.btnBack.setOnClickListener(this);
        binding.invoiceVu.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == binding.btnBack){
            finish();
        }
        else if (v == binding.invoiceVu) {
            showImageDialog();
        }

    }


    private void getSingleUserTicket(boolean isLoader, int ticketId) {
        Call<ResponseBody> call;
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing"): null;
        call = AppManager.getInstance().apiInterface.getSingleUserTicket(ticketId);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            JSONObject obj = object.getJSONObject(Constants.kData);
                            Gson gson = new Gson();
                            myTicket = gson.fromJson(obj.toString(), MyTicket.class);
                            populateMyTicket();

                        }else {
                            Functions.showToast(getContext(), object.getString(Constants.kMsg), FancyToast.ERROR);
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        Functions.showToast(getContext(), e.getLocalizedMessage(), FancyToast.ERROR);
                    }
                }
                else {
                    Functions.showToast(getContext(), getString(R.string.error_occured), FancyToast.ERROR);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Functions.hideLoader(hud);
                if (t instanceof UnknownHostException) {
                    Functions.showToast(getContext(), getString(R.string.check_internet_connection), FancyToast.ERROR);
                }
                else {
                    Functions.showToast(getContext(), t.getLocalizedMessage(), FancyToast.ERROR);
                }
            }
        });
    }

    private void populateMyTicket() {
        if (myTicket != null){
            try {
                SimpleDateFormat inputFormat = new SimpleDateFormat("dd/MM/yyyy hh:mma", Locale.getDefault());
                SimpleDateFormat outputDateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                SimpleDateFormat outputTimeFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());

                Date startTime = inputFormat.parse(myTicket.getStartTime());
                Date endTime = inputFormat.parse(myTicket.getEndTime());

                String formattedArrivalDate = outputDateFormat.format(startTime);
                String formattedExDate = outputDateFormat.format(endTime);
                String formattedArrivalTime = outputTimeFormat.format(startTime);
                String formattedExTime = outputTimeFormat.format(endTime);

                binding.tvArrivalDate.setText(formattedArrivalDate);
                binding.tvExDate.setText(formattedExDate);
                binding.tvArrivalTime.setText(formattedArrivalTime);
                binding.tvExTime.setText(formattedExTime);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            binding.tvParkingName.setText(myTicket.getParking().getName());
            binding.tvLoc.setText(myTicket.getParking().getLocation());
            binding.tvParkedBy.setText(myTicket.getActivatedBy().getName());
            binding.tvDeliveredBy.setText(myTicket.getClosedBy().getName());
            binding.tvVehicle.setText(myTicket.getCar().getName());
            binding.tvCarNumber.setText(myTicket.getCar().getPlateNumber());
            if (!myTicket.getSlotNumber().isEmpty()){
                binding.tvSlot.setText(myTicket.getSlotNumber());
            }
            else{
                binding.tvSlot.setText("N/A");
            }
            if (!myTicket.getKeyCode().isEmpty()){
                binding.tvKey.setText(myTicket.getKeyCode());
            }
            else{
                binding.tvKey.setText("N/A");
            }

            binding.tvDuration.setText(myTicket.getDuration());
            binding.tvPaid.setText(myTicket.getPaidAmount());
            if (!myTicket.getInvoice().isEmpty()){
                Glide.with(getApplicationContext()).load(myTicket.getInvoice()).into(binding.invoiceVu);
            }
            if (!myTicket.getParking().getPhoto().isEmpty()){
                Glide.with(getApplicationContext()).load(myTicket.getParking().getPhoto()).into(binding.imgVu);
            }

        }
    }

    private void showImageDialog() {
        dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_invoice_vu); // Custom dialog layout with an ImageView
        ImageView dialogImageView = dialog.findViewById(R.id.dialogImageView);
        ImageButton downloadImgBtn = dialog.findViewById(R.id.btnDownload);

        String imageUrl = myTicket.getInvoice();

        Glide.with(getApplicationContext())
                .load(imageUrl)
                .into(new CustomTarget<Drawable>() {
                    @Override
                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                        // Image loaded successfully
                        dialogImageView.setImageDrawable(resource);
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {
                        // Called when the resource is cleared
                    }
                });

        downloadImgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveToGallery(imageUrl);
            }
        });


        dialog.show();
    }

    private void saveToGallery(String fileUrl) {
        String[] permissions;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            permissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_MEDIA_IMAGES};
        } else {
            permissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
        }

        Permissions.check(getApplicationContext(), permissions, null, null, new PermissionHandler() {
            @Override
            public void onGranted() {
                Glide.with(getApplicationContext())
                        .asBitmap()
                        .load(fileUrl)
                        .into(new CustomTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                try {
                                    saveBitmapToGallery(getContext(), resource);
                                    Functions.showToast(getContext(), "Image saved to gallery", FancyToast.SUCCESS, FancyToast.LENGTH_SHORT);
                                    dialog.dismiss();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    Functions.showToast(getContext(), "Failed to download image", FancyToast.ERROR, FancyToast.LENGTH_SHORT);
                                }
                            }

                            @Override
                            public void onLoadCleared(@Nullable Drawable placeholder) {
                                // Called when the resource is cleared
                            }
                        });
            }

            @Override
            public void onDenied(Context context, ArrayList<String> deniedPermissions) {
                // Handle permission denial
            }
        });
    }

    private void saveBitmapToGallery(Context context, Bitmap bitmap) throws Exception {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "IMG_" + timeStamp + ".png";

        // Get the directory for saving the image
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File imageFile = new File(storageDir, imageFileName);

        // Save the bitmap to the image file
        FileOutputStream outputStream = new FileOutputStream(imageFile);
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
        outputStream.flush();
        outputStream.close();

        // Notify the gallery about the new image
        MediaScannerConnection.scanFile(context, new String[]{imageFile.getAbsolutePath()}, null, null);
    }



}