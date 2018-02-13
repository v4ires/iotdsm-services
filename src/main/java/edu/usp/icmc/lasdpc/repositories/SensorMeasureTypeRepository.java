package edu.usp.icmc.lasdpc.repositories;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import edu.usp.icmc.lasdpc.model.SensorMeasureType;
import edu.usp.icmc.lasdpc.utils.PropertiesReader;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import edu.usp.icmc.lasdpc.persistence.GenericJPA;
import edu.usp.icmc.lasdpc.persistence.SensorMeasureTypeSQL;
import edu.usp.icmc.lasdpc.utils.hibernate.CustomTransaction;
import edu.usp.icmc.lasdpc.utils.sql.JDBConnection;
import edu.usp.icmc.lasdpc.utils.sql.SQLQueryDatabase;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;

/**
 * University of Sao Paulo
 * IoT Repository Module
 *
 * @author Vinicius Aires Barros viniciusaires@usp.br
 */
public class SensorMeasureTypeRepository extends BaseRepository {

    private static final Logger log = LoggerFactory.getLogger(SensorMeasureTypeRepository.class);

    /**
     *
     */
    public SensorMeasureTypeRepository(CustomTransaction customTransaction) {
        this.hibernateTransaction = customTransaction;
    }

    /**
     *
     */
    public SensorMeasureTypeRepository() {

    }

    /**
     *
     */
    protected SensorMeasureTypeSQL getJdbcSql() {
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

            jdbcSql = new SensorMeasureTypeSQL(jdbConnection);
        }

        return (SensorMeasureTypeSQL) jdbcSql;
    }

    /**
     *
     */
    public List<SensorMeasureType> getSensorMeasureTypeBySensor(long sensorId) {
        if (useHibernate) {
            List<SensorMeasureType> sensorMeasureTypes = new GenericJPA<>(SensorMeasureType.class).resultList(getHibernateTransaction(), "SELECT FETCH s.sensorMeasures FROM Sensor s WHERE s.id = " + sensorId);

            return sensorMeasureTypes;
        } else {
            if (databaseType.equals("mongo")) {
                MongoCollection<Document> sensorCollection = getMongoConnection().getMongoCollection(PropertiesReader.getValue("DATABASE"), "sensor_measure_type");

                FindIterable<Document> sensorDocument = sensorCollection.find(eq("sensor_id", sensorId));

                List<SensorMeasureType> sensorMeasureTypes = new ArrayList<>();

                for (Document sensorMeasureTypeDocument : sensorDocument)
                    sensorMeasureTypes.add(_gson.fromJson(sensorMeasureTypeDocument.toJson(), SensorMeasureType.class));

                return sensorMeasureTypes;
            } else {
                try {
                    List<SensorMeasureType> sensorMeasureTypes = (List<SensorMeasureType>) (Object) getJdbcSql().select_sql(SQLQueryDatabase.sqlSensorMeasureTypeBySensorSelectQuery, sensorId);

                    return sensorMeasureTypes;
                } catch (SQLException e) {
                    log.error(e.getMessage());
                    e.printStackTrace();
                    return null;
                }
            }
        }
    }

    /**
     *
     */
    public void addSensorMeasureType(SensorMeasureType sensorMeasureType) {
        if (useHibernate) {
            new GenericJPA<>(SensorMeasureType.class).insert(getHibernateTransaction(), sensorMeasureType);
        } else {
            //Adicionamos as medidas no Mongo direto no Metodo de adicionar sensores
            if (!databaseType.equals("mongo")) {
                try {
                    getJdbcSql().insert_sql(SQLQueryDatabase.sqlSensorMeasureTypeInsertQuery, sensorMeasureType.getName(), sensorMeasureType.getUnit());
                    sensorMeasureType.setId(getJdbcSql().get_last_generated_key());
                } catch (SQLException e) {
                    log.error(e.getMessage());
                    e.printStackTrace();
                }
            }
        }
    }
}
