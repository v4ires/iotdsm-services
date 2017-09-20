package persistence;

import model.Sensor;

public class SensorJPA extends GenericJPA<Sensor> {

    public SensorJPA() {
        super(Sensor.class);
    }
}
