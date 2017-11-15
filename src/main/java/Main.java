import controllers.SensorController;
import controllers.SensorSourceController;
import org.apache.commons.cli.*;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
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
 * @author Vinícius Aires Barros <viniciusaires7@gmail.com>
 */
public class Main {

    private static String _configFileName = "config.properties";
    private static Options options = new Options();

    /**
     * @param args
     */
    public static void main(String[] args) {
        initCMDOptions(args);
        initProperties();
        initDatabaseConnection();
        initSpark();
    }

    private static void initDatabaseConnection() {
        BaseRepository.initializeConnections();
        System.out.println("Database Connection Enabled!");
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

        System.out.println("Web Server is Running...");
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
            System.out.println("Arquivo de configuracoes não encontrado no caminho \"" + path + "\".");
        } else {
            PropertiesReader.initialize(_configFileName);
            System.out.println("--------------------------");
            System.out.println("Config Properties File");
            System.out.println("--------------------------");
            System.out.println("HTTP API Port: " + PropertiesReader.getValue("APIPORT"));
            System.out.println("Database Type: " + PropertiesReader.getValue("DATABASETYPE"));
            System.out.println("Hibernate is On: " + PropertiesReader.getValue("USEHIBERNATE"));
            System.out.println("SQL Debug is On: " + PropertiesReader.getValue("SQL_DEBUG"));
            System.out.println("Thread Pool is On: " + Boolean.parseBoolean((PropertiesReader.getValue("SPARK_THREAD_POOL"))));
            if (Boolean.parseBoolean((PropertiesReader.getValue("SPARK_THREAD_POOL")))) {
                System.out.println("Thread Pool Timeout: " + Integer.parseInt((PropertiesReader.getValue("SPARK_THREAD_POOL_TIMEOUT"))));
                System.out.println("Thread Pool Min: " + Integer.parseInt((PropertiesReader.getValue("SPARK_THREAD_POOL_MIN"))));
                System.out.println("Thread Pool Max: " + Integer.parseInt((PropertiesReader.getValue("SPARK_THREAD_POOL_MAX"))));
            }
            System.out.println("--------------------------");
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

