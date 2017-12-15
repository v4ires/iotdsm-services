package deserialization;

import model.Sensor;
import model.SensorMeasure;
import model.SensorMeasureType;
import model.SensorSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.time.Instant;
import java.util.*;

/**
 * University of Sao Paulo
 * IoT Repository Module
 *
 * @author Vinicius Aires Barros <viniciusaires@usp.br>
 */
public class OpenWeatherCsvDeserializer implements IDeserializer {

    private static final Logger log = LoggerFactory.getLogger(OpenWeatherCsvDeserializer.class);

    private Scanner fileStream;
    private String delimiter = ",";
    private String nextLine;
    private String currentCity;

    private SensorSource sensorSource;
    private Sensor tempSensor;
    private Sensor pressureSensor;
    private Sensor humiditySensor;
    private Sensor speedWindSensor;
    private SensorMeasureType tempMeasureType;
    private SensorMeasureType pressMeasureType;
    private SensorMeasureType humidityMeasureType;
    private SensorMeasureType speedWindMeasureType;

    @Override
    public Object readObject() {
        return null;
    }

    @Override
    public List<Object> readArray() {
        String line;
        List<SensorMeasure> sensorMeasures = new ArrayList<>();

        while (true) {
            if (nextLine == null) {
                try {
                    line = fileStream.nextLine();
                } catch (Exception ex) {
                    log.error(ex.getMessage());

                    if (sensorMeasures.size() > 0)
                        return (List<Object>) (Object) sensorMeasures;

                    return null;
                }
            } else {
                line = nextLine;
                nextLine = null;
            }

            if (line != null && !line.equals("")) {
                String[] fields = line.split(delimiter);

                if (currentCity != null) {
                    if (!currentCity.equals(fields[0])) {
                        nextLine = line;
                        speedWindSensor = null;
                        tempSensor = null;
                        pressureSensor = null;
                        humiditySensor = null;
                        currentCity = fields[0];

                        return (List<Object>) (Object) sensorMeasures;
                    }
                } else {
                    currentCity = fields[0];
                }

                if (sensorSource == null)
                    sensorSource = SensorSource.builder()
                            .name("OpenWeatherMap")
                            .build();

                if (tempMeasureType == null)
                    tempMeasureType = SensorMeasureType.builder()
                            .name("temperature")
                            .unit("K")
                            .build();

                if (tempSensor == null) {
                    tempSensor = Sensor.builder()
                            .name("temperature sensor - " + fields[1])
                            .latitude(Double.parseDouble(fields[4]))
                            .longitude(Double.parseDouble(fields[3]))
                            .sensorSource(sensorSource)
                            .sensorMeasures(new HashSet<>())
                            .build();
                    tempSensor.getSensorMeasures().add(tempMeasureType);
                }

                if (pressMeasureType == null)
                    pressMeasureType = SensorMeasureType.builder()
                            .name("pressure")
                            .unit("hPa")
                            .build();

                if (pressureSensor == null) {
                    pressureSensor = Sensor.builder()
                            .name("pressure sensor - " + fields[1])
                            .latitude(Double.parseDouble(fields[4]))
                            .longitude(Double.parseDouble(fields[3]))
                            .sensorSource(sensorSource)
                            .sensorMeasures(new HashSet<>())
                            .build();
                    pressureSensor.getSensorMeasures().add(pressMeasureType);
                }

                if (humidityMeasureType == null)
                    humidityMeasureType = SensorMeasureType.builder()
                            .name("humidity")
                            .unit("%")
                            .build();

                if (humiditySensor == null) {
                    humiditySensor = Sensor.builder()
                            .name("humidity sensor - " + fields[1])
                            .latitude(Double.parseDouble(fields[4]))
                            .longitude(Double.parseDouble(fields[3]))
                            .sensorSource(sensorSource)
                            .sensorMeasures(new HashSet<>())
                            .build();
                    humiditySensor.getSensorMeasures().add(humidityMeasureType);
                }

                if (speedWindMeasureType == null)
                    speedWindMeasureType = SensorMeasureType.builder()
                            .name("speed_wind")
                            .unit("m/s")
                            .build();

                if (speedWindSensor == null) {
                    speedWindSensor = Sensor.builder()
                            .name("speed_wind sensor - " + fields[1])
                            .latitude(Double.parseDouble(fields[4]))
                            .longitude(Double.parseDouble(fields[3]))
                            .sensorSource(sensorSource)
                            .sensorMeasures(new HashSet<>())
                            .build();
                    speedWindSensor.getSensorMeasures().add(speedWindMeasureType);
                }


                SensorMeasure tempSensorMeasure = SensorMeasure.builder()
                        .value(fields[6])
                        .sensor(tempSensor)
                        .sensorMeasureType(tempMeasureType)
                        .build();

                tempSensorMeasure.setCreate_time(Date.from(Instant.ofEpochSecond(Long.parseLong(fields[5]))));
                sensorMeasures.add(tempSensorMeasure);

                SensorMeasure pressSensorMeasure = SensorMeasure.builder()
                        .value(fields[12])
                        .sensor(pressureSensor)
                        .sensorMeasureType(pressMeasureType)
                        .build();

                pressSensorMeasure.setCreate_time(Date.from(Instant.ofEpochSecond(Long.parseLong(fields[5]))));
                sensorMeasures.add(pressSensorMeasure);

                SensorMeasure humiditySensorMeasure = SensorMeasure.builder()
                        .value(fields[13])
                        .sensor(humiditySensor)
                        .sensorMeasureType(humidityMeasureType)
                        .build();

                humiditySensorMeasure.setCreate_time(Date.from(Instant.ofEpochSecond(Long.parseLong(fields[5]))));
                sensorMeasures.add(humiditySensorMeasure);

                SensorMeasure speedWindSensorMeasure = SensorMeasure.builder()
                        .value(fields[18])
                        .sensor(speedWindSensor)
                        .sensorMeasureType(speedWindMeasureType)
                        .build();

                speedWindSensorMeasure.setCreate_time(Date.from(Instant.ofEpochSecond(Long.parseLong(fields[5]))));
                sensorMeasures.add(speedWindSensorMeasure);
            }
        }
    }

    @Override
    public boolean loadContent(String... args) {
        String filePath = args[0];

        if (args.length > 1)
            delimiter = args[1];

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
        if (fileStream != null)
            fileStream.close();
    }
}
