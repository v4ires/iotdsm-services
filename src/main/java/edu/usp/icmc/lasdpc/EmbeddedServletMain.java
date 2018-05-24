package edu.usp.icmc.lasdpc;

import edu.usp.icmc.lasdpc.controllers.SensorController;
import edu.usp.icmc.lasdpc.controllers.SensorSourceController;
import edu.usp.icmc.lasdpc.utils.PropertiesReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Spark;

/**
 * University of Sao Paulo
 * IoT Repository Module
 *
 * @author Vinicius Aires Barros viniciusaires@usp.br
 */
public class EmbeddedServletMain extends BaseMain {

    static final Logger log = LoggerFactory.getLogger(EmbeddedServletMain.class);

    /**
     * Método ImportData da Aplicação IoTDSM. Inicializa todas as configurações necessárias para o seu funcionamento.
     *
     * @param args
     */
    public static void main(String[] args) {
        initOptions(args);
        initServerProperties();
        initDatabaseConnection();
        initSpark();
    }

    /**
     * Inicializa as configurações do Spark Java
     */
    private static void initSpark() {

        spark.Spark.port(Integer.parseInt(PropertiesReader.getValue("APIPORT")));
        if (Boolean.parseBoolean((PropertiesReader.getValue("SPARK_THREAD_POOL")))) {
            int maxThreads = Integer.parseInt(PropertiesReader.getValue("SPARK_THREAD_POOL_MIN"));
            int minThreads = Integer.parseInt(PropertiesReader.getValue("SPARK_THREAD_POOL_MAX"));
            int idleTimeoutMilis = Integer.parseInt(PropertiesReader.getValue("SPARK_THREAD_POOL_TIMEOUT"));
            spark.Spark.threadPool(maxThreads, minThreads, idleTimeoutMilis);
        }

        log.info("Web Server is Running...");
        Spark.staticFiles.location("/public");
        Spark.get("/sensorSource", SensorSourceController.serveSensorSourceListPage);
        Spark.get("/sensorSource/:id", SensorSourceController.serveSensorById);

        //TODO Implements this newer Endpoints

        //Returns a List of Sensor from SensorSource id
        //Spark.get("/sensorSource/:id/sensor", SensorSourceController.serveSensorListPage);

        //Returns a List of Sensor Measures from Sensor id and MeasureType id
        //Spark.get("/sensor/:id/measure/:id", SensorController.listPageSensorMeasureFromId);

        Spark.get("/sensor", SensorController.serveSensorListPage);
        Spark.get("/sensor/:id/measure", SensorController.serveSensorMeasuresBySensorId);
        Spark.get("/sensor/:id/measure/:measureTypeId/", SensorController.serveSensorMeasuresBySensorIdAndDate);
        Spark.get("/sensor/:id/measure/:measureTypeId/:startDate", SensorController.serveSensorMeasuresBySensorIdAndDate);
        Spark.get("/sensor/:id/measure/:measureTypeId/:startDate/:endDate", SensorController.serveSensorMeasuresBySensorIdAndDate);
        Spark.get("/sensor/:id", SensorController.serveSensorById);
        Spark.post("/sensor/upload", "multipart/form-data", SensorController.handleFileUpload);

        Spark.post("/sensor", "multipart/form-data", SensorController.brokerPost);

        Spark.notFound((req, res) -> "{\"message\":\"Rout Not Found 404\"}");
        spark.Spark.exception(Exception.class, (exception, request, response) -> {
            log.error(exception.getMessage());
            exception.printStackTrace();
        });
    }
}