package ae.valeto.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import com.bumptech.glide.Glide;
import com.google.android.material.chip.Chip;
import com.google.gson.Gson;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.shashank.sony.fancytoastlib.FancyToast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import ae.valeto.R;
import ae.valeto.adapters.FacilitiesAdapter;
import ae.valeto.adapters.ParkingAdapter;
import ae.valeto.base.BaseActivity;
import ae.valeto.databinding.ActivityForgetPasswordBinding;
import ae.valeto.databinding.ActivityParkingDetailsBinding;
import ae.valeto.models.Facilities;
import ae.valeto.models.MyTicket;
import ae.valeto.models.Parking;
import ae.valeto.util.AppManager;
import ae.valeto.util.Constants;
import ae.valeto.util.Functions;
import co.lujun.androidtagview.TagContainerLayout;

import okhttp3.ResponseBody;
import okqapps.com.tagslayout.TagItem;
import okqapps.com.tagslayout.TagTextSize;
import okqapps.com.tagslayout.TagsLayout;
import okqapps.com.tagslayout.UnSelectedTagTheme;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ParkingDetailsActivity extends BaseActivity implements View.OnClickListener {

    private ActivityParkingDetailsBinding binding;
    private FacilitiesAdapter facilitiesAdapter;
    private Parking parking;
    private final List<Facilities> facilitiesList = new ArrayList<>();
    private int parkingId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityParkingDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        makeStatusbarTransperant();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            parkingId = bundle.getInt("parking_id");
        }

        getSingleUserParking(true);

//        LinearLayoutManager facilitiesListLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
//        binding.facilitiesRecyclerVu.setLayoutManager(facilitiesListLayoutManager);
//        facilitiesAdapter = new FacilitiesAdapter(getContext(), facilitiesList);
//        facilitiesAdapter.setItemClickListener(itemClickListener);
//        binding.facilitiesRecyclerVu.setAdapter(facilitiesAdapter);

        binding.btnBack.setOnClickListener(this);
        binding.callBtn.setOnClickListener(this);
        binding.directionBtn.setOnClickListener(this);

    }

    FacilitiesAdapter.ItemClickListener itemClickListener = new FacilitiesAdapter.ItemClickListener() {
        @Override
        public void itemClicked(View view, int pos) {

        }
    };



    private void getSingleUserParking(boolean isLoader) {
        Call<ResponseBody> call;
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing"): null;
        call = AppManager.getInstance().apiInterface.getSingleUserParking(parkingId);
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
                            parking = gson.fromJson(obj.toString(), Parking.class);
                            populateParkingData();

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

    private void populateParkingData(){
        binding.tvRate.setText(String.valueOf(parking.getRating()));
        if (!parking.getPhoto().isEmpty()){
            Glide.with(getApplicationContext()).load(parking.getPhoto()).into(binding.imgVu);
        }
        binding.tvParkingName.setText(parking.getName());
        binding.tvPrice.setText(parking.getCurrency()+ parking.getPrice() + "/hr");
        binding.tvLoc.setText(parking.getDistance() +" "+ parking.getLocation());

        List<TagItem> tagItems = new ArrayList<>();
        for (int i = 0; i < parking.getFacilites().size(); i++) {
            String facilityName = parking.getFacilites().get(i).getName();
            TagItem tagItem = new TagItem(i, facilityName, "#E2E6EA", "#7A7E83", false); // Blue text, gray background
            tagItems.add(tagItem);
        }
        binding.tagsLayout.setTagTextSize(TagTextSize.SMALL);
        binding.tagsLayout.setUnSelectedTagTheme(UnSelectedTagTheme.GRAY);
        binding.tagsLayout.setBackgroundColor(Color.TRANSPARENT);
        binding.tagsLayout.initializeTags(this, tagItems);





    }


    @Override
    public void onClick(View v) {
        if (v == binding.btnBack){
            finish();
        }
        else if (v == binding.callBtn) {
            callBtnClicked();
        }
        else if (v == binding.directionBtn) {
            directionBtnClicked();
        }

    }

    private void directionBtnClicked() {

    }

    private void callBtnClicked() {
        Intent callIntent = new Intent(Intent.ACTION_VIEW);
        callIntent.setData(Uri.parse("tel:" + parking.getPhoneNumber()));
        startActivity(callIntent);

    }
}