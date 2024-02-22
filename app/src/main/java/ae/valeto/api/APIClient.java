package ae.valeto.api;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import ae.valeto.MyApp;
import ae.valeto.util.Constants;
import ae.valeto.util.Functions;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
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
                .addInterceptor(new  Interceptor() {    // .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Request.Builder requestBuilder = chain.request().newBuilder()
                                .addHeader("Accept", "application/json")
                                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                                .addHeader("Authorization", "Bearer "+Functions.getPrefValue(MyApp.getAppContext(), Constants.kaccessToken));
                        Response response = chain.proceed(requestBuilder.build());

//                        if (response.code() == 401){
//                            SharedPreferences.Editor editor = sharedPreferences.edit();
//                            editor.remove(Constants.kIsSignIn);
//                            editor.remove(Constants.kUserInfo);
//                            editor.remove(Constants.kCurrency);
//                            editor.remove(Constants.kUserType);
//                            editor.remove(Constants.kAppModule);
//                            editor.remove(Constants.kUserID);
//                            editor.remove(Constants.kaccessToken);
//                            editor.remove(Constants.kUserModule);
//                            editor.remove(Constants.kLoginType);
//                            editor.apply();
//                            Intent intent = new Intent(MyApp.getAppContext(), LoginActivity.class);
//                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                            MyApp.getAppContext().startActivity(intent);
//                        }

                        return response;
                    }
                }).build();


        retrofit = new Retrofit.Builder()
                .baseUrl(Constants.NODE_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();

        return retrofit;
    }

}