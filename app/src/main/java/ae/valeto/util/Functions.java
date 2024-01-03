package ae.valeto.util;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import com.google.gson.Gson;
import com.shashank.sony.fancytoastlib.FancyToast;
import java.util.Locale;

import ae.valeto.R;
import ae.valeto.models.UserInfo;

public class Functions {

    public static String getPrefValue(Context context, String key) {
        SharedPreferences prefs = context.getSharedPreferences(Constants.PREF_NAME, MODE_PRIVATE);
        return prefs.getString(key, "");

    }

    public static String getAppLang(Context context) {
        if (getPrefValue(context, Constants.kAppLang).equalsIgnoreCase("ar")) {
            return Constants.kArLang;
        }
        else {
            return Constants.kEnLang;
        }
    }
    public static void setCurrentPage(Context context, String kCurrentPage) {
        SharedPreferences prefs = context.getSharedPreferences(Constants.PREF_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(Constants.kCurrentPage, kCurrentPage);
        editor.apply();
    }

    public static String getCurrentPage(Context context, String kCurrentPage) {
        return getPrefValue(context, Constants.kCurrentPage);
    }


    public static void setSelectedCountry(Context context, String SelectedCountry) {
        SharedPreferences prefs = context.getSharedPreferences(Constants.PREF_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(Constants.kSelectedCountry, SelectedCountry);
        editor.apply();
    }

    public static String getSelectedCountry(Context context, String SelectedCountry) {
        return getPrefValue(context, Constants.kSelectedCountry);
    }
    
    public static String getAppLangStr(Context context) {
        return getPrefValue(context, Constants.kAppLang);
    }

    public static void setAppLang(Context context, String lang) {
        SharedPreferences prefs = context.getSharedPreferences(Constants.PREF_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(Constants.kAppLang, lang);
        editor.apply();
    }
    public static void setAppLangAr(Context context, String lang) {
        SharedPreferences prefs = context.getSharedPreferences(Constants.PREF_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(Constants.kAppLangAr, lang);
        editor.apply();
    }
    public static String getAppLangAr(Context context) {
        return getPrefValue(context, Constants.kAppLangAr);
    }

    public static void changeLanguage(Context context, String langStr) {
        if (langStr.equalsIgnoreCase("")) {
            langStr = "en";
        }
        Locale locale = new Locale(langStr);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        context.getResources().updateConfiguration(config, context.getResources().getDisplayMetrics());
    }

    public static void showToast(Context context, String text, int type) {
        try {
            FancyToast.makeText(context, text, FancyToast.LENGTH_LONG, type, false).show();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void showToast(Context context, String text, int type, int dur) {
        try {
            FancyToast.makeText(context, text, dur, type, false).show();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

//    public static void showAlert(Context context, String title, String desc, OleCustomAlertDialog.OnDismiss dismiss) {
//        OleCustomAlertDialog dialog = new OleCustomAlertDialog(context, title, desc);
//        dialog.setOnDismiss(dismiss);
//        dialog.show();
//    }


//    public static KProgressHUD showLoader(Context context, String image_processing) {
//        return KProgressHUD.create(context)
//                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
//                .setCancellable(false)
//                .setBackgroundColor(context.getResources().getColor(R.color.blueColorNew))
//                .setAnimationSpeed(2)
//                .setDimAmount(0.5f)
//                .show();
//    }
//    public static KProgressHUD showLoader(Context context) {
//        return KProgressHUD.create(context)
//                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
//                .setCancellable(false)
//                .setBackgroundColor(Color.parseColor("#18707B"))
//                .setAnimationSpeed(2)
//                .setDimAmount(0.5f)
//                .show();
//    }

//    public void showLoader(Context context) {
//        KProgressHUD hud = KProgressHUD.create(context)
//                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
//                .setLabel("Loading...")
//                .setCancellable(false)
//                .setAnimationSpeed(2)
//                .setDimAmount(0.5f);
//
//        if (!((Activity) context).isFinishing()) {
//            hud.show();
//        }
//    }



//    public static void hideLoader(KProgressHUD hud) {
//        if (hud != null) {
//            hud.dismiss();
//        }
//    }

//    public static boolean isValidEmail(CharSequence target) {
//        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
//    }
//
//    public static boolean isArabic(String text) {
//        for (char charac : text.toCharArray()) {
//            if (Character.UnicodeBlock.of(charac) == Character.UnicodeBlock.ARABIC) {
//                return true;
//            }
//        }
//        return false;
//    }
//
//    public static String getDayName(Date date) {
//        SimpleDateFormat  dateFormat = new SimpleDateFormat("EEEE", Locale.ENGLISH);
//        return dateFormat.format(date);
//    }
//
    public static void saveUserinfo(Context context, UserInfo info) {
        Gson gson = new Gson();
        String str = gson.toJson(info);
        SharedPreferences prefs = context.getSharedPreferences(Constants.PREF_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(Constants.kUserInfo, str);
        editor.apply();
    }
//
    public static UserInfo getUserinfo(Context context) {
        Gson gson = new Gson();
        SharedPreferences prefs = context.getSharedPreferences(Constants.PREF_NAME, MODE_PRIVATE);
        String str = prefs.getString(Constants.kUserInfo, "");
        return gson.fromJson(str, UserInfo.class);
    }

}
