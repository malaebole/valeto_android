package ae.valeto.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.kaopiz.kprogresshud.KProgressHUD;

import org.json.JSONException;
import org.json.JSONObject;

import ae.valeto.R;
import ae.valeto.base.BaseActivity;
import ae.valeto.databinding.ActivityCustomerQrCodeBinding;
import ae.valeto.databinding.ActivityForgetPasswordBinding;
import ae.valeto.dialogs.ScanSuccessPopupFragment;
import ae.valeto.models.UserInfo;
import ae.valeto.util.AppManager;
import ae.valeto.util.Constants;
import ae.valeto.util.Functions;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CustomerQrCodeActivity extends BaseActivity implements View.OnClickListener {

    private ActivityCustomerQrCodeBinding binding;
    private UserInfo userInfo;
    private String base64String;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCustomerQrCodeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        makeStatusbarTransperant();

        userInfo = Functions.getUserinfo(getContext());
        Bundle bundle = getIntent().getExtras();

        if (bundle != null) {
            base64String = bundle.getString("base64", "");
        }

        populateQrData();

        binding.btnBack.setOnClickListener(this);
        binding.btnGoBack.setOnClickListener(this);

    }

    private void populateQrData() {

            String cleanImage = base64String.replace("data:image/png;base64,", "").replace("data:image/jpeg;base64,", "");
            byte[] decodedString = Base64.decode(cleanImage, Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            int desiredWidth = 800;
            int desiredHeight = 800;
            Bitmap scaledBitmap = Bitmap.createScaledBitmap(decodedByte, desiredWidth, desiredHeight, true);
            Bitmap mutableBitmap = scaledBitmap.copy(Bitmap.Config.ARGB_8888, true);
            for (int y = 0; y < mutableBitmap.getHeight(); y++) {
                for (int x = 0; x < mutableBitmap.getWidth(); x++) {
                    int pixel = mutableBitmap.getPixel(x, y);
                    if (pixel == Color.BLACK) {
                        mutableBitmap.setPixel(x, y, getContext().getResources().getColor(R.color.appColor)); // Change black pixels to red
                    }
                }
            }
            Glide.with(getApplicationContext()).load(mutableBitmap).into(binding.qrImg);
            Glide.with(getApplicationContext()).load(userInfo.getPicture()).apply(RequestOptions.circleCropTransform()).into(binding.imgVu);
            binding.tvName.setText(userInfo.getName());
    }


    protected void showSuccessPopUp() {
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

    }

    @Override
    public void onClick(View v) {
        if (v == binding.btnBack || v == binding.btnGoBack){
            finish();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter("receive_push"));
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
    }

    private final BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String type = intent.getStringExtra("type");
            if (type.equalsIgnoreCase("barcodeScanned")) {
                showSuccessPopUp();
            }

        }
    };
}