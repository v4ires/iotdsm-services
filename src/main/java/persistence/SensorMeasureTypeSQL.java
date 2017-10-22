package persistence;

import lombok.Data;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import model.SensorMeasure;
import model.SensorMeasureType;
import utils.sql.JDBConnection;
import utils.sql.SQLOperation;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Data
@Getter
@Setter
public class SensorMeasureTypeSQL implements SQLOperation {

    @NonNull
    private JDBConnection jdbConn;

    private Long lastInsertedId;

    public SensorMeasureTypeSQL(JDBConnection jdbConn) {
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

    public List<SensorMeasureType> parseSensorMeasureTypes(ResultSet rs) throws SQLException {

        ArrayList<SensorMeasureType> sensorMeasureTypes = new ArrayList<>();

        while (rs.next()) {

            SensorMeasureType sensor_row = SensorMeasureType.builder()
                    .name(rs.getString("name"))
                    .unit(rs.getString("unit"))
                    .build();
            sensor_row.setId(rs.getLong("id"));
            sensor_row.setCreate_time(rs.getTimestamp("create_time"));

            sensorMeasureTypes.add(sensor_row);
        }

        return sensorMeasureTypes;
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

            return (List<Object>) (Object) parseSensorMeasureTypes(rs);

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
