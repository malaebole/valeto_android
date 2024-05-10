package ae.valeto.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Parking {
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("location")
    @Expose
    private String location;
    @SerializedName("price")
    @Expose
    private String price;
    @SerializedName("phone_number")
    @Expose
    private String phoneNumber;
    @SerializedName("latitude")
    @Expose
    private Double latitude;
    @SerializedName("longitude")
    @Expose
    private Double longitude;
    @SerializedName("photo")
    @Expose
    private String photo;
    @SerializedName("owner")
    @Expose
    private ParkingOwner owner;
    @SerializedName("facilities")
    @Expose
    private List<Facilities> facilities;
    @SerializedName("currency")
    @Expose
    private String currency;
    @SerializedName("distance")
    @Expose
    private String distance;
    @SerializedName("rating")
    @Expose
    private Double rating;
    @SerializedName("is_fixed_price")
    @Expose
    private String isFixedPrice;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public ParkingOwner getOwner() {
        return owner;
    }

    public void setOwner(ParkingOwner owner) {
        this.owner = owner;
    }

    public List<Facilities> getFacilites() {
        return facilities;
    }

    public void setFacilites(List<Facilities> facilities) {
        this.facilities = facilities;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public String getIsFixedPrice() {
        return isFixedPrice;
    }

    public void setIsFixedPrice(String isFixedPrice) {
        this.isFixedPrice = isFixedPrice;
    }

}
