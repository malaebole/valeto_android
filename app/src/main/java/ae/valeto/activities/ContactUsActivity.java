package ae.valeto.activities;

import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.kaopiz.kprogresshud.KProgressHUD;
import com.shashank.sony.fancytoastlib.FancyToast;

import org.json.JSONObject;

import java.net.UnknownHostException;

import ae.valeto.R;
import ae.valeto.base.BaseActivity;
import ae.valeto.databinding.ActivityContactUsBinding;
import ae.valeto.databinding.ActivityCustomerSignupBinding;
import ae.valeto.models.UserInfo;
import ae.valeto.util.AppManager;
import ae.valeto.util.Constants;
import ae.valeto.util.Functions;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ContactUsActivity extends BaseActivity implements View.OnClickListener {

    private ActivityContactUsBinding binding;
    private  UserInfo userInfo = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityContactUsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        makeStatusbarTransperant();
        setData();

        binding.btnSubmit.setOnClickListener(this);
        binding.btnBack.setOnClickListener(this);
    }

    private void setData() {
        userInfo = Functions.getUserinfo(getContext());
        binding.tvName.setText(userInfo.getName());
        binding.tvEmail.setText(userInfo.getEmail());
        binding.tvPhone.setText(userInfo.getPhone());

    }

    @Override
    public void onClick(View v) {
        if (v == binding.btnBack){
            finish();
        }
        else if (v == binding.btnSubmit) {

            if (binding.etMsg.getText().toString().isEmpty()){
                Functions.showToast(getContext(), "Oops, Message cannot be empty!", FancyToast.SUCCESS);
                return;
            }
            submitRequest(true, userInfo.getName(), userInfo.getEmail(),userInfo.getPhone(), binding.etMsg.getText().toString());
        }

    }

    private void submitRequest(boolean isLoader,String name, String email, String phone, String msg) {
        Call<ResponseBody> call;
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing"): null;
        call = AppManager.getInstance().apiInterface.submitRequest(name, email, phone, msg);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            Functions.showToast(getContext(), "Report Submitted Successfully!", FancyToast.SUCCESS);
                            finish();

                        }else {
                            Functions.showToast(getContext(), object.getString(Constants.kMsg), FancyToast.SUCCESS);
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
}