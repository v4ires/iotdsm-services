package controllers;

import deserialization.OpenWeatherCsvDeserializer;
import deserialization.OpenWeatherJsonDeserializer;
import deserialization.OpenWeatherXmlDeserializer;
import model.HTTPCompressType;
import model.Sensor;
import model.SensorMeasure;
import model.SensorMeasureType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import repositories.SensorMeasureRepository;
import repositories.SensorMeasureTypeRepository;
import repositories.SensorRepository;
import services.SensorService;
import spark.Request;
import spark.Response;
import spark.Route;
import utils.Utils;

import javax.servlet.MultipartConfigElement;
import javax.servlet.http.Part;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * University of São Paulo
 * IoT Repository Module
 *
 * @author Vinícius Aires Barros <viniciusaires@usp.br>
 */
public class SensorController extends BaseController {

    private static final Logger log = LoggerFactory.getLogger(SensorController.class);

    /**
     *
     */
    public static Route serveSensorListPage = (Request request, Response response) -> {
        SensorRepository _sensorRepository = new SensorRepository();
        String outputFormat = "json";
        String output_time = "thread-id: %d operation: serialization limit: %d output_format: %s %s";
        String httpResponse = "";
        int limit = 0;
        int offset = 0;
        long start = 0;
        long end = 0;

        try {
            if (request.queryParams("output_format") != null && !request.queryParams("output_format").equals("")) {
                outputFormat = request.queryParams("output_format");
            }

            if (request.queryParams("limit") != null && !request.queryParams("limit").equals("")) {
                try {
                    limit = Integer.parseInt(request.queryParams("limit"));
                } catch (Exception ex) {
                    log.error(ex.getMessage());
                    error(response, "Invalid value for limit.");
                }
            }

            if (request.queryParams("offset") != null && !request.queryParams("offset").equals("")) {
                try {
                    offset = Integer.parseInt(request.queryParams("offset"));
                } catch (Exception ex) {
                    log.error(ex.getMessage());
                    error(response, "Invalid value for offset.");
                }
            }

            List<Sensor> sensorList = _sensorRepository.getSensors(limit, offset);
            start = System.currentTimeMillis();

            switch (outputFormat) {
                default:
                case "json":
                    httpResponse = successJSON(response, _gson.toJson(sensorList), null);
                    break;
                case "json_gzip":
                    httpResponse = successJSON(response, _gson.toJson(sensorList), HTTPCompressType.gzip);
                    break;
                case "xml":
                    httpResponse = successXml(response, sensorList, null);
                    break;
                case "xml_gzip":
                    httpResponse = successXml(response, _gson.toJson(sensorList), HTTPCompressType.gzip);
                    break;
                case "csv":
                    httpResponse = successCsv(response, sensorList, null);
                    break;
                case "csv_gzip":
                    httpResponse = successCsv(response, sensorList, HTTPCompressType.gzip);
                    break;
            }
        } catch (Exception ex) {
            log.error(ex.getMessage());
            return serverError(response, ex);
        } finally {
            _sensorRepository.close();
            end = System.currentTimeMillis();
            output_time = String.format(output_time, Thread.currentThread().getId(),
                    limit, outputFormat, Utils.printElapsedTime(start, end));
            log.info(output_time);
        }
        return httpResponse;
    };

    /**
     *
     */
    public static Route serveSensorMeasureTypesBySensorId = (Request request, Response response) -> {
        SensorMeasureTypeRepository _sensorMeasureTypeRepository = new SensorMeasureTypeRepository();
        String outputFormat = "json";
        String output_time = "thread-id: %d operation: serialization output_format: %s %s";
        String httpResponse = "";
        long start = 0;
        long end = 0;

        try {
            Long sensorId;

            if (request.params("id") == null || request.params("id").equals("")) {
                log.warn("Invalid sensor id.");
                return error(response, "Invalid sensor id.");
            }

            if (request.queryParams("output_format") != null && !request.queryParams("output_format").equals("")) {
                outputFormat = request.queryParams("output_format");
            }

            try {
                sensorId = Long.parseLong(request.params("id"));
            } catch (Exception ex) {
                log.error(ex.getMessage());
                return error(response, "Invalid sensor id.");
            }

            List<SensorMeasureType> sensorMeasureTypeList = _sensorMeasureTypeRepository.getSensorMeasureTypeBySensor(sensorId);
            start = System.currentTimeMillis();
            switch (outputFormat) {
                default:
                case "json":
                    httpResponse = successJSON(response, _gson.toJson(sensorMeasureTypeList), null);
                    break;
                case "xml":
                    httpResponse = successXml(response, sensorMeasureTypeList, null);
                    break;
                case "csv":
                    httpResponse = successCsv(response, sensorMeasureTypeList, null);
                    break;
            }
        } catch (Exception ex) {
            log.error(ex.getMessage());
            return serverError(response, ex);
        } finally {
            _sensorMeasureTypeRepository.close();
            end = System.currentTimeMillis();
            output_time = String.format(output_time, Thread.currentThread().getId(), outputFormat, Utils.printElapsedTime(start, end));
            log.info(output_time);
        }
        return httpResponse;
    };

    /**
     *
     */
    public static Route serveSensorMeasuresBySensorIdAndDate = (Request request, Response response) -> {
        SensorMeasureRepository _sensorMeasureRepository = new SensorMeasureRepository();
        String outputFormat = "json";
        String output_time = "thread-id: %d operation: serialization output_format: %s %s";
        String httpResponse = "";
        long start = 0;
        long end = 0;

        try {
            Long sensorId;
            Long measureTypeId;
            Date startDate;
            Date endDate = new Date(Long.MAX_VALUE);

            if (request.params("id") == null || request.params("id").equals("")) {
                log.warn("Invalid sensor id.");
                return error(response, "Invalid sensor id.");
            }

            if (request.params("measureTypeId") == null || request.params("measureTypeId").equals("")) {
                log.warn("Invalid measure type id.");
                return error(response, "Invalid measure type id.");
            }

            if (request.params("startDate") == null || request.params("startDate").equals("")) {
                log.warn("Invalid start date. Must be in ISO-8601 format.");
                return error(response, "Invalid start date. Must be in ISO-8601 format.");
            }

            if (request.queryParams("output_format") != null && !request.queryParams("output_format").equals("")) {
                outputFormat = request.queryParams("output_format");
            }

            try {
                sensorId = Long.parseLong(request.params("id"));
            } catch (Exception ex) {
                log.error("Invalid sensor id.");
                return error(response, "Invalid sensor id.");
            }

            try {
                measureTypeId = Long.parseLong(request.params("measureTypeId"));
            } catch (Exception ex) {
                log.error("Invalid measure type id.");
                return error(response, "Invalid measure type id.");
            }

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX");

            try {
                startDate = simpleDateFormat.parse(request.params("startDate"));
            } catch (Exception ex) {
                log.error("Invalid start date. Must be in ISO-8601 format.");
                return error(response, "Invalid start date. Must be in ISO-8601 format.");
            }

            if (request.params("endDate") != null && !request.params("endDate").equals("")) {
                try {
                    endDate = simpleDateFormat.parse(request.params("endDate"));
                } catch (Exception ex) {
                    log.error("Invalid end date. Must be in ISO-8601 format.");
                    return error(response, "Invalid end date. Must be in ISO-8601 format.");
                }
            }

            List<SensorMeasure> sensorMeasureList = _sensorMeasureRepository.getSensorMeasure(sensorId, measureTypeId, startDate, endDate);
            start = System.currentTimeMillis();
            switch (outputFormat) {
                default:
                case "json":
                    httpResponse = successJSON(response, _gson.toJson(sensorMeasureList), null);
                    break;
                case "xml":
                    httpResponse = successXml(response, sensorMeasureList, null);
                    break;
                case "csv":
                    httpResponse = successCsv(response, sensorMeasureList, null);
                    break;
            }
        } catch (Exception ex) {
            log.error(ex.getMessage());
            return serverError(response, ex);
        } finally {
            _sensorMeasureRepository.close();
            end = System.currentTimeMillis();
            output_time = String.format(output_time, Thread.currentThread().getId(), outputFormat, Utils.printElapsedTime(start, end));
            log.info(output_time);
        }
        return httpResponse;
    };

    /**
     *
     */
    public static Route serveSensorById = (Request request, Response response) -> {
        SensorRepository _sensorRepository = new SensorRepository();
        String outputFormat = "json";
        String output_time = "thread-id: %d operation: serialization output_format: %s %s";
        String httpResponse = "";
        long start = 0;
        long end = 0;

        try {
            Long sensorId;

            if (request.params("id") == null || request.params("id").equals("")) {
                log.warn("Invalid sensor id.");
                return error(response, "Invalid sensor id.");
            }

            if (request.queryParams("output_format") != null && !request.queryParams("output_format").equals("")) {
                outputFormat = request.queryParams("output_format");
            }

            try {
                sensorId = Long.parseLong(request.params("id"));
            } catch (Exception ex) {
                log.error("Invalid sensor id.");
                return error(response, "Invalid sensor id.");
            }

            Sensor sensor = _sensorRepository.getSensorById(sensorId);
            start = System.currentTimeMillis();
            switch (outputFormat) {
                default:
                case "json":
                    httpResponse = successJSON(response, _gson.toJson(sensor), null);
                    break;
                case "xml":
                    httpResponse = successXml(response, sensor, null);
                    break;
                case "csv":
                    httpResponse = successCsv(response, sensor, null);
                    break;
            }
        } catch (Exception ex) {
            log.error(ex.getMessage());
            return serverError(response, ex);
        } finally {
            _sensorRepository.close();
            end = System.currentTimeMillis();
            output_time = String.format(output_time, Thread.currentThread().getId(), outputFormat, Utils.printElapsedTime(start, end));
            log.info(output_time);
        }
        return httpResponse;
    };

    /**
     *
     */
    public static Route handleFileUpload = (Request request, Response response) -> {
        String inputFormat = "json";
        String output_time = "thread-id: %d operation: serialization output_format: %s %s";
        String httpResponse = "";
        long start = 0;
        long end = 0;
        try {
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
            httpResponse = successJSON(response, "{\"result\": \"OK. " + insertedMeasures + " medidas inseridas.\"}", null);
        } catch (Exception ex) {
            log.error(ex.getMessage());
            return serverError(response, ex);
        } finally {
            end = System.currentTimeMillis();
            output_time = String.format(output_time, Thread.currentThread().getId(), inputFormat, Utils.printElapsedTime(start, end));
            log.info(output_time);
        }
        return httpResponse;
    };
}


