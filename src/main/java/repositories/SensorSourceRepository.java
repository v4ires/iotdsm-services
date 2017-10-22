package repositories;

import model.Sensor;
import model.SensorSource;
import org.hibernate.Session;
import org.hibernate.Transaction;
import persistence.GenericJPA;
import persistence.SensorSQL;
import persistence.SensorSourceSQL;
import utils.PropertiesReader;
import utils.hibernate.CustomTransation;
import utils.hibernate.HibernateUtil;
import utils.sql.JDBConnection;
import utils.sql.SQLQueryDatabase;

import java.sql.SQLException;
import java.util.List;

public class SensorSourceRepository {
    public List<SensorSource> getSensorSources()
    {
        if (Boolean.parseBoolean(PropertiesReader.getValue("USEHIBERNATE"))) {
            Session session = HibernateUtil.getSessionFactory().openSession();
            Transaction transaction = session.beginTransaction();
            List<SensorSource> sensorSources = new GenericJPA<>(SensorSource.class).findAll(new CustomTransation(session, transaction));
            transaction.commit();
            session.close();

            return sensorSources;
        } else {
            JDBConnection jdbConnection = JDBConnection
                    .builder().user(PropertiesReader.getValue("USER")).pass(PropertiesReader.getValue("PASSWORD"))
                    .urlConn("jdbc:" + PropertiesReader.getValue("DATABASETYPE") + "://" + PropertiesReader.getValue("HOST") + ":" + PropertiesReader.getValue("PORT") + "/" + PropertiesReader.getValue("DATABASE"))
                    .classDriver(PropertiesReader.getValue("DRIVER"))
                    .build();

            SensorSourceSQL sensorSourceSql = new SensorSourceSQL(jdbConnection);

            try {
                List<SensorSource> sensorSources = (List<SensorSource>)(Object) sensorSourceSql.select_sql(SQLQueryDatabase.mySqlSensorSourceSelectQuery);

                return sensorSources;
            } catch (SQLException e) {
                e.printStackTrace();
                return null;
            }
        }
    }
    public SensorSource getSensorSourceById(long sensorSourceId){
        if (Boolean.parseBoolean(PropertiesReader.getValue("USEHIBERNATE"))) {
            Session session = HibernateUtil.getSessionFactory().openSession();
            Transaction transaction = session.beginTransaction();
            SensorSource sensorSource = new GenericJPA<>(SensorSource.class).findById(new CustomTransation(session, transaction), sensorSourceId);
            transaction.commit();
            session.close();

            return sensorSource;
        } else {
            JDBConnection jdbConnection = JDBConnection
                    .builder().user(PropertiesReader.getValue("USER")).pass(PropertiesReader.getValue("PASSWORD"))
                    .urlConn("jdbc:" + PropertiesReader.getValue("DATABASETYPE") + "://" + PropertiesReader.getValue("HOST") + ":" + PropertiesReader.getValue("PORT") + "/" + PropertiesReader.getValue("DATABASE"))
                    .classDriver(PropertiesReader.getValue("DRIVER"))
                    .build();

            SensorSourceSQL sensorSourceSql = new SensorSourceSQL(jdbConnection);

            try {
                SensorSource sensorSource = (SensorSource) sensorSourceSql.select_unique_sql(SQLQueryDatabase.mySqlUniqueSensorSourceSelectQuery, sensorSourceId);

                return sensorSource;
            } catch (SQLException e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    public void addSensorSource(SensorSource sensorSource) {
        if (Boolean.parseBoolean(PropertiesReader.getValue("USEHIBERNATE"))) {
            Session session = HibernateUtil.getSessionFactory().openSession();
            Transaction transaction = session.beginTransaction();
            new GenericJPA<>(SensorSource.class).insert(new CustomTransation(session, transaction), sensorSource);
            transaction.commit();
            session.close();

        } else {
            JDBConnection jdbConnection = JDBConnection
                    .builder().user(PropertiesReader.getValue("USER")).pass(PropertiesReader.getValue("PASSWORD"))
                    .urlConn("jdbc:" + PropertiesReader.getValue("DATABASETYPE") + "://" + PropertiesReader.getValue("HOST") + ":" + PropertiesReader.getValue("PORT") + "/" + PropertiesReader.getValue("DATABASE"))
                    .classDriver(PropertiesReader.getValue("DRIVER"))
                    .build();

            SensorSourceSQL sensorSourceSql = new SensorSourceSQL(jdbConnection);

            try {
                sensorSourceSql.insert_sql(SQLQueryDatabase.mySqlSensorSourceInsertQuery, sensorSource.getDescription(), sensorSource.getName());
                sensorSource.setId(sensorSourceSql.get_last_generated_key());
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
