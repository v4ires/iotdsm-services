import com.google.gson.Gson;
import model.Sensor;
import org.hibernate.Session;
import org.hibernate.Transaction;
import persistence.SensorJPA;
import persistence.SensorSQL;
import utils.CustomTransation;
import utils.HibernateUtil;
import utils.JDBConnection;

import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static spark.Spark.get;

public class TestHibernate {

    public static void main(String[] args) {
        foo_insertSQL();
    }

    public static void foo_load() {
        ExecutorService executor = Executors.newFixedThreadPool(4);
        for (int i = 0; i < 1000; i++) {
            Runnable worker = () -> {
                Session session = HibernateUtil.getSessionFactory().openSession();
                Transaction transaction = session.beginTransaction();
                String out = new Gson().toJson(new SensorJPA().findAll(new CustomTransation(session, transaction)));
                System.out.println(out);
                session.close();
            };
            executor.execute(worker);
        }
        executor.shutdown();
        while (!executor.isTerminated()) ;
        HibernateUtil.getSessionFactory().close();
    }

    public static void foo_rest() {
        get("/hello", (req, res) -> {
            Session session = HibernateUtil.getSessionFactory().openSession();
            Transaction transaction = session.beginTransaction();
            CustomTransation tx = new CustomTransation(session, transaction);
            List<Sensor> sensors = new SensorJPA().findAll(tx);
            tx.close();
            return new Gson().toJson(sensors);
        });
    }

    public static void foo_insert() {

        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = session.beginTransaction();
        CustomTransation tx = new CustomTransation(session, transaction);

        Sensor sensor = Sensor.builder().name("aaaa").build();
        new SensorJPA().insert(tx, sensor);

        sensor = Sensor.builder().name("bbb").build();
        new SensorJPA().insert(tx, sensor);

        sensor = Sensor.builder().name("ccc").build();
        new SensorJPA().insert(tx, sensor);

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

            List<Sensor> sensors = (List<Sensor>) (Object) new SensorSQL().select_sql(sql_select, postgresConn);
            sensors.forEach(s -> System.out.println(s.getName()));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
