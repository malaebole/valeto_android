package ae.valeto.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import ae.valeto.R;
import ae.valeto.adapters.FacilitiesAdapter;
import ae.valeto.adapters.ParkingAdapter;
import ae.valeto.base.BaseActivity;
import ae.valeto.databinding.ActivityForgetPasswordBinding;
import ae.valeto.databinding.ActivityParkingDetailsBinding;
import ae.valeto.models.Facilities;
import ae.valeto.models.Parking;

public class ParkingDetailsActivity extends BaseActivity implements View.OnClickListener {

    private ActivityParkingDetailsBinding binding;
    private FacilitiesAdapter facilitiesAdapter;
    private final List<Facilities> facilitiesList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityParkingDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        makeStatusbarTransperant();

        LinearLayoutManager facilitiesListLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        binding.facilitiesRecyclerVu.setLayoutManager(facilitiesListLayoutManager);
        facilitiesAdapter = new FacilitiesAdapter(getContext(), facilitiesList);
        facilitiesAdapter.setItemClickListener(itemClickListener);
        binding.facilitiesRecyclerVu.setAdapter(facilitiesAdapter);

    }

    FacilitiesAdapter.ItemClickListener itemClickListener = new FacilitiesAdapter.ItemClickListener() {
        @Override
        public void itemClicked(View view, int pos) {

        }
    };

    @Override
    public void onClick(View v) {

    }
}