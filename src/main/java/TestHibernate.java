import com.google.gson.Gson;
import deserialization.OpenWeatherJsonDeserializer;
import model.Sensor;
import model.SensorMeasureType;
import model.SensorSource;
import org.hibernate.Session;
import org.hibernate.Transaction;
import persistence.GenericJPA;
import persistence.SensorSQL;
import utils.PropertiesReader;
import utils.hibernate.CustomTransation;
import utils.hibernate.HibernateUtil;
import utils.sql.JDBConnection;
import utils.sql.SQLQueryDatabase;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TestHibernate {

    private static String _configFileName = "out/production/resources/config.properties";

    public static void a(String[] args) {
        Path path = Paths.get(_configFileName);

        if (!Files.exists(path)) {
            System.out.println("Arquivo de configurações \"config.properties\" não encontrado.");
            return;
        }

        OpenWeatherJsonDeserializer deserializer = new OpenWeatherJsonDeserializer();
        deserializer.loadContent("C:\\Users\\leobe\\Desktop\\TCC\\daily_16.json");

        deserializer.readObject();

    }

    public static void testSensorSQL()
    {
        JDBConnection jdbConnection = JDBConnection
                .builder().user(PropertiesReader.getValue("USER")).pass(PropertiesReader.getValue("PASSWORD"))
                .urlConn("jdbc:"+PropertiesReader.getValue("DATABASETYPE")+"://"+PropertiesReader.getValue("HOST")+":"+PropertiesReader.getValue("PORT")+"/"+PropertiesReader.getValue("DATABASE"))
                .classDriver(PropertiesReader.getValue("DRIVER"))
                .build();

        SensorSQL sensorSql = new SensorSQL(jdbConnection);

        try {
            List<Sensor> sensors = (List<Sensor>) (Object)sensorSql.select_sql(SQLQueryDatabase.mySqlSensorQuery);
            for(Sensor sensor: sensors)
            {
                String out = new Gson().toJson(sensor);
                System.out.println(out);
            }

            Sensor s = (Sensor) sensorSql.select_unique_sql(String.format(SQLQueryDatabase.mySqlUniqueSensorQuery, 1));

            System.out.println(new Gson().toJson(s));

            s = (Sensor) sensorSql.select_unique_sql(String.format(SQLQueryDatabase.mySqlUniqueSensorQuery, 2));

            System.out.println(new Gson().toJson(s));

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



    public static void foo_load() {
        ExecutorService executor = Executors.newFixedThreadPool(4);
        for (int i = 0; i < 1000; i++) {
            Runnable worker = () -> {
                Session session = HibernateUtil.getSessionFactory().openSession();
                Transaction transaction = session.beginTransaction();
                String out = new Gson().toJson(new GenericJPA<>(Sensor.class).findAll(new CustomTransation(session, transaction)));
                System.out.println(out);
                session.close();
            };
            executor.execute(worker);
        }
        executor.shutdown();
        while (!executor.isTerminated()) ;
        HibernateUtil.getSessionFactory().close();
    }

    public static void foo_insert() {

        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = session.beginTransaction();
        CustomTransation tx = new CustomTransation(session, transaction);

        SensorSource sensorSrc = SensorSource.builder().name("OpenIoT").description("").build();
        new GenericJPA<>(SensorSource.class).insert(tx, sensorSrc);

        Sensor sensor = Sensor.builder().name("aaaa").sensorSource(sensorSrc).build();
        new GenericJPA<>(Sensor.class).insert(tx, sensor);

        sensor = Sensor.builder().name("bbb").sensorSource(sensorSrc).build();
        new GenericJPA<>(Sensor.class).insert(tx, sensor);

        sensor = Sensor.builder().name("ccc").sensorSource(sensorSrc).build();
        new GenericJPA<>(Sensor.class).insert(tx, sensor);

        SensorMeasureType sensorMeasureType = SensorMeasureType.builder().name("ccc").build();
        new GenericJPA<>(SensorMeasureType.class).insert(tx, sensorMeasureType);

        tx.commit();
        tx.close();
        HibernateUtil.getSessionFactory().close();
        System.out.println("Insert new Values");
    }

    public static void foo_insertSQL() {

        Sensor sensor = Sensor.builder().name("aaaa").build();

        JDBConnection postgresConn = JDBConnection
                .builder().user("postgres").pass("qwe1234@")
                .urlConn("jdbc:postgresql://localhost/iot-repository")
                .classDriver("org.postgresql.Driver")
                .build();

        JDBConnection mysqlConn = JDBConnection
                .builder().user("root").pass("qwe1234@")
                .urlConn("jdbc:mysql://localhost/iotrepository")
                .classDriver("com.mysql.jdbc.Driver")
                .build();

        try {

            String sql_insert = "INSERT INTO " +
                    "public.tb_sensor" +
                    "(description, latitude, longitude, name)" +
                    " VALUES " +
                    "('', 0.0, 0.0, 'foodeu');";

            String sql_select = "SELECT s.*,ss.name as sensor_source_name,ss.description as sensor_source_description FROM tb_sensor s JOIN tb_sensor_source ss ON (s.sensor_source_id = ss.id);";

            List<Sensor> sensors = (List<Sensor>) (Object) new SensorSQL(mysqlConn).select_sql(sql_select);
            sensors.forEach(s -> System.out.println(s.getName()));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
