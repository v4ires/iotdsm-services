package drivers;

import model.Sensor;
import model.SensorMeasure;
import model.SensorMeasureType;
import model.SensorSource;
import persistence.SensorSQL;
import utils.sql.JDBConnection;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

public class JDBCMySQLDriver implements DatabaseDriver {

    private JDBConnection _mysqlConn;

    public JDBCMySQLDriver() {
        _mysqlConn = JDBConnection
                .builder().user("root").pass("qwe1234@")
                .urlConn("jdbc:mysql://localhost/iotrepository")
                .classDriver("com.mysql.jdbc.Driver")
                .build();
    }

    public List<Sensor> parseSensors(ResultSet rs) throws SQLException {

        HashMap<Long, Sensor> sensors = new HashMap<>();

        while (rs.next()) {
            SensorSource sensor_src = SensorSource.builder()
                    .name(rs.getString("sensor_source_name"))
                    .description(rs.getString("sensor_source_description"))
                    .build();

            sensor_src.setId(rs.getLong("sensor_source_id"));

            SensorMeasureType sensor_measure_type = SensorMeasureType.builder()
                    .name(rs.getString("sensor_measure_type_name"))
                    .unit(rs.getString("sensor_measure_type_unit"))
                    .build();

            sensor_measure_type.setId(rs.getLong("sensor_measure_type_id"));

            Sensor sensor_row = Sensor.builder()
                    .name(rs.getString("name"))
                    .description(rs.getString("description"))
                    .latitude(rs.getDouble("latitude"))
                    .longitude(rs.getDouble("longitude"))
                    .sensorSource(sensor_src)
                    .sensorMeasures(new HashSet<>())
                    .build();

            sensor_row.getSensorMeasures().add(sensor_measure_type);
            sensor_row.setId(rs.getLong("id"));

            if(!sensors.containsKey(sensor_row.getId()))
                sensors.put(sensor_row.getId(), sensor_row);
            else
                sensors.get(sensor_row.getId()).getSensorMeasures().add(sensor_measure_type);
        }

        return new ArrayList<>(sensors.values());
    }

    @Override
    public Sensor getSensorById(long id) {
        try {
            Statement stmt = _mysqlConn.getJDBConn().createStatement();
            ResultSet rs = stmt.executeQuery("SELECT s.*, ss.name as sensor_source_name,ss.description as sensor_source_description,smt.id as sensor_measure_type_id,smt.name as sensor_measure_type_name,smt.unit as sensor_measure_type_unit FROM tb_sensor_has_sensor_measure_type ssmt JOIN tb_sensor_measure_type smt ON (smt.id = ssmt.sensor_measure_type_id) JOIN tb_sensor s ON (s.id = ssmt.sensor_id) JOIN tb_sensor_source ss ON (s.sensor_source_id = ss.id) WHERE ssmt.sensor_id = "+id+" LIMIT 1;");

            List<Sensor> sensorList = parseSensors(rs);

            if(sensorList.size() == 0)
                return null;

            return parseSensors(rs).get(0);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<Sensor> getSensors() {
        try {
            Statement stmt = _mysqlConn.getJDBConn().createStatement();
            ResultSet rs = stmt.executeQuery("SELECT s.*, ss.name as sensor_source_name,ss.description as sensor_source_description,smt.id as sensor_measure_type_id,smt.name as sensor_measure_type_name,smt.unit as sensor_measure_type_unit FROM tb_sensor_has_sensor_measure_type ssmt JOIN tb_sensor_measure_type smt ON (smt.id = ssmt.sensor_measure_type_id) JOIN tb_sensor s ON (s.id = ssmt.sensor_id) JOIN tb_sensor_source ss ON (s.sensor_source_id = ss.id);");

            return parseSensors(rs);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<SensorMeasure> getSensorMeasuresOnInterval(Sensor sensor, SensorMeasureType sensorMeasureType, Date startDate, Date endDate) {
        return null;
    }

    @Override
    public boolean addSensor(Sensor sensor) {
        return false;
    }

    @Override
    public boolean addSensorMeasure(SensorMeasure sensorMeasure) {
        return false;
    }

    @Override
    public boolean deleteSensor(Sensor sensor) {
        return false;
    }
}
