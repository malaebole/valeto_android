package ae.valeto.signup;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;

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

    public void devicesLoginLimit() {
//        String userId = Functions.getPrefValue(getContext(), Constants.kUserID);
//        String uniqueID = Functions.getPrefValue(this, Constants.kDeviceUniqueId);
//        //yeh server py unique id bhej rhi haii
//        Log.d("uniqueIDDDDDDD", uniqueID);
//        Call<ResponseBody> call = AppManager.getInstance().apiInterface.devicesLoginLimit(userId, uniqueID, "ole");
//        call.enqueue(new Callback<ResponseBody>() {
//            @Override
//            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//                if (response.body() != null) {
//                    try {
//                        JSONObject object = new JSONObject(response.body().string());
//                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
//                            Intent i = new Intent(SplashActivity.this, OleOwnerMainTabsActivity.class);
//                            startActivity(i);
//                            finish();
//                        }
//                        else{
//                            Intent i = new Intent(SplashActivity.this, IntroSliderActivity.class);
//                            startActivity(i);
//                            finish();
//
//                        }
//
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//
//            @Override
//            public void onFailure(Call<ResponseBody> call, Throwable t) {
//
//            }
//        });
    }
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
            checkUpdatesApi();
        }
    };

    @Override
    protected void onPause() {
        super.onPause();
        if(handler != null) {
            handler.removeCallbacks(runnable);
        }

    }
    protected void sendAppLangApi() {
//        String userId = Functions.getPrefValue(getContext(), Constants.kUserID);
//        if (userId!=null){
//            Call<ResponseBody> call = AppManager.getInstance().apiInterface.sendAppLang(userId,Functions.getAppLang(getContext()));
//            call.enqueue(new Callback<ResponseBody>() {
//                @Override
//                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//                    if (response.body() != null) {
//                        try {
//                            JSONObject object = new JSONObject(response.body().string());
//                            if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
//
//                            }
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }
//
//                @Override
//                public void onFailure(Call<ResponseBody> call, Throwable t) {
//
//                }
//            });
//        }
    }
    private void checkUpdatesApi() {
//        Call<ResponseBody> call = AppManager.getInstance().apiInterface.checkUpdate("android");
//        call.enqueue(new Callback<ResponseBody>() {
//            @Override
//            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//                if (response.body() != null) {
//                    try {
//                        JSONObject object = new JSONObject(response.body().string());
//                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
//                            String version = object.getJSONObject(Constants.kData).getString("version");
//                            String update = object.getJSONObject(Constants.kData).getString("force_update");
//                            if (!version.equalsIgnoreCase(BuildConfig.VERSION_NAME)) {
//                                Intent intent = new Intent(getContext(), OleUpdateAppActivity.class);
//                                intent.putExtra("version", version);
//                                intent.putExtra("force_update", update);
//                                startActivity(intent);
//                            }
//                        }
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//
//            @Override
//            public void onFailure(Call<ResponseBody> call, Throwable t) {
//            }
//        });
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
//    protected void isUserActive(boolean isLoader) {
//        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing") : null;
//        Call<ResponseBody> call = AppManager.getInstance().apiInterface.isUserActive(true); // Call isUserActive with false as the argument
//        call.enqueue(new Callback<ResponseBody>() {
//            @Override
//            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//                Functions.hideLoader(hud);
//                if (response.isSuccessful()) {
//                    try {
//                        JSONObject object = new JSONObject(response.body().string());
//                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
//                            // Handle success case here
//                        } else {
//                            Functions.showToast(getContext(), object.getString(Constants.kMsg), FancyToast.ERROR);
//                        }
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                        Functions.showToast(getContext(), e.getLocalizedMessage(), FancyToast.ERROR);
//                    }
//                } else {
//                    Functions.showToast(getContext(), getString(R.string.error_occured), FancyToast.ERROR);
//                }
//            }
//
//            @Override
//            public void onFailure(Call<ResponseBody> call, Throwable t) {
//                Functions.hideLoader(hud);
//                if (t instanceof UnknownHostException) {
//                    Functions.showToast(getContext(), getString(R.string.check_internet_connection), FancyToast.ERROR);
//                } else {
//                    Functions.showToast(getContext(), t.getLocalizedMessage(), FancyToast.ERROR);
//                }
//            }
//        });
//    }

//    protected void isUserActive(boolean isLoader, Boolen value) {
//        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing") : null;
//
//        try {
//            JSONObject requestData = new JSONObject();
//            requestData.put("is_online", true);
//
//            RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), requestData.toString());
//
//            Call<ResponseBody> call = AppManager.getInstance().apiInterface.isUserActive(requestBody);
//            call.enqueue(new Callback<ResponseBody>() {
//                @Override
//                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//                    Functions.hideLoader(hud);
//                    if (response.isSuccessful()) {
//
//                        // Handle success response
//
//                    } else {
//                        Functions.showToast(getContext(), getString(R.string.error_occured), FancyToast.ERROR);
//                    }
//                }
//
//                @Override
//                public void onFailure(Call<ResponseBody> call, Throwable t) {
//                    Functions.hideLoader(hud);
//                    if (t instanceof UnknownHostException) {
//                        Functions.showToast(getContext(), getString(R.string.check_internet_connection), FancyToast.ERROR);
//                    } else {
//                        Functions.showToast(getContext(), t.getLocalizedMessage(), FancyToast.ERROR);
//                    }
//                }
//            });
//        } catch (Exception e) {
//            e.printStackTrace();
//            Functions.showToast(getContext(), e.getLocalizedMessage(), FancyToast.ERROR);
//        }
//    }
}
