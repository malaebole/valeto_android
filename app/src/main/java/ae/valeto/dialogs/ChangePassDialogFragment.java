package ae.valeto.dialogs;

import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.text.method.TransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kaopiz.kprogresshud.KProgressHUD;
import com.shashank.sony.fancytoastlib.FancyToast;

import org.json.JSONObject;

import java.net.UnknownHostException;

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
    private final String photoUrl = "";


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
        binding.btnClose.setOnClickListener(this);
        binding.passwordToggle.setOnClickListener(this);
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
        else if (v == binding.btnClose) {
            dismiss();
        }
        else if (v == binding.passwordToggle) {
            TransformationMethod currentMethod = binding.etOldPass.getTransformationMethod();
            TransformationMethod currentNewMethod = binding.etNewPass.getTransformationMethod();
            if (currentMethod instanceof PasswordTransformationMethod) {
                binding.etOldPass.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                binding.passwordToggle.setImageResource(R.drawable.show);
            } else {
                binding.etOldPass.setTransformationMethod(PasswordTransformationMethod.getInstance());
                binding.passwordToggle.setImageResource(R.drawable.hide);
            }
            binding.etOldPass.setSelection(binding.etOldPass.getText().length());

            if (currentNewMethod instanceof PasswordTransformationMethod) {
                binding.etNewPass.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                binding.passwordToggle.setImageResource(R.drawable.show);
            } else {
                binding.etNewPass.setTransformationMethod(PasswordTransformationMethod.getInstance());
                binding.passwordToggle.setImageResource(R.drawable.hide);
            }
            binding.etNewPass.setSelection(binding.etNewPass.getText().length());


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
                            Functions.showToast(getContext(),"Password has been changed successfully!", FancyToast.SUCCESS);
                            dialogCallback.didSubmitResult(ChangePassDialogFragment.this);
                        }else {
                            Functions.showToast(getContext(), object.getString(Constants.kMsg), FancyToast.SUCCESS);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Functions.showToast(getContext(), e.getLocalizedMessage(), FancyToast.ERROR);

                    }
                }else {
                    Functions.showToast(getContext(), getString(R.string.error_occured), FancyToast.ERROR);
                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Functions.hideLoader(hud);
                if (t instanceof UnknownHostException) {
                    Functions.showToast(getContext(), getString(R.string.check_internet_connection), FancyToast.ERROR);
                } else {
                    Functions.showToast(getContext(), t.getLocalizedMessage(), FancyToast.ERROR);
                }
            }
        });
    }


    public interface ResultDialogCallback {
        void didSubmitResult(DialogFragment df);
    }


}