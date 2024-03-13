package ae.valeto.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.shashank.sony.fancytoastlib.FancyToast;

import org.json.JSONObject;

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
    }

    @Override
    public void onClick(View v) {
        if (v == binding.btnBack){
            finish();
        }
//        else if (v ) {
//
//        }

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
                            Functions.showToast(getContext(), object.getString(Constants.kMsg), FancyToast.SUCCESS);
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
            }else{
                binding.tvSlot.setText("N/A");
            }
            if (!myTicket.getKeyCode().isEmpty()){
                binding.tvKey.setText(myTicket.getKeyCode());
            }else{
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



}