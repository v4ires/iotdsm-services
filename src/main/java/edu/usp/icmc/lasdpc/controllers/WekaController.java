package edu.usp.icmc.lasdpc.controllers;

import spark.Request;
import spark.Response;
import spark.Route;

public class WekaController extends BaseController {

    public static Route knnPost = (Request request, Response response) -> {
        System.out.println(request.body());
        return "ok";
    };

    public static Route mlpPost = (Request request, Response response) -> {
        System.out.println(request.body());
        return "ok";
    };

    public static Route naiveBayesPost = (Request request, Response response) -> {
        System.out.println(request.body());
        return "ok";
    };

    public static Route lRPost = (Request request, Response response) -> {
        System.out.println(request.body());
        return "ok";
    };

    public static Route dTPost = (Request request, Response response) -> {
        System.out.println(request.body());
        return "ok";
    };

}
