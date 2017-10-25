package repositories;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import model.Sensor;
import model.SensorSource;
import org.bson.Document;
import org.hibernate.Session;
import org.hibernate.Transaction;
import persistence.GenericJPA;
import persistence.SensorSourceSQL;
import utils.PropertiesReader;
import utils.hibernate.CustomTransaction;
import utils.hibernate.HibernateUtil;
import utils.mongodb.MongoDBUtil;
import utils.sql.JDBConnection;
import utils.sql.SQLQueryDatabase;

import java.sql.SQLException;
import java.time.Instant;
import java.util.*;

import static com.mongodb.client.model.Filters.eq;

public class SensorSourceRepository extends BaseRepository{
    public SensorSourceRepository(CustomTransaction customTransaction){
        this.hibernateTransaction = customTransaction;
    }

    public SensorSourceRepository(){

    }
    protected SensorSourceSQL getJdbcSql()
    {
        if(jdbcSql == null) {
            JDBConnection jdbConnection = JDBConnection
                    .builder().user(PropertiesReader.getValue("USER")).pass(PropertiesReader.getValue("PASSWORD"))
                    .urlConn("jdbc:" + databaseType + "://" + PropertiesReader.getValue("HOST") + ":" + PropertiesReader.getValue("PORT") + "/" + PropertiesReader.getValue("DATABASE"))
                    .classDriver(PropertiesReader.getValue("DRIVER"))
                    .build();

            jdbcSql = new SensorSourceSQL(jdbConnection);
        }

        return (SensorSourceSQL) jdbcSql;
    }

    public List<SensorSource> getSensorSources()
    {
        if (useHibernate) {
            List<SensorSource> sensorSources = new GenericJPA<>(SensorSource.class).findAll(getHibernateTransaction());

            return sensorSources;
        } else {
            if (databaseType.equals("mongo")) {
                MongoCollection<Document> sensorCollection = getMongoConnection().getMongoCollection(getMongoConnection().getMongoDatabase(PropertiesReader.getValue("DATABASE")), PropertiesReader.getValue("DATABASE"), "sensor");

                FindIterable<Document> sensorDocument = sensorCollection.find();
                Map<Long, SensorSource> sensorSources = new HashMap<>();

                for(Document cur: sensorDocument) {
                    Sensor s = _gson.fromJson(cur.toJson(), Sensor.class);
                    if(!sensorSources.containsKey(s.getSensorSource().getId()))
                        sensorSources.put(s.getSensorSource().getId(), s.getSensorSource());
                }

                return new ArrayList<>(sensorSources.values());
            }
            else {

                try {
                    List<SensorSource> sensorSources = (List<SensorSource>) (Object) getJdbcSql().select_sql(SQLQueryDatabase.mySqlSensorSourceSelectQuery);

                    return sensorSources;
                } catch (SQLException e) {
                    e.printStackTrace();
                    return null;
                }
            }
        }
    }
    public SensorSource getSensorSourceById(long sensorSourceId){
        if (useHibernate) {

            SensorSource sensorSource = new GenericJPA<>(SensorSource.class).findById(getHibernateTransaction(), sensorSourceId);

            return sensorSource;
        } else {
            if (databaseType.equals("mongo")) {
                MongoCollection<Document> sensorCollection = getMongoConnection().getMongoCollection(getMongoConnection().getMongoDatabase(PropertiesReader.getValue("DATABASE")), PropertiesReader.getValue("DATABASE"), "sensor");

                Document sensorDocument = sensorCollection.find(eq("sensorSource.id", sensorSourceId)).first();

                if (sensorDocument != null) {
                    return _gson.fromJson(sensorDocument.toJson(), Sensor.class).getSensorSource();
                }

                return null;
            } else {

                try {
                    SensorSource sensorSource = (SensorSource) getJdbcSql().select_unique_sql(SQLQueryDatabase.mySqlUniqueSensorSourceSelectQuery, sensorSourceId);

                    return sensorSource;
                } catch (SQLException e) {
                    e.printStackTrace();
                    return null;
                }
            }
        }
    }

    public void addSensorSource(SensorSource sensorSource) {
        if (useHibernate) {
            new GenericJPA<>(SensorSource.class).insert(getHibernateTransaction(), sensorSource);

        } else {
            //Adicionamos os sources dos sensores direto na coleção de sensores no Mongo, no objeto Sensor. Aqui, só geramos um ID unico e a data de criação.
            if (databaseType.equals("mongo")) {

                sensorSource.setId(MongoDBUtil.getNextSequence(getMongoConnection(), "sensor_source"));
                sensorSource.setCreate_time(Date.from(Instant.now()));

            }else{
                try {
                    getJdbcSql().insert_sql(SQLQueryDatabase.mySqlSensorSourceInsertQuery, sensorSource.getDescription(), sensorSource.getName());
                    sensorSource.setId(getJdbcSql().get_last_generated_key());
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
