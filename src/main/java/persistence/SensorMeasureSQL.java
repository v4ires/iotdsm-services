package persistence;

import lombok.Data;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import model.SensorMeasure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.sql.JDBConnection;
import utils.sql.SQLOperation;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * University of São Paulo
 * IoT Repository Module
 *
 * @author Vinícius Aires Barros <viniciusaires@usp.br>
 */
@Data
@Getter
@Setter
public class SensorMeasureSQL implements SQLOperation {

    private static final Logger log = LoggerFactory.getLogger(SensorMeasureSQL.class);


    @NonNull
    private JDBConnection jdbConn;

    private Long lastInsertedId;

    /**
     *
     */
    public SensorMeasureSQL(JDBConnection jdbConn) {
        this.jdbConn = jdbConn;
    }

    /**
     *
     */
    @Override
    public boolean execute_sql(String sql, Object... params) throws SQLException {
        try {
            PreparedStatement stmt = jdbConn.getJDBConn().prepareStatement(sql);

            if (params != null) {
                for (int i = 0; i < params.length; i++)
                    stmt.setObject(i + 1, params[i]);
            }

            stmt.executeUpdate();

            ResultSet generatedKeys = stmt.getGeneratedKeys();

            if (generatedKeys.next()) {
                lastInsertedId = generatedKeys.getLong(1);
            }

            stmt.close();
        } catch (SQLException e) {
            log.error(e.getMessage());
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     *
     */
    @Override
    public Object select_unique_sql(String sql, Object... params) throws SQLException {
        List<Object> sensors_row = select_sql(sql, params);

        if (sensors_row.size() > 0)
            return sensors_row.get(0);

        return null;
    }

    /**
     *
     */
    public List<SensorMeasure> parseSensorMeasures(ResultSet rs) throws SQLException {

        ArrayList<SensorMeasure> sensors = new ArrayList<>();

        while (rs.next()) {

            SensorMeasure sensor_row = SensorMeasure.builder()
                    .value(rs.getString("value"))
                    .build();
            sensor_row.setId(rs.getLong("id"));
            sensor_row.setCreate_time(rs.getTimestamp("create_time"));

            sensors.add(sensor_row);
        }

        return sensors;
    }

    /**
     *
     */
    @Override
    public List<Object> select_sql(String sql, Object... params) throws SQLException {
        try {
            PreparedStatement stmt = jdbConn.getJDBConn().prepareStatement(sql);

            if (params != null) {
                for (int i = 0; i < params.length; i++)
                    stmt.setObject(i + 1, params[i]);
            }

            ResultSet rs = stmt.executeQuery();

            return (List<Object>) (Object) parseSensorMeasures(rs);

        } catch (SQLException e) {
            log.error(e.getMessage());
            throw e;
        }
    }

    /**
     *
     */
    @Override
    public boolean insert_sql(String sql, Object... params) throws SQLException {
        return execute_sql(sql, params);
    }

    /**
     *
     */
    @Override
    public boolean update_sql(String sql, Object... params) throws SQLException {
        return execute_sql(sql, params);
    }

    /**
     *
     */
    @Override
    public boolean delete_sql(String sql, Object... params) throws SQLException {
        return execute_sql(sql, params);
    }

    /**
     *
     */
    @Override
    public Long get_last_generated_key() {
        return lastInsertedId;
    }

    /**
     *
     */
    @Override
    public void close() {
        if (jdbConn != null)
            jdbConn.close();
    }
}
