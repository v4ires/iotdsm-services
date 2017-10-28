import controllers.SensorController;
import controllers.SensorSourceController;
import org.apache.commons.cli.*;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.slf4j.LoggerFactory;
import repositories.BaseRepository;
import utils.PropertiesReader;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {

    private static String _configFileName = "config.properties";
    private static Options options = new Options();

    public static void main(String[] args) {

        options.addOption("c", "configuration", true, "Caminho para o arquivo de configuracao.");
        options.addOption("l", "log", true, "Habilitar ou desabilitar log.");
        options.addOption("v", "log-level", true, "Muda o nivel do log. (OFF, TRACE, INFO, DEBUG, WARN, ERROR, FATAL, ALL)");
        options.addOption("h", "help", false, "Mostra ajuda.");

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

        if (cmd.hasOption("l")) {
            if (Boolean.parseBoolean(cmd.getOptionValue("l"))) {
                BasicConfigurator.configure();
            }
        }

        if (cmd.hasOption("v")) {
            LogManager.getRootLogger().setLevel(Level.toLevel(cmd.getOptionValue("v")));
        }

        Path path = Paths.get(_configFileName);

        if (!Files.exists(path)) {
            System.out.println("Arquivo de configuracoes não encontrado no caminho \"" + path + "\".");
            return;
        }

        PropertiesReader.initialize(_configFileName);

        System.out.println("Database Type: " + PropertiesReader.getValue("DATABASETYPE"));
        System.out.println("Hibernate is On: " + PropertiesReader.getValue("USEHIBERNATE"));
        System.out.println("SQL Debug is On: " + PropertiesReader.getValue("SQL_DEBUG"));

        //Spark config
        spark.Spark.port(Integer.parseInt(PropertiesReader.getValue("APIPORT")));
        //int cores = Runtime.getRuntime().availableProcessors();
        spark.Spark.threadPool(8, 2, 30000);

        spark.Spark.get("/sensorSource", SensorSourceController.serveSensorSourceListPage);
        spark.Spark.get("/sensorSource/:id", SensorSourceController.serveSensorById);
        spark.Spark.get("/sensor", SensorController.serveSensorListPage);
        spark.Spark.get("/sensor/:id/measure", SensorController.serveSensorMeasureTypesBySensorId);
        spark.Spark.get("/sensor/:id/measure/:measureTypeId/:startDate/:endDate", SensorController.serveSensorMeasuresBySensorIdAndDate);
        spark.Spark.get("/sensor/:id/measure/:measureTypeId/:startDate", SensorController.serveSensorMeasuresBySensorIdAndDate);
        spark.Spark.get("/sensor/:id", SensorController.serveSensorById);
        spark.Spark.post("/sensor/upload", "multipart/form-data", SensorController.handleFileUpload);

        spark.Spark.exception(Exception.class, (exception, request, response) -> {
            exception.printStackTrace();
        });

        BaseRepository.initializeConnections();
    }

    private static void showHelp() {
        HelpFormatter formatter = new HelpFormatter();

        formatter.printHelp("Main", options);

        System.exit(0);
    }
}

