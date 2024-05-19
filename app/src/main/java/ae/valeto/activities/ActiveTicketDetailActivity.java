package ae.valeto.activities;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

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
import ae.valeto.base.BaseActivity;
import ae.valeto.databinding.ActivityActiveTicketBinding;
import ae.valeto.dialogs.ChangePassDialogFragment;
import ae.valeto.dialogs.DurationDialogFragment;
import ae.valeto.fragments.ParkingListFragment;
import ae.valeto.models.MyTicket;
import ae.valeto.models.Parking;
import ae.valeto.models.ParkingCity;
import ae.valeto.util.AppManager;
import ae.valeto.util.Constants;
import ae.valeto.util.Functions;
import okhttp3.ResponseBody;
import okqapps.com.tagslayout.TagItem;
import okqapps.com.tagslayout.TagTextSize;
import okqapps.com.tagslayout.UnSelectedTagTheme;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ActiveTicketDetailActivity extends BaseActivity implements View.OnClickListener {

    private ActivityActiveTicketBinding binding;
    private MyTicket myTicket;
    private int ticketId;
//    private TicketTimer ticketTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityActiveTicketBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        makeStatusbarTransperant();

        Bundle bundle = getIntent().getExtras();
        if (bundle !=null){
           ticketId = bundle.getInt("ticket_id");
        }

        getSingleUserTicket(true, ticketId);

        binding.btnBack.setOnClickListener(this);
        binding.callBtn.setOnClickListener(this);
        binding.btnMakeCarReady.setOnClickListener(this);
        binding.directionBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == binding.btnBack){
            finish();
        }
        else if (v == binding.callBtn) {
            callBtnClicked();
        }
        else if (v == binding.btnMakeCarReady) {
            makeMyCarReadyDialog();
        }
        else if (v == binding.directionBtn) {
            directionBtnClicked();
        }

    }

    private void getSingleUserTicket(boolean isLoader, int ticketId) {
        Call<ResponseBody> call;
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing"): null;
        call = AppManager.getInstance().apiInterface.getSingleUserTicket(ticketId);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            JSONObject obj = object.getJSONObject(Constants.kData);
                            Gson gson = new Gson();
                            myTicket = gson.fromJson(obj.toString(), MyTicket.class);
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
                if (t instanceof UnknownHostException) {
                    Functions.showToast(getContext(), getString(R.string.check_internet_connection), FancyToast.ERROR);
                }
                else {
                    Functions.showToast(getContext(), t.getLocalizedMessage(), FancyToast.ERROR);
                }
            }
        });
    }
    private void makeMyCarReady(boolean isLoader, int ticketId, String time) {
        Call<ResponseBody> call;
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing"): null;
        call = AppManager.getInstance().apiInterface.makeMyCarReady(ticketId, time);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {

                            Functions.showToast(getContext(), "Please wait we are preparing your car", FancyToast.SUCCESS);
                            binding.btnMakeCarReady.setVisibility(View.GONE);
                            finish();

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
            binding.tvCarNumber.setText(myTicket.getCar().getPlateNumber());
            binding.tvParkingName.setText(myTicket.getParking().getName());
            binding.tvCurrency.setText(myTicket.getCurrency());
            binding.tvParkedBy.setText(myTicket.getActivatedBy().getName());
            binding.tvStatus.setText(myTicket.getStatus());
            if (!myTicket.getSlotNumber().isEmpty()){
                binding.tvSlot.setText(myTicket.getSlotNumber());
            }
            else {
                binding.tvSlot.setText("N/A");

            }
            if (!myTicket.getKeyCode().isEmpty()){
                binding.tvKey.setText(myTicket.getKeyCode());
            }
            else {
                binding.tvKey.setText("N/A");

            }
            binding.tvLoc.setText(myTicket.getParking().getLocation());

            List<TagItem> tagItems = new ArrayList<>();
            for (int i = 0; i < myTicket.getParking().getFacilites().size(); i++) {
                String facilityName = myTicket.getParking().getFacilites().get(i).getName();
                TagItem tagItem = new TagItem(i, facilityName, "#E2E6EA", "#7A7E83", false); // Blue text, gray background
                tagItems.add(tagItem);
            }
            binding.tagsLayout.setTagTextSize(TagTextSize.SMALL);
            binding.tagsLayout.setUnSelectedTagTheme(UnSelectedTagTheme.GRAY);
            binding.tagsLayout.setBackgroundColor(Color.TRANSPARENT);
            binding.tagsLayout.initializeTags(this, tagItems);
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

            if (myTicket.getStatus().equalsIgnoreCase("requested") || myTicket.getStatus().equalsIgnoreCase("accepted") || myTicket.getStatus().equalsIgnoreCase("closed") || myTicket.getStatus().equalsIgnoreCase("ready")){
                binding.btnMakeCarReady.setVisibility(View.GONE);
            }
            else{
                binding.btnMakeCarReady.setVisibility(View.VISIBLE);
            }
        }else{
            binding.hours.setText("");
            binding.minutes.setText("");
            binding.tvPrice.setText("");
        }
    }

    private void directionBtnClicked() {
        Intent directionIntent = new Intent(Intent.ACTION_VIEW);
        String uri = String.format("http://maps.google.com/maps?daddr=%f,%f", myTicket.getParking().getLatitude(), myTicket.getParking().getLongitude());
        directionIntent.setData(Uri.parse(uri));
        directionIntent.setPackage("com.google.android.apps.maps");
        startActivity(directionIntent);
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
//                //  Calendar calendar = Calendar.getInstance();
//                //  calendar.setTime(new SimpleDateFormat(TIME_FORMAT, Locale.getDefault()).parse(startTimeString));
//                //  calendar.add(Calendar.MINUTE, -47);
//                //  calendar.add(Calendar.SECOND, -59);
//                //  this.startTime = calendar.getTime();
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
//            getContext().runOnUiThread(new Runnable() {
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
//            getContext().runOnUiThread(new Runnable() {
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

    private void callBtnClicked() {
        Intent callIntent = new Intent(Intent.ACTION_VIEW);
        callIntent.setData(Uri.parse("tel:" + myTicket.getActivatedBy().getPhone()));
        startActivity(callIntent);

    }

    private void makeMyCarReadyDialog() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        Fragment fragment = getSupportFragmentManager().findFragmentByTag("DurationDialogFragment");
        if (fragment != null) {
            fragmentTransaction.remove(fragment);
        }
        fragmentTransaction.addToBackStack(null);
        DurationDialogFragment dialogFragment = new DurationDialogFragment();
        dialogFragment.setDialogCallback((df, time) -> {
            df.dismiss();
            makeMyCarReady(true, ticketId, time);



        });
        dialogFragment.show(fragmentTransaction, "DurationDialogFragment");

    }

//    private void getParkingList(boolean isLoader, int cityId) {
//        Call<ResponseBody> call;
//        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing"): null;
//        call = AppManager.getInstance().apiInterface.getParkingList(1,0,0);
//        call.enqueue(new Callback<ResponseBody>() {
//            @Override
//            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//                Functions.hideLoader(hud);
//                if (response.body() != null) {
//                    try {
//                        JSONObject object = new JSONObject(response.body().string());
//                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
//                            JSONObject data = object.getJSONObject(Constants.kData);
//                            if (data.has("ticket")) {
//                                myTicket = new Gson().fromJson(data.getString("ticket"), MyTicket.class);
//                                ticketId = myTicket.getId();
//                                getSingleUserTicket(false, ticketId);
//                            }
//
//                        }
//
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