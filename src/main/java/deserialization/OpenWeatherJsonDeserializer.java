package deserialization;

import com.google.gson.Gson;
import deserialization.openweather.OpenWeatherEntry;
import deserialization.openweather.SensorData;
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
 * University of São Paulo
 * IoT Repository Module
 *
 * @author Vinícius Aires Barros <viniciusaires@usp.br>
 */
public class OpenWeatherJsonDeserializer implements IDeserializer {

    private static final Logger log = LoggerFactory.getLogger(OpenWeatherJsonDeserializer.class);

    private Scanner fileStream;

    private SensorSource sensorSource;

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

        try {
            line = fileStream.nextLine();
        } catch (Exception ex) {
            log.error(ex.getMessage());
            return null;
        }

        List<SensorMeasure> sensorMeasures = new ArrayList<>();

        if (line != null && !line.equals("")) {
            OpenWeatherEntry result = new Gson().fromJson(line, OpenWeatherEntry.class);

            if (sensorSource == null)
                sensorSource = SensorSource.builder()
                        .name("OpenWeatherMap")
                        .build();

            Sensor tempSensor = Sensor.builder()
                    .name("temperature sensor - " + result.getCity().getName())
                    .latitude(result.getCity().getCoord().getLat())
                    .longitude(result.getCity().getCoord().getLon())
                    .sensorSource(sensorSource)
                    .sensorMeasures(new HashSet<>())
                    .build();

            if (tempMeasureType == null)
                tempMeasureType = SensorMeasureType.builder()
                        .name("temperature")
                        .unit("K")
                        .build();
            tempSensor.getSensorMeasures().add(tempMeasureType);

            Sensor pressureSensor = Sensor.builder()
                    .name("pressure sensor - " + result.getCity().getName())
                    .latitude(result.getCity().getCoord().getLat())
                    .longitude(result.getCity().getCoord().getLon())
                    .sensorSource(sensorSource)
                    .sensorMeasures(new HashSet<>())
                    .build();

            if (pressMeasureType == null)
                pressMeasureType = SensorMeasureType.builder()
                        .name("pressure")
                        .unit("hPa")
                        .build();
            pressureSensor.getSensorMeasures().add(pressMeasureType);

            Sensor humiditySensor = Sensor.builder()
                    .name("humidity sensor - " + result.getCity().getName())
                    .latitude(result.getCity().getCoord().getLat())
                    .longitude(result.getCity().getCoord().getLon())
                    .sensorSource(sensorSource)
                    .sensorMeasures(new HashSet<>())
                    .build();

            if (humidityMeasureType == null)
                humidityMeasureType = SensorMeasureType.builder()
                        .name("humidity")
                        .unit("%")
                        .build();
            humiditySensor.getSensorMeasures().add(humidityMeasureType);

            Sensor speedWindSensor = Sensor.builder()
                    .name("speed_wind sensor - " + result.getCity().getName())
                    .latitude(result.getCity().getCoord().getLat())
                    .longitude(result.getCity().getCoord().getLon())
                    .sensorSource(sensorSource)
                    .sensorMeasures(new HashSet<>())
                    .build();

            if (speedWindMeasureType == null)
                speedWindMeasureType = SensorMeasureType.builder()
                        .name("speed_wind")
                        .unit("m/s")
                        .build();
            speedWindSensor.getSensorMeasures().add(speedWindMeasureType);

            for (SensorData measure : result.getData()) {
                SensorMeasure tempSensorMeasure = SensorMeasure.builder()
                        .value(measure.getTemp().getDay().toString())
                        .sensor(tempSensor)
                        .sensorMeasureType(tempMeasureType)
                        .build();

                tempSensorMeasure.setCreate_time(Date.from(Instant.ofEpochSecond(measure.getDt())));
                sensorMeasures.add(tempSensorMeasure);

                SensorMeasure pressSensorMeasure = SensorMeasure.builder()
                        .value(measure.getPressure().toString())
                        .sensor(pressureSensor)
                        .sensorMeasureType(pressMeasureType)
                        .build();

                pressSensorMeasure.setCreate_time(Date.from(Instant.ofEpochSecond(measure.getDt())));
                sensorMeasures.add(pressSensorMeasure);

                SensorMeasure humiditySensorMeasure = SensorMeasure.builder()
                        .value(measure.getHumidity().toString())
                        .sensor(humiditySensor)
                        .sensorMeasureType(humidityMeasureType)
                        .build();

                humiditySensorMeasure.setCreate_time(Date.from(Instant.ofEpochSecond(measure.getDt())));
                sensorMeasures.add(humiditySensorMeasure);

                SensorMeasure speedWindSensorMeasure = SensorMeasure.builder()
                        .value(measure.getSpeed().toString())
                        .sensor(speedWindSensor)
                        .sensorMeasureType(speedWindMeasureType)
                        .build();

                speedWindSensorMeasure.setCreate_time(Date.from(Instant.ofEpochSecond(measure.getDt())));
                sensorMeasures.add(speedWindSensorMeasure);
            }

            return (List<Object>) (Object) sensorMeasures;
        }

        return null;
    }

    @Override
    public boolean loadContent(String... args) {
        String filePath = args[0];

        File file = new File(filePath);

        try {
            fileStream = new Scanner(file);
        } catch (FileNotFoundException e) {
            log.error(e.getMessage());
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
