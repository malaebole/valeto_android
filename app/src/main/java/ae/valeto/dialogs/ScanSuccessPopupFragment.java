package ae.valeto.dialogs;

import android.os.Build;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;

import ae.valeto.R;
import ae.valeto.databinding.FragmentScanSuccessPopupBinding;


public class ScanSuccessPopupFragment extends DialogFragment implements View.OnClickListener{

    private FragmentScanSuccessPopupBinding binding;
    private ResultDialogCallback dialogCallback;
    private String photoUrl = "";


    public ScanSuccessPopupFragment() {
        // Required empty public constructor
    }

    public ScanSuccessPopupFragment(String photoUrl) {
        this.photoUrl = photoUrl;
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
        binding = FragmentScanSuccessPopupBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        getDialog().getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        getDialog().getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        binding.btnOk.setOnClickListener(this);

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }



    @Override
    public void onClick(View v) {
        if (v == binding.btnOk){
            dialogCallback.didSubmitResult(this);
            this.dismiss();
        }

    }


    public interface ResultDialogCallback {
        void didSubmitResult(DialogFragment df);
    }


}