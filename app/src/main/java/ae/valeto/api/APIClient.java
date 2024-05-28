package ae.valeto.api;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import ae.valeto.MyApp;
import ae.valeto.R;
import ae.valeto.activities.LoginActivity;
import ae.valeto.util.Constants;
import ae.valeto.util.Functions;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class APIClient {

    private static Retrofit retrofit = null;
    private static final SharedPreferences sharedPreferences = MyApp.getAppContext().getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE);

    public static Retrofit getClient() {

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(20, TimeUnit.SECONDS)
                .writeTimeout(20, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .addInterceptor(new Interceptor() {
                    @NonNull
                    @Override
                    public Response intercept(@NonNull Chain chain) throws IOException {
                        Request originalRequest = chain.request();
                        Request.Builder requestBuilder = originalRequest.newBuilder()
                                .addHeader("Accept", "application/json")
                                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                                .addHeader("Authorization", "Bearer " + Functions.getPrefValue(MyApp.getAppContext(), Constants.kAccessToken));
                        Response response = chain.proceed(requestBuilder.build());

                        if (response.code() == 401) {
                            // Refresh the token
                            synchronized (this) {
                                String newToken = refreshToken();
                                if (newToken != null) {
                                    Request newRequest = originalRequest.newBuilder()
                                            .header("Authorization", "Bearer " + newToken)
                                            .build();
                                    response.close();
                                    return chain.proceed(newRequest);
                                } else {
                                    // If token refresh fails, handle the failure (e.g., logout)
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.remove(Constants.kIsSignIn);
                                    editor.remove(Constants.kUserInfo);
                                    editor.remove(Constants.kUserType);
                                    editor.remove(Constants.kUserID);
                                    editor.remove(Constants.kAccessToken);
                                    editor.apply();
                                    Intent intent = new Intent(MyApp.getAppContext(), LoginActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    MyApp.getAppContext().startActivity(intent);
                                }
                            }
                        }

                        return response;
                    }
                }).build();

        retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();

        return retrofit;
    }

    private static String refreshToken() {
        String refreshToken = Functions.getPrefValue(MyApp.getAppContext(), Constants.kRefreshToken);
        if (refreshToken == null) {
            return null;
        }

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        APIInterface authService = retrofit.create(APIInterface.class);
        Call<ResponseBody> call = authService.refreshAccessToken(refreshToken);
        try {
            retrofit2.Response<ResponseBody> response = call.execute();
            if (response.isSuccessful() && response.body() != null) {
                JSONObject object = new JSONObject(response.body().string());
                if (object.getInt("status") == 200) {
                    String newAccessToken = object.getString("access_token");
                    SharedPreferences.Editor editor = MyApp.getAppContext().getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE).edit();
                    editor.putString(Constants.kAccessToken, newAccessToken);
                    editor.apply();
                    return newAccessToken;
                } else {
                    Log.e("APIClient", "Failed to refresh token: " + object.getString("message"));
                }
            } else {
                Log.e("APIClient", "Failed to refresh token: " + response.errorBody().string());
            }
        } catch (IOException | JSONException e) {
            Log.e("APIClient", "Error refreshing token", e);
        }
        return null;
    }
}
