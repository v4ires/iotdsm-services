package persistence;

import lombok.Data;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import model.Sensor;
import model.SensorSource;
import utils.sql.JDBConnection;
import utils.sql.SQLOperation;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@Data
@Getter
@Setter
public class SensorSQL implements SQLOperation {

    @NonNull
    private JDBConnection jdbConn;

    private Long lastInsertedId;

    public SensorSQL(JDBConnection jdbConn) {
        this.jdbConn = jdbConn;
    }

    @Override
    public boolean execute_sql(String sql, Object... params) throws SQLException {
        try {
            PreparedStatement stmt = jdbConn.getJDBConn().prepareStatement(sql);

            if(params != null) {
                for(int i = 0;i < params.length;i++)
                    stmt.setObject(i+1, params[i]);
            }

            stmt.executeUpdate();

            ResultSet generatedKeys = stmt.getGeneratedKeys();

            if(generatedKeys.next()) {
                lastInsertedId = generatedKeys.getLong(1);
            }

            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public Object select_unique_sql(String sql, Object... params) throws SQLException {
        List<Object> sensors_row = select_sql(sql, params);

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

            sensor_src.setCreate_time(rs.getTimestamp("sensor_source_create_time"));

            sensor_src.setId(rs.getLong("sensor_source_id"));

            Sensor sensor_row = Sensor.builder()
                    .name(rs.getString("name"))
                    .description(rs.getString("description"))
                    .latitude(rs.getDouble("latitude"))
                    .longitude(rs.getDouble("longitude"))
                    .sensorSource(sensor_src)
                    .sensorMeasures(new HashSet<>())
                    .build();

            sensor_row.setCreate_time(rs.getTimestamp("create_time"));

            sensors.add(sensor_row);
        }

        return sensors;
    }

    @Override
    public List<Object> select_sql(String sql, Object... params) throws SQLException {
        try {
            PreparedStatement stmt = jdbConn.getJDBConn().prepareStatement(sql);

            if(params != null) {
                for(int i = 0;i < params.length;i++)
                    stmt.setObject(i+1, params[i]);
            }

            ResultSet rs = stmt.executeQuery();

            return (List<Object>) (Object) parseSensors(rs);

        } catch (SQLException e) {
            throw e;
        }
    }

    @Override
    public boolean insert_sql(String sql, Object... params) throws SQLException {
        return execute_sql(sql, params);
    }

    @Override
    public boolean update_sql(String sql, Object... params) throws SQLException {
        return execute_sql(sql, params);
    }

    @Override
    public boolean delete_sql(String sql, Object... params) throws SQLException {
        return execute_sql(sql, params);
    }

    @Override
    public Long get_last_generated_key() {
        return lastInsertedId;
    }
}
