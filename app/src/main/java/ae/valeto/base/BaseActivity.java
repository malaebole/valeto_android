package ae.valeto.base;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import ae.valeto.util.AppManager;
import ae.valeto.util.Constants;
import ae.valeto.util.Functions;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BaseActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        Functions.changeLanguage(this, Functions.getPrefValue(this, Constants.kAppLang));
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        //InAppReviewManager.showRateDialogIfMeetsConditions(this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!Functions.getPrefValue(getContext(), Constants.kAppModule).equalsIgnoreCase(Constants.kLineupModule)) {
            LocalBroadcastManager.getInstance(this).registerReceiver(movetoRatingReceiver, new IntentFilter("move_to_rating"));
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (!Functions.getPrefValue(getContext(), Constants.kAppModule).equalsIgnoreCase(Constants.kLineupModule)) {
            if (movetoRatingReceiver != null) {
                LocalBroadcastManager.getInstance(this).unregisterReceiver(movetoRatingReceiver);
            }
        }

    }

    public void hideKeyboard() {
        try {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    BroadcastReceiver movetoRatingReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
//            if (bundle != null) {
//                //get push data
//                String notType = bundle.getString("type", "");
//                String bookingId = bundle.getString("booking_id", "");
//                String clubId = bundle.getString("club_id", "");
//                String bookingType = bundle.getString("booking_type", "");
//                String isRated = bundle.getString("is_rated", "");
//                if (notType.equalsIgnoreCase(Constants.kBookingCompleteNotification) && !bookingId.isEmpty()) {
//                    if (bookingType.equalsIgnoreCase(Constants.kFriendlyGame)) {
//                        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
//                        Fragment prev = getSupportFragmentManager().findFragmentByTag("RatingPagerDialogFragment");
//                        if (prev != null) {
//                            fragmentTransaction.remove(prev);
//                        }
//                        fragmentTransaction.addToBackStack(null);
//                        OleRatingPagerDialogFragment dialogFragment = new OleRatingPagerDialogFragment(bookingId);
//                        dialogFragment.show(fragmentTransaction, "RatingPagerDialogFragment");
//                    } else {
//                        gotoEmpRate(bookingId, clubId, isRated);
//                    }
//                }
//            }
        }
    };

//    protected void showRatingDialog(String gameId) {
//        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
//        Fragment fragment = getSupportFragmentManager().findFragmentByTag("RatingPagerDialogFragment");
//        if (fragment != null) {
//            fragmentTransaction.remove(fragment);
//        }
//        fragmentTransaction.addToBackStack(null);
//        RatingPagerDialogFragment dialogFragment = new RatingPagerDialogFragment(gameId);
//        dialogFragment.show(fragmentTransaction, "RatingPagerDialogFragment");
//    }


//    public void showDateRangeFilter(String fromDate, String toDate, OleDateRangeFilterDialogFragment.DateRangeFilterDialogFragmentCallback callback) {
//        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
//        Fragment fragment = getSupportFragmentManager().findFragmentByTag("EmpReviewFilterDialogFragment");
//        if (fragment != null) {
//            fragmentTransaction.remove(fragment);
//        }
//        fragmentTransaction.addToBackStack(null);
//        OleDateRangeFilterDialogFragment dialogFragment = new OleDateRangeFilterDialogFragment(fromDate, toDate);
//        dialogFragment.setFragmentCallback(callback);
//        dialogFragment.show(fragmentTransaction, "EmpReviewFilterDialogFragment");
//    }

    protected int getRandomX(int viewWidth, float subVuW) {
        Random random = new Random();
        return random.nextInt(viewWidth-(int)subVuW);
    }

    protected int getRandomY(int viewHeight, float subVuH) {
        Random random = new Random();
        return random.nextInt(viewHeight-(int)subVuH);
    }

    public int getScreenWidth() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.widthPixels;
    }

    public int getScreenHeight() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.heightPixels;
    }
    protected void setBackground(ImageView imageView) {
        if (Functions.getPrefValue(getContext(), Constants.kUserType).equalsIgnoreCase(Constants.kRefereeType)) {
            //imageView.setImageResource(R.drawable.referee_bg);
        }
        else if (Functions.getPrefValue(getContext(), Constants.kUserType).equalsIgnoreCase(Constants.kOwnerType)) {
           // imageView.setImageResource(R.drawable.owner_bg);
        }
        else {
           // imageView.setImageResource(R.drawable.player_bg);
        }
    }

    public Activity getContext() {
        return this;
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }

    protected void makeStatusbarTransperant() {
        Window window = getWindow();
        WindowManager.LayoutParams winParams = window.getAttributes();
        winParams.flags &= ~WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        window.setAttributes(winParams);
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }
    private Bitmap getBitmap(Drawable drawable) {
        Bitmap bitmap;
        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if(bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }

        if(drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }
    protected Bitmap getBitmapFromView(View view, Drawable drawable) {
        //Define a bitmap with the same size as the view
        Bitmap returnedBitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(),Bitmap.Config.ARGB_8888);
        //Bind a canvas to it
        Canvas canvas = new Canvas(returnedBitmap);
        if (drawable != null) {
            Bitmap bitmap = getBitmap(drawable);
            canvas.drawBitmap(bitmap, canvas.getWidth() - bitmap.getWidth(), canvas.getHeight() - bitmap.getHeight(), new Paint());
        }
        else {
            canvas.drawColor(Color.WHITE);
        }
        // draw the view on the canvas
        view.draw(canvas);
        //return the bitmap
        return returnedBitmap;
    }
    protected Bitmap getBitmapFromView(View view) {
        //Define a bitmap with the same size as the view
        Bitmap returnedBitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(),Bitmap.Config.ARGB_8888);
        //Bind a canvas to it
        Canvas canvas = new Canvas(returnedBitmap);
        //Get the view's background
        Drawable bgDrawable =view.getBackground();
        if (bgDrawable!=null) {
            //has background drawable, then draw it on the canvas
            bgDrawable.draw(canvas);
        }   else{
            //does not have background drawable, then draw white background on the canvas
            canvas.drawColor(Color.WHITE);
        }
        // draw the view on the canvas
        view.draw(canvas);
        //return the bitmap
        return returnedBitmap;
    }

    protected Uri saveBitmap(@NonNull final Context context, @NonNull final Bitmap bitmap) throws IOException {

        final ContentValues values = new ContentValues();
        //values.put(MediaStore.MediaColumns.DISPLAY_NAME, "image_" + System.currentTimeMillis() + ".jpg");
        //values.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg");
        values.put(MediaStore.MediaColumns.DISPLAY_NAME, "image_" + System.currentTimeMillis() + ".png");
        values.put(MediaStore.MediaColumns.MIME_TYPE, "image/png");
        values.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DCIM);

        final ContentResolver resolver = context.getContentResolver();
        Uri uri = null;

        try {
            final Uri contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            uri = resolver.insert(contentUri, values);

            if (uri == null)
                throw new IOException("Failed to create new MediaStore record.");

            try (final OutputStream stream = resolver.openOutputStream(uri)) {
                if (stream == null)
                    throw new IOException("Failed to open output stream.");

//                if (!bitmap.compress(Bitmap.CompressFormat.JPEG, 95, stream))
//                    throw new IOException("Failed to save bitmap.");

                if (!bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream))
                    throw new IOException("Failed to save bitmap.");
            }

            return uri;
        }
        catch (IOException e) {

            if (uri != null) {
                // Don't leave an orphan entry in the MediaStore
                resolver.delete(uri, null, null);
            }

            throw e;
        }
    }
    public String saveBitmap(Bitmap bitmap) {
        File mediaStorageDir = getCacheDir();
        File myFilePath = new File(mediaStorageDir.getAbsolutePath() + "/camera");
        if (!myFilePath.exists())
            myFilePath.mkdir();

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File fileName = new File(myFilePath.getPath() + File.separator + "IMG_" + timeStamp + ".png");

        try {
            FileOutputStream outputStream = new FileOutputStream(String.valueOf(fileName));
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            outputStream.close();

            return fileName.getAbsolutePath();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }


    protected void getUnreadNotificationAPI(UnreadCountCallback callback) {
//        Call<ResponseBody> call = AppManager.getInstance().apiInterface.unreadNotifCount(Functions.getAppLang(getContext()), Functions.getPrefValue(getContext(), Constants.kUserID));
//        call.enqueue(new Callback<ResponseBody>() {
//            @Override
//            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//                int count = 0;
//                if (response.body() != null) {
//                    try {
//                        JSONObject object = new JSONObject(response.body().string());
//                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
//                            JSONObject obj = object.getJSONObject(Constants.kData);
//                            String c = obj.getString("total_unread");
//                            count = Integer.parseInt(c);
//                        }
//                        else {
//
//                        }
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
//                callback.unreadNotificationCount(count);
//            }
//            @Override
//            public void onFailure(Call<ResponseBody> call, Throwable t) {
//                callback.unreadNotificationCount(0);
//            }
//        });
    }



    public interface UnreadCountCallback {
        void unreadNotificationCount(int count);
    }

}
