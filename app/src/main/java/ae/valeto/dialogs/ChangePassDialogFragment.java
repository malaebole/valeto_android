package ae.valeto.dialogs;

import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kaopiz.kprogresshud.KProgressHUD;
import com.shashank.sony.fancytoastlib.FancyToast;

import org.json.JSONObject;

import ae.valeto.R;
import ae.valeto.databinding.FragmentChangePassDialogBinding;
import ae.valeto.databinding.FragmentEditCarDialogBinding;
import ae.valeto.util.AppManager;
import ae.valeto.util.Constants;
import ae.valeto.util.Functions;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ChangePassDialogFragment extends DialogFragment implements View.OnClickListener{

    private FragmentChangePassDialogBinding binding;
    private ResultDialogCallback dialogCallback;
    private String photoUrl = "";


    public ChangePassDialogFragment() {
        // Required empty public constructor
    }

    public void setDialogCallback(ResultDialogCallback dialogCallback) {
        this.dialogCallback = dialogCallback;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialogTransparentStyle);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentChangePassDialogBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        getDialog().getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        getDialog().getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        binding.btnConfirm.setOnClickListener(this);
        //Glide.with(getActivity()).load(photoUrl).into(binding.shirtImgVuAd);

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }



    @Override
    public void onClick(View v) {
        if (v == binding.btnConfirm){
            if (binding.etOldPass.getText().toString().isEmpty()){
                Functions.showToast(getActivity(), "Old password cannot be empty", FancyToast.ERROR);
                return;
            }
            if (binding.etNewPass.getText().toString().isEmpty()){
                Functions.showToast(getActivity(), "New password cannot be empty", FancyToast.ERROR);
                return;
            }

            changePassword(true,binding.etOldPass.getText().toString(), binding.etNewPass.getText().toString());

        }

    }

    private void changePassword(boolean isLoader, String oldPass, String newPass) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing"): null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.changePassword(oldPass,newPass);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {

                            dialogCallback.didSubmitResult(ChangePassDialogFragment.this);

                        }
                    } catch (Exception e) {
                        e.printStackTrace();

                    }
                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Functions.hideLoader(hud);
            }
        });
    }


    public interface ResultDialogCallback {
        void didSubmitResult(DialogFragment df);
    }


}