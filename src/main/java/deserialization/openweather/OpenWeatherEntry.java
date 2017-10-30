package deserialization.openweather;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class OpenWeatherEntry {

    @SerializedName("city")
    @Expose
    private City city;
    @SerializedName("time")
    @Expose
    private Integer time;
    @SerializedName("data")
    @Expose
    private List<SensorData> data = null;

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }

    public Integer getTime() {
        return time;
    }

    public void setTime(Integer time) {
        this.time = time;
    }

    public List<SensorData> getData() {
        return data;
    }

    public void setData(List<SensorData> data) {
        this.data = data;
    }

}
