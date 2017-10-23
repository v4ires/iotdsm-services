package repositories;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import model.SensorMeasure;
import org.bson.Document;
import org.hibernate.Session;
import org.hibernate.Transaction;
import persistence.GenericJPA;
import persistence.SensorMeasureSQL;
import utils.PropertiesReader;
import utils.hibernate.CustomTransation;
import utils.hibernate.HibernateUtil;
import utils.mongodb.MongoDBUtil;
import utils.sql.JDBConnection;
import utils.sql.SQLQueryDatabase;

import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.mongodb.client.model.Filters.*;

public class SensorMeasureRepository extends BaseRepository {

    protected SensorMeasureSQL getJdbcSql()
    {
        if(jdbcSql == null) {
            JDBConnection jdbConnection = JDBConnection
                    .builder().user(PropertiesReader.getValue("USER")).pass(PropertiesReader.getValue("PASSWORD"))
                    .urlConn("jdbc:" + databaseType + "://" + PropertiesReader.getValue("HOST") + ":" + PropertiesReader.getValue("PORT") + "/" + PropertiesReader.getValue("DATABASE"))
                    .classDriver(PropertiesReader.getValue("DRIVER"))
                    .build();

            jdbcSql = new SensorMeasureSQL(jdbConnection);
        }

        return (SensorMeasureSQL) jdbcSql;
    }

    public SensorMeasure getSensorMeasureById(long sensorMeasureId){
        if (useHibernate) {
            Session session = HibernateUtil.getSessionFactory().openSession();
            Transaction transaction = session.beginTransaction();
            SensorMeasure sensorMeasure = new GenericJPA<>(SensorMeasure.class).findById(new CustomTransation(session, transaction), sensorMeasureId);
            transaction.commit();
            session.close();

            return sensorMeasure;
        } else {
            if (databaseType.equals("mongo")) {
                MongoCollection<Document> sensorMeasureCollection = getMongoConnection().getMongoCollection(getMongoConnection().getMongoDatabase(PropertiesReader.getValue("DATABASE")), PropertiesReader.getValue("DATABASE"), "sensor_measure");

                Document sensorMeasureDocument = sensorMeasureCollection.find(eq("id", sensorMeasureId)).first();

                if(sensorMeasureDocument != null)
                    return _gson.fromJson(sensorMeasureDocument.toJson(), SensorMeasure.class);

                return null;
            } else {


                try {
                    SensorMeasure sensorMeasure = (SensorMeasure) getJdbcSql().select_unique_sql(String.format(SQLQueryDatabase.mySqlUniqueSensorMeasureSelectQuery, sensorMeasureId));

                    return sensorMeasure;
                } catch (SQLException e) {
                    e.printStackTrace();
                    return null;
                }
            }
        }
    }

    public List<SensorMeasure> getSensorMeasure(long sensorId, long measureTypeId, Date startDate, Date endDate){
        if (useHibernate) {
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            Session session = HibernateUtil.getSessionFactory().openSession();
            Transaction transaction = session.beginTransaction();
            List<SensorMeasure> sensors = new GenericJPA<>(SensorMeasure.class).resultList(new CustomTransation(session, transaction), "FROM SensorMeasure WHERE sensor.id="+sensorId+" AND sensorMeasureType.id="+measureTypeId+" AND create_time >= '"+df.format(startDate)+"' AND create_time <= '"+df.format(endDate)+"'");
            transaction.commit();
            session.close();

            return sensors;
        } else {
            if (databaseType.equals("mongo")) {
                MongoCollection<Document> sensorMeasureCollection = getMongoConnection().getMongoCollection(getMongoConnection().getMongoDatabase(PropertiesReader.getValue("DATABASE")), PropertiesReader.getValue("DATABASE"), "sensor_measure");

                FindIterable<Document> sensorMeasureDocuments = sensorMeasureCollection.find(and(eq("sensor_id", sensorId), gte("create_time", startDate), lte("create_time", endDate)));

                List<SensorMeasure> sensorMeasures = new ArrayList<>();

                DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX");

                for(Document smDocument : sensorMeasureDocuments){
                    smDocument.put("create_time", df.format((Date)smDocument.get("create_time")));
                    sensorMeasures.add(_gson.fromJson(smDocument.toJson(), SensorMeasure.class));
                }

                return sensorMeasures;
            } else {
                try {
                    List<SensorMeasure> sensors = (List<SensorMeasure>) (Object) getJdbcSql().select_sql(SQLQueryDatabase.mySqlSensorMeasureByDateAndSensorSelectQuery, sensorId, measureTypeId, startDate, endDate);

                    return sensors;
                } catch (SQLException e) {
                    e.printStackTrace();
                    return null;
                }
            }
        }
    }

    public void addSensorMeasure(SensorMeasure sensorMeasure) {
        if (useHibernate) {
            Session session = HibernateUtil.getSessionFactory().openSession();
            Transaction transaction = session.beginTransaction();
            new GenericJPA<>(SensorMeasure.class).insert(new CustomTransation(session, transaction), sensorMeasure);
            transaction.commit();
            session.close();
        } else {
            if (databaseType.equals("mongo")) {
                MongoCollection<Document> sensorMeasureCollection = getMongoConnection().getMongoCollection(getMongoConnection().getMongoDatabase(PropertiesReader.getValue("DATABASE")), PropertiesReader.getValue("DATABASE"), "sensor_measure");

                sensorMeasure.setId(MongoDBUtil.getNextSequence(getMongoConnection(), "sensor_measure"));

                Document sensorMeasureDocument = getMongoConnection().createMongoDocument(_gson.toJson(sensorMeasure));
                sensorMeasureDocument.append("sensor_id", sensorMeasure.getSensor().getId());
                sensorMeasureDocument.append("sensor_measure_type_id", sensorMeasure.getSensorMeasureType().getId());
                sensorMeasureDocument.put("create_time", sensorMeasure.getCreate_time());

                sensorMeasureCollection.insertOne(sensorMeasureDocument);
            } else {

                try {
                    getJdbcSql().insert_sql(SQLQueryDatabase.mySqlSensorMeasureInsertQuery, sensorMeasure.getSensor().getId(), sensorMeasure.getValue(), sensorMeasure.getSensorMeasureType().getId(), sensorMeasure.getCreate_time());
                    sensorMeasure.setId(getJdbcSql().get_last_generated_key());
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
