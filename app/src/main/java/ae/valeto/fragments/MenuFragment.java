package ae.valeto.fragments;

import android.content.Intent;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;
import java.util.List;
import ae.valeto.activities.CustomerMainTabsActivity;
import ae.valeto.activities.ParkingDetailsActivity;
import ae.valeto.adapters.ParkingAdapter;
import ae.valeto.base.BaseFragment;
import ae.valeto.databinding.FragmentMenuBinding;
import ae.valeto.databinding.FragmentParkingListBinding;
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
//        setBadgeValue();
    }

    @Override
    public void onClick(View v) {

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