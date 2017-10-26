package controllers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import spark.Response;

import java.lang.reflect.Field;
import java.util.Set;

public class BaseController {
    protected static String error(Response response, String message)
    {
        response.status(400);
        response.type("application/json");

        return String.format("{\"error\": \"%s\"}", message);
    }

    protected static String serverError(Response response, Exception ex)
    {
        response.status(500);
        response.type("application/json");

        ex.printStackTrace();

        return String.format("{\"error\": \"%s\"}", ex.getMessage());
    }

    protected static String success(Response response, String message)
    {
        response.status(200);
        response.type("application/json");

        return message;
    }

    protected static String successXml(Response response, Object message)
    {
        response.status(200);
        response.type("application/xml");

        XStream xstream = new XStream(new DomDriver());

        return xstream.toXML(message);
    }

    protected static String successCsv(Response response, Object message){
        StringBuilder appendable = new StringBuilder();
        try {
            CSVPrinter printer = new CSVPrinter(appendable, CSVFormat.DEFAULT);

            Set<Field> exposedFields = utils.Utils.findFields(message.getClass(), Expose.class);



            printer.printRecord(message);
        }catch(Exception ex){
            return error(response, ex.getMessage());
        }

        return appendable.toString();
    }

    protected static Gson _gson = new GsonBuilder()
            .excludeFieldsWithoutExposeAnnotation()
            .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
            .serializeNulls()
            .create();
}
