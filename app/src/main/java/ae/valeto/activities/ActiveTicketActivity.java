package ae.valeto.activities;

import android.os.Bundle;
import android.view.View;
import ae.valeto.base.BaseActivity;
import ae.valeto.databinding.ActivityActiveTicketBinding;

public class ActiveTicketActivity extends BaseActivity implements View.OnClickListener {

    private ActivityActiveTicketBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityActiveTicketBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        makeStatusbarTransperant();
    }

    @Override
    public void onClick(View v) {

    }
}