package drivers;

import model.Sensor;
import model.SensorMeasure;
import model.SensorMeasureType;

import java.util.Date;
import java.util.List;

public interface DatabaseDriver {
    Sensor getSensorById(long id);
    List<Sensor> getSensors();
    List<SensorMeasure> getSensorMeasuresOnInterval(Sensor sensor, SensorMeasureType sensorMeasureType, Date startDate, Date endDate);
    boolean addSensor(Sensor sensor);
    boolean addSensorMeasure(SensorMeasure sensorMeasure);
    boolean deleteSensor(Sensor sensor);
}
