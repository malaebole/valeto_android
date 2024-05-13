package ae.valeto.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UserInfo {
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("phone")
    @Expose
    private String phone;
    @SerializedName("picture")
    @Expose
    private String picture;

    @SerializedName("points")
    @Expose
    private Double points;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("user_type")
    @Expose
    private String userType;
    @SerializedName("latitude")
    @Expose
    private String latitude;
    @SerializedName("longitude")
    @Expose
    private String longitude;
    @SerializedName("email_verified")
    @Expose
    private String emailVerified;
    @SerializedName("app_owner_phone")
    @Expose
    private String appOwnerPhone;


    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getPhone() {
        return phone;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }
    public String getPicture() {
        return picture;
    }
    public void setPicture(String picture) {
        this.picture = picture;
    }
    public Double getPoints () {
        return points;
    }
    public void setPoints(Double points) {
        this.points = points;
    }
    public String getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
    public String getUserType() {
        return userType;
    }
    public void setUserType(String userType) {
        this.userType = userType;
    }
    public String getLatitude() {
        return latitude;
    }
    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }
    public String getLongitude() {
        return longitude;
    }
    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }
    public String getEmailVerified() {
        return emailVerified;
    }
    public void setEmailVerified(String emailVerified) {
        this.emailVerified = emailVerified;
    }
    public String getAppOwnerPhone() {
        return appOwnerPhone;
    }
    public void setAppOwnerPhone(String appOwnerPhone) {
        this.appOwnerPhone = appOwnerPhone;
    }

}
