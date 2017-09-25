import com.google.gson.Gson;
import lombok.experimental.var;
import model.Sensor;
import model.SensorMeasureType;
import model.SensorSource;
import org.hibernate.Session;
import org.hibernate.Transaction;
import persistence.*;
import utils.hibernate.CustomTransation;
import utils.hibernate.HibernateUtil;
import utils.sql.JDBConnection;

import java.io.Console;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static spark.Spark.get;

public class TestHibernate {

    public static void main(String[] args) {
        foo_insert();
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
            String sql_select = "SELECT * FROM tb_sensor;";

            List<Sensor> sensors = (List<Sensor>) (Object) new SensorSQL(mysqlConn).select_sql(sql_select);
            sensors.forEach(s -> System.out.println(s.getName()));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
