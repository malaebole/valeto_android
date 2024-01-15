package ae.valeto.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;
import java.util.List;
import ae.valeto.activities.CustomerMainTabsActivity;
import ae.valeto.activities.MyCarsActivity;
import ae.valeto.activities.ProfileActivity;
import ae.valeto.adapters.ParkingAdapter;
import ae.valeto.base.BaseFragment;
import ae.valeto.databinding.FragmentMenuBinding;
import ae.valeto.models.Parking;
import ae.valeto.util.AppManager;


public class MenuFragment extends BaseFragment implements View.OnClickListener {

    private FragmentMenuBinding binding;
    private final List<Parking> parkingList = new ArrayList<>();
    private ParkingAdapter parkingAdapter;

    public MenuFragment() {
        //Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentMenuBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        binding.profileVu.setOnClickListener(this);
        binding.carsVu.setOnClickListener(this);
        binding.activeTicketVu.setOnClickListener(this);
        binding.closedTicketVu.setOnClickListener(this);
        binding.termsAndConditionsVu.setOnClickListener(this);
        binding.privacyAndPolicyVu.setOnClickListener(this);
        binding.contactVu.setOnClickListener(this);
        binding.logoutVu.setOnClickListener(this);
        binding.customerImgVu.setOnClickListener(this);
        binding.relNotif.setOnClickListener(this);

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
        else if (v == binding.activeTicketVu){
            activeTicketClicked();
        }
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
        else if (v == binding.customerImgVu) {
            customerClicked();
        }
        else if (v == binding.relNotif) {
            notifClicked();
        }

    }

    private void closedTicketClicked() {

    }

    private void termsAndConditionsClicked() {

    }

    private void privacyPolicyClicked() {

    }

    private void contactClicked() {

    }

    private void logoutClicked() {

    }

    private void customerClicked() {

    }

    private void activeTicketClicked() {

    }

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