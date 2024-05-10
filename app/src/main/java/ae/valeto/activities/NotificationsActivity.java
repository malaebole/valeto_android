package ae.valeto.activities;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.webkit.PermissionRequest;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.baoyz.actionsheet.ActionSheet;
import com.google.gson.Gson;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.single.PermissionListener;
import com.nabinbhandari.android.permissions.PermissionHandler;
import com.nabinbhandari.android.permissions.Permissions;
import com.shashank.sony.fancytoastlib.FancyToast;
import org.json.JSONArray;
import org.json.JSONObject;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import ae.valeto.R;
import ae.valeto.adapters.NotificationListAdapter;
import ae.valeto.base.BaseActivity;
import ae.valeto.databinding.ActivityNotificationsBinding;
import ae.valeto.dialogs.RatingDialogFragment;
import ae.valeto.models.NotificationList;
import ae.valeto.util.AppManager;
import ae.valeto.util.Constants;
import ae.valeto.util.Functions;
import okhttp3.ResponseBody;
import pl.aprilapps.easyphotopicker.ChooserType;
import pl.aprilapps.easyphotopicker.EasyImage;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationsActivity extends BaseActivity implements View.OnClickListener {

    private ActivityNotificationsBinding binding;
    private final List<NotificationList> notificationList = new ArrayList<>();
    private NotificationListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNotificationsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        makeStatusbarTransperant();

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        binding.recyclerVu.setLayoutManager(layoutManager);
        adapter = new NotificationListAdapter(getContext(), notificationList);
        adapter.setItemClickListener(itemClickListener);
        binding.recyclerVu.setAdapter(adapter);

        grantNotificationPermission();

        getNotifications(true);

        binding.btnClose.setOnClickListener(this);
        binding.btnClear.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view == binding.btnClose) {
            finish();
        }
        else if (view == binding.btnClear) {
            clearClicked();
        }
    }


    private void grantNotificationPermission() {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                == PackageManager.PERMISSION_GRANTED){

        }else{
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
                resultLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
            }
        }

    }

    private final ActivityResultLauncher<String> resultLauncher =  registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                  //  Functions.showToast(getContext(), "Permission granted", FancyToast.SUCCESS);
                } else {
                   // Functions.showToast(getContext(), "Permission denied", FancyToast.ERROR);
                }
    });

    NotificationListAdapter.ItemClickListener itemClickListener = new NotificationListAdapter.ItemClickListener() {
        @Override
        public void itemClicked(View view, int pos) {

            if (!notificationList.get(pos).getIsRead()){
                readNotificationAPI(String.valueOf(notificationList.get(pos).getId()));
                notificationList.get(pos).setIsRead(true);
                adapter.notifyItemChanged(pos);
            }

            NotificationList notification = notificationList.get(pos);
//            if (notification.getType().equalsIgnoreCase("gameScoreAdded") && !notification.getIsRated().equalsIgnoreCase("1")) {
//                showBestPlayerDialog(notification.getGameId());
//            }
//            else if (notification.getType().equalsIgnoreCase("newCaptain")) {
////                Intent intent = new Intent(getContext(), MainActivity.class); // GroupFormationActitvity.java // checkx
////                intent.putExtra("game_id", notification.getGameId());
////                startActivity(intent);
//            }
//            else if (notification.getType().equalsIgnoreCase("lineupGameAdded")) {
//                Intent intent = new Intent(getContext(), SplashActivity.class); // any issue convert splash to main and comment //GroupFormationActitvity.java // checkx
//                //intent.putExtra("game_id", notification.getGameId());
//                startActivity(intent);
//                finish();
//            }
             if (notification.getType().equalsIgnoreCase("ticketClosed")) {
                Intent intent = new Intent(getContext(), ClosedTicketDetailActivity.class);
                intent.putExtra("ticket_id", notification.getTicket().getId());
                startActivity(intent);

            }

            if (notificationList.get(pos).getType().equalsIgnoreCase("ticketClosedRateNow")) {
               // if (notification.getTicket().getReviews() == 0){
                    showRatingDialog(notification.getTicket().getParking().getId(),
                            notification.getTicket().getParking().getPhoto(),
                            notification.getTicket().getParking().getName(),
                            notification.getTicket().getParking().getLocation());
                //}

            }
        }
    };

//    protected void showRatingDialog() {
//        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
//        Fragment fragment = getSupportFragmentManager().findFragmentByTag("RatingDialogFragment");
//        if (fragment != null) {
//            fragmentTransaction.remove(fragment);
//        }
//        fragmentTransaction.addToBackStack(null);
//        RatingDialogFragment dialogFragment = new RatingDialogFragment(String.valueOf(myTicket.getParking().getId()),
//                myTicket.getParking().getPhoto(), myTicket.getParking().getName(),
//                myTicket.getParking().getLocation());
//
//        dialogFragment.setDialogCallback((df) -> {
//            df.dismiss();
//        });
//        dialogFragment.show(fragmentTransaction, "RatingDialogFragment");
//
//    }
    private void clearClicked() {
        if (notificationList.isEmpty()) {
            return;
        }
        ActionSheet.createBuilder(getContext(), getSupportFragmentManager())
                .setCancelButtonTitle("Cancel")
                .setOtherButtonTitles("Read all notifications", "Delete all notifications")
                .setCancelableOnTouchOutside(true)
                .setListener(new ActionSheet.ActionSheetListener() {
                    @Override
                    public void onDismiss(ActionSheet actionSheet, boolean isCancel) {
                    }

                    @Override
                    public void onOtherButtonClick(ActionSheet actionSheet, int index) {
                        if (index == 0) {
                            readAllNotificationAPI(true);
                        }
                        else if (index == 1) {
                            deleteAllNotificationAPI(true);
                        }
                    }
                }).show();
    }
    private void getNotifications(boolean isLoader) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext()): null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.getUserNotification();
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            JSONArray arr = object.getJSONArray(Constants.kData);
                            notificationList.clear();
                            Gson gson = new Gson();
                            for (int i = 0; i < arr.length(); i++) {
                                notificationList.add(gson.fromJson(arr.get(i).toString(), NotificationList.class));
                            }
                            adapter.notifyDataSetChanged();
                        }
                        else {
                            notificationList.clear();
                            adapter.notifyDataSetChanged();
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
    private void deleteAllNotificationAPI(boolean isLoader) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext()): null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.deleteAllNotification("");
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            Functions.showToast(getContext(), object.getString(Constants.kMsg), FancyToast.SUCCESS);
                            AppManager.getInstance().notificationCount = 0;
                            finish();
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
    private void readAllNotificationAPI(boolean isLoader) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext()): null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.readAllNotification("");
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            Functions.showToast(getContext(), object.getString(Constants.kMsg), FancyToast.SUCCESS);
                            AppManager.getInstance().notificationCount = 0;
                            finish();
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
    private void readNotificationAPI(String notId) {
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.readNotification(notId);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            if (AppManager.getInstance().notificationCount > 0) {
                                AppManager.getInstance().notificationCount -= 1;
                            }
//                            adapter.notifyDataSetChanged();
                        }
                        else {
                            Functions.showToast(getContext(), object.getString(Constants.kMsg), FancyToast.ERROR);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Functions.showToast(getContext(), e.getLocalizedMessage(), FancyToast.ERROR);
                    }
                } else {
                    Functions.showToast(getContext(), getString(R.string.error_occured), FancyToast.ERROR);
                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                if (t instanceof UnknownHostException) {
                    Functions.showToast(getContext(), getString(R.string.check_internet_connection), FancyToast.ERROR);
                }
                else {
                    Functions.showToast(getContext(), t.getLocalizedMessage(), FancyToast.ERROR);
                }
            }
        });
    }
}