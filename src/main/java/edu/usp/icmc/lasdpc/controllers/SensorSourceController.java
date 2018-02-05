package edu.usp.icmc.lasdpc.controllers;

import edu.usp.icmc.lasdpc.repositories.SensorSourceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Request;
import spark.Response;
import spark.Route;

/**
 * University of Sao Paulo
 * IoT Repository Module
 *
 * @author Vinicius Aires Barros <viniciusaires@usp.br>
 */
public class SensorSourceController extends BaseController {

    private static final Logger log = LoggerFactory.getLogger(SensorSourceController.class);

    /**
     * Função responsável pela chamada GET para mostrar as fontes de sensores disponíveis
     * Aceita o seguinte parâmetro na query:
     * - output_format: (Opcional) Formato da saída dos dados. Suporta JSON, XML ou CSF (Padrão: JSON)
     *
     * @param request Objeto {@link spark.Request} do SparkJava contendo os dados da requisição do cliente
     * @param response Objeto {@link spark.Response} do SparkJava para a resposta ao cliente da requisição
     * @return Retorna o conteúdo no formato especificado pelo parâmetro na query da chamada ou uma mensagem de erro (formato JSON) caso algum problema ocorra.
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
                    return successJSON(response, _gson.toJson(new SensorSourceRepository().getSensorSources()), null);
                case "xml":
                    return successXml(response, new SensorSourceRepository().getSensorSources(), null);
                case "csv":
                    return successCsv(response, new SensorSourceRepository().getSensorSources(), null);
            }
        } catch (Exception ex) {
            log.error(ex.getMessage());
            return serverError(response, ex);
        } finally {
            _sensorSourceRepository.close();
        }
    };

    /**
     * Função responsável pela chamada GET para mostrar os dados de uma fonte de sensores
     * Aceita os seguintes parâmetros na query:
     * - output_format: (Opcional) Formato da saída dos dados. Suporta JSON, XML ou CSF (Padrão: JSON)
     * - id: Id do sensor para mostrar os dados
     *
     * @param request Objeto {@link spark.Request} do SparkJava contendo os dados da requisição do cliente
     * @param response Objeto {@link spark.Response} do SparkJava para a resposta ao cliente da requisição
     * @return Retorna o conteúdo no formato especificado pelo parâmetro na query da chamada ou uma mensagem de erro (formato JSON) caso algum problema ocorra.
     */
    public static Route serveSensorById = (Request request, Response response) -> {
        SensorSourceRepository _sensorSourceRepository = new SensorSourceRepository();

        try {
            String outputFormat = "json";
            Long sensorId;

            if (request.params("id") == null || request.params("id").equals("")) {
                log.warn("Invalid sensor source id.");
                return error(response, "Invalid sensor source id.");
            }

            if (request.queryParams("output_format") != null && !request.queryParams("output_format").equals("")) {
                outputFormat = request.queryParams("output_format");
            }

            try {
                sensorId = Long.parseLong(request.params("id"));
            } catch (Exception ex) {
                log.error(ex.getMessage());
                return error(response, "Invalid sensor source id.");
            }

            switch (outputFormat) {
                default:
                case "json":
                    return successJSON(response, _gson.toJson(_sensorSourceRepository.getSensorSourceById(sensorId)), null);
                case "xml":
                    return successXml(response, _sensorSourceRepository.getSensorSourceById(sensorId), null);
                case "csv":
                    return successCsv(response, _sensorSourceRepository.getSensorSourceById(sensorId), null);

            }
        } catch (Exception ex) {
            log.error(ex.getMessage());
            return serverError(response, ex);
        } finally {
            _sensorSourceRepository.close();
        }
    };
}


