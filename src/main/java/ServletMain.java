import spark.servlet.SparkApplication;

import static spark.Spark.get;

/**
 * University of São Paulo
 * IoT Repository Module
 *
 * @author Vinícius Aires Barros <viniciusaires@usp.br>
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
