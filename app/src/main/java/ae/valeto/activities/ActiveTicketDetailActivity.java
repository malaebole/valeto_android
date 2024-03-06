package ae.valeto.activities;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
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
//        if (ticketId == 0){
//            getParkingList(true,1);
//        }else{
            getSingleUserTicket(true, ticketId);
//        }



        binding.btnBack.setOnClickListener(this);
        binding.callBtn.setOnClickListener(this);
        binding.btnMakeCarReady.setOnClickListener(this);
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
            if (!myTicket.getSlotNumber().isEmpty()){
                binding.tvSlot.setText(myTicket.getSlotNumber());
            }else {
                binding.tvSlot.setText("N/A");

            }
            if (!myTicket.getKeyCode().isEmpty()){
                binding.tvKey.setText(myTicket.getKeyCode());
            }else {
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
            getContext().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    binding.hours.setText(hoursString);
                    binding.minutes.setText(minutesString);
                    binding.seconds.setText(secondsString);
                }
            });

            double price = calculatePrice(elapsedTime);
            // Update UI with calculated price on the main UI thread
            getContext().runOnUiThread(new Runnable() {
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