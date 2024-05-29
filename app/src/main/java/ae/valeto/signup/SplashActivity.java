package ae.valeto.signup;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.ContextCompat;

import com.google.android.datatransport.backend.cct.BuildConfig;
import com.google.gson.Gson;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.shashank.sony.fancytoastlib.FancyToast;

import org.json.JSONObject;

import java.net.UnknownHostException;

import ae.valeto.R;
import ae.valeto.activities.CustomerMainTabsActivity;
import ae.valeto.activities.LoginActivity;
import ae.valeto.base.BaseActivity;
import ae.valeto.databinding.ActivitySplashBinding;
import ae.valeto.models.UserInfo;
import ae.valeto.util.AppManager;
import ae.valeto.util.Constants;
import ae.valeto.util.Functions;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SplashActivity extends BaseActivity {
    private ActivitySplashBinding binding;
    private Handler handler;
    TextView version_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySplashBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        makeStatusbarTransperant();

        if (Functions.getAppLangStr(getContext()).isEmpty()) {
            Functions.setAppLang(getContext(), "en");
        }

        grantNotificationPermission();

        version_name = findViewById(R.id.version_text);
        PackageManager pm = getApplicationContext().getPackageManager();
        String pkgName = getApplicationContext().getPackageName();
        PackageInfo pkgInfo = null;
        try {
            pkgInfo = pm.getPackageInfo(pkgName, 0);
            String ver = BuildConfig.VERSION_NAME;
            version_name.setText("Version "+pkgInfo.versionName);
        }
        catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        //version_name.setText(pkgInfo.versionName);

    }

    @Override
    protected void onResume() {
        super.onResume();
        handler = new Handler();
        handler.postDelayed(runnable, 1000);
    }

    private void grantNotificationPermission() {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                == PackageManager.PERMISSION_GRANTED){

        }else{
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
                resultLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
            }
        }

    }

    private final ActivityResultLauncher<String> resultLauncher =  registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
        if (isGranted) {
            //Functions.showToast(getContext(), "Permission granted", FancyToast.SUCCESS);
        } else {
           // Functions.showToast(getContext(), "Permission denied", FancyToast.ERROR);
        }
    });

    Runnable runnable = new Runnable() {
        @Override
        public void run() {

            if (Functions.getPrefValue(getContext(), Constants.kIsSignIn).equalsIgnoreCase("1")) {

                getProfileAPI(false);
                Intent intent = new Intent(getContext(), CustomerMainTabsActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
            else {
                Intent i = new Intent(getContext(), LoginActivity.class);
                startActivity(i);
                finish();
            }
        }
    };

    @Override
    protected void onPause() {
        super.onPause();
        if(handler != null) {
            handler.removeCallbacks(runnable);
        }

    }

    private void getProfileAPI(boolean isLoader) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing"): null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.getUserProfile();
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
                            UserInfo userInfo = gson.fromJson(obj.toString(), UserInfo.class);
                            userInfo.setAppOwnerPhone(object.getString("app_owner_phone"));
                            Functions.saveUserinfo(getContext(), userInfo);

                        }  else {
                            Functions.showToast(getContext(), object.getString(Constants.kMsg), FancyToast.SUCCESS);
                        }
                    } catch (Exception e) {
                        Functions.showToast(getContext(), e.getLocalizedMessage(), FancyToast.ERROR);
                        e.printStackTrace();
                    }
                }else {
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
