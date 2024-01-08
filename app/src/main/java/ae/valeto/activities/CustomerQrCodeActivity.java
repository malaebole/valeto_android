package ae.valeto.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.gson.Gson;

import ae.valeto.R;
import ae.valeto.base.BaseActivity;
import ae.valeto.databinding.ActivityCustomerQrCodeBinding;
import ae.valeto.databinding.ActivityForgetPasswordBinding;
import ae.valeto.dialogs.ScanSuccessPopupFragment;

public class CustomerQrCodeActivity extends BaseActivity implements View.OnClickListener {

    private ActivityCustomerQrCodeBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCustomerQrCodeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        makeStatusbarTransperant();



        binding.btnBack.setOnClickListener(this);
        binding.btnGoBack.setOnClickListener(this);


    }


    protected void showSuccessPopUp(String popupId, String msg) {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            Fragment fragment = getSupportFragmentManager().findFragmentByTag("UnlockedJerseyPopupFragment");
            if (fragment != null) {
                fragmentTransaction.remove(fragment);
            }
            fragmentTransaction.addToBackStack(null);
            ScanSuccessPopupFragment dialogFragment = new ScanSuccessPopupFragment("");
            dialogFragment.setDialogCallback((df) -> {
                df.dismiss();
            });
            dialogFragment.show(fragmentTransaction, "UnlockedJerseyPopupFragment");
            //playSoundFromAssets("congratulations_tone.mp3");

    }

    @Override
    public void onClick(View v) {
        if (v == binding.btnBack || v == binding.btnGoBack){
            finish();
        }

    }
}