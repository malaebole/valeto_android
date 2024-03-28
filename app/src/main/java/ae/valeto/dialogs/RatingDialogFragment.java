package ae.valeto.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.bumptech.glide.Glide;
import com.hedgehog.ratingbar.RatingBar;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.shashank.sony.fancytoastlib.FancyToast;

import org.json.JSONObject;

import java.net.UnknownHostException;

import ae.valeto.R;
import ae.valeto.databinding.FragmentRatingDialogBinding;
import ae.valeto.databinding.FragmentScanSuccessPopupBinding;
import ae.valeto.util.AppManager;
import ae.valeto.util.Constants;
import ae.valeto.util.Functions;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class RatingDialogFragment extends DialogFragment implements View.OnClickListener{

    private FragmentRatingDialogBinding binding;
    private ResultDialogCallback dialogCallback;
    private String photo= "", name= "", location= "";
    private int parkingId;
    private float rating = 5;


    public RatingDialogFragment() {
        // Required empty public constructor
    }

    public RatingDialogFragment(int parkingId, String photo, String name, String location) {
        this.parkingId = parkingId;
        this.photo = photo;
        this.name = name;
        this.location = location;
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
        //Inflate the layout for this fragment

        binding = FragmentRatingDialogBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        getDialog().getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        getDialog().getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);


        binding.tvName.setText(name);
        binding.tvLoc.setText(location);
        if (!photo.isEmpty()){
            Glide.with(getActivity()).load(photo).into(binding.imgVu);
        }
        binding.ratingBar.setStar(rating);
        binding.ratingBar.setOnRatingChangeListener(new RatingBar.OnRatingChangeListener() {
            @Override
            public void onRatingChange(float RatingCount) {
                rating = RatingCount;
            }
        });
        binding.btnSubmit.setOnClickListener(this);

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }



    @Override
    public void onClick(View v) {
        if (v == binding.btnSubmit){
            if (rating > 0) {
                addParkingReview(true);
            }
            else {
                Functions.showToast(getActivity(), "Rating cannot be empty", FancyToast.ERROR);
            }

//            dialogCallback.didSubmitResult(this);
//            this.dismiss();
        }

    }


    private void addParkingReview(boolean isLoader) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext()): null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.addParkingReview(parkingId, String.valueOf(rating), binding.etComment.getText().toString());
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            Functions.showToast(getContext(), object.getString(Constants.kMsg), FancyToast.SUCCESS);
                            dialogCallback.didSubmitResult(RatingDialogFragment.this);
                            dismiss();
                        }
                        else {
                            Functions.showToast(getContext(), object.getString(Constants.kMsg), FancyToast.ERROR);
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


    public interface ResultDialogCallback {
        void didSubmitResult(DialogFragment df);
    }


}