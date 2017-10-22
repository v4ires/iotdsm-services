package repositories;

import model.SensorMeasure;
import model.SensorMeasureType;
import org.hibernate.Session;
import org.hibernate.Transaction;
import persistence.GenericJPA;
import persistence.SensorMeasureTypeSQL;
import persistence.SensorSQL;
import utils.PropertiesReader;
import utils.hibernate.CustomTransation;
import utils.hibernate.HibernateUtil;
import utils.sql.JDBConnection;
import utils.sql.SQLQueryDatabase;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

public class SensorMeasureTypeRepository {
    public List<SensorMeasureType> getSensorMeasureTypeBySensor(long sensorId){
        if (Boolean.parseBoolean(PropertiesReader.getValue("USEHIBERNATE"))) {
            Session session = HibernateUtil.getSessionFactory().openSession();
            Transaction transaction = session.beginTransaction();
            List<SensorMeasureType> sensorMeasureTypes = new GenericJPA<>(SensorMeasureType.class).resultList(new CustomTransation(session, transaction), "SELECT s.sensorMeasures FROM Sensor s WHERE s.id = "+sensorId);

            return sensorMeasureTypes;
        } else {
            JDBConnection jdbConnection = JDBConnection
                    .builder().user(PropertiesReader.getValue("USER")).pass(PropertiesReader.getValue("PASSWORD"))
                    .urlConn("jdbc:" + PropertiesReader.getValue("DATABASETYPE") + "://" + PropertiesReader.getValue("HOST") + ":" + PropertiesReader.getValue("PORT") + "/" + PropertiesReader.getValue("DATABASE"))
                    .classDriver(PropertiesReader.getValue("DRIVER"))
                    .build();

            SensorMeasureTypeSQL sensorMeasureTypeSql = new SensorMeasureTypeSQL(jdbConnection);

            try {
                List<SensorMeasureType> sensorMeasureTypes = (List<SensorMeasureType>)(Object)sensorMeasureTypeSql.select_sql(SQLQueryDatabase.mySqlSensorMeasureTypeBySensorSelectQuery, sensorId);

                return sensorMeasureTypes;
            } catch (SQLException e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    public void addSensorMeasureType(SensorMeasureType sensorMeasureType) {
        if (Boolean.parseBoolean(PropertiesReader.getValue("USEHIBERNATE"))) {
            Session session = HibernateUtil.getSessionFactory().openSession();
            Transaction transaction = session.beginTransaction();
            new GenericJPA<>(SensorMeasureType.class).insert(new CustomTransation(session, transaction), sensorMeasureType);

        } else {
            JDBConnection jdbConnection = JDBConnection
                    .builder().user(PropertiesReader.getValue("USER")).pass(PropertiesReader.getValue("PASSWORD"))
                    .urlConn("jdbc:" + PropertiesReader.getValue("DATABASETYPE") + "://" + PropertiesReader.getValue("HOST") + ":" + PropertiesReader.getValue("PORT") + "/" + PropertiesReader.getValue("DATABASE"))
                    .classDriver(PropertiesReader.getValue("DRIVER"))
                    .build();

            SensorMeasureTypeSQL sensorMeasureTypeSql = new SensorMeasureTypeSQL(jdbConnection);

            try {
                sensorMeasureTypeSql.insert_sql(SQLQueryDatabase.mySqlSensorMeasureTypeInsertQuery, sensorMeasureType.getName(), sensorMeasureType.getUnit());
                sensorMeasureType.setId(sensorMeasureTypeSql.get_last_generated_key());
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
