package ae.valeto.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;

import com.kaopiz.kprogresshud.KProgressHUD;
import com.shashank.sony.fancytoastlib.FancyToast;

import org.json.JSONObject;

import java.net.UnknownHostException;

import ae.valeto.R;
import ae.valeto.base.BaseActivity;
import ae.valeto.databinding.ActivityVerifyEmailBinding;
import ae.valeto.models.UserInfo;
import ae.valeto.util.AppManager;
import ae.valeto.util.Constants;
import ae.valeto.util.Functions;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VerifyEmailActivity extends BaseActivity implements View.OnClickListener {

    private ActivityVerifyEmailBinding binding;
    private UserInfo userInfo;
    private CountDownTimer countDownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityVerifyEmailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        makeStatusbarTransperant();

        userInfo = Functions.getUserinfo(getContext());
        binding.btnBack.setOnClickListener(this);
        binding.btnVerify.setOnClickListener(this);
        binding.etResendEmail.setOnClickListener(this);

        if (userInfo != null){
            binding.tvInfo.setText("An OTP code has been sent to " + userInfo.getEmail() + " this email, please open your email and enter the OTP code.");
        }
        resendOtp(true);
        startTimer();


    }

    private void startTimer() {
        countDownTimer = new CountDownTimer(60000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long secondsLeft = millisUntilFinished / 1000;
                binding.etTimer.setText("Resend code in " + secondsLeft + " seconds");
            }

            @Override
            public void onFinish() {
                binding.etTimer.setVisibility(View.GONE);
                binding.etResendEmail.setVisibility(View.VISIBLE);

            }
        }.start();
    }

    @Override
    public void onClick(View v) {
        if (v == binding.btnBack){
            finish();
        }
        else if (v == binding.btnVerify) {

            if (Integer.parseInt(binding.etOtp.getText().toString())  < 6) {
                Functions.showToast(getContext(),"Invalid OTP, Please enter valid OTP.", FancyToast.ERROR);
                return;
            }
            verifyEmailClicked(true, binding.etOtp.getText().toString());
        }
        else if (v == binding.etResendEmail) {
            resendOtpCodeClicked();
        }


    }

    private void resendOtpCodeClicked() {
        resendOtp(true);
        binding.etTimer.setVisibility(View.VISIBLE);
        binding.etResendEmail.setVisibility(View.GONE);
        startTimer();
    }


    private void verifyEmailClicked(boolean isLoader, String otp) {
            KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing"): null;
            Call<ResponseBody> call = AppManager.getInstance().apiInterface.verifyEmail(otp);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    Functions.hideLoader(hud);
                    if (response.body() != null) {
                        try {
                            JSONObject object = new JSONObject(response.body().string());
                            if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {

                                binding.tvInfo.setText("Your email has been verified successfully!");
                                binding.otpLayout.setVisibility(View.GONE);
                                binding.verifiedImg.setVisibility(View.VISIBLE);

                                userInfo.setEmailVerified("yes");
                                Functions.saveUserinfo(getContext(), userInfo);

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

    private void resendOtp(boolean isLoader) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing"): null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.sendOtp();
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            Functions.showToast(getContext(), "OTP Code has been sent successfully!", FancyToast.SUCCESS);
                        }
                        else {
                            Functions.showToast(getContext(), object.getString(Constants.kMsg), FancyToast.SUCCESS);
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