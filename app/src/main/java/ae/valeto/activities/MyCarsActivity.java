package ae.valeto.activities;

import androidx.recyclerview.widget.LinearLayoutManager;
import android.os.Bundle;
import android.view.View;
import java.util.ArrayList;
import java.util.List;
import ae.valeto.adapters.CarsAdapter;
import ae.valeto.base.BaseActivity;
import ae.valeto.databinding.ActivityMyCarsBinding;
import ae.valeto.models.Cars;

public class MyCarsActivity extends BaseActivity implements View.OnClickListener {

    private ActivityMyCarsBinding binding;
    private CarsAdapter carsAdapter;
    private final List<Cars> carsList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMyCarsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        makeStatusbarTransperant();


        LinearLayoutManager carsListLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        binding.carsRecyclerVu.setLayoutManager(carsListLayoutManager);
        carsAdapter = new CarsAdapter(getContext(), carsList);
        carsAdapter.setItemClickListener(itemClickListener);
        binding.carsRecyclerVu.setAdapter(carsAdapter);

        binding.btnBack.setOnClickListener(this);
    }


    CarsAdapter.ItemClickListener itemClickListener = new CarsAdapter.ItemClickListener() {
        @Override
        public void itemClicked(View view, int pos) {

        }
    };


    @Override
    public void onClick(View v) {
        if (v == binding.btnBack){
            finish();
        }
    }
}