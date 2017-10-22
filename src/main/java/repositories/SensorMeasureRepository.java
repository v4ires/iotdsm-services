package repositories;

import model.Sensor;
import model.SensorMeasure;
import org.hibernate.Session;
import org.hibernate.Transaction;
import persistence.GenericJPA;
import persistence.SensorMeasureSQL;
import persistence.SensorSQL;
import utils.PropertiesReader;
import utils.hibernate.CustomTransation;
import utils.hibernate.HibernateUtil;
import utils.sql.JDBConnection;
import utils.sql.SQLQueryDatabase;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class SensorMeasureRepository {
    public SensorMeasure getSensorMeasureById(long sensorMeasureId){
        if (Boolean.parseBoolean(PropertiesReader.getValue("USEHIBERNATE"))) {
            Session session = HibernateUtil.getSessionFactory().openSession();
            Transaction transaction = session.beginTransaction();
            SensorMeasure sensorMeasure = new GenericJPA<>(SensorMeasure.class).findById(new CustomTransation(session, transaction), sensorMeasureId);
            transaction.commit();
            session.close();

            return sensorMeasure;
        } else {
            JDBConnection jdbConnection = JDBConnection
                    .builder().user(PropertiesReader.getValue("USER")).pass(PropertiesReader.getValue("PASSWORD"))
                    .urlConn("jdbc:" + PropertiesReader.getValue("DATABASETYPE") + "://" + PropertiesReader.getValue("HOST") + ":" + PropertiesReader.getValue("PORT") + "/" + PropertiesReader.getValue("DATABASE"))
                    .classDriver(PropertiesReader.getValue("DRIVER"))
                    .build();

            SensorMeasureSQL sensorMeasureSql = new SensorMeasureSQL(jdbConnection);

            try {
                SensorMeasure sensorMeasure = (SensorMeasure) sensorMeasureSql.select_unique_sql(String.format(SQLQueryDatabase.mySqlUniqueSensorMeasureSelectQuery, sensorMeasureId));

                return sensorMeasure;
            } catch (SQLException e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    public List<SensorMeasure> getSensorMeasure(long sensorId, long measureTypeId, Date startDate, Date endDate){
        if (Boolean.parseBoolean(PropertiesReader.getValue("USEHIBERNATE"))) {
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            Session session = HibernateUtil.getSessionFactory().openSession();
            Transaction transaction = session.beginTransaction();
            List<SensorMeasure> sensors = new GenericJPA<>(SensorMeasure.class).resultList(new CustomTransation(session, transaction), "FROM SensorMeasure WHERE sensor.id="+sensorId+" AND sensorMeasureType.id="+measureTypeId+" AND create_time >= '"+df.format(startDate)+"' AND create_time <= '"+df.format(endDate)+"'");
            transaction.commit();
            session.close();

            return sensors;
        } else {
            JDBConnection jdbConnection = JDBConnection
                    .builder().user(PropertiesReader.getValue("USER")).pass(PropertiesReader.getValue("PASSWORD"))
                    .urlConn("jdbc:" + PropertiesReader.getValue("DATABASETYPE") + "://" + PropertiesReader.getValue("HOST") + ":" + PropertiesReader.getValue("PORT") + "/" + PropertiesReader.getValue("DATABASE"))
                    .classDriver(PropertiesReader.getValue("DRIVER"))
                    .build();

            SensorMeasureSQL sensorMeasureSql = new SensorMeasureSQL(jdbConnection);

            try {
                List<SensorMeasure> sensors = (List<SensorMeasure>) (Object) sensorMeasureSql.select_sql(SQLQueryDatabase.mySqlSensorMeasureByDateAndSensorSelectQuery, sensorId, measureTypeId, startDate, endDate);

                return sensors;
            } catch (SQLException e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    public void addSensorMeasure(SensorMeasure sensorMeasure) {
        if (Boolean.parseBoolean(PropertiesReader.getValue("USEHIBERNATE"))) {
            Session session = HibernateUtil.getSessionFactory().openSession();
            Transaction transaction = session.beginTransaction();
            new GenericJPA<>(SensorMeasure.class).insert(new CustomTransation(session, transaction), sensorMeasure);
            transaction.commit();
            session.close();
        } else {
            JDBConnection jdbConnection = JDBConnection
                    .builder().user(PropertiesReader.getValue("USER")).pass(PropertiesReader.getValue("PASSWORD"))
                    .urlConn("jdbc:" + PropertiesReader.getValue("DATABASETYPE") + "://" + PropertiesReader.getValue("HOST") + ":" + PropertiesReader.getValue("PORT") + "/" + PropertiesReader.getValue("DATABASE"))
                    .classDriver(PropertiesReader.getValue("DRIVER"))
                    .build();

            SensorMeasureSQL sensorMeasureSql = new SensorMeasureSQL(jdbConnection);

            try {
                sensorMeasureSql.insert_sql(SQLQueryDatabase.mySqlSensorMeasureInsertQuery, sensorMeasure.getSensor().getId(), sensorMeasure.getValue(), sensorMeasure.getSensorMeasureType().getId(), sensorMeasure.getCreate_time());
                sensorMeasure.setId(sensorMeasureSql.get_last_generated_key());
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
