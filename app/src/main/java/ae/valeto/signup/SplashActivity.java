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
import org.json.JSONObject;

import ae.valeto.R;
import ae.valeto.activities.LoginActivity;
import ae.valeto.base.BaseActivity;
import ae.valeto.databinding.ActivitySplashBinding;
import ae.valeto.util.AppManager;
import ae.valeto.util.Constants;
import ae.valeto.util.Functions;
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
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        //version_name.setText(pkgInfo.versionName);

    }

    @Override
    protected void onResume() {
        super.onResume();
        handler = new Handler();
        handler.postDelayed(runnable, 2000);
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
            Intent intent = new Intent(getContext(), LoginActivity.class);
            startActivity(intent);
            finish();



//            if (Functions.getPrefValue(getContext(), Constants.kIsSignIn).equalsIgnoreCase("1")) {
//                if (Functions.getPrefValue(getContext(), Constants.kUserType).equalsIgnoreCase(Constants.kPlayerType)) {
//                    if (Functions.getPrefValue(getContext(), Constants.kAppModule).equalsIgnoreCase("")) {
//                       // Intent intent = new Intent(getContext(), ModuleOptionsActivity.class);
//                       // startActivity(intent);
//                    }
//                    else if (Functions.getPrefValue(getContext(), Constants.kAppModule).equalsIgnoreCase(Constants.kLineupModule))  {
//                        Functions.setAppLang(getContext(), "en");
//                        Functions.changeLanguage(getContext(),"en");
//                        sendAppLangApi();
//                       // Intent i = new Intent(getContext(), MainActivity.class);
//                       // startActivity(i);
//                       // finish();
//
//                    }else{
//                       // Intent i = new Intent(getContext(), OlePlayerMainTabsActivity.class);
//                       // startActivity(i);
//                       // finish();
//
//                    }
//                    //share link intent
//                   // handleIntent(getIntent());
//                } else if (Functions.getPrefValue(getContext(), Constants.kUserType).equalsIgnoreCase(Constants.kOwnerType)) {
//                    devicesLoginLimit();
//                }
//
//                getProfileAPI(false);
//            }
//            else {
//               // Intent i = new Intent(getContext(), IntroSliderActivity.class);
//               // startActivity(i);
//               // finish();
//            }
//            checkUpdatesApi();
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
//        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing"): null;
//        Call<ResponseBody> call = AppManager.getInstance().apiInterface.getUserProfile(Functions.getAppLang(getContext()), Functions.getPrefValue(getContext(),Constants.kUserID),"", Functions.getPrefValue(getContext(), Constants.kAppModule));
//        call.enqueue(new Callback<ResponseBody>() {
//            @Override
//            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//                Functions.hideLoader(hud);
//                if (response.body() != null) {
//                    try {
//                        JSONObject object = new JSONObject(response.body().string());
//                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
//                            JSONObject obj = object.getJSONObject(Constants.kData);
//                            Gson gson = new Gson();
//                            UserInfo userInfo = gson.fromJson(obj.toString(), UserInfo.class);
//                            SharedPreferences.Editor editor = getContext().getSharedPreferences(Constants.PREF_NAME, MODE_PRIVATE).edit();
//                            editor.putString(Constants.kUserType, userInfo.getUserRole());
//                            editor.putString(Constants.kCurrency, userInfo.getCurrency());
//                            editor.apply();
//
//                            Functions.saveUserinfo(getContext(), userInfo);
//                        }
//                    } catch (Exception e) {
//                        e.printStackTrace();
//
//                    }
//                }
//                else {
//                }
//            }
//            @Override
//            public void onFailure(Call<ResponseBody> call, Throwable t) {
//                Functions.hideLoader(hud);
//            }
//        });
    }
}
