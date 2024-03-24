package ae.valeto.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.shashank.sony.fancytoastlib.FancyToast;

import org.json.JSONObject;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import ae.valeto.activities.ActiveTicketDetailActivity;
import ae.valeto.activities.CloseTicketActivity;
import ae.valeto.activities.CustomerMainTabsActivity;
import ae.valeto.activities.LoginActivity;
import ae.valeto.activities.MyCarsActivity;
import ae.valeto.activities.ProfileActivity;
import ae.valeto.activities.VerifyEmailActivity;
import ae.valeto.adapters.ParkingAdapter;
import ae.valeto.base.BaseFragment;
import ae.valeto.databinding.FragmentMenuBinding;
import ae.valeto.models.Parking;
import ae.valeto.models.UserInfo;
import ae.valeto.util.AppManager;
import ae.valeto.util.Constants;
import ae.valeto.util.Functions;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MenuFragment extends BaseFragment implements View.OnClickListener {

    private FragmentMenuBinding binding;
    private UserInfo userInfo;

    public MenuFragment() {
        //Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentMenuBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        binding.profileVu.setOnClickListener(this);
        binding.carsVu.setOnClickListener(this);
//        binding.activeTicketVu.setOnClickListener(this);
        binding.closedTicketVu.setOnClickListener(this);
        binding.termsAndConditionsVu.setOnClickListener(this);
        binding.privacyAndPolicyVu.setOnClickListener(this);
        binding.contactVu.setOnClickListener(this);
        binding.logoutVu.setOnClickListener(this);
        binding.imgVu.setOnClickListener(this);
        binding.relNotif.setOnClickListener(this);
        binding.tvEmailVerify.setOnClickListener(this);

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onResume() {
        super.onResume();
         userInfo = Functions.getUserinfo(getContext());
         if (userInfo !=null){
             Glide.with(getActivity()).load(userInfo.getPicture()).apply(RequestOptions.circleCropTransform()).into(binding.imgVu);
             binding.tvName.setText(userInfo.getName());
             binding.tvPoints.setText(String.valueOf(userInfo.getPoints()));
             if (userInfo.getEmailVerified().equalsIgnoreCase("no")){
                 binding.tvEmailVerify.setVisibility(View.VISIBLE);
             }
             else {
                 binding.tvEmailVerify.setVisibility(View.GONE);

             }
         }

        // setBadgeValue();
    }

    @Override
    public void onClick(View v) {

        if (v == binding.profileVu){
            profileClicked();
        }
        else if (v == binding.carsVu){
            carsClicked();
        }
//        else if (v == binding.activeTicketVu){
//            activeTicketClicked();
//        }
        else if (v == binding.closedTicketVu) {
            closedTicketClicked();
        }
        else if (v == binding.termsAndConditionsVu) {
            termsAndConditionsClicked();
        }
        else if (v == binding.privacyAndPolicyVu) {
           privacyPolicyClicked();
        }
        else if (v == binding.contactVu) {
            contactClicked();
        }
        else if (v == binding.logoutVu) {
            logoutClicked();
        }
        else if (v == binding.imgVu) {
            customerClicked();
        }
        else if (v == binding.relNotif) {
            notifClicked();
        }
        else if (v == binding.tvEmailVerify) {
            Intent intent = new Intent(getActivity(), VerifyEmailActivity.class);
            startActivity(intent);
//            verifyEmailClicked(true);
        }

    }



//    private void verifyEmailClicked(boolean isLoader) {
//        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing"): null;
//        Call<ResponseBody> call = AppManager.getInstance().apiInterface.sendOtp();
//        call.enqueue(new Callback<ResponseBody>() {
//            @Override
//            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//                Functions.hideLoader(hud);
//                if (response.body() != null) {
//                    try {
//                        JSONObject object = new JSONObject(response.body().string());
//                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
//
//                            Intent intent = new Intent(getActivity(), VerifyEmailActivity.class);
//                            startActivity(intent);
//
//                        }
//                        else {
//                            Functions.showToast(getContext(), object.getString(Constants.kMsg), FancyToast.ERROR);
//                        }
//                    } catch (Exception e) {
//                        e.printStackTrace();
//
//                    }
//                }
//            }
//            @Override
//            public void onFailure(Call<ResponseBody> call, Throwable t) {
//                Functions.hideLoader(hud);
//            }
//        });
//    }



    private void closedTicketClicked() {
        Intent intent = new Intent(getActivity(), CloseTicketActivity.class);
        startActivity(intent);
    }

    private void termsAndConditionsClicked() {

    }

    private void privacyPolicyClicked() {

    }

    private void contactClicked() {

    }

    private void logoutClicked() {
        logoutApi();

    }

    private void customerClicked() {

    }

//    private void activeTicketClicked() {
//
//        Intent intent = new Intent(getActivity(), ActiveTicketDetailActivity.class);
//        startActivity(intent);
//
//    }

    private void carsClicked() {
        Intent intent = new Intent(getActivity(), MyCarsActivity.class);
        startActivity(intent);
    }

    private void profileClicked() {

        Intent intent = new Intent(getActivity(), ProfileActivity.class);
        startActivity(intent);

    }

    private void notifClicked() {
        if (getActivity() instanceof CustomerMainTabsActivity) {
            ((CustomerMainTabsActivity) getActivity()).notificationsClicked();
        }
    }

    public void setBadgeValue() {
        if (AppManager.getInstance().notificationCount > 0) {
            binding.toolbarBadge.setVisibility(View.VISIBLE);
            binding.toolbarBadge.setNumber(AppManager.getInstance().notificationCount);
        }
        else  {
            binding.toolbarBadge.setVisibility(View.GONE);
        }
    }


}