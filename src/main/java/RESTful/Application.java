package RESTful;

import org.bson.Document;
import org.json.JSONObject;
import spark.Filter;
import java.net.URL;
import java.net.URLClassLoader;

import Database.MongoDBConnectionHandler;
import static spark.Spark.*;

public class Application {
    public static void main(String[] args) {
        MongoDBConnectionHandler connectionHandler = new MongoDBConnectionHandler();
        
        options("/*",
        (request, response) -> {

            String accessControlRequestHeaders = request
                    .headers("Access-Control-Request-Headers");
            if (accessControlRequestHeaders != null) {
                response.header("Access-Control-Allow-Headers",
                        accessControlRequestHeaders);
            }

            String accessControlRequestMethod = request
                    .headers("Access-Control-Request-Method");
            if (accessControlRequestMethod != null) {
                response.header("Access-Control-Allow-Methods",
                        accessControlRequestMethod);
            }

            return "OK";
        });

        before((request, response) -> {
            response.header("Access-Control-Allow-Origin", "*");
        });

        get("/tokens", (req, res) -> {
            Document doc = connectionHandler.getDocumentFromCollection("tokens", "statistics");
            res.raw().setContentType("application/json");
            res.raw().getWriter().print(doc.toJson());
            return true;
        });
    }
}
