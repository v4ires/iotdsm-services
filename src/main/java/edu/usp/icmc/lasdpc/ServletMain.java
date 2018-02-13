package edu.usp.icmc.lasdpc;

import spark.servlet.SparkApplication;

import static spark.Spark.get;

/**
 * University of Sao Paulo
 * IoT Repository Module
 *
 * @author Vinicius Aires Barros viniciusaires@usp.br
 */
public class ServletMain implements SparkApplication {

    public static void main(String[] args) {
        new ServletMain().init();
    }

    /**
     *
     */
    @Override
    public void init() {
        get("/hello", (req, res) -> "Hello World");
    }
}
