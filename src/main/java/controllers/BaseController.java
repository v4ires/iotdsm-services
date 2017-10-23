package controllers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import spark.Response;

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

    protected static Gson _gson = new GsonBuilder()
            .excludeFieldsWithoutExposeAnnotation()
            .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
            .serializeNulls()
            .create();
}
