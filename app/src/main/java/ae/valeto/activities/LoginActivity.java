package ae.valeto.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import com.google.gson.Gson;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.shashank.sony.fancytoastlib.FancyToast;

import org.json.JSONObject;

import java.net.UnknownHostException;

import ae.valeto.R;
import ae.valeto.base.BaseActivity;
import ae.valeto.databinding.ActivityLoginBinding;
import ae.valeto.models.UserInfo;
import ae.valeto.signup.CustomerSignupActivity;
import ae.valeto.util.AppManager;
import ae.valeto.util.Constants;
import ae.valeto.util.Functions;
import ae.valeto.util.KeyboardUtils;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class LoginActivity extends BaseActivity implements View.OnClickListener {

    private ActivityLoginBinding binding;
    //    private kProgressHud hudd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        makeStatusbarTransperant();

        checkKeyboardListener();

        //  CCPCountry.setDialogTitle(getString(R.string.select_country_region));
        //  CCPCountry.setSearchHintMessage(getString(R.string.search_hint));
        //  binding.btnSkip.setOnClickListener(this);
        //  binding.btnContinue.setOnClickListener(this);
        //  binding.btnBack.setOnClickListener(this);
        //  binding.infoIcon.setOnClickListener(this);
        //  binding.resetPassword.setOnClickListener(this);

        binding.tvForgetPass.setOnClickListener(this);
        binding.btnLogin.setOnClickListener(this);
        binding.btnSignup.setOnClickListener(this);




    }

    @Override
    protected void onResume() {
        super.onResume();
    }
    private void checkKeyboardListener() {
        KeyboardUtils.addKeyboardToggleListener(this, new KeyboardUtils.SoftKeyboardToggleListener() {
            @Override
            public void onToggleSoftKeyboard(boolean isVisible) {
                if (isVisible) {

                    int newHeightInPixels = (int) getResources().getDimension(R.dimen._100sdp); // Get the new height in pixels from resources
                    binding.imgCharac.getLayoutParams().height = newHeightInPixels;
                    binding.imgCharac.requestLayout(); // Refresh the layout to reflect the changes
                }
                else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            final Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {

                                    int newHeightInPixels = (int) getResources().getDimension(R.dimen._200sdp); // Get the new height in pixels from resources
                                    binding.imgCharac.getLayoutParams().height = newHeightInPixels;
                                    binding.imgCharac.requestLayout(); // Refresh the layout to reflect the changes

                                }
                            }, 50);
                        }
                    });
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v == binding.tvForgetPass){
            Intent intent = new Intent(getContext(), ForgetPasswordActivity.class);
            startActivity(intent);
        }
        else if (v == binding.btnLogin) {
            btnLoginClicked();
        }
        else if (v == binding.btnSignup) {
            Intent intent = new Intent(getContext(), CustomerSignupActivity.class);
            startActivity(intent);
        }

    }
    private void btnLoginClicked() {
            //        SelectedcountryCode = binding.ccp.getSelectedCountryCodeWithPlus();
            //        if (SelectedcountryCode.isEmpty()) {
            //            Functions.showToast(getContext(), getString(R.string.select_country_code), FancyToast.ERROR);
            //            return;
            //        }
        if (binding.etEmail.getText().toString().isEmpty()) {
            Functions.showToast(getContext(), "Please enter your email", FancyToast.ERROR);
            return;
        }
        if (binding.etPassword.getText().toString().isEmpty()) {
            Functions.showToast(getContext(), "Password cannot be empty", FancyToast.ERROR);
            return;
        }

        loginApi(binding.etEmail.getText().toString(),binding.etPassword.getText().toString());

    }


    private void loginApi(String email, String password) {
        KProgressHUD hud = Functions.showLoader(getContext(), "Image processing");
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.userLogin(email, password);
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
                            JSONObject userInfoData = data.getJSONObject("user_info");
                            JSONObject auth = data.getJSONObject("auth");
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

                        }
                        else {
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


}
