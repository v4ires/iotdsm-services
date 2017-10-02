package persistence;

import lombok.*;
import model.Sensor;
import model.SensorMeasureType;
import model.SensorSource;
import utils.sql.JDBConnection;
import utils.sql.SQLOperation;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

@Data
@AllArgsConstructor
@Getter
@Setter
public class SensorSQL implements SQLOperation {

    private JDBConnection jdbConn;

    @Override
    public boolean execute_sql(String sql) throws SQLException {
        try {
            Statement stmt = jdbConn.getJDBConn().createStatement();

            stmt.executeUpdate(sql);
            stmt.close();
        } catch (SQLException e) {
            throw new SQLException();
        }
        return true;
    }

    @Override
    public Object select_unique_sql(String sql) throws SQLException {
        List<Object> sensors_row = select_sql(sql);

        if(sensors_row.size() > 0)
            return sensors_row.get(0);

        return null;
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
    public List<Object> select_sql(String sql) throws SQLException {
        try {
            Statement stmt = jdbConn.getJDBConn().createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            return (List<Object>) (Object) parseSensors(rs);

        } catch (SQLException e) {
            throw e;
        }
    }

    @Override
    public boolean insert_sql(String sql) throws SQLException {
        return execute_sql(sql);
    }

    @Override
    public boolean update_sql(String sql) throws SQLException {
        return execute_sql(sql);
    }

    @Override
    public boolean delete_sql(String sql) throws SQLException {
        return execute_sql(sql);
    }
}
