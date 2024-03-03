package ae.valeto.api;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface APIInterface {

        //Auth Apis
    @Multipart
    @POST("auth/register")
    Call<ResponseBody> registerUser(@Part MultipartBody.Part file,
                                    @Part("name") RequestBody name,
                                    @Part("email") RequestBody bibId,
                                    @Part("phone") RequestBody teamId,
                                    @Part("password") RequestBody countryId);
    @FormUrlEncoded
    @POST("auth/login")
    Call<ResponseBody> userLogin(@Field("email") String email,
                                 @Field("password") String password);
    @FormUrlEncoded
    @POST("auth/verify-email")
    Call<ResponseBody> verifyEmail(@Field("otp_code") String otpCode);
    @FormUrlEncoded
    @POST("auth/forgot-password")
    Call<ResponseBody> forgetPassword(@Field("email") String email);

    @POST("auth/resend-otp")
    Call<ResponseBody> sendOtp();


    //user Apis
    @FormUrlEncoded
    @POST("user/profile/add-device-token")
    Call<ResponseBody> sendFcmToken(@Field("device_id") String deviceId,
                                    @Field("device_type") String type,
                                    @Field("device_token") String token);
    @GET("user/profile")
    Call<ResponseBody> getUserProfile();
    @FormUrlEncoded
    @POST("user/profile/logout")
    Call<ResponseBody> userLogout(@Field("device_id") String deviceId);
    @GET("user/profile/generate-qrcode")
    Call<ResponseBody> generateQrCode();
    @Multipart
    @PUT("user/profile")
    Call<ResponseBody> updateUserProfile(@Part MultipartBody.Part file,
                                         @Part("name") RequestBody name,
                                         @Part("email") RequestBody email,
                                         @Part("phone") RequestBody phone);
    @FormUrlEncoded
    @POST("user/profile/change-password")
    Call<ResponseBody> changePassword(@Field("old_password") String oldPassword,
                                      @Field("new_password") String newPassword);



    //User car Apis


    @GET("user/cars")
    Call<ResponseBody> getUserCars();

    @GET("user/cars/brands-with-models")
    Call<ResponseBody> getCarManufacturerList();

    @GET("user/cars/{id}")
    Call<ResponseBody> getSingleUserCar(@Path("id") int id);

    @Multipart
    @POST("user/cars")
    Call<ResponseBody> addUserCar(@Part MultipartBody.Part picture,
                                     @Part("name") RequestBody name,
                                     @Part("plate_number") RequestBody plateNumber,
                                     @Part("brand") RequestBody brand,
                                     @Part("model") RequestBody model);

    @Multipart
    @PUT("user/cars")
    Call<ResponseBody> updateUserCar(@Part MultipartBody.Part picture,
                                     @Part("car_id") RequestBody cardId,
                                     @Part("name") RequestBody name,
                                     @Part("plate_number") RequestBody plateNumber,
                                     @Part("brand") RequestBody brand,
                                     @Part("model") RequestBody model);

    @DELETE("user/cars")
    Call<ResponseBody> deleteUserCar(@Query("car_id") int carId);


    //User Parking Apis


    @GET("user/tickets/closed")
    Call<ResponseBody> getUserClosedTickets();


    @GET("user/tickets/{id}")
    Call<ResponseBody> getSingleUserTicket(@Path("id") int id);
    @FormUrlEncoded
    @POST("user/tickets/make-car-ready")
    Call<ResponseBody> makeMyCarReady(@Field("ticket_id") int ticketId,
                                      @Field("make_ready_in") String makeItReadyIn);

    @GET("user/parking")
    Call<ResponseBody> getParkingList(@Query("city_id") int cityId,
                                      @Query("latitude") double latitude,
                                      @Query("longitude") double longitude);

    @GET("user/parking/{id}")
    Call<ResponseBody> getSingleUserParking(@Path("id") int id);
    @FormUrlEncoded
    @POST("user/parking/add-review")
    Call<ResponseBody> addParkingReview(@Field("parking_id") String parkingId,
                                        @Field("stars") String stars,
                                        @Field("comments") String comments);
}
