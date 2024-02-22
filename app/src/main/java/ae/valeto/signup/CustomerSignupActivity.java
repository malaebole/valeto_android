package ae.valeto.signup;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

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
import ae.valeto.activities.CustomerMainTabsActivity;
import ae.valeto.base.BaseActivity;
import ae.valeto.databinding.ActivityCustomerSignupBinding;
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


public class CustomerSignupActivity extends BaseActivity implements View.OnClickListener {

    private ActivityCustomerSignupBinding binding;
    private EasyImage easyImage;
    private String photoFilePath = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCustomerSignupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        makeStatusbarTransperant();

        binding.btnBack.setOnClickListener(this);
        binding.btnSignup.setOnClickListener(this);
        binding.imgVu.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        if (v == binding.btnBack){
            backClicked();
        }
        else if (v == binding.btnSignup) {
            signupClicked();
        }
        else if (v == binding.imgVu) {
            pickImage();
        }

    }





    private void signupClicked() {
        if (binding.etName.getText().toString().isEmpty()){
            Functions.showToast(getContext(), "Please enter your name", FancyToast.ERROR);
            return;
        }
        if (binding.etPhone.getText().toString().isEmpty()){
            Functions.showToast(getContext(), "Please enter your phone number", FancyToast.ERROR);
            return;
        }
        if (binding.etPassword.getText().toString().isEmpty()){
            Functions.showToast(getContext(), "Please enter password", FancyToast.ERROR);
            return;
        }
        if (binding.etCnfrmPass.getText().toString().isEmpty()){
            Functions.showToast(getContext(), "Please re-enter", FancyToast.ERROR);
            return;
        }

        if (!Functions.isValidEmail(binding.etEmail.getText().toString())) {
            Functions.showToast(getContext(), "Invalid Email", FancyToast.ERROR);
            return;
        }

        if (binding.etCnfrmPass.getText().toString().equalsIgnoreCase(binding.etPassword.getText().toString())){
            registerUser(binding.etName.getText().toString(), binding.etEmail.getText().toString(), binding.etPhone.getText().toString(), binding.etPassword.getText().toString());
        }else{
            Functions.showToast(getContext(), "Password does not match", FancyToast.ERROR);
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
//                Glide.with(getApplicationContext()).load(file).into(binding.imgVu);
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

    private void registerUser(String name, String email, String phone, String password) {
        KProgressHUD hud = Functions.showLoader(getContext(), "Image processing");
        MultipartBody.Part filePart = null;
        if (!photoFilePath.isEmpty()) {
            File file = new File(photoFilePath);
            RequestBody fileReqBody = RequestBody.create(file, MediaType.parse("image/*"));
            filePart = MultipartBody.Part.createFormData("picture", file.getName(), fileReqBody);
        }
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.registerUser(filePart,
                RequestBody.create(name, MediaType.parse("multipart/form-data")),
                RequestBody.create(email, MediaType.parse("multipart/form-data")),
                RequestBody.create(phone, MediaType.parse("multipart/form-data")),
                RequestBody.create(password, MediaType.parse("multipart/form-data")));
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            Functions.showToast(getContext(), object.getString(Constants.kMsg), FancyToast.SUCCESS);
                            JSONObject data = object.getJSONObject(Constants.kData);

                            JSONObject auth = data.getJSONObject("auth");
                            JSONObject userInfoData = data.getJSONObject("user_info");

                            String accessToken = auth.getString("access_token");
                            String refreshToken = auth.getString("refresh_token");


                            UserInfo userInfo = new Gson().fromJson(userInfoData.toString(), UserInfo.class);
                            SharedPreferences.Editor editor = getContext().getSharedPreferences(Constants.PREF_NAME, MODE_PRIVATE).edit();
                            editor.putString(Constants.kUserID, accessToken);
                            editor.putString(Constants.kUserType, userInfo.getUserType());
                            editor.putString(Constants.kAccessToken, accessToken);
                            editor.putString(Constants.kRefreshToken, refreshToken);
                            editor.putString(Constants.kIsSignIn, "1");
                            editor.apply();

                            Functions.saveUserinfo(getContext(), userInfo);
                            String fcmToken = Functions.getPrefValue(getContext(), Constants.kFCMToken);
                            if (!fcmToken.isEmpty()) {
                                sendFcmTokenApi(fcmToken);
                            }


                            Intent intent = new Intent(getContext(), CustomerMainTabsActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();


                        } else {
                            Functions.showToast(getContext(), object.getString(Constants.kMsg), FancyToast.ERROR);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Functions.showToast(getContext(), e.getLocalizedMessage(), FancyToast.ERROR);
                    }
                } else {
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


    private void backClicked() {
        hideKeyboard();
        finish();
    }

}
