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

public class WisdmCsvDeserializer implements IDeserializer {

    private static final Logger log = LoggerFactory.getLogger(WisdmCsvDeserializer.class);

    private Scanner fileStream;
    private String delimiter = ",";
    private SensorSource sensorSource;
    private String[] fixedMeasureTypes = {"x-acceleration", "y-accel", "z-accel"};

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
                .name("WISDSM")
                .description("").build();

        log.info("Read File");
        while (fileStream.hasNext()) {
            String[] token = fileStream.nextLine().split(delimiter);
            csvFile.add(token);
            String sensorName = token[0] + " - " + token[1];
            sensorsNameString.add(sensorName);
        }
        log.info("End Read File");

        log.info("Measure Type");
        //Measures Types
        try {
            for (String msType : fixedMeasureTypes) {
                mesureTypesString.add(msType);
            }
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

                Optional<SensorMeasureType> measureTypeXAceleration = measureTypes.stream()
                        .filter(sensorMeasureType -> sensorMeasureType.getName().contains(fixedMeasureTypes[0])).findFirst();

                Optional<SensorMeasureType> measureTypeYAccel = measureTypes.stream()
                        .filter(sensorMeasureType -> sensorMeasureType.getName().contains(fixedMeasureTypes[1])).findFirst();

                Optional<SensorMeasureType> measureTypeZAccel = measureTypes.stream()
                        .filter(sensorMeasureType -> sensorMeasureType.getName().contains(fixedMeasureTypes[2])).findFirst();

                if (measureTypeXAceleration.isPresent() && measureTypeYAccel.isPresent() && measureTypeZAccel.isPresent()) {
                    sensor.getSensorMeasures().add(measureTypeXAceleration.get());
                    sensor.getSensorMeasures().add(measureTypeYAccel.get());
                    sensor.getSensorMeasures().add(measureTypeZAccel.get());
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
                String sensorName = token[0] + " - " + token[1];
                Optional<Sensor> sensor_x = sensors.stream().filter(sensor -> sensor.getName().contains(sensorName)).findFirst();

                Optional<SensorMeasureType> sensor_measuretype_x = measureTypes.stream().filter(sensor_type -> sensor_type.getName().equals(fixedMeasureTypes[0])).findFirst();
                Optional<SensorMeasureType> sensor_measuretype_y = measureTypes.stream().filter(sensor_type -> sensor_type.getName().equals(fixedMeasureTypes[1])).findFirst();
                Optional<SensorMeasureType> sensor_measuretype_z = measureTypes.stream().filter(sensor_type -> sensor_type.getName().equals(fixedMeasureTypes[2])).findFirst();

                if (sensor_x.isPresent() && sensor_measuretype_x.isPresent()) {
                    SensorMeasure sensorMeasure = SensorMeasure.builder()
                            .value(token[3])
                            .sensor(sensor_x.get())
                            .sensorMeasureType(sensor_measuretype_x.get())
                            .create_time(new Date())
                            .build();
                    sensorMeasures.add(sensorMeasure);
                }

                if (sensor_x.isPresent() && sensor_measuretype_y.isPresent()) {
                    SensorMeasure sensorMeasure = SensorMeasure.builder()
                            .value(token[4])
                            .sensor(sensor_x.get())
                            .sensorMeasureType(sensor_measuretype_y.get())
                            .create_time(new Date())
                            .build();
                    sensorMeasures.add(sensorMeasure);
                }

                if (sensor_x.isPresent() && sensor_measuretype_z.isPresent()) {
                    SensorMeasure sensorMeasure = SensorMeasure.builder()
                            .value(token[5])
                            .sensor(sensor_x.get())
                            .sensorMeasureType(sensor_measuretype_z.get())
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
