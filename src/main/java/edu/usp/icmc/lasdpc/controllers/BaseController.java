package edu.usp.icmc.lasdpc.controllers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import edu.usp.icmc.lasdpc.model.BasicEntity;
import edu.usp.icmc.lasdpc.model.HTTPCompressType;
import edu.usp.icmc.lasdpc.utils.Utils;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Response;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * University of Sao Paulo
 * IoT Repository Module
 *
 * @author Vinicius Aires Barros viniciusaires@usp.br
 */
public class BaseController {

    private static final Logger log = LoggerFactory.getLogger(BaseController.class);

    protected static Gson _gson = new GsonBuilder()
            .excludeFieldsWithoutExposeAnnotation()
            .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
            .serializeNulls()
            .create();

    /**
     * Envia uma resposta HTTP de código 400 ao cliente. Utilizada para erros causados por entradas inválidas e demais necessidades de informação manual de erro em requisições.
     *
     * @param response Objeto Response do SparkJava para escrita da resposta ao cliente
     * @param message Mensagem de erro que será informada ao cliente
     * @return Retorna um objeto JSON convertido para String contendo a mensagem de erro para o usuário
     */
    protected static String error(Response response, String message) {
        response.status(400);
        response.type("application/json");
        return String.format("{\"error\": \"%s\"}", message);
    }

    /**
     * Envia uma resposta ao cliente de erros internos ocorridos durante o processamento de requisições.
     *
     * @param response Objeto Response do SparkJava para escrita da resposta ao cliente
     * @param ex Exceção ocorrida durante o processamento da requisição
     * @return Retorna um objeto JSON convertido para String contendo a mensagem de erro para o usuário
     *
     */
    protected static String serverError(Response response, Exception ex) {
        response.status(500);
        response.type("application/json");
        return String.format("{\"error\": \"%s\"}", ex.getMessage());
    }

    /**
     * Envia uma resposta de sucesso (código 200) ao cliente e retorna o conteúdo da requisição no formato JSON.
     *
     * @param response Objeto Response do SparkJava para escrita da resposta ao cliente
     * @param message Resposta JSON convertida para String
     * @param type Especifica o tipo de encoding utilizado no conteúdo
     * @return Retorna um objeto JSON convertido para String com o conteúdo
     */
    protected static String successJSON(Response response, String message, HTTPCompressType type) {
        response.status(200);
        response.type("application/json");
        if (type != null) {
            response.header("Content-Encoding", type.getContentEncoding());
        }
        return message;
    }

    /**
     * Envia uma resposta de sucesso (código 200) ao cliente e retorna o conteúdo da requisição no formato XML.
     *
     * @param response Objeto Response do SparkJava para escrita da resposta ao cliente
     * @param message Objeto a ser convertido para XML resultante da requisição
     * @param type Especifica o tipo de encoding utilizado no conteúdo
     * @return Retorna um objeto XML convertido para String com o conteúdo
     *
     */
    protected static String successXml(Response response, Object message, HTTPCompressType type) {
        response.status(200);
        response.type("application/xml");
        if (type != null) {
            response.header("Content-Encoding", type.getContentEncoding());
        }
        XStream xstream = new XStream(new DomDriver());
        return xstream.toXML(message);
    }

    /**
     * Envia uma resposta de sucesso ao cliente com o conteúdo da requisição no formato XML.
     *
     * @param o Objeto a ser convertido para CSV
     * @param exposedFields Campos que serão mostrados no CSV de saída
     * @return Retorna uma lista com os valores dos campos a serem expostos na ordem do parâmetro exposedFields
     *
     */
    protected static List<String> printCsvObject(Object o, Set<Field> exposedFields) throws Exception {
        List<String> values = new ArrayList<>();
        for (Field f : exposedFields) {
            if (BasicEntity.class.isAssignableFrom(f.getType())) {
                Set<Field> exposedFieldsOn = Utils.findFields(f.getType(), Expose.class);
                for (Field ef : exposedFieldsOn) {
                    Method innerObjectMethod = o.getClass().getMethod("get" + f.getName().substring(0, 1).toUpperCase() + f.getName().substring(1));
                    Object innerObject;

                    if (innerObjectMethod == null) {
                        values.add("");
                        continue;
                    }

                    innerObject = innerObjectMethod.invoke(o);

                    if (innerObject == null) {
                        values.add("");
                        continue;
                    }

                    Method method = innerObject.getClass().getMethod("get" + ef.getName().substring(0, 1).toUpperCase() + ef.getName().substring(1));

                    if (method != null) {
                        Object result = method.invoke(innerObject);
                        values.add(result == null ? "" : result.toString());
                    }
                }
            } else {
                Method method = o.getClass().getMethod("get" + f.getName().substring(0, 1).toUpperCase() + f.getName().substring(1));

                if (method != null) {
                    Object result = method.invoke(o);


                    values.add(result == null ? "" : result.toString());
                }
            }
        }
        return values;
    }

    /**
     * Envia uma resposta de sucesso (código 200) ao cliente e retorna o conteúdo da requisição no formato CSV.
     * Os Campos que serão enviados nessa resposta devem estar marcados com a anotação {@code @Expose} e seu nome
     * será utilizado no cabeçalho do arquivo CSV.
     *
     * @param response Objeto Response do SparkJava para escrita da resposta ao cliente
     * @param message Objeto a ser convertido para CSV resultante da requisição
     * @param type Especifica o tipo de encoding utilizado no conteúdo
     * @return Retorna um objeto CSV convertido para String com o conteúdo
     *
     */
    protected static String successCsv(Response response, Object message, HTTPCompressType type) {
        StringBuilder appendable = new StringBuilder();

        response.status(200);
        response.type("text/csv");
        if (type != null) {
            response.header("Content-Encoding", type.getContentEncoding());
        }

        if (message == null || message instanceof List && ((List<Object>) message).size() == 0)
            return "";

        try {
            CSVPrinter printer = new CSVPrinter(appendable, CSVFormat.DEFAULT);
            Class objectClass;

            if (message instanceof List)
                objectClass = ((List<Object>) message).get(0).getClass();
            else
                objectClass = message.getClass();

            Set<Field> exposedFields = Utils.findFields(objectClass, Expose.class);
            List<String> headers = new ArrayList<>();

            for (Field f : exposedFields) {
                if (BasicEntity.class.isAssignableFrom(f.getType())) {
                    Set<Field> exposedFieldsOn = Utils.findFields(f.getType(), Expose.class);
                    for (Field ef : exposedFieldsOn) {
                        headers.add(f.getName() + "/" + ef.getName());
                    }
                } else
                    headers.add(f.getName());
            }

            printer.printRecord(headers);

            if (message instanceof List) {

                for (Object o : ((List<Object>) message)) {
                    printer.printRecord(printCsvObject(o, exposedFields));
                }
            } else {
                printer.printRecord(printCsvObject(message, exposedFields));
            }
        } catch (Exception ex) {
            log.error(ex.getMessage());
            ex.printStackTrace();
            return error(response, ex.getMessage());
        }

        return appendable.toString();
    }
}
