package controllers;

import repositories.SensorRepository;
import repositories.SensorSourceRepository;
import spark.Request;
import spark.Response;
import spark.Route;

public class SensorSourceController extends BaseController {

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
                case "xml":
                    return successXml(response, new SensorSourceRepository().getSensorSources());
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
                case "xml":
                    return successXml(response, new SensorSourceRepository().getSensorSourceById(sensorId));
            }
        } catch (Exception ex) {
            return serverError(response, ex);
        }
    };

}


