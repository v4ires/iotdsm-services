import spark.servlet.SparkApplication;

import static spark.Spark.get;

public class ServletMain implements SparkApplication {

    public static void main(String[] args) {
        new ServletMain().init();
    }

    @Override
    public void init() {
        get("/hello", (req, res) -> "Hello World");
    }
}
