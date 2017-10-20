package utils.sql;

public class SQLQueryDatabase {
    public static String mySqlUniqueSensorQuery = "SELECT s.*, ss.name as sensor_source_name,ss.description as sensor_source_description,ss.create_time as sensor_source_create_time FROM tb_sensor s LEFT JOIN tb_sensor_source ss ON (s.sensor_source_id = ss.id) WHERE s.id = %d LIMIT 1;";
    public static String mySqlSensorQuery = "SELECT s.*, ss.name as sensor_source_name,ss.description as sensor_source_description,ss.create_time as sensor_source_create_time FROM tb_sensor s LEFT JOIN tb_sensor_source ss ON (s.sensor_source_id = ss.id)";
}
