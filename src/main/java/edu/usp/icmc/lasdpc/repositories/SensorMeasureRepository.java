package edu.usp.icmc.lasdpc.repositories;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import edu.usp.icmc.lasdpc.model.SensorMeasure;
import edu.usp.icmc.lasdpc.utils.PropertiesReader;
import edu.usp.icmc.lasdpc.utils.Utils;
import lombok.NoArgsConstructor;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import edu.usp.icmc.lasdpc.persistence.GenericJPA;
import edu.usp.icmc.lasdpc.persistence.SensorMeasureSQL;
import edu.usp.icmc.lasdpc.utils.hibernate.CustomTransaction;
import edu.usp.icmc.lasdpc.utils.mongodb.MongoDBUtil;
import edu.usp.icmc.lasdpc.utils.sql.JDBConnection;
import edu.usp.icmc.lasdpc.utils.sql.SQLQueryDatabase;

import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.mongodb.client.model.Filters.*;

/**
 * University of Sao Paulo
 * IoT Repository Module
 *
 * @author Vinicius Aires Barros viniciusaires@usp.br
 */
@NoArgsConstructor
public class SensorMeasureRepository extends BaseRepository {

    private static final Logger log = LoggerFactory.getLogger(SensorMeasureRepository.class);

    /**
     * Construtor do repositório de valores de medida de sensores.
     *
     * @param customTransaction Objeto da classe que contém uma sessão e uma transação para uso do Hibernate.
     */
    public SensorMeasureRepository(CustomTransaction customTransaction) {
        this.hibernateTransaction = customTransaction;
    }

    /**
     * Método que cria um objeto da classe wrapper que usa conexão JDBC, utilizando os parâmetros de configuração. Caso já exista uma conexão aberta, retorna o objeto já existente.
     *
     * @return Classe wrapper que utiliza JDBC para acesso ao banco.
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
     * Retorna um valor de medida de sensor pelo id especificado, usando o banco de dados especificado nos parâmetros de configuração.
     *
     * @param sensorMeasureId Id da entrada do valor da medida de um sensor.
     * @return Retorna um objeto de valor da medida de um sensor, caso exista, ou null se não existe.
     */
    public SensorMeasure getSensorMeasureById(long sensorMeasureId) {
        SensorMeasure sensorMeasure = null;
        String output_time = "thread-id: %d operation: serialization db_type: %s %s";
        long start = 0;
        long end = 0;
        start = System.currentTimeMillis();
        if (useHibernate) {
            sensorMeasure = new GenericJPA<>(SensorMeasure.class).findById(getHibernateTransaction(), sensorMeasureId);
        } else {
            if (databaseType.equals("mongo")) {
                MongoCollection<Document> sensorMeasureCollection = getMongoConnection().getMongoCollection(PropertiesReader.getValue("DATABASE"), "sensor_measure");
                Document sensorMeasureDocument = sensorMeasureCollection.find(eq("id", sensorMeasureId)).first();
                if (sensorMeasureDocument != null) {
                    sensorMeasure = _gson.fromJson(sensorMeasureDocument.toJson(), SensorMeasure.class);
                }
            } else {
                try {
                    sensorMeasure = (SensorMeasure) getJdbcSql().select_unique_sql(String.format(SQLQueryDatabase.sqlUniqueSensorMeasureSelectQuery, sensorMeasureId));
                } catch (SQLException e) {
                    log.error(e.getMessage());
                    e.printStackTrace();
                }
            }
        }
        end = System.currentTimeMillis();
        output_time = String.format(output_time, Thread.currentThread().getId(), databaseType, Utils.printElapsedTime(start, end));
        log.info(output_time);
        return sensorMeasure;
    }

    //TODO Test this method
    public List<SensorMeasure> getSensorMeasureBySensorId(long sensorId) {
        List<SensorMeasure> sensorMeasures = null;
        String output_time = "thread-id: %d operation: serialization db_type: %s %s";
        long start = 0;
        long end = 0;
        start = System.currentTimeMillis();
        if (useHibernate) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            sensorMeasures = new GenericJPA<>(SensorMeasure.class).resultList(getHibernateTransaction(), "FROM SensorMeasure WHERE sensor.id=" + sensorId + "'");
        } else {
            if (databaseType.equals("mongo")) {
                MongoCollection<Document> sensorMeasureCollection = getMongoConnection().getMongoCollection(PropertiesReader.getValue("DATABASE"), "sensor_measure");
                FindIterable<Document> sensorMeasureDocuments = sensorMeasureCollection.find(and(eq("sensor_id", sensorId)));
                sensorMeasures = new ArrayList<>();
                DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX");

                for (Document smDocument : sensorMeasureDocuments) {
                    smDocument.put("create_time", df.format((Date) smDocument.get("create_time")));
                    sensorMeasures.add(_gson.fromJson(smDocument.toJson(), SensorMeasure.class));
                }
            } else {
                try {
                    List<SensorMeasure> sensors = (List<SensorMeasure>) (Object) getJdbcSql().select_sql(SQLQueryDatabase.sqlSensorMeasureBySensorId, sensorId);
                } catch (SQLException e) {
                    log.error(e.getMessage());
                    e.printStackTrace();
                }
            }
        }
        end = System.currentTimeMillis();
        output_time = String.format(output_time, Thread.currentThread().getId(), databaseType, Utils.printElapsedTime(start, end));
        log.info(output_time);
        return sensorMeasures;
    }


    /**
     * Retorna valores de medida de sensor no intervalo de tempo especificado, usando o banco de dados especificado nos parâmetros de configuração.
     *
     * @param sensorId Id do sensor que efetuou as medidas
     * @param measureTypeId Id do tipo da medida de um sensor a ser mostrado
     * @param startDate Data mínima a ser retornada na lista
     * @param endDate Data máxima a ser retornada na lista
     *
     * @return Retorna uma lista de objetos de valor da medida de um sensor no intervalo especificado.
     */
    public List<SensorMeasure> getSensorMeasure(long sensorId, long measureTypeId, Date startDate, Date endDate) {
        List<SensorMeasure> sensorMeasures = null;
        String output_time = "thread-id: %d operation: serialization db_type: %s %s";
        long start = 0;
        long end = 0;
        start = System.currentTimeMillis();
        if (useHibernate) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            sensorMeasures = new GenericJPA<>(SensorMeasure.class).resultList(getHibernateTransaction(), "FROM SensorMeasure WHERE sensor.id=" + sensorId + " AND sensorMeasureType.id=" + measureTypeId + " AND create_time >= '" + sdf.format(startDate) + "' AND create_time <= '" + sdf.format(endDate) + "'");
        } else {
            if (databaseType.equals("mongo")) {
                MongoCollection<Document> sensorMeasureCollection = getMongoConnection().getMongoCollection(PropertiesReader.getValue("DATABASE"), "sensor_measure");
                FindIterable<Document> sensorMeasureDocuments = sensorMeasureCollection.find(and(eq("sensor_id", sensorId), gte("create_time", startDate), lte("create_time", endDate)));
                sensorMeasures = new ArrayList<>();
                DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX");

                for (Document smDocument : sensorMeasureDocuments) {
                    smDocument.put("create_time", df.format((Date) smDocument.get("create_time")));
                    sensorMeasures.add(_gson.fromJson(smDocument.toJson(), SensorMeasure.class));
                }
            } else {
                try {
                    List<SensorMeasure> sensors = (List<SensorMeasure>) (Object) getJdbcSql().select_sql(SQLQueryDatabase.sqlSensorMeasureByDateAndSensorSelectQuery, sensorId, measureTypeId, startDate, endDate);
                } catch (SQLException e) {
                    log.error(e.getMessage());
                    e.printStackTrace();
                }
            }
        }
        end = System.currentTimeMillis();
        output_time = String.format(output_time, Thread.currentThread().getId(), databaseType, Utils.printElapsedTime(start, end));
        log.info(output_time);
        return sensorMeasures;
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
                    log.error(e.getMessage());
                    e.printStackTrace();
                }
            }
        }
    }
}
