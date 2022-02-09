package RESTful;

import java.net.URL;
import java.net.URLClassLoader;

import static spark.Spark.*;

public class Application {
    public static void main(String[] args) {
        get("/hello", (req, res) -> "Hello World");
    }
}
