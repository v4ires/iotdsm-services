package repositories;

import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import model.Sensor;
import model.SensorMeasureType;
import org.bson.Document;
import org.hibernate.Session;
import org.hibernate.Transaction;
import persistence.GenericJPA;
import persistence.SensorSQL;
import utils.PropertiesReader;
import utils.hibernate.CustomTransation;
import utils.hibernate.HibernateUtil;
import utils.mongodb.GenericMongoDB;
import utils.mongodb.MongoDBUtil;
import utils.sql.JDBConnection;
import utils.sql.SQLQueryDatabase;

import java.sql.SQLException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;

public class SensorRepository extends BaseRepository {
    protected SensorSQL getJdbcSql()
    {
        if(jdbcSql == null) {
            JDBConnection jdbConnection = JDBConnection
                    .builder().user(PropertiesReader.getValue("USER")).pass(PropertiesReader.getValue("PASSWORD"))
                    .urlConn("jdbc:" + databaseType + "://" + PropertiesReader.getValue("HOST") + ":" + PropertiesReader.getValue("PORT") + "/" + PropertiesReader.getValue("DATABASE"))
                    .classDriver(PropertiesReader.getValue("DRIVER"))
                    .build();

            jdbcSql = new SensorSQL(jdbConnection);
        }

        return (SensorSQL) jdbcSql;
    }

    public Sensor getSensorById(long sensorId){
        if (useHibernate) {
            Session session = HibernateUtil.getSessionFactory().openSession();
            Transaction transaction = session.beginTransaction();
            Sensor sensor = new GenericJPA<>(Sensor.class).findById(new CustomTransation(session, transaction), sensorId);
            transaction.commit();
            session.close();

            return sensor;
        } else {
            if (databaseType.equals("mongo")) {
                GenericMongoDB mongoConn = new GenericMongoDB(new MongoClient(PropertiesReader.getValue("HOST"), Integer.parseInt(PropertiesReader.getValue("PORT"))));
                MongoCollection<Document> sensorCollection = mongoConn.getMongoCollection(mongoConn.getMongoDatabase(PropertiesReader.getValue("DATABASE")), PropertiesReader.getValue("DATABASE"), "sensor");

                Document sensorDocument = sensorCollection.find(eq("id", sensorId)).first();

                if (sensorDocument != null) {
                    return _gson.fromJson(sensorDocument.toJson(), Sensor.class);
                }

                return null;
            } else {

                try {
                    Sensor sensor = (Sensor) getJdbcSql().select_unique_sql(SQLQueryDatabase.mySqlUniqueSensorSelectQuery, sensorId);

                    return sensor;
                } catch (SQLException e) {
                    e.printStackTrace();
                    return null;
                }
            }
        }
    }

    public List<Sensor> getSensors(){
        if (useHibernate) {
            Session session = HibernateUtil.getSessionFactory().openSession();
            Transaction transaction = session.beginTransaction();
            List<Sensor> sensors = new GenericJPA<>(Sensor.class).findAll(new CustomTransation(session, transaction));
            transaction.commit();
            session.close();

            return sensors;
        } else {
            if (databaseType.equals("mongo")) {
                GenericMongoDB mongoConn = new GenericMongoDB(new MongoClient(PropertiesReader.getValue("HOST"), Integer.parseInt(PropertiesReader.getValue("PORT"))));
                MongoCollection<Document> sensorCollection = mongoConn.getMongoCollection(mongoConn.getMongoDatabase(PropertiesReader.getValue("DATABASE")), PropertiesReader.getValue("DATABASE"), "sensor");

                FindIterable<Document> sensorDocuments = sensorCollection.find();
                List<Sensor> sensors = new ArrayList<>();

                for(Document cur: sensorDocuments)
                    sensors.add(_gson.fromJson(cur.toJson(), Sensor.class));

                return sensors;
            } else {

                try {
                    List<Sensor> sensors = (List<Sensor>) (Object) getJdbcSql().select_sql(SQLQueryDatabase.mySqlSensorSelectQuery);

                    return sensors;
                } catch (SQLException e) {
                    e.printStackTrace();
                    return null;
                }
            }
        }
    }

    public void addSensor(Sensor sensor) {
        if (useHibernate) {
            Session session = HibernateUtil.getSessionFactory().openSession();
            Transaction transaction = session.beginTransaction();
            new GenericJPA<>(Sensor.class).insertOrUpdate(new CustomTransation(session, transaction), sensor);
            transaction.commit();
            session.close();

        } else {
            if (databaseType.equals("mongo")) {
                MongoCollection<Document> sensorCollection = getMongoConnection().getMongoCollection(getMongoConnection().getMongoDatabase(PropertiesReader.getValue("DATABASE")), PropertiesReader.getValue("DATABASE"), "sensor");

                sensor.setId(MongoDBUtil.getNextSequence(getMongoConnection(), "sensor"));
                sensor.setCreate_time(Date.from(Instant.now()));

                Document sensorDocument = getMongoConnection().createMongoDocument(_gson.toJson(sensor));

                sensorCollection.insertOne(sensorDocument);

                MongoCollection<Document> sensorMeasureTypeCollection = getMongoConnection().getMongoCollection(getMongoConnection().getMongoDatabase(PropertiesReader.getValue("DATABASE")), PropertiesReader.getValue("DATABASE"), "sensor_measure_type");

                for(SensorMeasureType m : sensor.getSensorMeasures()){
                    m.setId(MongoDBUtil.getNextSequence(getMongoConnection(), "sensor_measure_type"));
                    m.setCreate_time(Date.from(Instant.now()));

                    Document sensorMeasureTypeDocument = getMongoConnection().createMongoDocument(_gson.toJson(m));
                    sensorMeasureTypeDocument.append("sensor_id", sensor.getId());

                    sensorMeasureTypeCollection.insertOne(sensorMeasureTypeDocument);
                }
            } else {

                try {
                    getJdbcSql().insert_sql(SQLQueryDatabase.mySqlSensorInsertQuery, sensor.getDescription(), sensor.getLatitude(), sensor.getLongitude(), sensor.getName(), sensor.getSensorSource().getId());
                    sensor.setId(getJdbcSql().get_last_generated_key());
                } catch (SQLException e) {
                    e.printStackTrace();
                    return;
                }

                for (SensorMeasureType smt : sensor.getSensorMeasures()) {
                    try {
                        getJdbcSql().insert_sql(SQLQueryDatabase.mySqlSensorSensorMeasureInsertQuery, sensor.getId(), smt.getId());
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
