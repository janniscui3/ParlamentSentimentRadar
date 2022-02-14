package RESTful;

import org.bson.Document;
import org.json.JSONObject;
import spark.Filter;
import java.net.URL;
import java.net.URLClassLoader;

import Database.MongoDBConnectionHandler;
import static spark.Spark.*;

/** Spark that will run the api.
 * @Author Jannis Cui, Timo Eisert
 */
public class Application {
    public static void main(String[] args) {
        MongoDBConnectionHandler connectionHandler = new MongoDBConnectionHandler();
        // Pre-doing some stuff
        Document temp = new Document()
                .append("_id", "progress")
                .append("current", Long.toString(connectionHandler.getCollection("CAS").countDocuments()))
                .append("total", Long.toString(connectionHandler.getCollection("speech").countDocuments()))
                .append("success", "true");
        connectionHandler.replaceDocument("statistics", "progress", temp);

        // Options to disable CORS
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


        // Main Paths
        get("/tokens", (req, res) -> {
            Document doc = connectionHandler.getDocumentFromCollection("tokens", "statistics");
            res.raw().setContentType("application/json");
            return doc.toJson();
        });

        get("/progress", (req, res) -> {
            Document doc = connectionHandler.getDocumentFromCollection("progress", "statistics");
            res.raw().setContentType("application/json");
            return doc.toJson();
        });

        get("/pos", (req, res) -> {
            Document doc = connectionHandler.getDocumentFromCollection("POS", "statistics");
            res.raw().setContentType("application/json");
            return doc.toJson();
        });

        get("/namedEntities", (req, res) -> {
            Document doc = connectionHandler.getDocumentFromCollection("namedentities", "statistics");
            res.raw().setContentType("application/json");
            return doc.toJson();
        });

        get("/sentiment", (req, res) -> {
            Document doc = connectionHandler.getDocumentFromCollection("sentiment", "statistics");
            res.raw().setContentType("application/json");
            return doc.toJson();
        });
    }
}
