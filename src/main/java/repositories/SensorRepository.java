package repositories;

import model.Sensor;
import model.SensorMeasureType;
import org.hibernate.Session;
import org.hibernate.Transaction;
import persistence.GenericJPA;
import persistence.SensorSQL;
import utils.PropertiesReader;
import utils.hibernate.CustomTransation;
import utils.hibernate.HibernateUtil;
import utils.sql.JDBConnection;
import utils.sql.SQLQueryDatabase;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SensorRepository {
    public Sensor getSensorById(long sensorId){
        if (Boolean.parseBoolean(PropertiesReader.getValue("USEHIBERNATE"))) {
            Session session = HibernateUtil.getSessionFactory().openSession();
            Transaction transaction = session.beginTransaction();
            Sensor sensor = new GenericJPA<>(Sensor.class).findById(new CustomTransation(session, transaction), sensorId);
            transaction.commit();
            session.close();

            return sensor;
        } else {
            JDBConnection jdbConnection = JDBConnection
                    .builder().user(PropertiesReader.getValue("USER")).pass(PropertiesReader.getValue("PASSWORD"))
                    .urlConn("jdbc:" + PropertiesReader.getValue("DATABASETYPE") + "://" + PropertiesReader.getValue("HOST") + ":" + PropertiesReader.getValue("PORT") + "/" + PropertiesReader.getValue("DATABASE"))
                    .classDriver(PropertiesReader.getValue("DRIVER"))
                    .build();

            SensorSQL sensorSql = new SensorSQL(jdbConnection);

            try {
                Sensor sensor = (Sensor) sensorSql.select_unique_sql(SQLQueryDatabase.mySqlUniqueSensorSelectQuery, sensorId);

                return sensor;
            } catch (SQLException e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    public List<Sensor> getSensors(){
        if (Boolean.parseBoolean(PropertiesReader.getValue("USEHIBERNATE"))) {
            Session session = HibernateUtil.getSessionFactory().openSession();
            Transaction transaction = session.beginTransaction();
            List<Sensor> sensors = new GenericJPA<>(Sensor.class).findAll(new CustomTransation(session, transaction));
            transaction.commit();
            session.close();

            return sensors;
        } else {
            JDBConnection jdbConnection = JDBConnection
                    .builder().user(PropertiesReader.getValue("USER")).pass(PropertiesReader.getValue("PASSWORD"))
                    .urlConn("jdbc:" + PropertiesReader.getValue("DATABASETYPE") + "://" + PropertiesReader.getValue("HOST") + ":" + PropertiesReader.getValue("PORT") + "/" + PropertiesReader.getValue("DATABASE"))
                    .classDriver(PropertiesReader.getValue("DRIVER"))
                    .build();

            SensorSQL sensorSql = new SensorSQL(jdbConnection);

            try {
                List<Sensor> sensors = (List<Sensor>) (Object) sensorSql.select_sql(SQLQueryDatabase.mySqlSensorSelectQuery);

                return sensors;
            } catch (SQLException e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    public void addSensor(Sensor sensor) {
        if (Boolean.parseBoolean(PropertiesReader.getValue("USEHIBERNATE"))) {
            Session session = HibernateUtil.getSessionFactory().openSession();
            Transaction transaction = session.beginTransaction();
            new GenericJPA<>(Sensor.class).insertOrUpdate(new CustomTransation(session, transaction), sensor);
            transaction.commit();
            session.close();

        } else {
            JDBConnection jdbConnection = JDBConnection
                    .builder().user(PropertiesReader.getValue("USER")).pass(PropertiesReader.getValue("PASSWORD"))
                    .urlConn("jdbc:" + PropertiesReader.getValue("DATABASETYPE") + "://" + PropertiesReader.getValue("HOST") + ":" + PropertiesReader.getValue("PORT") + "/" + PropertiesReader.getValue("DATABASE"))
                    .classDriver(PropertiesReader.getValue("DRIVER"))
                    .build();

            SensorSQL sensorSql = new SensorSQL(jdbConnection);

            try {
                sensorSql.insert_sql(SQLQueryDatabase.mySqlSensorInsertQuery, sensor.getDescription(), sensor.getLatitude(), sensor.getLongitude(), sensor.getName(), sensor.getSensorSource().getId());
                sensor.setId(sensorSql.get_last_generated_key());
            } catch (SQLException e) {
                e.printStackTrace();
                return;
            }

            for(SensorMeasureType smt : sensor.getSensorMeasures())
            {
                try {
                    sensorSql.insert_sql(SQLQueryDatabase.mySqlSensorSensorMeasureInsertQuery, sensor.getId(), smt.getId());
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
