package ae.valeto.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import android.view.View;
import android.view.Window;
import com.hedgehog.ratingbar.RatingBar;
import com.shashank.sony.fancytoastlib.FancyToast;
import ae.valeto.R;
import ae.valeto.databinding.FragmentRatingDialogBinding;
import ae.valeto.util.Functions;


public class RatingDialogFragment extends Dialog {

    private FragmentRatingDialogBinding binding;
    private final Context context;
    private String clubId = "";
    private float rating = 5;

    public RatingDialogFragment(@NonNull Context context, String clubId) {
        super(context);
        this.context = context;
        this.clubId = clubId;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        binding = FragmentRatingDialogBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        if (getWindow() != null) {
            getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        binding.ratingBar.setStar(rating);

        binding.ratingBar.setOnRatingChangeListener(new RatingBar.OnRatingChangeListener() {
            @Override
            public void onRatingChange(float RatingCount) {
                rating = RatingCount;
            }
        });

        binding.btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitClicked();
            }
        });
    }

    private void submitClicked() {
        if (rating > 0) {
            //rateClubAPI();
        }
        else {
            Functions.showToast(context, "Rating cannot be zero", FancyToast.ERROR);
        }
    }

//    private void rateClubAPI() {
//        String userId = Functions.getPrefValue(context, Constants.kUserID);
//        KProgressHUD hud = Functions.showLoader(context, "Image processing");
//        Call<ResponseBody> call = AppManager.getInstance().apiInterface.rateClub(Functions.getAppLang(getContext()),Functions.getPrefValue(getContext(), Constants.kUserID), clubId, rating);
//        call.enqueue(new Callback<ResponseBody>() {
//            @Override
//            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//                Functions.hideLoader(hud);
//                if (response.body() != null) {
//                    try {
//                        JSONObject object = new JSONObject(response.body().string());
//                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
//                            Functions.showToast(context, object.getString(Constants.kMsg), FancyToast.SUCCESS);
//                        }
//                        else {
//                            Functions.showToast(context, object.getString(Constants.kMsg), FancyToast.ERROR);
//                        }
//                        dismiss();
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                        Functions.showToast(context, e.getLocalizedMessage(), FancyToast.ERROR);
//                    }
//                }
//                else {
//                    Functions.showToast(context, context.getString(R.string.error_occured), FancyToast.ERROR);
//                }
//            }
//
//            @Override
//            public void onFailure(Call<ResponseBody> call, Throwable t) {
//                Functions.hideLoader(hud);
//                if (t instanceof UnknownHostException) {
//                    Functions.showToast(getContext(), context.getString(R.string.check_internet_connection), FancyToast.ERROR);
//                }
//                else {
//                    Functions.showToast(getContext(), t.getLocalizedMessage(), FancyToast.ERROR);
//                }
//            }
//        });
//    }
}
