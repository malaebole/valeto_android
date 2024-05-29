package ae.valeto.base;


import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.kaopiz.kprogresshud.KProgressHUD;
import com.shashank.sony.fancytoastlib.FancyToast;

import org.json.JSONObject;

import java.net.UnknownHostException;

import ae.valeto.MyApp;
import ae.valeto.R;
import ae.valeto.activities.LoginActivity;
import ae.valeto.dialogs.RatingDialogFragment;
import ae.valeto.util.AppManager;
import ae.valeto.util.Constants;
import ae.valeto.util.Functions;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * A simple {@link Fragment} subclass.
 */
public class BaseFragment extends Fragment {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        Functions.changeLanguage(getActivity(), Functions.getPrefValue(getActivity(), Constants.kAppLang));
    }

    protected void hideKeyboard() {
        try {
            InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
        } catch (Exception e) {
            // TODO: handle exception
        }
    }
    protected void logoutApi() {
        KProgressHUD hud = Functions.showLoader(getContext());
        String uniqueID = Functions.getPrefValue(getContext(), Constants.kDeviceUniqueId);
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.userLogout(uniqueID);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {

                            SharedPreferences.Editor editor = MyApp.getAppContext().getSharedPreferences(Constants.PREF_NAME, MODE_PRIVATE).edit();
                            editor.remove(Constants.kUserID);
                            editor.remove(Constants.kAccessToken);
                            editor.remove(Constants.kRefreshToken);
                            editor.remove(Constants.kIsSignIn);
                            editor.remove(Constants.kUserInfo);
                            editor.remove(Constants.kUserType);
                            editor.apply();

                            Intent intent = new Intent(getActivity(),LoginActivity.class);
                            startActivity(intent);
                            getActivity().finish();


                        }
                        else {
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
                }
                else {
                    Functions.showToast(getContext(), t.getLocalizedMessage(), FancyToast.ERROR);
                }
            }
        });
    }

    protected void showRatingDialog(int id, String photo, String name, String location) {
        FragmentTransaction fragmentTransaction = getParentFragmentManager().beginTransaction();
        Fragment fragment = getParentFragmentManager().findFragmentByTag("RatingDialogFragment");
        if (fragment != null) {
            fragmentTransaction.remove(fragment);
        }
        fragmentTransaction.addToBackStack(null);
        RatingDialogFragment dialogFragment = new RatingDialogFragment(id ,photo,name,location);
        dialogFragment.setDialogCallback((df) -> {
            df.dismiss();
        });
        dialogFragment.show(fragmentTransaction, "RatingDialogFragment");

    }

}
