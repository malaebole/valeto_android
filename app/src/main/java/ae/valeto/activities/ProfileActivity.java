package ae.valeto.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import ae.valeto.R;
import ae.valeto.base.BaseActivity;
import ae.valeto.databinding.ActivityProfileBinding;

public class ProfileActivity extends BaseActivity implements View.OnClickListener {

    private ActivityProfileBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        makeStatusbarTransperant();


        binding.btnBack.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        if (v == binding.btnBack){
           finish();
        }
    }

}