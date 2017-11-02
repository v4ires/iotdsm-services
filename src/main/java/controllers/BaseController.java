package controllers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import model.BasicEntity;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import spark.Response;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * University of São Paulo
 * IoT Repository Module
 * @author Vinícius Aires Barros <viniciusaires7@gmail.com>
 */
public class BaseController {

    protected static Gson _gson = new GsonBuilder()
            .excludeFieldsWithoutExposeAnnotation()
            .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
            .serializeNulls()
            .create();
    /**
     *
     */
    protected static String error(Response response, String message) {
        response.status(400);
        response.type("application/json");
        return String.format("{\"error\": \"%s\"}", message);
    }

    /**
     *
     */
    protected static String serverError(Response response, Exception ex) {
        response.status(500);
        response.type("application/json");
        return String.format("{\"error\": \"%s\"}", org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(ex).replaceAll("\"", "\\\""));
    }

    /**
     *
     */
    protected static String success(Response response, String message) {
        response.status(200);
        response.type("application/json");
        return message;
    }

    /**
     *
     */
    protected static String successXml(Response response, Object message) {
        response.status(200);
        response.type("application/xml");
        XStream xstream = new XStream(new DomDriver());
        return xstream.toXML(message);
    }

    /**
     *
     */
    protected static List<String> printCsvObject(Object o, Set<Field> exposedFields) throws Exception {
        List<String> values = new ArrayList<>();
        for (Field f : exposedFields) {
            if (BasicEntity.class.isAssignableFrom(f.getType())) {
                Set<Field> exposedFieldsOn = utils.Utils.findFields(f.getType(), Expose.class);
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
     *
     */
    protected static String successCsv(Response response, Object message) {
        StringBuilder appendable = new StringBuilder();

        response.status(200);
        response.type("text/csv");

        if (message == null || message instanceof List && ((List<Object>) message).size() == 0)
            return "";

        try {
            CSVPrinter printer = new CSVPrinter(appendable, CSVFormat.DEFAULT);
            Class objectClass;

            if (message instanceof List)
                objectClass = ((List<Object>) message).get(0).getClass();
            else
                objectClass = message.getClass();

            Set<Field> exposedFields = utils.Utils.findFields(objectClass, Expose.class);
            List<String> headers = new ArrayList<>();

            for (Field f : exposedFields) {
                if (BasicEntity.class.isAssignableFrom(f.getType())) {
                    Set<Field> exposedFieldsOn = utils.Utils.findFields(f.getType(), Expose.class);
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
            ex.printStackTrace();
            return error(response, ex.getMessage());
        }

        return appendable.toString();
    }
}
