package controllers;

import deserialization.OpenWeatherCsvDeserializer;
import deserialization.OpenWeatherJsonDeserializer;
import deserialization.OpenWeatherXmlDeserializer;
import model.HTTPCompressType;
import model.Sensor;
import model.SensorMeasure;
import model.SensorMeasureType;
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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * University of São Paulo
 * IoT Repository Module
 *
 * @author Vinícius Aires Barros <viniciusaires7@gmail.com>
 */
public class SensorController extends BaseController {

    /**
     *
     */
    public static Route serveSensorListPage = (Request request, Response response) -> {
        SensorRepository _sensorRepository = new SensorRepository();
        int limit = 0;
        int offset = 0;

        try {
            String outputFormat = "json";

            if (request.queryParams("output_format") != null && !request.queryParams("output_format").equals("")) {
                outputFormat = request.queryParams("output_format");
            }

            if (request.queryParams("limit") != null && !request.queryParams("limit").equals("")) {
                try {
                    limit = Integer.parseInt(request.queryParams("limit"));
                } catch (Exception ex) {
                    error(response, "Invalid value for limit.");
                }
            }

            if (request.queryParams("offset") != null && !request.queryParams("offset").equals("")) {
                try {
                    offset = Integer.parseInt(request.queryParams("offset"));
                } catch (Exception ex) {
                    error(response, "Invalid value for offset.");
                }
            }

            List<Sensor> sensorList = _sensorRepository.getSensors(limit, offset);

            switch (outputFormat) {
                default:
                case "json":
                    return successJSON(response, _gson.toJson(sensorList), null);
                case "json_gzip":
                    return successJSON(response, _gson.toJson(sensorList), HTTPCompressType.gzip);
                case "xml":
                    return successXml(response, sensorList, null);
                case "xml_gzip":
                    return successXml(response, _gson.toJson(sensorList), HTTPCompressType.gzip);
                case "csv":
                    return successCsv(response, sensorList, null);
                case "csv_gzip":
                    return successCsv(response, sensorList, HTTPCompressType.gzip);
            }
        } catch (Exception ex) {
            return serverError(response, ex);
        } finally {
            _sensorRepository.close();
        }
    };

    /**
     *
     */
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

            List<SensorMeasureType> sensorMeasureTypeList = _sensorMeasureTypeRepository.getSensorMeasureTypeBySensor(sensorId);

            switch (outputFormat) {
                default:
                case "json":
                    return successJSON(response, _gson.toJson(sensorMeasureTypeList), null);
                case "xml":
                    return successXml(response, sensorMeasureTypeList, null);
                case "csv":
                    return successCsv(response, sensorMeasureTypeList, null);
            }
        } catch (Exception ex) {
            return serverError(response, ex);
        } finally {
            _sensorMeasureTypeRepository.close();
        }
    };

    /**
     *
     */
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

            List<SensorMeasure> sensorMeasureList = _sensorMeasureRepository.getSensorMeasure(sensorId, measureTypeId, startDate, endDate);

            switch (outputFormat) {
                default:
                case "json":
                    return successJSON(response, _gson.toJson(sensorMeasureList), null);
                case "xml":
                    return successXml(response, sensorMeasureList, null);
                case "csv":
                    return successCsv(response, sensorMeasureList, null);
            }
        } catch (Exception ex) {
            return serverError(response, ex);
        } finally {
            _sensorMeasureRepository.close();
        }
    };

    /**
     *
     */
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

            Sensor sensor = _sensorRepository.getSensorById(sensorId);

            switch (outputFormat) {
                default:
                case "json":
                    return successJSON(response, _gson.toJson(sensor), null);
                case "xml":
                    return successXml(response, sensor, null);
                case "csv":
                    return successCsv(response, sensor, null);
            }
        } catch (Exception ex) {
            return serverError(response, ex);
        } finally {
            _sensorRepository.close();
        }
    };

    /**
     *
     */
    public static Route handleFileUpload = (Request request, Response response) -> {
        try {
            String inputFormat = "json";
            String location = System.getProperty("java.io.tmpdir");          // the directory location where files will be stored
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

            return successJSON(response, "{\"result\": \"OK. " + insertedMeasures + " medidas inseridas.\"}", null);
        } catch (Exception ex) {
            return serverError(response, ex);
        }
    };

}


