package controllers;

import deserialization.OpenWeatherCsvDeserializer;
import deserialization.OpenWeatherJsonDeserializer;
import deserialization.OpenWeatherXmlDeserializer;
import repositories.SensorMeasureRepository;
import repositories.SensorMeasureTypeRepository;
import repositories.SensorRepository;
import services.SensorService;
import spark.Request;
import spark.Response;
import spark.Route;
import utils.PropertiesReader;

import javax.servlet.MultipartConfigElement;
import javax.servlet.http.Part;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SensorController extends BaseController {

    public static Route serveSensorListPage = (Request request, Response response) -> {
        SensorRepository _sensorRepository = new SensorRepository();

        try {
            String outputFormat = "json";

            if (request.queryParams("output_format") != null && !request.queryParams("output_format").equals("")) {
                outputFormat = request.queryParams("output_format");
            }

            switch (outputFormat) {
                default:
                case "json":
                    return success(response, _gson.toJson(_sensorRepository.getSensors()));
                case "xml":
                    return successXml(response, _sensorRepository.getSensors());
                case "csv":
                    return successCsv(response, _sensorRepository.getSensors());

            }
        } catch (Exception ex) {
            return serverError(response, ex);
        }
        finally {
            _sensorRepository.close();
        }
    };

    public static Route serveSensorMeasureTypesBySensorId = (Request request, Response response) -> {
        SensorMeasureTypeRepository _sensorMeasureTypeRepository = new SensorMeasureTypeRepository();
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
                    return success(response, _gson.toJson(_sensorMeasureTypeRepository.getSensorMeasureTypeBySensor(sensorId)));
                case "xml":
                    return successXml(response, _sensorMeasureTypeRepository.getSensorMeasureTypeBySensor(sensorId));
                case "csv":
                    return successCsv(response, _sensorMeasureTypeRepository.getSensorMeasureTypeBySensor(sensorId));
            }
        } catch (Exception ex) {
            return serverError(response, ex);
        }
        finally{
            _sensorMeasureTypeRepository.close();
        }
    };

    public static Route serveSensorMeasuresBySensorIdAndDate = (Request request, Response response) -> {
        SensorMeasureRepository _sensorMeasureRepository = new SensorMeasureRepository();

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
                    return success(response, _gson.toJson(_sensorMeasureRepository.getSensorMeasure(sensorId, measureTypeId, startDate, endDate)));
                case "xml":
                    return successXml(response, _sensorMeasureRepository.getSensorMeasure(sensorId, measureTypeId, startDate, endDate));
                case "csv":
                    return successCsv(response, _sensorMeasureRepository.getSensorMeasure(sensorId, measureTypeId, startDate, endDate));
            }
        } catch (Exception ex) {
            return serverError(response, ex);
        }
        finally {
            _sensorMeasureRepository.close();
        }
    };

    public static Route serveSensorById = (Request request, Response response) -> {
        SensorRepository _sensorRepository = new SensorRepository();

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
                    return success(response, _gson.toJson(_sensorRepository.getSensorById(sensorId)));
                case "xml":
                    return successXml(response, _sensorRepository.getSensorById(sensorId));
                case "csv":
                    return successCsv(response, _sensorRepository.getSensorById(sensorId));
            }
        } catch (Exception ex) {
            return serverError(response, ex);
        }
        finally {
            _sensorRepository.close();
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
                    System.getProperty("java.io.tmpdir"), maxFileSize, maxRequestSize, fileSizeThreshold);
            request.raw().setAttribute("org.eclipse.jetty.multipartConfig",
                    multipartConfigElement);

            String fName = request.raw().getPart("file").getSubmittedFileName();

            Part uploadedFile = request.raw().getPart("file");

            uploadedFile.write(location + "/" + fName);

            // cleanup
            multipartConfigElement = null;
            uploadedFile = null;

            SensorService sensorService = new SensorService();

            switch (inputFormat) {
                case "csv":
                    OpenWeatherCsvDeserializer csvDeserializer = new OpenWeatherCsvDeserializer();
                    csvDeserializer.loadContent(location + "/" + fName, ",");

                    insertedMeasures = sensorService.deserializeMeasures(csvDeserializer);

                    csvDeserializer.close();
                    break;
                case "xml":
                    OpenWeatherXmlDeserializer xmlDeserializer = new OpenWeatherXmlDeserializer();
                    xmlDeserializer.loadContent(location + "/" + fName);

                    insertedMeasures = sensorService.deserializeMeasures(xmlDeserializer);

                    xmlDeserializer.close();
                    break;
                default:
                case "json":
                    OpenWeatherJsonDeserializer deserializer = new OpenWeatherJsonDeserializer();
                    deserializer.loadContent(location + "/" + fName);

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


