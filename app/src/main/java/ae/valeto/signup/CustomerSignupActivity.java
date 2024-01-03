package ae.valeto.signup;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

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

//        if (!list.get(position).getPhotoUrl().isEmpty()){
//            Glide.with(context).load(list.get(position).getPhotoUrl()).apply(RequestOptions.circleCropTransform()).into(holder.bankImg);
//        }else{
//            Glide.with(context).load(R.drawable.finance_temp).apply(RequestOptions.circleCropTransform()).into(holder.bankImg);
//        }


        binding.btnBack.setOnClickListener(this);
        binding.btnSignup.setOnClickListener(this);
        binding.customerImgVu.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        if (v == binding.btnBack){
            backClicked();
        }
        else if (v == binding.btnSignup) {

        }
        else if (v == binding.customerImgVu) {

        }

    }

    private void backClicked() {
        hideKeyboard();
        finish();
    }


}
