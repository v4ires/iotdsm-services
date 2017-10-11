import com.google.gson.Gson;
import controllers.SensorController;
import utils.PropertiesReader;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {

    private static String _configFileName = "out/production/resources/config.properties";

    public static void main(String[] args) {
        Path path = Paths.get(_configFileName);

        if (!Files.exists(path)) {
            System.out.println("Arquivo de configurações \"config.properties\" não encontrado.");
            return;
        }

        PropertiesReader.initialize(_configFileName);

        Gson gson = new Gson();

        //Spark config
        spark.Spark.port(Integer.parseInt(PropertiesReader.getValue("APIPORT")));

        spark.Spark.get("/sensor", SensorController.serveSensorListPage, gson::toJson);
    }
}
