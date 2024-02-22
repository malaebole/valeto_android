package ae.valeto.api;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface APIInterface {


    @Multipart
    @POST("auth/register")
    Call<ResponseBody> registerUser(@Part MultipartBody.Part file,
                                    @Part("name") RequestBody name,
                                    @Part("email") RequestBody bibId,
                                    @Part("phone") RequestBody teamId,
                                    @Part("password") RequestBody countryId);

    @FormUrlEncoded
    @POST("user/profile/add-device-token")
    Call<ResponseBody> sendFcmToken(@Field("device_id") String deviceId,
                                    @Field("device_type") String type,
                                    @Field("device_token") String token);

    @FormUrlEncoded
    @POST("auth/login")
    Call<ResponseBody> userLogin(@Field("email") String email,
                                 @Field("password") String password);

    @GET("user/profile")
    Call<ResponseBody> getUserProfile();

    @FormUrlEncoded
    @POST("user/profile/logout")
    Call<ResponseBody> userLogout(@Field("device_id") String deviceId);



//    @GET("lineup/global/start-match")
//    Call<ResponseBody> startRealLineup(@Query("team_id") String teamId);





}
