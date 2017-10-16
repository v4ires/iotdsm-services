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

        ArrayList<Sensor> sensors = new ArrayList<>();

        while (rs.next()) {
            SensorSource sensor_src = SensorSource.builder()
                    .name(rs.getString("sensor_source_name"))
                    .description(rs.getString("sensor_source_description"))
                    .build();

            sensor_src.setCreate_time(rs.getDate("sensor_source_create_time"));

            sensor_src.setId(rs.getLong("sensor_source_id"));

            Sensor sensor_row = Sensor.builder()
                    .name(rs.getString("name"))
                    .description(rs.getString("description"))
                    .latitude(rs.getDouble("latitude"))
                    .longitude(rs.getDouble("longitude"))
                    .sensorSource(sensor_src)
                    .sensorMeasures(new HashSet<>())
                    .build();

            sensor_row.setCreate_time(rs.getDate("create_time"));

            sensors.add(sensor_row);
        }

        return sensors;
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
