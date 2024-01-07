package ae.valeto.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.shashank.sony.fancytoastlib.FancyToast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.UnknownHostException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import ae.valeto.R;
import ae.valeto.activities.ParkingDetailsActivity;
import ae.valeto.adapters.ParkingAdapter;
import ae.valeto.base.BaseFragment;
import ae.valeto.customer.CustomerMainTabsActivity;
import ae.valeto.databinding.FragmentParkingListBinding;
import ae.valeto.models.Parking;
import ae.valeto.util.AppManager;
import ae.valeto.util.Constants;
import ae.valeto.util.Functions;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ParkingListFragment extends BaseFragment implements View.OnClickListener {

    private FragmentParkingListBinding binding;
    private final List<Parking> parkingList = new ArrayList<>();
    private ParkingAdapter parkingAdapter;

    public ParkingListFragment() {
        //Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentParkingListBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        LinearLayoutManager customerParkingListLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        binding.parkingRecyclerVu.setLayoutManager(customerParkingListLayoutManager);
        parkingAdapter = new ParkingAdapter(getContext(), parkingList);
        parkingAdapter.setItemClickListener(itemClickListener);
        binding.parkingRecyclerVu.setAdapter(parkingAdapter);

        binding.relNotif.setOnClickListener(this);
        binding.relMenu.setOnClickListener(this);

        return view;
    }


    ParkingAdapter.ItemClickListener itemClickListener = new ParkingAdapter.ItemClickListener() {
        @Override
        public void itemClicked(View view, int pos) {
            Intent intent = new Intent(getActivity(), ParkingDetailsActivity.class);
            startActivity(intent);
//            populateClubData(pos);
        }
    };

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        //getClubList(clubList.isEmpty());
        setBadgeValue();
    }

    @Override
    public void onClick(View v) {
        if (v == binding.relNotif) {
            notifClicked();
        }

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


    private void populateClubData (int pos){

    }


//    private void getClubList(boolean isLoader) {
//        Call<ResponseBody> call;
//        KProgressHUD hud = isLoader ? Functions.showLoader(getActivity(), "Image processing"): null;
//        call = AppManager.getInstance().apiInterface.getMyClubs(Functions.getAppLang(getActivity()),Functions.getPrefValue(getContext(), Constants.kUserID), "");
//        call.enqueue(new Callback<ResponseBody>() {
//            @Override
//            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//                Functions.hideLoader(hud);
//                if (response.body() != null) {
//                    try {
//                        JSONObject object = new JSONObject(response.body().string());
//                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
//                            JSONArray arr = object.getJSONArray(Constants.kData);
//                            Gson gson = new Gson();
//                            clubList.clear();
//                            //clubName.clear();
//                            AppManager.getInstance().clubs.clear();
//                            for (int i = 0; i < arr.length(); i++) {
//                                Club club = gson.fromJson(arr.get(i).toString(), Club.class);
//                                clubList.add(club);
//                                // clubName.add(club);
//                                AppManager.getInstance().clubs.add(club);
//                            }
//
//                            oleClubNameAdapter.setSelectedIndex(selectedIndex);
//                            selectedClubId = clubList.get(selectedIndex).getId();
//                            oleClubNameAdapter.setSelectedId(selectedClubId);
//                            populateClubData(selectedIndex);
//
////                            if (clubList.size() > 0) {
////                                clubList.add(1, null);
////                            }
//
//                            if (clubList.isEmpty()) {
//                                binding.noStadiumVu.setVisibility(View.VISIBLE);
//                            }
//                            else {
//                                binding.noStadiumVu.setVisibility(View.GONE);
//                            }
//                            //adapter.setAvailable(isFootball, isPadel);
//                            //adapter.notifyDataSetChanged();
//                        }
//                        else {
//                            binding.noStadiumVu.setVisibility(View.VISIBLE);
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
//    }
}