package RESTful;

import org.bson.Document;
import org.json.JSONObject;

import java.net.URL;
import java.net.URLClassLoader;

import Database.MongoDBConnectionHandler;
import static spark.Spark.*;

public class Application {
    public static void main(String[] args) {
        MongoDBConnectionHandler connectionHandler = new MongoDBConnectionHandler();

        get("/tokens", (req, res) -> {
            Document doc = connectionHandler.getDocumentFromCollection("tokens", "statistics");
            res.raw().setContentType("application/json");
            res.raw().getWriter().print(doc.toJson());
            return true;
        });
    }
}
