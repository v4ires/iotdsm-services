package repositories;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import model.Sensor;
import model.SensorMeasureType;
import org.bson.Document;
import persistence.GenericJPA;
import persistence.SensorSQL;
import utils.PropertiesReader;
import utils.hibernate.CustomTransaction;
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
    public SensorRepository(CustomTransaction customTransaction) {
        this.hibernateTransaction = customTransaction;
    }

    public SensorRepository() {

    }

    protected SensorSQL getJdbcSql() {
        if (jdbcSql == null) {
            JDBConnection jdbConnection = JDBConnection.builder()
                    .user(PropertiesReader.getValue("USER"))
                    .pass(PropertiesReader.getValue("PASSWORD"))
                    .host(PropertiesReader.getValue("HOST"))
                    .port(Integer.parseInt(PropertiesReader.getValue("PORT")))
                    .database(PropertiesReader.getValue("DATABASE"))
                    .databaseType(PropertiesReader.getValue("DATABASETYPE"))
                    .classDriver(PropertiesReader.getValue("DRIVER"))
                    .build();

            jdbcSql = new SensorSQL(jdbConnection);
        }

        return (SensorSQL) jdbcSql;
    }

    public Sensor getSensorById(long sensorId) {
        if (useHibernate) {

            Sensor sensor = new GenericJPA<>(Sensor.class).findById(getHibernateTransaction(), sensorId);

            return sensor;
        } else {
            if (databaseType.equals("mongo")) {
                MongoCollection<Document> sensorCollection = getMongoConnection().getMongoCollection(PropertiesReader.getValue("DATABASE"), "sensor");


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

    public List<Sensor> getSensors(int limit, int offset) {
        if (useHibernate) {

            List<Sensor> sensors;

            if (limit > 0)
                sensors = new GenericJPA<>(Sensor.class).resultList(getHibernateTransaction(), offset, limit);
            else if (offset > 0)
                sensors = new GenericJPA<>(Sensor.class).resultList(getHibernateTransaction(), offset);
            else
                sensors = new GenericJPA<>(Sensor.class).findAll(getHibernateTransaction());

            return sensors;
        } else {
            if (databaseType.equals("mongo")) {
                MongoCollection<Document> sensorCollection = getMongoConnection().getMongoCollection(PropertiesReader.getValue("DATABASE"), "sensor");

                FindIterable<Document> sensorDocuments;

                if (limit > 0)
                    sensorDocuments = sensorCollection.find().limit(limit).skip(offset);
                else
                    sensorDocuments = sensorCollection.find().skip(offset);

                List<Sensor> sensors = new ArrayList<>();

                for (Document cur : sensorDocuments)
                    sensors.add(_gson.fromJson(cur.toJson(), Sensor.class));

                return sensors;
            } else {

                try {
                    List<Sensor> sensors;

                    if (limit > 0)
                        sensors = (List<Sensor>) (Object) getJdbcSql().select_sql(SQLQueryDatabase.mySqlSensorSelectWithLimitAndOffsetQuery, limit, offset);
                    else if (offset > 0)
                        sensors = (List<Sensor>) (Object) getJdbcSql().select_sql(SQLQueryDatabase.mySqlSensorSelectWithOffsetQuery, offset);
                    else
                        sensors = (List<Sensor>) (Object) getJdbcSql().select_sql(SQLQueryDatabase.mySqlSensorSelectQuery);

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
            new GenericJPA<>(Sensor.class).insertOrUpdate(getHibernateTransaction(), sensor);

        } else {
            if (databaseType.equals("mongo")) {
                MongoCollection<Document> sensorCollection = getMongoConnection().getMongoCollection(PropertiesReader.getValue("DATABASE"), "sensor");

                sensor.setId(MongoDBUtil.getNextSequence(getMongoConnection(), "sensor"));
                sensor.setCreate_time(Date.from(Instant.now()));

                Document sensorDocument = getMongoConnection().createMongoDocument(_gson.toJson(sensor));

                sensorCollection.insertOne(sensorDocument);

                MongoCollection<Document> sensorMeasureTypeCollection = getMongoConnection().getMongoCollection(PropertiesReader.getValue("DATABASE"), "sensor_measure_type");

                for (SensorMeasureType m : sensor.getSensorMeasures()) {
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
