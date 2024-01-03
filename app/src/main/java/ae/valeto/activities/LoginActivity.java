package ae.valeto.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import androidx.constraintlayout.widget.ConstraintLayout;
import ae.valeto.base.BaseActivity;
import ae.valeto.databinding.ActivityLoginBinding;
import ae.valeto.util.Constants;
import ae.valeto.util.Functions;
import ae.valeto.util.KeyboardUtils;



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

//        CCPCountry.setDialogTitle(getString(R.string.select_country_region));
//        CCPCountry.setSearchHintMessage(getString(R.string.search_hint));
//        binding.btnSkip.setOnClickListener(this);
//        binding.btnContinue.setOnClickListener(this);
//        binding.btnBack.setOnClickListener(this);
//        binding.infoIcon.setOnClickListener(this);
//        binding.resetPassword.setOnClickListener(this);

        binding.tvForgetPass.setOnClickListener(this);
        binding.btnLogin.setOnClickListener(this);
        binding.btnSignup.setOnClickListener(this);




    }

//    @Override
//    public void onIPReceived(String ipAddress) {
//        Functions.hideLoader(hudd);
//        //ipdetails(true, ipAddress);
//    }


//    @Override
//    public void onIPFailed() {
//        SharedPreferences.Editor editor = getContext().getSharedPreferences(Constants.PREF_NAME, MODE_PRIVATE).edit();
//        editor.putString(Constants.kLoginType, "");
//        editor.putString(Constants.kUserModule, "");
//        editor.apply();
//        Functions.showToast(getContext(), getString(R.string.switch_internet), FancyToast.ERROR);
//    }
//    private void fetchPublicIPAddress(Boolean isLoader) {
//        hudd = isLoader ? Functions.showLoader(this,"Image processing") : null;
//        PublicIPGetter publicIPGetter = new PublicIPGetter(this);
//        publicIPGetter.execute();
//    }

//    private class FetchPublicIpAddressTask extends AsyncTask<Void, Void, String> {
//
//        @Override
//        protected String doInBackground(Void... params) {
//            try {
//                URL url = new URL("https://api.ipify.org?format=json");
//                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//                connection.setRequestMethod("GET");
//
//                int responseCode = connection.getResponseCode();
//                if (responseCode == HttpURLConnection.HTTP_OK) {
//                    try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
//                        String response = reader.readLine().trim();
//                        //Parse the JSON response to extract the IP address
//                        JSONObject jsonObject = new JSONObject(response);
//                        return jsonObject.getString("ip");
//                    }
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(String publicIpAddress) {
//            if (publicIpAddress != null) {
//                ipdetails(publicIpAddress);
//               // Functions.hideLoader(hudd);
//            }
//        }
//    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void checkKeyboardListener() {
        KeyboardUtils.addKeyboardToggleListener(this, new KeyboardUtils.SoftKeyboardToggleListener() {
            @Override
            public void onToggleSoftKeyboard(boolean isVisible) {
                if (isVisible) {
                    setHeight(0.85f); //0.65f
                    setHeightRel(0.40f);
                   // binding.logo.setVisibility(View.GONE);
                }
                else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            final Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    //your code here
                                    setHeight(0.4f);
                                    setHeightRel(0.65f);
                                   // binding.logo.setVisibility(View.VISIBLE);
                                }
                            }, 50);
                        }
                    });
                }
            }
        });
    }
    private void setHeight(float height) {
      //  ConstraintLayout.LayoutParams lp = (ConstraintLayout.LayoutParams) binding.bottomContainer.getLayoutParams();
       // lp.matchConstraintPercentHeight = height;
       // binding.bottomContainer.setLayoutParams(lp);
    }
    private void setHeightRel(float height) {
      //  ConstraintLayout.LayoutParams lp = (ConstraintLayout.LayoutParams) binding.rel.getLayoutParams();
      //  lp.matchConstraintPercentHeight = height;
       // binding.rel.setLayoutParams(lp);
    }
    @Override
    public void onClick(View v) {
        if (v == binding.tvForgetPass){
            Intent intent = new Intent(getContext(), ForgetPasswordActivity.class);
            startActivity(intent);
        }
        else if (v == binding.btnLogin) {

        }
        else if (v == binding.btnSignup) {

        }


        // if (v == binding.btnBack) {
       //     finish();
        //}
      //  else if (v == binding.btnContinue) {
           // btnContinueClicked();

        //}
       // else if (v == binding.infoIcon){
//            PopUpClass popUpClass = new PopUpClass();
//            popUpClass.showPopupWindow(v,true,"");
       // }
        //else if (v == binding.resetPassword){
            //Intent intent = new Intent(LoginActivity.this, ForgotPassActivity.class);
            //startActivity(intent);
        //}
    }
    private void btnContinueClicked() {
//        SelectedcountryCode = binding.ccp.getSelectedCountryCodeWithPlus();
//        if (SelectedcountryCode.isEmpty()) {
//            Functions.showToast(getContext(), getString(R.string.select_country_code), FancyToast.ERROR);
//            return;
//        }
//        if (binding.etPhone.getText().toString().isEmpty()) {
//            Functions.showToast(getContext(), getString(R.string.enter_phone), FancyToast.ERROR);
//            return;
//        }
//        if (binding.etPhone.getText().toString().startsWith("0")) {
//            Functions.showToast(getContext(), getString(R.string.phone_not_start_0), FancyToast.ERROR);
//            return;
//        }
//
//        if (!userIpDetails.equalsIgnoreCase("otp")){
//            binding.passwordVu.setVisibility(View.VISIBLE);
//            binding.resetPassword.setVisibility(View.VISIBLE);
//            if (binding.etPassword.getText().toString().isEmpty()){
//                Functions.showToast(getContext(), getString(R.string.enter_password), FancyToast.ERROR);
//                return;
//            }else if (binding.etPassword.getText().length() < 4){
//                Functions.showToast(getContext(), getString(R.string.pass_must), FancyToast.ERROR);
//                return;
//            }
//            loginApi(String.format("%s%s", SelectedcountryCode,  binding.etPhone.getText().toString()), binding.etPassword.getText().toString());
//        }else {
//            binding.passwordVu.setVisibility(View.GONE);
//            binding.resetPassword.setVisibility(View.GONE);
//            loginApi(String.format("%s%s", SelectedcountryCode, binding.etPhone.getText().toString()),"");
//        }

    }


    private void loginApi(String phone, String password) {  //
//        KProgressHUD hud = Functions.showLoader(getContext(), "Image processing");
//        Call<ResponseBody> call;
//        if (!userIpDetails.equalsIgnoreCase("otp")){
//            call = AppManager.getInstance().apiInterface.withPasswordlogin(Functions.getAppLang(getContext()), phone, password, Functions.getPrefValue(getContext(), Constants.kFCMToken), "android"); //, password
//        }else{
//            call = AppManager.getInstance().apiInterface.loginWithPhone(Functions.getAppLang(getContext()), phone, Functions.getPrefValue(getContext(), Constants.kFCMToken), "android"); //, OTP
//        }
//        call.enqueue(new Callback<ResponseBody>() {
//            @Override
//            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//                Functions.hideLoader(hud);
//                if (response.body() != null) {
//                    try {
//                        JSONObject object = new JSONObject(response.body().string());
//                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
//                            JSONObject obj = object.getJSONObject(Constants.kData);
//                            String userId = obj.getString("id");
//                            SharedPreferences.Editor editor = getContext().getSharedPreferences(Constants.PREF_NAME, MODE_PRIVATE).edit();
//                            editor.putString(Constants.kUserID, userId);
//                            editor.apply();
//                            if (!userIpDetails.equalsIgnoreCase("otp")){
//                                Functions.showToast(getContext(), object.getString(Constants.kMsg), FancyToast.SUCCESS);
//                                String isSignupRequired = object.getString("is_signup_required");
//                                SharedPreferences.Editor editor1 = getContext().getSharedPreferences(Constants.PREF_NAME, MODE_PRIVATE).edit();
//                                editor1.putString(Constants.kaccessToken, object.getString("access_token"));
//                                editor1.apply();
//
//                                Gson gson = new Gson();
//                                    UserInfo userInfo = gson.fromJson(obj.toString(), UserInfo.class);
//                                    if (isSignupRequired.equalsIgnoreCase("1")) {
//                                        if (userInfo != null && !userInfo.isEmpty()) {
//                                            if (userInfo.getUserRole().equalsIgnoreCase(Constants.kPlayerType)) {
//                                                Functions.saveUserinfo(getContext(), userInfo);
//                                                Intent intent = new Intent(getContext(), PlayerSignupActivity.class);
//                                                intent.putExtra("is_referee", false);
//                                                startActivity(intent);
//                                            }
//                                            else {
//                                                Intent intent = new Intent(getContext(), UserTypeActivity.class);
//                                                startActivity(intent);
//                                            }
//                                        }
//                                        else {
//                                            Intent intent = new Intent(getContext(), UserTypeActivity.class);
//                                            startActivity(intent);
//                                        }
//                                    }
//                                    else {
//                                        SharedPreferences.Editor editor2 = getContext().getSharedPreferences(Constants.PREF_NAME, MODE_PRIVATE).edit();
//                                        editor2.putString(Constants.kUserID, userInfo.getId());
//                                        editor2.putString(Constants.kIsSignIn, "1");
//                                        editor2.putString(Constants.kUserType, userInfo.getUserRole());
//                                        editor2.putString(Constants.kCurrency, userInfo.getCurrency());
//                                        editor2.apply();
//
//                                        //userInfo.setPhoneVerified("0");
//                                        Functions.saveUserinfo(getContext(), userInfo);
//
//                                        String fcmToken = Functions.getPrefValue(getContext(), Constants.kFCMToken);
//                                        if (!fcmToken.isEmpty()) {
//                                            sendFcmTokenApi(fcmToken);
//                                        }
//
//                                        if (userInfo.getUserRole().equalsIgnoreCase(Constants.kPlayerType)) {
//                                            if (!userModule.equalsIgnoreCase("all")){
//                                                SharedPreferences.Editor editor3 = getContext().getSharedPreferences(Constants.PREF_NAME, MODE_PRIVATE).edit();
//                                                editor3.putString(Constants.kAppModule, Constants.kLineupModule);
//                                                editor3.apply();
//                                                Intent intent = new Intent(getContext(), MainActivity.class);
//                                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
//                                                startActivity(intent);
//                                            }else {
//                                                SharedPreferences.Editor editor4 = getContext().getSharedPreferences(Constants.PREF_NAME, MODE_PRIVATE).edit();
//                                                editor4.putString(Constants.kAppModule, Constants.kFootballModule);
//                                                editor4.apply();
//                                                Intent intent = new Intent(getContext(), OlePlayerMainTabsActivity.class);
//                                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
//                                                startActivity(intent);
//                                            }
////                                    if (Functions.getPrefValue(getContext(), Constants.kAppModule).equalsIgnoreCase("")) {
////                                        Intent intent = new Intent(getContext(), ModuleOptionsActivity.class);
////                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
////                                        startActivity(intent);
////                                    }
//
//                                            finish();
//                                        }
//                                        else  if (userInfo.getUserRole().equalsIgnoreCase(Constants.kOwnerType)) {
//                                            Intent intent = new Intent(getContext(), OleOwnerMainTabsActivity.class);
//                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
//                                            startActivity(intent);
//                                            finish();
//                                        }
//                                        else {
//                                            Functions.showToast(getContext(), "Referee module is coming soon", FancyToast.SUCCESS);
//                                        }
//                                    }
//
//                            }
//                            else {
//                                Intent intent = new Intent(getContext(), VerifyPhoneActivity.class);
//                                intent.putExtra("phone", phone);
//                                startActivity(intent);
//                            }
//
//                        }
//                        else {
//                            Functions.showToast(getContext(), object.getString(Constants.kMsg), FancyToast.ERROR);
//                        }
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                        Functions.showToast(getContext(), e.getLocalizedMessage(), FancyToast.ERROR);
//                    }
//                }
//                else {
//                    Functions.showToast(getContext(), getString(R.string.error_occured), FancyToast.ERROR);
//                }
//            }
//
//            @Override
//            public void onFailure(Call<ResponseBody> call, Throwable t) {
//                Functions.hideLoader(hud);
//                if (t instanceof UnknownHostException) {
//                    Functions.showToast(getContext(), getString(R.string.check_internet_connection), FancyToast.ERROR);
//                }
//                else {
//                    Functions.showToast(getContext(), t.getLocalizedMessage(), FancyToast.ERROR);
//                }
//            }
//        });
    }


}
