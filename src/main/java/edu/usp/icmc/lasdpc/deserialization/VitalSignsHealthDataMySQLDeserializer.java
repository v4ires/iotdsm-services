package edu.usp.icmc.lasdpc.deserialization;

import edu.usp.icmc.lasdpc.model.Sensor;
import edu.usp.icmc.lasdpc.model.SensorMeasure;
import edu.usp.icmc.lasdpc.model.SensorMeasureType;
import edu.usp.icmc.lasdpc.model.SensorSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.*;
import java.util.Date;

public class VitalSignsHealthDataMySQLDeserializer implements IDeserializer {

    private static final Logger log = LoggerFactory.getLogger(VitalSignsHealthDataMySQLDeserializer.class);

    private Connection conn;
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

        sensorSource = SensorSource.builder()
                .name("Health Data")
                .description("").build();

        System.out.println("Select Measures Types");
        //Measures Types
        try {
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery("SELECT DISTINCT DSC_PARAMETRO_MONITORIZACAO FROM parametro_monitorizacao;");
            while (rs.next()) {
                String value = rs.getString("DSC_PARAMETRO_MONITORIZACAO");
                SensorMeasureType measureType = SensorMeasureType.builder()
                        .name(value)
                        .unit("")
                        .build();
                measureType.setCreate_time(new Date());
                measureTypes.add(measureType);
            }
            st.close();
        } catch (Exception e) {
            log.error(e.getMessage());
        }

        System.out.println("Select Sensors");
        //Sensors
        try {
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery("SELECT DISTINCT NUM_USER_BANCO, DSC_PARAMETRO_MONITORIZACAO FROM parametro_monit_paciente INNER JOIN parametro_monitorizacao ON parametro_monit_paciente.SEQ_PARAMETRO_MONITORIZACAO = parametro_monitorizacao.SEQ_PARAMETRO_MONITORIZACAO;");
            while (rs.next()) {
                String num_user_banco = rs.getString("NUM_USER_BANCO");
                String dsc_parametro_monitorizacao = rs.getString("DSC_PARAMETRO_MONITORIZACAO");

                Sensor sensor = Sensor.builder().name(num_user_banco + " - " + dsc_parametro_monitorizacao)
                        .sensorSource(sensorSource)
                        .description("")
                        .latitude(0.0f)
                        .longitude(0.0f)
                        .sensorMeasures(new HashSet<>()).build();
                sensor.setCreate_time(new Date());

                Optional<SensorMeasureType> measureType = measureTypes.stream().filter(sensorMeasureType -> sensorMeasureType.getName().equals(dsc_parametro_monitorizacao)).findFirst();

                if (measureType.isPresent()) {
                    sensor.getSensorMeasures().add(measureType.get());
                    sensors.add(sensor);
                }
            }
            st.close();
        } catch (Exception e) {
            log.error(e.getMessage());
        }

        System.out.println("Select Sensor Measures");
        //Sensor Measure
        try {
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM parametro_monit_paciente INNER JOIN parametro_monitorizacao ON parametro_monit_paciente.SEQ_PARAMETRO_MONITORIZACAO = parametro_monitorizacao.SEQ_PARAMETRO_MONITORIZACAO;");
            while (rs.next()) {
                String vlr_parametro_monitorizacao = rs.getString("VLR_PARAMETRO_MONITORIZACAO");
                String dsc_parametro_monitorizacao = rs.getString("DSC_PARAMETRO_MONITORIZACAO");

                Optional<Sensor> sensor_x = sensors.stream().filter(sensor -> sensor.getName().contains(dsc_parametro_monitorizacao)).findFirst();
                Optional<SensorMeasureType> sensor_measuretype_x = measureTypes.stream().filter(sensor_type -> sensor_type.getName().equals(dsc_parametro_monitorizacao)).findFirst();

                if (sensor_x.isPresent() && sensor_measuretype_x.isPresent()) {

                    SensorMeasure sensorMeasure = SensorMeasure.builder()
                            .value(vlr_parametro_monitorizacao)
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

        System.out.println("Sensor Measures Size: " + sensorMeasures.size());
        return (List<Object>) (Object) sensorMeasures;
    }

    @Override
    public boolean loadContent(String... args) {
        String myDriver = "com.mysql.cj.jdbc.Driver";
        String myUrl = "jdbc:mysql://localhost/final_trauma_v2";
        try {
            Class.forName(myDriver);
            conn = DriverManager.getConnection(myUrl, "root", "qwe1234@");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return true;
    }

    @Override
    public void close() {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
