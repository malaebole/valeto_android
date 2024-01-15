package ae.valeto.dialogs;

import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ae.valeto.R;
import ae.valeto.databinding.FragmentDurationDialogBinding;
import ae.valeto.databinding.FragmentEditCarDialogBinding;

public class EditCarDialogFragment extends DialogFragment implements View.OnClickListener{

    private FragmentEditCarDialogBinding binding;
    private ResultDialogCallback dialogCallback;
    private String photoUrl = "";


    public EditCarDialogFragment() {
        // Required empty public constructor
    }

    public EditCarDialogFragment(String photoUrl) {
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
        binding = FragmentEditCarDialogBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        getDialog().getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        getDialog().getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        //binding.btnClose.setOnClickListener(this);
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
//        if (v == binding.btnClose){
//            this.dismiss();
//        }

    }


    public interface ResultDialogCallback {
        void didSubmitResult(DialogFragment df);
    }


}