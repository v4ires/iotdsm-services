package controllers;

import deserialization.OpenWeatherJsonDeserializer;
import model.Sensor;
import model.SensorMeasure;
import org.hibernate.Session;
import org.hibernate.Transaction;
import persistence.GenericJPA;
import persistence.SensorSQL;
import spark.Request;
import spark.Response;
import spark.Route;
import utils.PropertiesReader;
import utils.hibernate.CustomTransation;
import utils.hibernate.HibernateUtil;
import utils.sql.JDBConnection;
import utils.sql.SQLQueryDatabase;

import javax.servlet.MultipartConfigElement;
import javax.servlet.http.Part;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class SensorController {

    public static Route serveSensorListPage = (Request request, Response response) -> {
        response.status(200);
        response.type("application/json");

        if (Boolean.parseBoolean(PropertiesReader.getValue("USEHIBERNATE"))) {
            Session session = HibernateUtil.getSessionFactory().openSession();
            Transaction transaction = session.beginTransaction();
            List<Sensor> sensors = new GenericJPA<>(Sensor.class).findAll(new CustomTransation(session, transaction));

            return sensors;
        } else {
            JDBConnection jdbConnection = JDBConnection
                    .builder().user(PropertiesReader.getValue("USER")).pass(PropertiesReader.getValue("PASSWORD"))
                    .urlConn("jdbc:" + PropertiesReader.getValue("DATABASETYPE") + "://" + PropertiesReader.getValue("HOST") + ":" + PropertiesReader.getValue("PORT") + "/" + PropertiesReader.getValue("DATABASE"))
                    .classDriver(PropertiesReader.getValue("DRIVER"))
                    .build();

            SensorSQL sensorSql = new SensorSQL(jdbConnection);

            try {
                List<Sensor> sensors = (List<Sensor>) (Object) sensorSql.select_sql(SQLQueryDatabase.mySqlSensorQuery);

                return sensors;
            } catch (SQLException e) {
                e.printStackTrace();
                return new ArrayList<Sensor>();
            }
        }
    };
    public static Route serveSensorById = (Request request, Response response) -> {
        if (request.params("id") == null || request.params("id").equals("")) {
            response.status(400);
            response.type("application/json");
            return "{\"error\": \"Invalid sensor id.\"";
        }

        Long sensorId;

        try {
            sensorId = Long.parseLong(request.params("id"));
        } catch (Exception ex) {
            response.status(400);
            response.type("application/json");
            return "{\"error\": \"Invalid sensor id.\"";
        }

        response.status(200);
        response.type("application/json");

        if (Boolean.parseBoolean(PropertiesReader.getValue("USEHIBERNATE"))) {
            Session session = HibernateUtil.getSessionFactory().openSession();
            Transaction transaction = session.beginTransaction();
            Sensor sensor = new GenericJPA<>(Sensor.class).findById(new CustomTransation(session, transaction), sensorId);

            return sensor;
        } else {
            JDBConnection jdbConnection = JDBConnection
                    .builder().user(PropertiesReader.getValue("USER")).pass(PropertiesReader.getValue("PASSWORD"))
                    .urlConn("jdbc:" + PropertiesReader.getValue("DATABASETYPE") + "://" + PropertiesReader.getValue("HOST") + ":" + PropertiesReader.getValue("PORT") + "/" + PropertiesReader.getValue("DATABASE"))
                    .classDriver(PropertiesReader.getValue("DRIVER"))
                    .build();

            SensorSQL sensorSql = new SensorSQL(jdbConnection);

            try {
                Sensor sensor = (Sensor) sensorSql.select_unique_sql(String.format(SQLQueryDatabase.mySqlUniqueSensorQuery, sensorId));

                return sensor;
            } catch (SQLException e) {
                e.printStackTrace();
                return null;
            }
        }
    };

    public static Route handleFileUpload = (Request request, Response response) -> {

        String location = PropertiesReader.getValue("UPLOADDIR");          // the directory location where files will be stored
        long maxFileSize = 100000000;       // the maximum size allowed for uploaded files
        long maxRequestSize = 100000000;    // the maximum size allowed for multipart/form-data requests
        int fileSizeThreshold = 1024;       // the size threshold after which files will be written to disk

        MultipartConfigElement multipartConfigElement = new MultipartConfigElement(
                location, maxFileSize, maxRequestSize, fileSizeThreshold);
        request.raw().setAttribute("org.eclipse.jetty.multipartConfig",
                multipartConfigElement);

        Collection<Part> parts = request.raw().getParts();
        for (Part part : parts) {
            System.out.println("Name: " + part.getName());
            System.out.println("Size: " + part.getSize());
            System.out.println("Filename: " + part.getSubmittedFileName());
        }

        String fName = request.raw().getPart("file").getSubmittedFileName();
        System.out.println("Title: " + request.raw().getParameter("title"));
        System.out.println("File: " + fName);

        Part uploadedFile = request.raw().getPart("file");
        Path out = Paths.get(location+"/" + fName);

        try (final InputStream in = uploadedFile.getInputStream()) {
            Files.copy(in, out, StandardCopyOption.REPLACE_EXISTING);
            uploadedFile.delete();
        }

        // cleanup
        multipartConfigElement = null;
        parts = null;
        uploadedFile = null;

        OpenWeatherJsonDeserializer deserializer = new OpenWeatherJsonDeserializer();
        deserializer.loadContent(out.toAbsolutePath().toString());

        List<SensorMeasure> smList = (List<SensorMeasure>)(Object) deserializer.readArray();

        return "OK";
    };
}


