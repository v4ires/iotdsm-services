import controllers.SensorController;
import controllers.SensorSourceController;
import org.apache.commons.cli.*;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import repositories.BaseRepository;
import spark.Spark;
import utils.PropertiesReader;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

/**
 * University of Sao Paulo
 * IoT Repository Module
 *
 * @author Vinicius Aires Barros <viniciusaires@usp.br>
 */
public class Main {

    private static final Logger log = LoggerFactory.getLogger(Main.class);
    private static String _configFileName = "config.properties";
    private static String _log4jFile = "log4j.properties";
    private static String _logLevel = "ALL";
    private static Options options = new Options();

    /**
     * @param args
     */
    public static void main(String[] args) {
        System.out.println("Starting IoT Repository Module...");
        initOptions(args);
        initServerProperties();
        initDatabaseConnection();
        initSpark();
    }

    /**
     *
     */
    private static void initDatabaseConnection() {
        BaseRepository.initializeConnections();
        log.info("Database Connection Enabled!");
    }

    /**
     *
     */
    private static void initSpark() {
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

    /**
     * @param args
     */
    private static void initOptions(String[] args) {
        options.addOption("c", "configuration", true, "Caminho para o arquivo de configuracao [config.properties].");
        options.addOption("l", "log", true, "Habilitar ou desabilitar log [true, false].");
        options.addOption("lf", "log-file", true, "Arquivo de Configuracao do Log4J [log4j.properties].");
        options.addOption("lv", "log-level", true, "Muda o nivel do log [OFF, TRACE, INFO, DEBUG, WARN, ERROR, FATAL, ALL].");
        options.addOption("h", "help", false, "Mostrar ajuda [true, false].");

        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = null;

        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            log.error(e.getMessage());
            showHelp();
        }

        if (cmd.hasOption("h")) {
            showHelp();
            System.exit(0);
        }

        if (cmd.hasOption("c")) {
            _configFileName = cmd.getOptionValue("c");
        }

        if (cmd.hasOption("l")) {
            if (Boolean.parseBoolean(cmd.getOptionValue("l"))) {
                if (cmd.hasOption("lf")) {
                    _log4jFile = cmd.getOptionValue("lf");
                }
                if (cmd.hasOption("lv")) {
                    _logLevel = cmd.getOptionValue("lv");
                }
                enableLog4J(_logLevel);
            } else {
                disableLog4J();
            }
        }
    }

    /**
     *
     */
    public static void initServerProperties() {
        Path path = Paths.get(_configFileName);
        if (!Files.exists(path)) {
            log.error("Arquivo de configuracoes nao encontrado no caminho \"" + path + "\".");
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

    /**
     *
     */
    private static void enableLog4J(String logLevel) {
        LogManager.getRootLogger().setLevel(Level.toLevel(_logLevel));
        Properties properties = PropertiesReader.initialize(_log4jFile);
        LogManager.getRootLogger().setLevel(Level.toLevel(logLevel));
        LogManager.resetConfiguration();
        PropertyConfigurator.configure(properties);
    }

    private static void disableLog4J() {
        LogManager.resetConfiguration();
    }
}