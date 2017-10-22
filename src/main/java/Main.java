import com.google.gson.Gson;
import controllers.SensorController;
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

        SensorSourceController sensorSourceController = new SensorSourceController();
        spark.Spark.get("/sensorSource", sensorSourceController.serveSensorSourceListPage);
        spark.Spark.get("/sensorSource/:id", sensorSourceController.serveSensorById);

        SensorController sensorController = new SensorController();
        spark.Spark.get("/sensor", sensorController.serveSensorListPage);
        spark.Spark.get("/sensor/:id/measure", sensorController.serveSensorMeasureTypesBySensorId);
        spark.Spark.get("/sensor/:id/measure/:measureTypeId/:startDate/:endDate", sensorController.serveSensorMeasuresBySensorIdAndDate);
        spark.Spark.get("/sensor/:id/measure/:measureTypeId/:startDate", sensorController.serveSensorMeasuresBySensorIdAndDate);
        spark.Spark.get("/sensor/:id", sensorController.serveSensorById);
        spark.Spark.post("/sensor/upload", "multipart/form-data", sensorController.handleFileUpload);

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
