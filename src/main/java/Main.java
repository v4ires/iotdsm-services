import controllers.SensorController;
import controllers.SensorSourceController;
import org.apache.commons.cli.*;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import repositories.BaseRepository;
import spark.Spark;
import utils.PropertiesReader;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * University of São Paulo
 * IoT Repository Module
 *
 * @author Vinícius Aires Barros <viniciusaires@usp.br>
 */
public class Main {

    private static final Logger log = LoggerFactory.getLogger(Main.class);
    private static String _configFileName = "config.properties";
    private static Options options = new Options();
    private static String logDefault = "INFO";

    /**
     * @param args
     */
    public static void main(String[] args) {
        LogManager.getRootLogger().setLevel(Level.toLevel(logDefault));
        initCMDOptions(args);
        initProperties();
        initDatabaseConnection();
        initSpark();
    }

    private static void initDatabaseConnection() {
        BaseRepository.initializeConnections();
        log.info("Database Connection Enabled!");
    }

    private static void initSpark() {

        //Spark config
        //Spark.secure("keystore.jks", "password", null, null);
        spark.Spark.port(Integer.parseInt(PropertiesReader.getValue("APIPORT")));
        if (Boolean.parseBoolean((PropertiesReader.getValue("SPARK_THREAD_POOL")))) {
            int maxThreads = Integer.parseInt(PropertiesReader.getValue("SPARK_THREAD_POOL_MIN"));
            int minThreads = Integer.parseInt(PropertiesReader.getValue("SPARK_THREAD_POOL_MAX"));
            int idleTimeoutMilis = Integer.parseInt(PropertiesReader.getValue("SPARK_THREAD_POOL_TIMEOUT"));
            spark.Spark.threadPool(maxThreads, minThreads, idleTimeoutMilis);
        }

        Spark.staticFiles.location("/public");

        Spark.get("/sensorSource", SensorSourceController.serveSensorSourceListPage);

        Spark.get("/sensorSource/:id", SensorSourceController.serveSensorById);

        Spark.get("/sensor", SensorController.serveSensorListPage);

        Spark.get("/sensor/:id/measure", SensorController.serveSensorMeasureTypesBySensorId);

        Spark.get("/sensor/:id/measure/:measureTypeId/:startDate/:endDate", SensorController.serveSensorMeasuresBySensorIdAndDate);

        Spark.get("/sensor/:id/measure/:measureTypeId/:startDate", SensorController.serveSensorMeasuresBySensorIdAndDate);

        Spark.get("/sensor/:id", SensorController.serveSensorById);

        Spark.post("/sensor/upload", "multipart/form-data", SensorController.handleFileUpload);

        Spark.notFound((req, res) -> "{\"message\":\"Rout Not Found 404\"}");

        spark.Spark.exception(Exception.class, (exception, request, response) -> exception.printStackTrace());

        log.info("Web Server is Running...");
    }

    private static void initCMDOptions(String[] args) {

        options.addOption("c", "configuration", true, "Caminho para o arquivo de configuracao.");
        options.addOption("l", "log", true, "Habilitar ou desabilitar log.");
        options.addOption("v", "log-level", true, "Muda o nível do log (OFF, TRACE, INFO, DEBUG, WARN, ERROR, FATAL, ALL).");
        options.addOption("h", "help", false, "Mostrar ajuda.");

        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = null;

        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            log.error(e.getMessage());
            showHelp();
        }

        if (cmd.hasOption("c")) {
            _configFileName = cmd.getOptionValue("c");
        }

        if ((cmd.hasOption("l") && Boolean.parseBoolean(cmd.getOptionValue("l")))) {
            BasicConfigurator.configure();
        }

        if (cmd.hasOption("v")) {
            LogManager.getRootLogger().setLevel(Level.toLevel(cmd.getOptionValue("v")));
        }
    }

    public static void initProperties() {

        Path path = Paths.get(_configFileName);
        if (!Files.exists(path)) {
            log.error("Arquivo de configuracoes não encontrado no caminho \"" + path + "\".");
        } else {
            PropertiesReader.initialize(_configFileName);
            log.info("--------------------------");
            log.info("Config Properties File");
            log.info("--------------------------");
            log.info("HTTP API Port: " + PropertiesReader.getValue("APIPORT"));
            log.info("Database Type: " + PropertiesReader.getValue("DATABASETYPE"));
            log.info("Hibernate is On: " + PropertiesReader.getValue("USEHIBERNATE"));
            log.info("SQL Debug is On: " + PropertiesReader.getValue("SQL_DEBUG"));
            log.info("Thread Pool is On: " + Boolean.parseBoolean((PropertiesReader.getValue("SPARK_THREAD_POOL"))));
            if (Boolean.parseBoolean((PropertiesReader.getValue("SPARK_THREAD_POOL")))) {
                log.info("Thread Pool Timeout: " + Integer.parseInt((PropertiesReader.getValue("SPARK_THREAD_POOL_TIMEOUT"))));
                log.info("Thread Pool Min: " + Integer.parseInt((PropertiesReader.getValue("SPARK_THREAD_POOL_MIN"))));
                log.info("Thread Pool Max: " + Integer.parseInt((PropertiesReader.getValue("SPARK_THREAD_POOL_MAX"))));
            }
            log.info("--------------------------");
        }
    }

    /**
     *
     */
    private static void showHelp() {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("Main", options);
        System.exit(0);
    }
}

