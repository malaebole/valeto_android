package ae.valeto.base;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONObject;
import java.net.UnknownHostException;

import ae.valeto.R;
import ae.valeto.models.UserInfo;
import ae.valeto.util.Constants;
import ae.valeto.util.Functions;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BaseTabActivity extends BaseActivity {

    //protected ResideMenu resideMenu;
    protected RelativeLayout ownerCardView,tcVu, ppVu, deleteUserAccount, rankVu, globalRankVu, shopOrderVu, wishlistVu, paymentVu, savedCardVu, oleCreditVu, addPriceVu, membershipPlansVu, scheduleVu, settingVu, shareVu, playerSearchVu, footballVu, padelVu;
    protected ImageView userImageVu, imgVuFootball, imgVuPadel, imgVuSide, emojiImgVu, shirtImgVu;
    protected TextView tvName, tvFootball, tvPadel, tvRank;
    protected LinearLayout switchVu;
    protected MaterialCardView switchCard;
    protected CardView playerCardView;
    UserInfo info;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if (!task.isSuccessful()) {
                    return;
                }

                // Get new FCM registration token
                String token = task.getResult();
                if (Functions.getPrefValue(getContext(), Constants.kIsSignIn).equalsIgnoreCase("1")) {
                    //sendFcmTokenApi(token);
                }
            }
        });
        info = Functions.getUserinfo(getContext());
    }

}
