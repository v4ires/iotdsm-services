package edu.usp.icmc.lasdpc;

import edu.usp.icmc.lasdpc.controllers.SensorController;
import edu.usp.icmc.lasdpc.controllers.SensorSourceController;
import edu.usp.icmc.lasdpc.controllers.WekaController;
import edu.usp.icmc.lasdpc.utils.PropertiesReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Spark;

public class WekaServletMain extends BaseMain {

    private static final Logger log = LoggerFactory.getLogger(WekaController.class);

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
    static void initSpark() {
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
        Spark.get("/sensor", SensorController.serveSensorListPage);
        Spark.get("/sensor/:id/measure", SensorController.serveSensorMeasuresBySensorId);
        Spark.get("/sensor/:id/measure/:measureTypeId/", SensorController.serveSensorMeasuresBySensorIdAndDate);
        Spark.get("/sensor/:id/measure/:measureTypeId/:startDate", SensorController.serveSensorMeasuresBySensorIdAndDate);
        Spark.get("/sensor/:id/measure/:measureTypeId/:startDate/:endDate", SensorController.serveSensorMeasuresBySensorIdAndDate);
        Spark.get("/sensor/:id", SensorController.serveSensorById);

        //Weka Classification
        Spark.post("/sensor/nv", WekaController.naiveBayesPost);
        Spark.post("/sensor/lr", WekaController.lRPost);
        Spark.post("/sensor/knn", WekaController.dTPost);
        Spark.post("/sensor/mlp", WekaController.mlpPost);
        Spark.post("/sensor/knn", WekaController.knnPost);

        Spark.notFound((req, res) -> "{\"message\":\"Rout Not Found 404\"}");
        spark.Spark.exception(Exception.class, (exception, request, response) -> {
            log.error(exception.getMessage());
            exception.printStackTrace();
        });
    }
}
