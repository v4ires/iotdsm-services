package edu.usp.icmc.lasdpc;

import edu.usp.icmc.lasdpc.deserialization.VitalSignsHealthDataCsvDeserializer;
import edu.usp.icmc.lasdpc.repositories.BaseRepository;
import edu.usp.icmc.lasdpc.services.SensorService;
import edu.usp.icmc.lasdpc.utils.PropertiesReader;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {

    public static void main(String[] args) {
        initServerProperties();
        initDatabaseConnection();

        String fName = "vital_signs_health_data.csv";
        //String fName = "xaa";

        SensorService sensorService = new SensorService();
        VitalSignsHealthDataCsvDeserializer deserializer = new VitalSignsHealthDataCsvDeserializer();
        deserializer.loadContent("/home/aires/dataset/final_trauma_v2/" + fName);
        long insertedMeasures = sensorService.deserializeMeasures(deserializer);
        System.out.println(insertedMeasures);
    }

    private static void initDatabaseConnection() {
        BaseRepository.initializeConnections();
        System.out.println("Database Connection Enabled!");
    }

    private static void initServerProperties() {
        Path path = Paths.get("pgsql-hb.properties");
        if (Files.exists(path)) {
            PropertiesReader.initialize("pgsql-hb.properties");
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
        } else {
            System.out.println("Arquivo de configuracoes nao encontrado no caminho " + path);
        }
    }
}
