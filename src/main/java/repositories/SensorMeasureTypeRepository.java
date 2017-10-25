package repositories;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import model.SensorMeasureType;
import org.bson.Document;
import org.hibernate.Session;
import org.hibernate.Transaction;
import persistence.GenericJPA;
import persistence.SensorMeasureTypeSQL;
import utils.PropertiesReader;
import utils.hibernate.CustomTransaction;
import utils.hibernate.HibernateUtil;
import utils.sql.JDBConnection;
import utils.sql.SQLQueryDatabase;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;

public class SensorMeasureTypeRepository extends BaseRepository {
    public SensorMeasureTypeRepository(CustomTransaction customTransaction){
        this.hibernateTransaction = customTransaction;
    }

    public SensorMeasureTypeRepository(){

    }
    protected SensorMeasureTypeSQL getJdbcSql()
    {
        if(jdbcSql == null) {
            JDBConnection jdbConnection = JDBConnection
                    .builder().user(PropertiesReader.getValue("USER")).pass(PropertiesReader.getValue("PASSWORD"))
                    .urlConn("jdbc:" + databaseType + "://" + PropertiesReader.getValue("HOST") + ":" + PropertiesReader.getValue("PORT") + "/" + PropertiesReader.getValue("DATABASE"))
                    .classDriver(PropertiesReader.getValue("DRIVER"))
                    .build();

            jdbcSql = new SensorMeasureTypeSQL(jdbConnection);
        }

        return (SensorMeasureTypeSQL) jdbcSql;
    }

    public List<SensorMeasureType> getSensorMeasureTypeBySensor(long sensorId){
        if (useHibernate) {
            List<SensorMeasureType> sensorMeasureTypes = new GenericJPA<>(SensorMeasureType.class).resultList(getHibernateTransaction(), "SELECT s.sensorMeasures FROM Sensor s WHERE s.id = "+sensorId);

            return sensorMeasureTypes;
        } else {
            if (databaseType.equals("mongo")) {
                MongoCollection<Document> sensorCollection = getMongoConnection().getMongoCollection(getMongoConnection().getMongoDatabase(PropertiesReader.getValue("DATABASE")), PropertiesReader.getValue("DATABASE"), "sensor_measure_type");

                FindIterable<Document> sensorDocument = sensorCollection.find(eq("sensor_id", sensorId));

                List<SensorMeasureType> sensorMeasureTypes = new ArrayList<>();

                for(Document sensorMeasureTypeDocument : sensorDocument)
                    sensorMeasureTypes.add(_gson.fromJson(sensorMeasureTypeDocument.toJson(), SensorMeasureType.class));

                return sensorMeasureTypes;
            }else {

                try {
                    List<SensorMeasureType> sensorMeasureTypes = (List<SensorMeasureType>) (Object) getJdbcSql().select_sql(SQLQueryDatabase.mySqlSensorMeasureTypeBySensorSelectQuery, sensorId);

                    return sensorMeasureTypes;
                } catch (SQLException e) {
                    e.printStackTrace();
                    return null;
                }
            }
        }
    }

    public void addSensorMeasureType(SensorMeasureType sensorMeasureType) {
        if (useHibernate) {
            new GenericJPA<>(SensorMeasureType.class).insert(getHibernateTransaction(), sensorMeasureType);
        } else {
            //Adicionamos as medidas no Mongo direto no método de adicionar sensores
            if (!databaseType.equals("mongo")){

                try {
                    getJdbcSql().insert_sql(SQLQueryDatabase.mySqlSensorMeasureTypeInsertQuery, sensorMeasureType.getName(), sensorMeasureType.getUnit());
                    sensorMeasureType.setId(getJdbcSql().get_last_generated_key());
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
