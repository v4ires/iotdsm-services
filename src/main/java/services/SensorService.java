package services;

import deserialization.IDeserializer;
import model.Sensor;
import model.SensorMeasure;
import model.SensorMeasureType;
import model.SensorSource;
import repositories.SensorMeasureRepository;
import repositories.SensorMeasureTypeRepository;
import repositories.SensorRepository;
import repositories.SensorSourceRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SensorService {
    public Long deserializeMeasures(IDeserializer deserializer)
    {
        List<SensorMeasure> smList;
        Long insertedMeasures = 0L;

        SensorSourceRepository _sensorSourceRepository = new SensorSourceRepository();
        SensorRepository _sensorRepository = new SensorRepository();
        SensorMeasureTypeRepository _sensorMeasureTypeRepository = new SensorMeasureTypeRepository();
        SensorMeasureRepository _sensorMeasureRepository = new SensorMeasureRepository();

        smList = (List<SensorMeasure>) (Object) deserializer.readArray();

        Map<String, SensorMeasureType> insertedSensorMeasureType = new HashMap<String, SensorMeasureType>();
        Map<String, SensorSource> insertedSensorSource = new HashMap<String, SensorSource>();

        while (smList != null) {
            List<Sensor> insertedSensor = new ArrayList<Sensor>();

            for (SensorMeasure sm : smList) {

                if (!insertedSensorSource.containsKey(sm.getSensor().getSensorSource().getName())) {
                    _sensorSourceRepository.addSensorSource(sm.getSensor().getSensorSource());
                    insertedSensorSource.put(sm.getSensor().getSensorSource().getName(), sm.getSensor().getSensorSource());
                }

                if (!insertedSensorMeasureType.containsKey(sm.getSensorMeasureType().getName())) {
                    _sensorMeasureTypeRepository.addSensorMeasureType(sm.getSensorMeasureType());
                    insertedSensorMeasureType.put(sm.getSensorMeasureType().getName(), sm.getSensorMeasureType());
                }

                if (!insertedSensor.contains(sm.getSensor())) {
                    _sensorRepository.addSensor(sm.getSensor());
                    insertedSensor.add(sm.getSensor());
                }

                _sensorMeasureRepository.addSensorMeasure(sm);
                insertedMeasures++;
            }

            smList = (List<SensorMeasure>) (Object) deserializer.readArray();
        }

        return insertedMeasures;
    }
}
