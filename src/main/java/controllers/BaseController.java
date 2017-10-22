package controllers;

import spark.Response;

public class BaseController {
    protected String error(Response response, String message)
    {
        response.status(400);
        response.type("application/json");

        return String.format("{\"error\": \"%s\"}", message);
    }

    protected String serverError(Response response, Exception ex)
    {
        response.status(500);
        response.type("application/json");

        return String.format("{\"error\": \"%s\"}", ex.getMessage());
    }

    protected String success(Response response, String message)
    {
        response.status(200);
        response.type("application/json");

        return message;
    }
}
