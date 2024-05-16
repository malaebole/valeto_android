package ae.valeto.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.nabinbhandari.android.permissions.PermissionHandler;
import com.nabinbhandari.android.permissions.Permissions;
import com.shashank.sony.fancytoastlib.FancyToast;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.json.JSONObject;

import java.io.File;
import java.net.UnknownHostException;

import ae.valeto.R;
import ae.valeto.base.BaseActivity;
import ae.valeto.databinding.ActivityProfileBinding;
import ae.valeto.dialogs.ChangePassDialogFragment;
import ae.valeto.dialogs.ScanSuccessPopupFragment;
import ae.valeto.models.UserInfo;
import ae.valeto.util.AppManager;
import ae.valeto.util.Constants;
import ae.valeto.util.Functions;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import pl.aprilapps.easyphotopicker.ChooserType;
import pl.aprilapps.easyphotopicker.EasyImage;
import pl.aprilapps.easyphotopicker.MediaFile;
import pl.aprilapps.easyphotopicker.MediaSource;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends BaseActivity implements View.OnClickListener {

    private ActivityProfileBinding binding;
    private UserInfo userInfo;
    private EasyImage easyImage;
    private String photoFilePath = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        makeStatusbarTransperant();

        userInfo = Functions.getUserinfo(getContext());
        Glide.with(getApplicationContext()).load(userInfo.getPicture()).apply(RequestOptions.circleCropTransform()).into(binding.imgVu);
        binding.etName.setText(userInfo.getName());
        binding.etEmail.setText(userInfo.getEmail());
        binding.etPhone.setText(userInfo.getPhone());


        binding.btnBack.setOnClickListener(this);
        binding.btnChangePass.setOnClickListener(this);
        binding.btnUpdate.setOnClickListener(this);
        binding.imgVu.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        if (v == binding.btnBack){
           finish();
        } 
        else if (v == binding.btnChangePass) {
            changePassClicked();
        }
        else if (v == binding.btnUpdate) {
            if (binding.etName.getText().toString().isEmpty()){
                Functions.showToast(getContext(), "Name cannot be empty", FancyToast.ERROR);
                return;
            }
            if (binding.etEmail.getText().toString().isEmpty()){
                Functions.showToast(getContext(), "Email cannot be empty", FancyToast.ERROR);
                return;
            }
            if (binding.etPhone.getText().toString().isEmpty()){
                Functions.showToast(getContext(), "Phone cannot be empty", FancyToast.ERROR);
                return;
            }

            updateUserProfile(binding.etName.getText().toString(), binding.etEmail.getText().toString(), binding.etPhone.getText().toString());
        }
        else if (v == binding.imgVu) {
            pickImage();
        }
    }

    private void pickImage() {
        String[] permissions = new String[0];
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            permissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_MEDIA_IMAGES};
        }else{
            permissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
        }
        Permissions.check(getContext(), permissions, null/*rationale*/, null/*options*/, new PermissionHandler() {
            @Override
            public void onGranted() {
                // do your task.
                easyImage = new EasyImage.Builder(getContext())
                        .setChooserType(ChooserType.CAMERA_AND_GALLERY)
                        .setCopyImagesToPublicGalleryFolder(false)
                        .allowMultiple(false).build();
                easyImage.openChooser(getContext());
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                File file = new File(resultUri.getPath());
                photoFilePath = resultUri.getPath();
                Glide.with(getApplicationContext()).load(photoFilePath).apply(RequestOptions.circleCropTransform()).into(binding.imgVu);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
        else {
            easyImage.handleActivityResult(requestCode, resultCode, data, getContext(), new EasyImage.Callbacks() {
                @Override
                public void onMediaFilesPicked(MediaFile[] mediaFiles, MediaSource mediaSource) {
                    if (mediaFiles.length > 0) {
                        CropImage.activity(Uri.fromFile(mediaFiles[0].getFile()))
                                .setGuidelines(CropImageView.Guidelines.ON)
                                .setCropShape(CropImageView.CropShape.RECTANGLE)
                                .setFixAspectRatio(true).setScaleType(CropImageView.ScaleType.CENTER_INSIDE)
                                .setAspectRatio(1,1)
                                .start(getContext());
                    }
                }

                @Override
                public void onImagePickerError(Throwable error, MediaSource source) {
                    Functions.showToast(getContext(), error.getLocalizedMessage(), FancyToast.ERROR);
                }

                @Override
                public void onCanceled(@NonNull MediaSource mediaSource) {

                }
            });
        }
    }


    private void changePassClicked() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        Fragment fragment = getSupportFragmentManager().findFragmentByTag("ChangePassDialogFragment");
        if (fragment != null) {
            fragmentTransaction.remove(fragment);
        }
        fragmentTransaction.addToBackStack(null);
        ChangePassDialogFragment dialogFragment = new ChangePassDialogFragment();
        dialogFragment.setDialogCallback((df) -> {
            df.dismiss();

        });
        dialogFragment.show(fragmentTransaction, "ChangePassDialogFragment");
        //playSoundFromAssets("congratulations_tone.mp3");

    }
    private void updateUserProfile(String name, String email, String phone) {
        KProgressHUD hud = Functions.showLoader(getContext(), "Image processing");
        MultipartBody.Part filePart = null;
        if (!photoFilePath.isEmpty()) {
            File file = new File(photoFilePath);
            RequestBody fileReqBody = RequestBody.create(file, MediaType.parse("image/*"));
            filePart = MultipartBody.Part.createFormData("picture", file.getName(), fileReqBody);
        }
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.updateUserProfile(filePart,
                RequestBody.create(name, MediaType.parse("multipart/form-data")),
                RequestBody.create(email, MediaType.parse("multipart/form-data")),
                RequestBody.create(phone, MediaType.parse("multipart/form-data")));
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            Functions.showToast(getContext(), object.getString(Constants.kMsg), FancyToast.SUCCESS);
                            UserInfo info = new Gson().fromJson(object.getString(Constants.kData), UserInfo.class);
                            userInfo.setAppOwnerPhone(object.getString("app_owner_phone"));
                            Functions.saveUserinfo(getContext(), info);

                        } else {
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
                } else {
                    Functions.showToast(getContext(), t.getLocalizedMessage(), FancyToast.ERROR);
                }
            }
        });
    }

}