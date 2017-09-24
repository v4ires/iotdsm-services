package persistence;

import model.SensorMeasureType;
import model.SensorSource;

public class SensorSourceJPA extends GenericJPA<SensorSource> {

    public SensorSourceJPA() {
        super(SensorSource.class);
    }
}
