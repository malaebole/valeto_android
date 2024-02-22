package ae.valeto.base;


import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import ae.valeto.R;
import ae.valeto.util.Constants;
import ae.valeto.util.Functions;


/**
 * A simple {@link Fragment} subclass.
 */
public class BaseFragment extends Fragment {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        Functions.changeLanguage(getActivity(), Functions.getPrefValue(getActivity(), Constants.kAppLang));
    }

    protected void hideKeyboard() {
        try {
            InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

//    protected void setBackground(ImageView imageView) {
//        if (Functions.getPrefValue(getActivity(), Constants.kUserType).equalsIgnoreCase(Constants.kRefereeType)) {
//           // imageView.setImageResource(R.drawable.referee_bg);
//        }
//        else if (Functions.getPrefValue(getActivity(), Constants.kUserType).equalsIgnoreCase(Constants.kOwnerType)) {
//           // imageView.setImageResource(R.drawable.owner_bg);
//        }
//        else {
//           // imageView.setImageResource(R.drawable.player_bg);
//        }
//    }

}
