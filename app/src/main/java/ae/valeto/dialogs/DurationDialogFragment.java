package ae.valeto.dialogs;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ae.valeto.R;
import ae.valeto.databinding.FragmentDurationDialogBinding;
import ae.valeto.databinding.FragmentScanSuccessPopupBinding;


public class DurationDialogFragment extends DialogFragment implements View.OnClickListener{

    private FragmentDurationDialogBinding binding;
    private ResultDialogCallback dialogCallback;
    private String time = "";


    public DurationDialogFragment() {
        // Required empty public constructor
    }

//    public DurationDialogFragment(String photoUrl) {
//        this.photoUrl = photoUrl;
//    }

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
        binding = FragmentDurationDialogBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        getDialog().getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        getDialog().getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        binding.layImd.setOnClickListener(this);
        binding.lay10.setOnClickListener(this);
        binding.lay15.setOnClickListener(this);
        binding.btnConfirm.setOnClickListener(this);
        binding.btnClose.setOnClickListener(this);

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }



    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onClick(View v) {
        if (v == binding.layImd){

            binding.layImd.setBackground(getContext().getResources().getDrawable(R.drawable.rounded_corner_bg_green_selected));
            binding.lay10.setBackground(getContext().getResources().getDrawable(R.drawable.rounded_corner_bg_white));
            binding.lay15.setBackground(getContext().getResources().getDrawable(R.drawable.rounded_corner_bg_white));
            binding.etImd.setTextColor(getContext().getResources().getColor(R.color.appColor));
            binding.et10.setTextColor(getContext().getResources().getColor(R.color.black30Color));
            binding.et15.setTextColor(getContext().getResources().getColor(R.color.black30Color));
            time = binding.etImd.getText().toString();

        }
        else if (v == binding.lay10) {
            binding.layImd.setBackground(getContext().getResources().getDrawable(R.drawable.rounded_corner_bg_white));
            binding.lay10.setBackground(getContext().getResources().getDrawable(R.drawable.rounded_corner_bg_green_selected));
            binding.lay15.setBackground(getContext().getResources().getDrawable(R.drawable.rounded_corner_bg_white));
            binding.etImd.setTextColor(getContext().getResources().getColor(R.color.black30Color));
            binding.et10.setTextColor(getContext().getResources().getColor(R.color.appColor));
            binding.et15.setTextColor(getContext().getResources().getColor(R.color.black30Color));
            time = binding.et10.getText().toString();


        }
        else if (v == binding.lay15) {
            binding.layImd.setBackground(getContext().getResources().getDrawable(R.drawable.rounded_corner_bg_white));
            binding.lay10.setBackground(getContext().getResources().getDrawable(R.drawable.rounded_corner_bg_white));
            binding.lay15.setBackground(getContext().getResources().getDrawable(R.drawable.rounded_corner_bg_green_selected));
            binding.etImd.setTextColor(getContext().getResources().getColor(R.color.black30Color));
            binding.et10.setTextColor(getContext().getResources().getColor(R.color.black30Color));
            binding.et15.setTextColor(getContext().getResources().getColor(R.color.appColor));
            time = binding.et15.getText().toString();


        }
        else if (v == binding.btnConfirm) {
            dialogCallback.didSubmitResult(this, time);
        }
        else if (v == binding.btnClose) {
          dismiss();
        }

    }


    public interface ResultDialogCallback {
        void didSubmitResult(DialogFragment df, String time);
    }


}