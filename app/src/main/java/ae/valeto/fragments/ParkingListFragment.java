package ae.valeto.fragments;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.kaopiz.kprogresshud.KProgressHUD;
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
import java.util.Timer;
import java.util.TimerTask;

import ae.valeto.R;
import ae.valeto.activities.ActiveTicketDetailActivity;
import ae.valeto.activities.ParkingDetailsActivity;
import ae.valeto.adapters.ParkingAdapter;
import ae.valeto.adapters.ParkingCityAdapter;
import ae.valeto.base.BaseFragment;
import ae.valeto.activities.CustomerMainTabsActivity;
import ae.valeto.databinding.FragmentParkingListBinding;
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

    private String selectedClubId = "";
    private int selectedIndex = 0;
    private int parkingCityId;
    private Location location;
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1; // 1 minute
    private static final float MIN_DISTANCE_CHANGE_FOR_UPDATES = 10.0f; // 10 meters


    public ParkingListFragment() {
        //Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentParkingListBinding.inflate(inflater, container, false);
        View view = binding.getRoot();


//        getParkingList(parkingList.isEmpty());

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

        binding.relNotif.setOnClickListener(this);
        binding.relMenu.setOnClickListener(this);
        binding.activeTicketVu.setOnClickListener(this);

        return view;
    }

    ParkingCityAdapter.ItemClickListener parkingNameClickListener = new ParkingCityAdapter.ItemClickListener() {
        @Override
        public void itemClicked(View view, int pos) {
            parkingCityAdapter.setSelectedId(parkingCityList.get(pos).getId());
            parkingCityId = parkingCityList.get(pos).getId();
//            getParkingList(true, parkingCityId);
            parkingAdapter.notifyDataSetChanged();
        }
    };

    ParkingAdapter.ItemClickListener itemClickListener = new ParkingAdapter.ItemClickListener() {
        @Override
        public void itemClicked(View view, int pos) {
            Intent intent = new Intent(getActivity(), ParkingDetailsActivity.class);
            intent.putExtra("parking_id", parkingList.get(pos).getId());
            startActivity(intent);
            // populateClubData(pos);
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
        getLocationAndCallAPI();
        setBadgeValue();
    }

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


//    private void populateClubData (int pos){
//
//    }


    private void getParkingList(boolean isLoader, int cityId) {
        Call<ResponseBody> call;
        KProgressHUD hud = isLoader ? Functions.showLoader(getActivity(), "Image processing"): null;
//        call = AppManager.getInstance().apiInterface.getParkingList(latitude,longitude);
        if (location == null) {
            call = AppManager.getInstance().apiInterface.getParkingList(cityId,0, 0);
        }
        else {
            call = AppManager.getInstance().apiInterface.getParkingList(cityId, location.getLatitude(), location.getLongitude());
        }
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            JSONObject data = object.getJSONObject(Constants.kData);

                            JSONArray arr = data.getJSONArray("parking");
                            Gson gson = new Gson();
                            parkingList.clear();
                            AppManager.getInstance().parkings.clear();
                            for (int i = 0; i < arr.length(); i++) {
                                Parking parking = gson.fromJson(arr.get(i).toString(), Parking.class);
                                parkingList.add(parking);
                                AppManager.getInstance().parkings.add(parking);
                            }

                            JSONArray citiesArr = data.getJSONArray("cities");
                            Gson gson1 = new Gson();
                            parkingCityList.clear();

                            ParkingCity allParkingCity = new ParkingCity();
                            allParkingCity.setId(0);
                            allParkingCity.setName("All");
                            parkingCityList.add(allParkingCity);

                            for (int i = 0; i < arr.length(); i++) {
                                ParkingCity parkingCity = gson1.fromJson(citiesArr.get(i).toString(), ParkingCity.class);
                                parkingCityList.add(parkingCity);
                            }
                            parkingCityAdapter.setSelectedId(parkingCityList.get(0).getId());

                            if (data.has("ticket")) {
                                myTicket = new Gson().fromJson(data.getString("ticket"), MyTicket.class);
                                populateMyTicket();
                            }


                            parkingAdapter.notifyDataSetChanged();
                            parkingCityAdapter.notifyDataSetChanged();


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

    private void populateMyTicket() {
    if (myTicket.getId() != null){
            binding.activeTicketVu.setVisibility(View.VISIBLE);
            binding.tvCarNumber.setText(myTicket.getCar().getPlateNumber());
            binding.tvParkingName.setText(myTicket.getParking().getName());
            binding.tvParkingPrice.setText("AED " + myTicket.getParking().getPrice()+ "/hr");

            TicketTimer ticketTimer = new TicketTimer(myTicket.getStartTime(), Double.parseDouble(myTicket.getParking().getPrice()));
            ticketTimer.start();
        }

    }


    private class TicketTimer {
        private static final String TIME_FORMAT = "dd/MM/yyyy hh:mma";
        private static final long TICK_INTERVAL = 1000; // Update timer every second

        private Date startTime;
        private Timer timer;
        private double parkingPrice;

        public TicketTimer(String startTimeString, double parkingPrice) {
            this.parkingPrice = parkingPrice;
            try {
                this.startTime = new SimpleDateFormat(TIME_FORMAT, Locale.getDefault()).parse(startTimeString);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        public void start() {
            timer = new Timer();
            timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    updateTimer();
                }
            }, 0, TICK_INTERVAL);
        }

        public void stop() {
            if (timer != null) {
                timer.cancel();
                timer = null;
            }
        }

        private void updateTimer() {
            Date currentTime = new Date();
            long elapsedTime = currentTime.getTime() - startTime.getTime();
            long hours = elapsedTime / (60 * 60 * 1000);
            long minutes = (elapsedTime / (60 * 1000)) % 60;
            long seconds = (elapsedTime / 1000) % 60;

            // Convert numerical values to strings with leading zeros
            String hoursString = String.format(Locale.getDefault(), "%02d", hours);
            String minutesString = String.format(Locale.getDefault(), "%02d", minutes);
            String secondsString = String.format(Locale.getDefault(), "%02d", seconds);

            // Update UI with hours, minutes, and seconds on the main UI thread
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    binding.hours.setText(hoursString);
                    binding.minutes.setText(minutesString);
                    binding.seconds.setText(secondsString);
                }
            });

            double price = calculatePrice(elapsedTime);
            // Update UI with calculated price on the main UI thread
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    binding.tvPrice.setText(String.format(Locale.getDefault(), "%.2f", price));
                }
            });
        }



        private double calculatePrice(long elapsedTime) {
            double totalMinutes = elapsedTime / (60 * 1000);
            return (parkingPrice / 60) * totalMinutes;
        }
    }


    private void enableLocationUpdates() {
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        // Check if GPS provider is enabled
        boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if (!isGPSEnabled) {
            // GPS provider is not enabled, prompt user to enable it
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage("Location services are disabled. Please enable them to see nearest parking's.")
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // Open location settings to allow the user to enable location services
                            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // User declined, inform the user about the necessity of location services or handle the situation accordingly
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        } else {
            // GPS provider is enabled, proceed with requesting location updates
            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                getLocationAndCallAPI();
            }
            //locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, locationListener);
        }
    }

//    private LocationListener locationListener = new LocationListener() {
//        @Override
//        public void onLocationChanged(Location location) {
//            // Handle location updates
//            double latitude = location.getLatitude();
//            double longitude = location.getLongitude();
//            // Do something with latitude and longitude
//        }
//
//        @Override
//        public void onStatusChanged(String provider, int status, Bundle extras) {}
//
//        @Override
//        public void onProviderEnabled(String provider) {}
//
//        @Override
//        public void onProviderDisabled(String provider) {}
//    };



    private void getLocationAndCallAPI() {
        new AirLocation(getActivity(), true, false, new AirLocation.Callbacks() {
            @Override
            public void onSuccess(Location loc) {
                location = loc;
                getParkingList(parkingList.isEmpty(), parkingCityId);
            }

            @Override
            public void onFailed(AirLocation.LocationFailedEnum locationFailedEnum) {
                getParkingList(false,parkingCityId);
            }
        });
    }



}