package ae.valeto.activities;

import androidx.fragment.app.Fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;

import com.bumptech.glide.Glide;
import com.jpeng.jptabbar.OnTabSelectListener;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.shashank.sony.fancytoastlib.FancyToast;
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import ae.valeto.R;
import ae.valeto.base.BaseActivity;
import ae.valeto.databinding.ActivityCustomerMainTabsBinding;
import ae.valeto.fragments.MenuFragment;
import ae.valeto.fragments.ParkingListFragment;
import ae.valeto.util.AppManager;
import ae.valeto.util.Constants;
import ae.valeto.util.Functions;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import org.json.JSONObject;

public class CustomerMainTabsActivity extends BaseActivity {

    private ActivityCustomerMainTabsBinding binding;
    private final List<Fragment> list = new ArrayList<>();
    private final ParkingListFragment parkingListFragment = new ParkingListFragment();
    private final MenuFragment menuFragment = new MenuFragment();
    private int selectedTabIndex = 0;

    // private final OleBookingListFragment oleBookingListFragment = new OleBookingListFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCustomerMainTabsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        makeStatusbarTransperant();


        setupTabLayout();
        binding.contentMainLay.contentMain.tabBar.setSelectTab(0);
        binding.contentMainLay.contentMain.viewPager.setCurrentItem(0);

        KeyboardVisibilityEvent.setEventListener(getContext(), new KeyboardVisibilityEventListener() {
            @Override
            public void onVisibilityChanged(boolean isOpen) {
                if (isOpen) {
                    binding.contentMainLay.contentMain.tabBar.setVisibility(View.GONE);
                    binding.contentMainLay.contentMain.tabBar.getMiddleView().setVisibility(View.GONE);
                }
                else {
                    binding.contentMainLay.contentMain.tabBar.setVisibility(View.VISIBLE);
                    binding.contentMainLay.contentMain.tabBar.getMiddleView().setVisibility(View.VISIBLE);
                }
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Functions.getPrefValue(getContext(), Constants.kIsSignIn).equalsIgnoreCase("1")){
            callUnreadNotifAPI();
        }

//        //callUnreadNotifAPI();
//        populateSideMenuData();
        try {
            LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, new IntentFilter("receive_push"));
        }
        catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
        devicesLoginLimit();

    }
    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            AppManager.getInstance().notificationCount += 1;
            setBadgeValue();
        }
    };

    @Override
    protected void onPause() {
        super.onPause();
        if (broadcastReceiver != null) {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
        }
    }


    public void notificationsClicked() {
        if (!Functions.getPrefValue(getContext(), Constants.kIsSignIn).equalsIgnoreCase("1")) {
            Functions.showToast(getContext(), "Please Login First", FancyToast.ERROR, FancyToast.LENGTH_SHORT);
        }
        startActivity(new Intent(getContext(), NotificationsActivity.class));
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void callUnreadNotifAPI() {
        getUnreadNotificationAPI(new UnreadCountCallback() {
            @Override
            public void unreadNotificationCount(int count) {
                AppManager.getInstance().notificationCount = count;
                setBadgeValue();
            }
        });
    }

    private void setBadgeValue() {
        if (parkingListFragment.isVisible()) {
            parkingListFragment.setBadgeValue();
        }

    }

    private void setupTabLayout() {
        binding.contentMainLay.contentMain.tabBar.setTitles("Parking", "Menu")
                .setNormalIcons(R.drawable.parking_inactive, R.drawable.menu_inactive).generate();
        binding.contentMainLay.contentMain.tabBar.setNormalColor(getResources().getColor(R.color.separatorColor));
        binding.contentMainLay.contentMain.tabBar.setSelectedColor(getResources().getColor(R.color.appColor));
        binding.contentMainLay.contentMain.tabBar.setIconSize(17);
        binding.contentMainLay.contentMain.tabBar.setTabTextSize(12);
        list.add(parkingListFragment);
        list.add(menuFragment);
        binding.contentMainLay.contentMain.tabBar.setGradientEnable(false);
        binding.contentMainLay.contentMain.tabBar.setPageAnimateEnable(true);

        binding.contentMainLay.contentMain.viewPager.setAdapter(new PagerAdapter(getSupportFragmentManager(), list));
        binding.contentMainLay.contentMain.tabBar.setContainer(binding.contentMainLay.contentMain.viewPager);

        binding.contentMainLay.contentMain.tabBar.setTabListener(new OnTabSelectListener() {
            @Override
            public void onTabSelect(int index) {
                selectedTabIndex = index;
                devicesLoginLimit();
            }

            @Override
            public boolean onInterruptSelect(int index) {
                return false;
            }
        });
        if(binding.contentMainLay.contentMain.tabBar.getMiddleView() != null)
            binding.contentMainLay.contentMain.tabBar.getMiddleView().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    generateQrCode(true);
                }
            });
    }

    private void generateQrCode(boolean isLoader) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing"): null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.generateQrCode();
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            Functions.showToast(getContext(), object.getString(Constants.kMsg), FancyToast.SUCCESS);
                            JSONObject dataObject = object.getJSONObject(Constants.kData);
                            String base64String = dataObject.getString("qr_token");
                            Intent intent = new Intent(getContext(), CustomerQrCodeActivity.class);
                            intent.putExtra("base64", base64String);
                            startActivity(intent);

                        }else {
                            Functions.showToast(getContext(), object.getString(Constants.kMsg), FancyToast.SUCCESS);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Functions.showToast(getContext(), e.getLocalizedMessage(), FancyToast.ERROR);

                    }
                }else {
                    Functions.showToast(getContext(), getString(R.string.error_occured), FancyToast.ERROR);
                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Functions.hideLoader(hud);
                if (t instanceof UnknownHostException) {
                    Functions.showToast(getContext(), getString(R.string.check_internet_connection), FancyToast.ERROR);
                } else {
                    Functions.showToast(getContext(), t.getLocalizedMessage(), FancyToast.ERROR);
                }
            }
        });
    }

    public class PagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> list;

        public PagerAdapter(FragmentManager supportFragmentManager, List<Fragment> list) {
            super(supportFragmentManager, FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
            this.list = list;
        }

        @Override
        public Fragment getItem(int position) {
            return list.get(position);
        }


        @Override
        public int getCount() {
            return list.size();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        if (oleOwnerProfileFragment.isVisible()) {
//            oleOwnerProfileFragment.onActivityResult(requestCode, resultCode, data);
//        }
    }

    public void devicesLoginLimit() {
//        String userId = Functions.getPrefValue(getContext(), Constants.kUserID);
//        String uniqueID = Functions.getPrefValue(this, Constants.kDeviceUniqueId);
//        //yeh server py unique id bhej rhi haii
//        Log.d("uniqueIDDDDDDD", uniqueID);
//        Call<ResponseBody> call = AppManager.getInstance().apiInterface.devicesLoginLimit(userId, uniqueID, "ole");
//        call.enqueue(new Callback<ResponseBody>() {
//            @Override
//            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//                if (response.body() != null) {
//                    try {
//                        JSONObject object = new JSONObject(response.body().string());
//                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
//
//                        }
//                        else{
//                            Functions.showToast(getContext(), getString(R.string.you_have_been_log), FancyToast.ERROR, FancyToast.LENGTH_LONG);
//                            Intent i = new Intent(getContext(), SplashActivity.class);
//                            startActivity(i);
//                            finish();
//                        }
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//
//            @Override
//            public void onFailure(Call<ResponseBody> call, Throwable t) {
//
//            }
//        });
    }
}