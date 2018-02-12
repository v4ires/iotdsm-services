package edu.usp.icmc.lasdpc.deserialization;

import edu.usp.icmc.lasdpc.model.Sensor;
import edu.usp.icmc.lasdpc.model.SensorMeasure;
import edu.usp.icmc.lasdpc.model.SensorMeasureType;
import edu.usp.icmc.lasdpc.model.SensorSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class VitalSignsHealthDataCsvDeserializer implements IDeserializer {

    private static final Logger log = LoggerFactory.getLogger(VitalSignsHealthDataCsvDeserializer.class);

    private Scanner fileStream;
    private String delimiter = ",";
    private SensorSource sensorSource;


    @Override
    public Object readObject() {
        return null;
    }

    @Override
    public List<Object> readArray() {

        List<SensorMeasure> sensorMeasures = new ArrayList<>();
        List<SensorMeasureType> measureTypes = new ArrayList<>();
        List<Sensor> sensors = new ArrayList<>();
        List<String[]> csvFile = new ArrayList<>();
        Set<String> mesureTypesString = new LinkedHashSet<>();
        Set<String> sensorsNameString = new LinkedHashSet<>();

        sensorSource = SensorSource.builder()
                .name("Health Data")
                .description("").build();

        log.info("Read File");
        while (fileStream.hasNext()) {
            String[] token = fileStream.nextLine().split(delimiter);
            csvFile.add(token);
            mesureTypesString.add(token[3]);
            sensorsNameString.add(token[1] + " - " + token[3]);
        }
        log.info("End Read File");

        log.info("Measure Type");
        //Measures Types
        try {
            for (String msType : mesureTypesString) {
                SensorMeasureType measureType = SensorMeasureType.builder()
                        .name(msType)
                        .unit(msType)
                        .build();
                measureType.setCreate_time(null);
                measureTypes.add(measureType);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        log.info("End Measure Type");

        log.info("Start Sensors");
        //Sensors
        try {
            for (String sensorName : sensorsNameString) {
                Sensor sensor = Sensor.builder().name(sensorName)
                        .sensorSource(sensorSource)
                        .description("")
                        .latitude(0.0f)
                        .longitude(0.0f)
                        .sensorMeasures(new HashSet<>()).build();
                sensor.setCreate_time(new Date());
                String msType = sensorName.split("-")[1].trim();

                Optional<SensorMeasureType> measureType = measureTypes.stream()
                        .filter(sensorMeasureType -> sensorMeasureType.getName().contains(msType)).findFirst();

                if (measureType.isPresent()) {
                    sensor.getSensorMeasures().add(measureType.get());
                    sensors.add(sensor);
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        log.info("End Sensors");

        log.info("Start Sensors Measures");
        //Sensor Measure
        try {
            for (String[] token : csvFile) {
                Optional<Sensor> sensor_x = sensors.stream().filter(sensor -> sensor.getName().contains(token[1] + " - " + token[3])).findFirst();
                Optional<SensorMeasureType> sensor_measuretype_x = measureTypes.stream().filter(sensor_type -> sensor_type.getName().equals(token[3])).findFirst();

                if (sensor_x.isPresent() && sensor_measuretype_x.isPresent()) {
                    SensorMeasure sensorMeasure = SensorMeasure.builder()
                            .value(token[2])
                            .sensor(sensor_x.get())
                            .sensorMeasureType(sensor_measuretype_x.get())
                            .create_time(new Date())
                            .build();

                    sensorMeasures.add(sensorMeasure);
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        log.info("End Sensors Measures");
        return (List<Object>) (Object) sensorMeasures;
    }

    @Override
    public boolean loadContent(String... args) {
        String filePath = args[0];

        if (args.length > 1) {
            delimiter = args[1];
        }

        File file = new File(filePath);

        try {
            fileStream = new Scanner(file);
        } catch (FileNotFoundException e) {
            log.error(e.getMessage());
            return false;
        }

        //Ignora a primeira linha (cabecalhos)
        try {
            fileStream.nextLine();
        } catch (Exception ex) {
            log.error(ex.getMessage());
            return false;
        }
        return true;
    }

    @Override
    public void close() {
        if (fileStream != null) {
            fileStream.close();
        }
    }
}
