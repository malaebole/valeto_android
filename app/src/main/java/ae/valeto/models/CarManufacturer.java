package ae.valeto.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CarManufacturer {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("models")
    @Expose
    private List<CarsModel> carsModel;

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

    public List<CarsModel> getModels() {
        return carsModel;
    }

    public void setModels(List<CarsModel> carsModel) {
        this.carsModel = carsModel;
    }
}
