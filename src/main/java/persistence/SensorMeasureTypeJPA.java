package persistence;

import model.SensorMeasureType;

public class SensorMeasureTypeJPA extends GenericJPA<SensorMeasureType> {

    public SensorMeasureTypeJPA() {
        super(SensorMeasureType.class);
    }
}
