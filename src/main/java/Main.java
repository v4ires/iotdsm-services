import com.google.gson.Gson;
import controllers.SensorController;
import org.apache.log4j.BasicConfigurator;
import controllers.SensorSourceController;
import org.apache.commons.cli.*;
import utils.PropertiesReader;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {

    private static String _configFileName = "out/production/resources/config.properties";
    private static Options options = new Options();

    public static void main(String[] args) {
        BasicConfigurator.configure();

        options.addOption("c", "configuration", true, "Caminho para o arquivo de configuração.");
        options.addOption("h", "help", false, "Mostra ajuda.");

        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = null;

        try
        {
            cmd = parser.parse(options, args);

        } catch (ParseException e) {
            showHelp();
        }

        if(cmd.hasOption("c"))
            _configFileName = cmd.getOptionValue("c");

        Path path = Paths.get(_configFileName);

        if (!Files.exists(path)) {
            System.out.println("Arquivo de configurações \"config.properties\" não encontrado no caminho \""+path+"\".");
            return;
        }

        PropertiesReader.initialize(_configFileName);

        //Spark config
        spark.Spark.port(Integer.parseInt(PropertiesReader.getValue("APIPORT")));

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
    }

    private static void showHelp()
    {
        HelpFormatter formater = new HelpFormatter();

        formater.printHelp("Main", options);

        System.exit(0);
    }
}
