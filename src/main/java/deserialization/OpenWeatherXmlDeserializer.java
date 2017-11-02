package deserialization;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import deserialization.openweather.OpenWeatherEntry;
import deserialization.openweather.SensorData;
import deserialization.openweather.Weather;
import model.Sensor;
import model.SensorMeasure;
import model.SensorMeasureType;
import model.SensorSource;

import java.io.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

/**
 * University of São Paulo
 * IoT Repository Module
 * @author Vinícius Aires Barros <viniciusaires7@gmail.com>
 */
public class OpenWeatherXmlDeserializer implements IDeserializer {

    private SensorSource sensorSource;

    private SensorMeasureType tempMeasureType;

    private SensorMeasureType pressMeasureType;

    private SensorMeasureType humidityMeasureType;

    private SensorMeasureType speedWindMeasureType;

    private XStream xstream;

    private ObjectInputStream objectInputStream;

    private InputStream inputStream;

    @Override
    public Object readObject() {
        return null;
    }

    @Override
    public List<Object> readArray() {

        List<SensorMeasure> sensorMeasures = new ArrayList<>();

        OpenWeatherEntry result = null;

        try {
            result = (OpenWeatherEntry) objectInputStream.readObject();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

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

    @Override
    public boolean loadContent(String... args) {
        String filePath = args[0];
        xstream = new XStream(new DomDriver());

        xstream.alias("rowEntry", OpenWeatherEntry.class);
        xstream.alias("entry", SensorData.class);
        xstream.alias("weatherEntry", Weather.class);

        File file = new File(filePath);

        try {
            inputStream = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            return false;
        }

        try {
            objectInputStream = xstream.createObjectInputStream(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
            this.close();
            return false;
        }
        return true;
    }

    @Override
    public void close() {
        if (objectInputStream != null) {
            try {
                objectInputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (inputStream != null) {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
