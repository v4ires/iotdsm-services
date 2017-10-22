package utils.sql;

public class SQLQueryDatabase {
    public static String mySqlUniqueSensorSelectQuery = "SELECT s.*, ss.name as sensor_source_name,ss.description as sensor_source_description,ss.create_time as sensor_source_create_time FROM tb_sensor s LEFT JOIN tb_sensor_source ss ON (s.sensor_source_id = ss.id) WHERE s.id = ? LIMIT 1;";
    public static String mySqlUniqueSensorSourceSelectQuery = "SELECT * FROM tb_sensor_source WHERE id = ?";
    public static String mySqlSensorSourceSelectQuery = "SELECT * FROM tb_sensor_source";
    public static String mySqlSensorMeasureTypeBySensorSelectQuery = "SELECT * FROM tb_sensor_has_sensor_measure_type ssmt JOIN tb_sensor_measure_type smt ON (smt.id = ssmt.sensor_measure_type_id) WHERE ssmt.sensor_id = ?;";
    public static String mySqlUniqueSensorMeasureSelectQuery = "SELECT * FROM tb_sensor_measure WHERE id = ? LIMIT 1;";
    public static String mySqlSensorMeasureByDateAndSensorSelectQuery = "SELECT * FROM tb_sensor_measure WHERE sensor_id = ? AND sensor_measure_type_id = ? AND create_time >= ? AND create_time <= ?";
    public static String mySqlSensorSelectQuery = "SELECT s.*, ss.name as sensor_source_name,ss.description as sensor_source_description,ss.create_time as sensor_source_create_time FROM tb_sensor s LEFT JOIN tb_sensor_source ss ON (s.sensor_source_id = ss.id)";
    public static String mySqlSensorSourceInsertQuery = "INSERT INTO tb_sensor_source (description, name) VALUES (?, ?)";
    public static String mySqlSensorInsertQuery = "INSERT INTO tb_sensor (description, latitude, longitude, name, sensor_source_id) VALUES (?, ?, ?, ?, ?)";
    public static String mySqlSensorSensorMeasureInsertQuery = "INSERT INTO tb_sensor_has_sensor_measure_type (sensor_id, sensor_measure_type_id) VALUES (?, ?)";
    public static String mySqlSensorMeasureInsertQuery = "INSERT INTO tb_sensor_measure (sensor_id, value, sensor_measure_type_id, create_time) VALUES (?,?,?,?)";
    public static String mySqlSensorMeasureTypeInsertQuery = "INSERT INTO tb_sensor_measure_type (name, unit) VALUES (?,?)";
}
