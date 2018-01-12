package edu.usp.icmc.lasdpc.utils.sql;

public class SQLQueryDatabase {

    //SQL Querys
    public static String sqlUniqueSensorSelectQuery = "SELECT s.*, ss.name as sensor_source_name,ss.description as sensor_source_description,ss.create_time as sensor_source_create_time FROM tb_sensor s LEFT JOIN tb_sensor_source ss ON (s.sensor_source_id = ss.id) WHERE s.id = ? LIMIT 1;";
    public static String sqlUniqueSensorSourceSelectQuery = "SELECT * FROM tb_sensor_source WHERE id = ?";
    public static String sqlSensorSourceSelectQuery = "SELECT * FROM tb_sensor_source";
    public static String sqlSensorMeasureTypeBySensorSelectQuery = "SELECT * FROM tb_sensor_has_sensor_measure_type ssmt JOIN tb_sensor_measure_type smt ON (smt.id = ssmt.sensor_measure_type_id) WHERE ssmt.sensor_id = ?;";
    public static String sqlUniqueSensorMeasureSelectQuery = "SELECT * FROM tb_sensor_measure WHERE id = ? LIMIT 1;";
    public static String sqlSensorMeasureByDateAndSensorSelectQuery = "SELECT * FROM tb_sensor_measure WHERE sensor_id = ? AND sensor_measure_type_id = ? AND create_time >= ? AND create_time <= ?";
    public static String sqlSensorSelectQuery = "SELECT s.*, ss.name as sensor_source_name,ss.description as sensor_source_description,ss.create_time as sensor_source_create_time FROM tb_sensor s LEFT JOIN tb_sensor_source ss ON (s.sensor_source_id = ss.id)";
    public static String sqlSensorSelectWithLimitAndOffsetQuery = "SELECT s.*, ss.name as sensor_source_name,ss.description as sensor_source_description,ss.create_time as sensor_source_create_time FROM tb_sensor s LEFT JOIN tb_sensor_source ss ON (s.sensor_source_id = ss.id) ORDER BY id LIMIT ? OFFSET ?";
    public static String sqlSensorSelectWithOffsetQuery = "SELECT s.*, ss.name as sensor_source_name,ss.description as sensor_source_description,ss.create_time as sensor_source_create_time FROM tb_sensor s LEFT JOIN tb_sensor_source ss ON (s.sensor_source_id = ss.id) ORDER BY id LIMIT 18446744073709551615 OFFSET ?";
    public static String sqlSensorSourceInsertQuery = "INSERT INTO tb_sensor_source (description, name) VALUES (?, ?)";
    public static String sqlSensorInsertQuery = "INSERT INTO tb_sensor (description, latitude, longitude, name, sensor_source_id) VALUES (?, ?, ?, ?, ?)";
    public static String sqlSensorSensorMeasureInsertQuery = "INSERT INTO tb_sensor_has_sensor_measure_type (sensor_id, sensor_measure_type_id) VALUES (?, ?)";
    public static String sqlSensorMeasureInsertQuery = "INSERT INTO tb_sensor_measure (sensor_id, value, sensor_measure_type_id, create_time) VALUES (?,?,?,?)";
    public static String sqlSensorMeasureTypeInsertQuery = "INSERT INTO tb_sensor_measure_type (name, unit) VALUES (?,?)";
}
