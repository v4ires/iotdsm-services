package repositories;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import model.SensorMeasure;
import org.bson.Document;
import persistence.GenericJPA;
import persistence.SensorMeasureSQL;
import utils.PropertiesReader;
import utils.hibernate.CustomTransaction;
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

/**
 * University of São Paulo
 * IoT Repository Module
 * @author Vinícius Aires Barros <viniciusaires7@gmail.com>
 */
public class SensorMeasureRepository extends BaseRepository {

    /**
     *
     */
    public SensorMeasureRepository(CustomTransaction customTransaction) {
        this.hibernateTransaction = customTransaction;
    }

    /**
     *
     */
    public SensorMeasureRepository() {

    }

    /**
     *
     */
    protected SensorMeasureSQL getJdbcSql() {
        if (jdbcSql == null) {
            JDBConnection jdbConnection = JDBConnection
                    .builder().user(PropertiesReader.getValue("USER"))
                    .pass(PropertiesReader.getValue("PASSWORD"))
                    .host(PropertiesReader.getValue("HOST"))
                    .port(Integer.parseInt(PropertiesReader.getValue("PORT")))
                    .database(PropertiesReader.getValue("DATABASE"))
                    .databaseType(PropertiesReader.getValue("DATABASETYPE"))
                    .classDriver(PropertiesReader.getValue("DRIVER"))
                    .build();

            jdbcSql = new SensorMeasureSQL(jdbConnection);
        }

        return (SensorMeasureSQL) jdbcSql;
    }

    /**
     *
     */
    public SensorMeasure getSensorMeasureById(long sensorMeasureId) {
        if (useHibernate) {

            SensorMeasure sensorMeasure = new GenericJPA<>(SensorMeasure.class).findById(getHibernateTransaction(), sensorMeasureId);

            return sensorMeasure;
        } else {
            if (databaseType.equals("mongo")) {
                MongoCollection<Document> sensorMeasureCollection = getMongoConnection().getMongoCollection(PropertiesReader.getValue("DATABASE"), "sensor_measure");

                Document sensorMeasureDocument = sensorMeasureCollection.find(eq("id", sensorMeasureId)).first();

                if (sensorMeasureDocument != null)
                    return _gson.fromJson(sensorMeasureDocument.toJson(), SensorMeasure.class);

                return null;
            } else {


                try {
                    SensorMeasure sensorMeasure = (SensorMeasure) getJdbcSql().select_unique_sql(String.format(SQLQueryDatabase.sqlUniqueSensorMeasureSelectQuery, sensorMeasureId));

                    return sensorMeasure;
                } catch (SQLException e) {
                    e.printStackTrace();
                    return null;
                }
            }
        }
    }

    /**
     *
     */
    public List<SensorMeasure> getSensorMeasure(long sensorId, long measureTypeId, Date startDate, Date endDate) {
        if (useHibernate) {
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            List<SensorMeasure> sensors = new GenericJPA<>(SensorMeasure.class).resultList(getHibernateTransaction(), "FROM SensorMeasure WHERE sensor.id=" + sensorId + " AND sensorMeasureType.id=" + measureTypeId + " AND create_time >= '" + df.format(startDate) + "' AND create_time <= '" + df.format(endDate) + "'");

            return sensors;
        } else {
            if (databaseType.equals("mongo")) {
                MongoCollection<Document> sensorMeasureCollection = getMongoConnection().getMongoCollection(PropertiesReader.getValue("DATABASE"), "sensor_measure");

                FindIterable<Document> sensorMeasureDocuments = sensorMeasureCollection.find(and(eq("sensor_id", sensorId), gte("create_time", startDate), lte("create_time", endDate)));

                List<SensorMeasure> sensorMeasures = new ArrayList<>();

                DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX");

                for (Document smDocument : sensorMeasureDocuments) {
                    smDocument.put("create_time", df.format((Date) smDocument.get("create_time")));
                    sensorMeasures.add(_gson.fromJson(smDocument.toJson(), SensorMeasure.class));
                }

                return sensorMeasures;
            } else {
                try {
                    List<SensorMeasure> sensors = (List<SensorMeasure>) (Object) getJdbcSql().select_sql(SQLQueryDatabase.sqlSensorMeasureByDateAndSensorSelectQuery, sensorId, measureTypeId, startDate, endDate);

                    return sensors;
                } catch (SQLException e) {
                    e.printStackTrace();
                    return null;
                }
            }
        }
    }

    /**
     *
     */
    public void addSensorMeasure(SensorMeasure sensorMeasure) {
        if (useHibernate) {
            new GenericJPA<>(SensorMeasure.class).insert(getHibernateTransaction(), sensorMeasure);

        } else {
            if (databaseType.equals("mongo")) {
                MongoCollection<Document> sensorMeasureCollection = getMongoConnection().getMongoCollection(PropertiesReader.getValue("DATABASE"), "sensor_measure");

                sensorMeasure.setId(MongoDBUtil.getNextSequence(getMongoConnection(), "sensor_measure"));

                Document sensorMeasureDocument = getMongoConnection().createMongoDocument(_gson.toJson(sensorMeasure));
                sensorMeasureDocument.append("sensor_id", sensorMeasure.getSensor().getId());
                sensorMeasureDocument.append("sensor_measure_type_id", sensorMeasure.getSensorMeasureType().getId());
                sensorMeasureDocument.put("create_time", sensorMeasure.getCreate_time());

                sensorMeasureCollection.insertOne(sensorMeasureDocument);
            } else {

                try {
                    getJdbcSql().insert_sql(SQLQueryDatabase.sqlSensorMeasureInsertQuery, sensorMeasure.getSensor().getId(), sensorMeasure.getValue(), sensorMeasure.getSensorMeasureType().getId(), sensorMeasure.getCreate_time());
                    sensorMeasure.setId(getJdbcSql().get_last_generated_key());
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
