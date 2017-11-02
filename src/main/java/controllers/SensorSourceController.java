package controllers;

import repositories.SensorSourceRepository;
import spark.Request;
import spark.Response;
import spark.Route;

/**
 * University of São Paulo
 * IoT Repository Module
 * @author Vinícius Aires Barros <viniciusaires7@gmail.com>
 */
public class SensorSourceController extends BaseController {

    /**
     *
     */
    public static Route serveSensorSourceListPage = (Request request, Response response) -> {
        SensorSourceRepository _sensorSourceRepository = new SensorSourceRepository();

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
                case "csv":
                    return successCsv(response, new SensorSourceRepository().getSensorSources());
            }
        } catch (Exception ex) {
            return serverError(response, ex);
        } finally {
            _sensorSourceRepository.close();
        }
    };

    /**
     *
     */
    public static Route serveSensorById = (Request request, Response response) -> {
        SensorSourceRepository _sensorSourceRepository = new SensorSourceRepository();

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
                    return success(response, _gson.toJson(_sensorSourceRepository.getSensorSourceById(sensorId)));
                case "xml":
                    return successXml(response, _sensorSourceRepository.getSensorSourceById(sensorId));
                case "csv":
                    return successCsv(response, _sensorSourceRepository.getSensorSourceById(sensorId));

            }
        } catch (Exception ex) {
            return serverError(response, ex);
        } finally {
            _sensorSourceRepository.close();
        }
    };
}


