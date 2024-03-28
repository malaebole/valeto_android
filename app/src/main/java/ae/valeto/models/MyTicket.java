package ae.valeto.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MyTicket {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("slot_number")
    @Expose
    private String slotNumber;
    @SerializedName("key_code")
    @Expose
    private String keyCode;
    @SerializedName("start_time")
    @Expose
    private String startTime;
    @SerializedName("end_time")
    @Expose
    private String endTime;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("activated_by")
    @Expose
    private ActivatedBy activatedBy;
    @SerializedName("closed_by")
    @Expose
    private ClosedBy closedBy;
    @SerializedName("car")
    @Expose
    private Cars car;
    @SerializedName("parking")
    @Expose
    private Parking parking;
    @SerializedName("invoice")
    @Expose
    private String invoice;
    @SerializedName("duration")
    @Expose
    private String duration;
    @SerializedName("paid_amount")
    @Expose
    private String paidAmount;
    @SerializedName("currency")
    @Expose
    private String currency;
    @SerializedName("reviews")
    @Expose
    private int reviews;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getSlotNumber() {
        return slotNumber;
    }

    public void setSlotNumber(String slotNumber) {
        this.slotNumber = slotNumber;
    }

    public String getKeyCode() {
        return keyCode;
    }

    public void setKeyCode(String keyCode) {
        this.keyCode = keyCode;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public ActivatedBy getActivatedBy() {
        return activatedBy;
    }

    public void setActivatedBy(ActivatedBy activatedBy) {
        this.activatedBy = activatedBy;
    }

    public ClosedBy getClosedBy() {
        return closedBy;
    }

    public void setClosedBy(ClosedBy closedBy) {
        this.closedBy = closedBy;
    }

    public Cars getCar() {
        return car;
    }

    public void setCar(Cars car) {
        this.car = car;
    }

    public Parking getParking() {
        return parking;
    }

    public void setParking(Parking parking) {
        this.parking = parking;
    }

    public String getInvoice() {
        return invoice;
    }

    public void setInvoice(String invoice) {
        this.invoice = invoice;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getPaidAmount() {
        return paidAmount;
    }

    public void setPaidAmount(String paidAmount) {
        this.paidAmount = paidAmount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public int getReviews() {
        return reviews;
    }

    public void setReviews(int reviews) {
        this.reviews = reviews;
    }

}
