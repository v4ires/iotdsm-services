package deserialization;

import com.google.gson.Gson;
import deserialization.openweather.OpenWeatherEntry;
import deserialization.openweather.SensorData;
import model.Sensor;
import model.SensorMeasure;
import model.SensorMeasureType;
import model.SensorSource;

import java.io.File;
import java.io.FileNotFoundException;
import java.time.Instant;
import java.util.*;

public class OpenWeatherJsonDeserializer implements IDeserializer {
    private Scanner fileStream;

    @Override
    public Object readObject() {
        return null;
    }

    @Override
    public List<Object> readArray() {
        String line = fileStream.nextLine();

        List<SensorMeasure> sensorMeasures = new ArrayList<>();

        if(line != null && !line.equals("")) {
            OpenWeatherEntry result = new Gson().fromJson(line, OpenWeatherEntry.class);

            SensorSource sensorSource = SensorSource.builder()
                    .name("OpenWeatherMap")
                    .build();

            Sensor tempSensor = Sensor.builder()
                    .name("temperature sensor - "+result.getCity().getName())
                    .latitude(result.getCity().getCoord().getLat())
                    .longitude(result.getCity().getCoord().getLon())
                    .sensorSource(sensorSource)
                    .sensorMeasures(new HashSet<>())
                    .build();

            SensorMeasureType tempMeasureType = SensorMeasureType.builder()
                    .name("temperature")
                    .unit("K")
                    .build();

            Sensor pressureSensor = Sensor.builder()
                    .name("pressure sensor - "+result.getCity().getName())
                    .latitude(result.getCity().getCoord().getLat())
                    .longitude(result.getCity().getCoord().getLon())
                    .sensorSource(sensorSource)
                    .sensorMeasures(new HashSet<>())
                    .build();

            SensorMeasureType pressMeasureType = SensorMeasureType.builder()
                    .name("pressure")
                    .unit("hPa")
                    .build();

           Sensor humiditySensor = Sensor.builder()
                    .name("humidity sensor - "+result.getCity().getName())
                    .latitude(result.getCity().getCoord().getLat())
                    .longitude(result.getCity().getCoord().getLon())
                    .sensorSource(sensorSource)
                    .sensorMeasures(new HashSet<>())
                    .build();

            SensorMeasureType humidityMeasureType = SensorMeasureType.builder()
                    .name("humidity")
                    .unit("%")
                    .build();

           Sensor speedWindSensor = Sensor.builder()
                    .name("speed_wind sensor - "+result.getCity().getName())
                    .latitude(result.getCity().getCoord().getLat())
                    .longitude(result.getCity().getCoord().getLon())
                    .sensorSource(sensorSource)
                    .sensorMeasures(new HashSet<>())
                    .build();

            SensorMeasureType speedWindMeasureType = SensorMeasureType.builder()
                    .name("speed_wind")
                    .unit("m/s")
                    .build();

            for(SensorData measure : result.getData())
            {
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

            return (List<Object>)(Object)sensorMeasures;
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
            return false;
        }

        return true;
    }
}
