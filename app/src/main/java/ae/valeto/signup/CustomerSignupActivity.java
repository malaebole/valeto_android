package ae.valeto.signup;

import android.os.Bundle;
import android.view.View;
import ae.valeto.base.BaseActivity;
import ae.valeto.databinding.ActivityCustomerSignupBinding;


public class CustomerSignupActivity extends BaseActivity implements View.OnClickListener {

    private ActivityCustomerSignupBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCustomerSignupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        makeStatusbarTransperant();


    }

    @Override
    public void onClick(View v) {

    }
}
