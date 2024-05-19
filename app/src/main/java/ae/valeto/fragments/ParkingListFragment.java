package ae.valeto.fragments;

import android.Manifest;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.shashank.sony.fancytoastlib.FancyToast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.UnknownHostException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ae.valeto.R;
import ae.valeto.activities.ActiveTicketDetailActivity;
import ae.valeto.activities.ParkingDetailsActivity;
import ae.valeto.adapters.ParkingAdapter;
import ae.valeto.adapters.ParkingCityAdapter;
import ae.valeto.base.BaseFragment;
import ae.valeto.activities.CustomerMainTabsActivity;
import ae.valeto.databinding.FragmentParkingListBinding;
import ae.valeto.dialogs.RatingDialogFragment;
import ae.valeto.dialogs.ScanSuccessPopupFragment;
import ae.valeto.models.MyTicket;
import ae.valeto.models.Parking;
import ae.valeto.models.ParkingCity;
import ae.valeto.util.AppManager;
import ae.valeto.util.Constants;
import ae.valeto.util.Functions;
import mumayank.com.airlocationlibrary.AirLocation;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ParkingListFragment extends BaseFragment implements View.OnClickListener {

    private FragmentParkingListBinding binding;
    private final List<Parking> parkingList = new ArrayList<>();
    private final List<ParkingCity> parkingCityList = new ArrayList<>();
    private ParkingCityAdapter parkingCityAdapter;
    private ParkingAdapter parkingAdapter;
    private MyTicket myTicket;
    private final String selectedClubId = "";
    private final int selectedIndex = 0;
    private String parkingCityId = "";
    private Location location;
//    TicketTimer ticketTimer;

    public ParkingListFragment() {
        //Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentParkingListBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        // getParkingList(parkingList.isEmpty());

        enableLocationUpdates();


        LinearLayoutManager ParkingCityNameLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        binding.parkingCityRecyclerVu.setLayoutManager(ParkingCityNameLayoutManager);
        parkingCityAdapter = new ParkingCityAdapter(getContext(), parkingCityList);
        parkingCityAdapter.setItemClickListener(parkingNameClickListener);
        binding.parkingCityRecyclerVu.setAdapter(parkingCityAdapter);

        LinearLayoutManager customerParkingListLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        binding.parkingRecyclerVu.setLayoutManager(customerParkingListLayoutManager);
        parkingAdapter = new ParkingAdapter(getContext(), parkingList);
        parkingAdapter.setItemClickListener(itemClickListener);
        binding.parkingRecyclerVu.setAdapter(parkingAdapter);

        binding.pullRefresh.setColorSchemeColors(getResources().getColor(R.color.appColor));
        binding.pullRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getParkingList(false);
            }
        });

        binding.relNotif.setOnClickListener(this);
        binding.relMenu.setOnClickListener(this);
        binding.activeTicketVu.setOnClickListener(this);

        return view;
    }
    ParkingCityAdapter.ItemClickListener parkingNameClickListener = new ParkingCityAdapter.ItemClickListener() {
        @Override
        public void itemClicked(View view, int pos) {


            if (parkingCityList.get(pos) == null){
                parkingCityId = "";
                getParkingList(true);

            }else{
                parkingCityId = String.valueOf(parkingCityList.get(pos).getId());
                getParkingList(true);
            }
            parkingCityAdapter.setSelectedIndex(pos);
//            parkingAdapter.notifyDataSetChanged();


        }
    };
    ParkingAdapter.ItemClickListener itemClickListener = new ParkingAdapter.ItemClickListener() {
        @Override
        public void itemClicked(View view, int pos) {
            Intent intent = new Intent(getActivity(), ParkingDetailsActivity.class);
            intent.putExtra("parking_id", parkingList.get(pos).getId());
            startActivity(intent);
        }
    };

    @Override
    public void onDestroyView() {
        super.onDestroyView();
//        if (ticketTimer !=null){
//            ticketTimer.stop();
//        }
        binding = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        getLocationAndCallAPI();
        setBadgeValue();
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mMessageReceiver, new IntentFilter("receive_push"));
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mMessageReceiver);
    }

    private final BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String type = intent.getStringExtra("type");
            if (type !=null){
                if (type.equalsIgnoreCase("ticketAcceptedByEmployee")) {
                    binding.tvStatus.setText("Accepted");
                }
                else if (type.equalsIgnoreCase("ticketClosed")) {
                    binding.activeTicketVu.setVisibility(View.GONE);
                    showRatingDialog(myTicket.getParking().getId(),myTicket.getParking().getPhoto(), myTicket.getParking().getName(), myTicket.getParking().getLocation());
                }
            }
        }
    };

    @Override
    public void onClick(View v) {
        if (v == binding.relNotif) {
            notifClicked();
        }
        else if (v == binding.activeTicketVu) {

            Intent intent = new Intent(getActivity(), ActiveTicketDetailActivity.class);
            intent.putExtra("ticket_id", myTicket.getId());
            startActivity(intent);

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

    private void getParkingList(boolean isLoader) {
        Call<ResponseBody> call;
        KProgressHUD hud = isLoader ? Functions.showLoader(getActivity(), "Image processing"): null;
        if (location == null) {
            call = AppManager.getInstance().apiInterface.getParkingList(parkingCityId,0, 0);
        }
        else {
            call = AppManager.getInstance().apiInterface.getParkingList(parkingCityId, location.getLatitude(), location.getLongitude());
        }
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (binding != null) {
                    binding.pullRefresh.setRefreshing(false);
                }
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            JSONObject data = object.getJSONObject(Constants.kData);

                            AppManager.getInstance().notificationCount = Integer.parseInt(data.getString("unread_count"));

                            JSONArray arr = data.getJSONArray("parking");
                            Gson gson = new Gson();
                            parkingList.clear();
                            myTicket = null;
                            for (int i = 0; i < arr.length(); i++) {
                                Parking parking = gson.fromJson(arr.get(i).toString(), Parking.class);
                                parkingList.add(parking);
                            }
                            if (parkingCityList.isEmpty()){
                                JSONArray citiesArr = data.getJSONArray("cities");
                                Gson gson1 = new Gson();
                                parkingCityList.clear();
                                for (int i = 0; i < citiesArr.length(); i++) {
                                    ParkingCity parkingCity = gson1.fromJson(citiesArr.get(i).toString(), ParkingCity.class);
                                    parkingCityList.add(parkingCity);
                                }
                                parkingCityList.add(0, null);
                                parkingCityAdapter.setSelectedIndex(0);
                                parkingCityAdapter.notifyDataSetChanged();
                            }

                            if (data.has("ticket")) {
                                myTicket = new Gson().fromJson(data.getString("ticket"), MyTicket.class);
                            }
                            populateMyTicket();

                        }else {
                            Functions.showToast(getContext(), object.getString(Constants.kMsg), FancyToast.SUCCESS);
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
                if (binding != null) {
                    binding.pullRefresh.setRefreshing(false);
                }
                if (t instanceof UnknownHostException) {
                    Functions.showToast(getContext(), getString(R.string.check_internet_connection), FancyToast.ERROR);
                }
                else {
                    Functions.showToast(getContext(), t.getLocalizedMessage(), FancyToast.ERROR);
                }
            }
        });
    }
    private void populateMyTicket() {
        if (myTicket != null){
            binding.activeTicketVu.setVisibility(View.VISIBLE);
            binding.tvCarNumber.setText(myTicket.getCar().getPlateNumber());
            binding.tvParkingName.setText(myTicket.getParking().getName());
            if (String.valueOf(myTicket.getParking().getIsFixedPrice()).equalsIgnoreCase("1")){
                binding.tvParkingPrice.setText("AED "+myTicket.getParking().getPrice() + " ");
            }
            else{
                binding.tvParkingPrice.setText("AED "+myTicket.getParking().getPrice() + "/hr");
            }
            binding.tvStatus.setText(myTicket.getStatus());


            String[] parts = myTicket.getDuration().split(" ");
            String hours = parts[0].substring(0, parts[0].length() - 1);
            String minutes = parts[1].substring(0, parts[1].length() - 1);

            int hoursInt = Integer.parseInt(hours);
            int minutesInt = Integer.parseInt(minutes);

            String formattedHours = String.format(Locale.getDefault(), "%02d", hoursInt);
            String formattedMinutes = String.format(Locale.getDefault(), "%02d", minutesInt);


            binding.hours.setText(formattedHours);
            binding.minutes.setText(formattedMinutes);
            binding.tvPrice.setText(myTicket.getTicketPrice()+".00");

        }else{
            binding.hours.setText("");
            binding.minutes.setText("");
            binding.tvPrice.setText("");
            binding.activeTicketVu.setVisibility(View.GONE);
        }
        parkingAdapter.notifyDataSetChanged();
    }
//    public class TicketTimer {
//        private static final String TIME_FORMAT = "dd/MM/yyyy hh:mma";
//        private static final long TICK_INTERVAL = 1000; // Update timer every second
//
//        private Date startTime;
//        private Timer timer;
//        private final double parkingPrice;
//        private String isFixedPrice;
//
//        public TicketTimer(String startTimeString, double parkingPrice, String isFixedPrice) {
//            this.parkingPrice = parkingPrice;
//            this.isFixedPrice = isFixedPrice;
//            try {
//                // Test Code
//                // Set the initial start time to 59 minutes and 59 seconds ago
//
////                Calendar calendar = Calendar.getInstance();
////                calendar.setTime(new SimpleDateFormat(TIME_FORMAT, Locale.getDefault()).parse(startTimeString));
////                calendar.add(Calendar.MINUTE, -47);
////                calendar.add(Calendar.SECOND, -59);
////                this.startTime = calendar.getTime();
//
//                this.startTime = new SimpleDateFormat(TIME_FORMAT, Locale.getDefault()).parse(startTimeString);
//            } catch (ParseException e) {
//                e.printStackTrace();
//            }
//        }
//
//        public void start() {
//            timer = new Timer();
//            timer.schedule(new TimerTask() {
//                @Override
//                public void run() {
//                    updateTimer();
//                }
//            }, 0, TICK_INTERVAL);
//        }
//
//        public void stop() {
//            if (timer != null) {
//                timer.cancel();
//                timer = null;
//            }
//        }
//
//        private void updateTimer() {
//            Date currentTime = new Date();
//            long elapsedTime = currentTime.getTime() - startTime.getTime();
//            long hours = elapsedTime / (60 * 60 * 1000);
//            long minutes = (elapsedTime / (60 * 1000)) % 60;
//            long seconds = (elapsedTime / 1000) % 60;
//
//            // Convert numerical values to strings with leading zeros
//            String hoursString = String.format(Locale.getDefault(), "%02d", hours);
//            String minutesString = String.format(Locale.getDefault(), "%02d", minutes);
//            String secondsString = String.format(Locale.getDefault(), "%02d", seconds);
//
//            // Update UI with hours, minutes, and seconds on the main UI thread
//            getActivity().runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    binding.hours.setText(hoursString);
//                    binding.minutes.setText(minutesString);
//                    binding.seconds.setText(secondsString);
//                }
//            });
//
//            double price = calculatePrice(elapsedTime);
//            // Update UI with calculated price on the main UI thread
//            getActivity().runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    binding.tvPrice.setText(String.format(Locale.getDefault(), "%.2f", price));
//                }
//            });
//        }
//
//        private double calculatePrice(long elapsedTime) {
//            if (isFixedPrice.equalsIgnoreCase("1")) {
//                return parkingPrice;
//            } else {
//                long totalHours = elapsedTime / (60 * 60 * 1000);
//                double originalPrice = parkingPrice * totalHours;
//                double additionalPrice = 0;
//                long remainingMinutes = (elapsedTime / (60 * 1000)) % 60;
//                if (remainingMinutes > 0) {
//                    additionalPrice = parkingPrice;
//                }
//                double totalPrice = originalPrice + additionalPrice;
//                if (totalHours == 0) {
//                    return parkingPrice;
//                } else {
//                    return totalPrice;
//                }
//            }
//        }
//    }
    private void enableLocationUpdates() {
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!isGPSEnabled) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage("Location services are disabled. Please enable them to see nearest parking's.")
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        } else {
            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                getLocationAndCallAPI();
            }
        }
    }
    private void getLocationAndCallAPI() {
        new AirLocation(getActivity(), true, false, new AirLocation.Callbacks() {
            @Override
            public void onSuccess(Location loc) {
                location = loc;
                getParkingList(parkingList.isEmpty());
            }
            @Override
            public void onFailed(AirLocation.LocationFailedEnum locationFailedEnum) {
                getParkingList(false);
            }
        });
    }

}