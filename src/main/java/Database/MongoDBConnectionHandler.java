package Database;

import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.InsertManyOptions;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.Arrays;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;

/**
 * Author: Jannis Cui
 * Connection Handler to a given database given by the resource/config.properties file.
 */
public class MongoDBConnectionHandler {
    String host;
    String port;
    String user;
    char[] password;
    String database;
    String collection;
    MongoCredential credential;
    MongoClient mongoClient;
    MongoDatabase currentDatabase;

    // Constructor

    /**
     * @author Jannis Cui
     * Gets data from the resources/config.properties file
     */
    public MongoDBConnectionHandler() {
        CreateConfig config = new CreateConfig();
        host = config.readProperty("remote_host");
        port = config.readProperty("remote_port");
        user = config.readProperty("remote_user");
        database = config.readProperty("remote_database");
        collection = config.readProperty("remote_collection");
        String convert = config.readProperty("remote_password");
        password = convert.toCharArray();

        // Creating Credentials
        credential = MongoCredential.createCredential(user, database, password);

        // Connecting to the database
        mongoClient = MongoClients.create(
                MongoClientSettings.builder()
                        .applyToClusterSettings(builder ->
                                builder.hosts(Arrays.asList(new ServerAddress(host, 27020))))
                        .credential(credential)
                        .build());

        // Accessing the Database
        currentDatabase = mongoClient.getDatabase(database);
    }

    // Getter and Setter
    public String getHost() {
        return host;
    }

    public String getPort() {
        return port;
    }

    public String getUser() {
        return user;
    }

    public char[] getPassword() {
        return password;
    }

    public String getDatabase() {
        return database;
    }

    public String getCollection() {
        return collection;
    }


    /** Add a single given document to a given collection
     * @author Jannis Cui
     * @param doc Document to be added
     * @param collection Collection which
     */
    public void addDocumentToCollection(Document doc, String collection) {
        currentDatabase.getCollection(collection).insertOne(doc);
        System.out.println("Inserted document.");
    }


    /** Add multiple documents to a given collection
     * @author Jannis Cui
     * @param docs List of Documents
     * @param collection Collection
     * @param order Insert in ordered or not
     */
    public void addManyDocumentsToCollection(List<Document> docs, String collection, Boolean order) {
        InsertManyOptions options = new InsertManyOptions();
        options.ordered(order);
        currentDatabase.getCollection(collection).insertMany(docs, options);
        System.out.println("Inserted many documents.");
    }

    /**
     * Returns a single document from a collection by ID
     * @author Jannis Cui
     * @param ID ID of document
     * @param collection Collectionn it is in
     * @return Document
     */
    public Document getDocumentFromCollection(String ID, String collection) {
        MongoCollection<Document> myCollection = currentDatabase.getCollection(collection);
        Document document = myCollection.find(eq("_id", new ObjectId(ID))).first();
        if (document == null) {
            //Document does not exist
        } else {
            //We found the document
        }
        return document;
    }

    /**
     * Return all documents in a given collection
     * @author Jannis Cui
     * @param collection Collection
     * @return MongoCollection of Documents
     */
    public MongoCollection<Document> getCollection(String collection) {
        MongoCollection<Document> coll = currentDatabase.getCollection(collection);
        return coll;
    }
}
