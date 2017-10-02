package utils.sql;

import java.sql.ResultSet;

public class SQLQueryDatabase {
    public static String mySqlUniqueSensorQuery = "SELECT s.*, ss.name as sensor_source_name,ss.description as sensor_source_description,smt.id as sensor_measure_type_id,smt.name as sensor_measure_type_name,smt.unit as sensor_measure_type_unit FROM tb_sensor_has_sensor_measure_type ssmt JOIN tb_sensor_measure_type smt ON (smt.id = ssmt.sensor_measure_type_id) JOIN tb_sensor s ON (s.id = ssmt.sensor_id) JOIN tb_sensor_source ss ON (s.sensor_source_id = ss.id) WHERE ssmt.sensor_id = %d LIMIT 1;";
    public static String mySqlSensorQuery = "SELECT s.*, ss.name as sensor_source_name,ss.description as sensor_source_description,smt.id as sensor_measure_type_id,smt.name as sensor_measure_type_name,smt.unit as sensor_measure_type_unit FROM tb_sensor_has_sensor_measure_type ssmt JOIN tb_sensor_measure_type smt ON (smt.id = ssmt.sensor_measure_type_id) JOIN tb_sensor s ON (s.id = ssmt.sensor_id) JOIN tb_sensor_source ss ON (s.sensor_source_id = ss.id);";

}
