package controllers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import deserialization.OpenWeatherCsvDeserializer;
import deserialization.OpenWeatherJsonDeserializer;
import model.Sensor;
import model.SensorMeasure;
import model.SensorMeasureType;
import model.SensorSource;
import repositories.SensorMeasureRepository;
import repositories.SensorMeasureTypeRepository;
import repositories.SensorRepository;
import repositories.SensorSourceRepository;
import services.SensorService;
import spark.Request;
import spark.Response;
import spark.Route;
import utils.PropertiesReader;
import utils.hibernate.CustomTransation;

import javax.servlet.MultipartConfigElement;
import javax.servlet.http.Part;
import java.io.InputStream;
import java.nio.file.*;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.*;

public class SensorController extends BaseController {
    private static Gson _gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();

    public static Route serveSensorListPage = (Request request, Response response) -> {
        try {
            String outputFormat = "json";

            if (request.queryParams("output_format") != null && !request.queryParams("output_format").equals("")) {
                outputFormat = request.queryParams("output_format");
            }

            switch (outputFormat) {
                default:
                case "json":
                    return success(response, _gson.toJson(new SensorRepository().getSensors()));
            }
        } catch (Exception ex) {
            return serverError(response, ex);
        }
    };

    public static Route serveSensorMeasureTypesBySensorId = (Request request, Response response) -> {
        try {
            String outputFormat = "json";
            Long sensorId;

            if (request.params("id") == null || request.params("id").equals("")) {
                return error(response, "Invalid sensor id.");
            }

            if (request.queryParams("output_format") != null && !request.queryParams("output_format").equals("")) {
                outputFormat = request.queryParams("output_format");
            }

            try {
                sensorId = Long.parseLong(request.params("id"));
            } catch (Exception ex) {
                return error(response, "Invalid sensor id.");
            }

            switch (outputFormat) {
                default:
                case "json":
                    return success(response, _gson.toJson(new SensorMeasureTypeRepository().getSensorMeasureTypeBySensor(sensorId)));
            }
        } catch (Exception ex) {
            return serverError(response, ex);
        }
    };

    public static Route serveSensorMeasuresBySensorIdAndDate = (Request request, Response response) -> {
        try {
            String outputFormat = "json";
            Long sensorId;
            Long measureTypeId;
            Date startDate;
            Date endDate = new Date(Long.MAX_VALUE);

            if (request.params("id") == null || request.params("id").equals("")) {
                return error(response, "Invalid sensor id.");
            }

            if (request.params("measureTypeId") == null || request.params("measureTypeId").equals("")) {
                return error(response, "Invalid measure type id.");
            }

            if (request.params("startDate") == null || request.params("startDate").equals("")) {
                return error(response, "Invalid start date. Must be in ISO-8601 format.");
            }

            if (request.queryParams("output_format") != null && !request.queryParams("output_format").equals("")) {
                outputFormat = request.queryParams("output_format");
            }

            try {
                sensorId = Long.parseLong(request.params("id"));
            } catch (Exception ex) {
                return error(response, "Invalid sensor id.");
            }

            try {
                measureTypeId = Long.parseLong(request.params("measureTypeId"));
            } catch (Exception ex) {
                return error(response, "Invalid measure type id.");
            }

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX");

            try {
                startDate = simpleDateFormat.parse(request.params("startDate"));
            } catch (Exception ex) {
                return error(response, "Invalid start date. Must be in ISO-8601 format.");
            }

            if (request.params("endDate") != null && !request.params("endDate").equals("")) {
                try {
                    endDate = simpleDateFormat.parse(request.params("endDate"));
                } catch (Exception ex) {
                    return error(response, "Invalid end date. Must be in ISO-8601 format.");
                }
            }

            switch (outputFormat) {
                default:
                case "json":
                    return success(response, _gson.toJson(new SensorMeasureRepository().getSensorMeasure(sensorId, measureTypeId, startDate, endDate)));
            }
        } catch (Exception ex) {
            return serverError(response, ex);
        }
    };

    public static Route serveSensorById = (Request request, Response response) -> {
        try {
            String outputFormat = "json";
            Long sensorId;

            if (request.params("id") == null || request.params("id").equals("")) {
                return error(response, "Invalid sensor id.");
            }

            if (request.queryParams("output_format") != null && !request.queryParams("output_format").equals("")) {
                outputFormat = request.queryParams("output_format");
            }

            try {
                sensorId = Long.parseLong(request.params("id"));
            } catch (Exception ex) {
                return error(response, "Invalid sensor id.");
            }

            switch (outputFormat) {
                default:
                case "json":
                    return success(response, _gson.toJson(new SensorRepository().getSensorById(sensorId)));
            }
        } catch (Exception ex) {
            return serverError(response, ex);
        }
    };

    public static Route handleFileUpload = (Request request, Response response) -> {
        try {
            String inputFormat = "json";
            String location = PropertiesReader.getValue("UPLOADDIR");          // the directory location where files will be stored
            long maxFileSize = 10000000000L;       // the maximum size allowed for uploaded files
            long maxRequestSize = 10000000000L;    // the maximum size allowed for multipart/form-data requests
            int fileSizeThreshold = 1024;       // the size threshold after which files will be written to disk
            long insertedMeasures = 0;

            if (request.queryParams("input_format") != null && !request.queryParams("input_format").equals("")) {
                inputFormat = request.queryParams("input_format");
            }

            MultipartConfigElement multipartConfigElement = new MultipartConfigElement(
                    location, maxFileSize, maxRequestSize, fileSizeThreshold);
            request.raw().setAttribute("org.eclipse.jetty.multipartConfig",
                    multipartConfigElement);

            String fName = request.raw().getPart("file").getSubmittedFileName();

            Part uploadedFile = request.raw().getPart("file");
            Path out = Paths.get(location + "/" + fName);

            try (final InputStream in = uploadedFile.getInputStream()) {
                Files.copy(in, out, StandardCopyOption.REPLACE_EXISTING);
                uploadedFile.delete();
            }

            // cleanup
            multipartConfigElement = null;
            uploadedFile = null;

            SensorService sensorService = new SensorService();

            switch (inputFormat) {
                case "csv":
                    OpenWeatherCsvDeserializer csvDeserializer = new OpenWeatherCsvDeserializer();
                    csvDeserializer.loadContent(out.toAbsolutePath().toString(), ",");

                    insertedMeasures = sensorService.deserializeMeasures(csvDeserializer);

                    csvDeserializer.close();
                    break;
                default:
                case "json":
                    OpenWeatherJsonDeserializer deserializer = new OpenWeatherJsonDeserializer();
                    deserializer.loadContent(out.toAbsolutePath().toString());

                    insertedMeasures = sensorService.deserializeMeasures(deserializer);

                    deserializer.close();
                    break;
            }

            return success(response, "{\"result\": \"OK. " + insertedMeasures + " medidas inseridas.\"}");
        } catch (Exception ex) {
            return serverError(response, ex);
        }
    };

}


