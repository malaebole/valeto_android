package ae.valeto;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;


import java.util.UUID;

import ae.valeto.signup.SplashActivity;
import ae.valeto.util.Constants;
import ae.valeto.util.Functions;

public class MyApp extends Application implements Application.ActivityLifecycleCallbacks {

    private static Context mContext;


    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();


        if (Functions.getPrefValue(this, Constants.kDeviceUniqueId).equalsIgnoreCase("")) {
            String uniqueID = UUID.randomUUID().toString();
            SharedPreferences sharedPreferences = getSharedPreferences(Constants.PREF_NAME, MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(Constants.kDeviceUniqueId, uniqueID);
            editor.apply();
        }

        registerActivityLifecycleCallbacks(this);

    }



    public static Context getAppContext() {
        return mContext;
    }

    @Override
    public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {

    }

    @Override
    public void onActivityStarted(@NonNull Activity activity) {

    }

    @Override
    public void onActivityResumed(@NonNull Activity activity) {
        if (activity instanceof SplashActivity) {

        }
    }

    @Override
    public void onActivityPaused(@NonNull Activity activity) {

    }

    @Override
    public void onActivityStopped(@NonNull Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {

    }
}
