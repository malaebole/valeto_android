package ae.valeto.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import ae.valeto.R;
import ae.valeto.base.BaseActivity;
import ae.valeto.databinding.ActivityClosedTicketDetailBinding;

public class ClosedTicketDetailActivity extends BaseActivity implements View.OnClickListener {

    private ActivityClosedTicketDetailBinding binding;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityClosedTicketDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        makeStatusbarTransperant();

        binding.btnBack.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        if (v == binding.btnBack){
            finish();
        }
        else if () {

        }

    }
}