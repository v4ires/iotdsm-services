package controllers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import deserialization.OpenWeatherJsonDeserializer;
import model.Sensor;
import model.SensorMeasure;
import model.SensorMeasureType;
import model.SensorSource;
import repositories.SensorMeasureRepository;
import repositories.SensorMeasureTypeRepository;
import repositories.SensorRepository;
import repositories.SensorSourceRepository;
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
import java.util.*;

public class SensorSourceController extends BaseController {
    private static Gson _gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();

    public static Route serveSensorSourceListPage = (Request request, Response response) -> {
        try {
            String outputFormat = "json";

            if (request.queryParams("output_format") != null && !request.queryParams("output_format").equals("")) {
                outputFormat = request.queryParams("output_format");
            }

            switch (outputFormat) {
                default:
                case "json":
                    return success(response, _gson.toJson(new SensorSourceRepository().getSensorSources()));
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
                return error(response, "Invalid sensor source id.");
            }

            if (request.queryParams("output_format") != null && !request.queryParams("output_format").equals("")) {
                outputFormat = request.queryParams("output_format");
            }

            try {
                sensorId = Long.parseLong(request.params("id"));
            } catch (Exception ex) {
                return error(response, "Invalid sensor source id.");
            }

            switch (outputFormat) {
                default:
                case "json":
                    return success(response, _gson.toJson(new SensorSourceRepository().getSensorSourceById(sensorId)));
            }
        } catch (Exception ex) {
            return serverError(response, ex);
        }
    };

}


