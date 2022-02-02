package Database;

import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCredential;
import com.mongodb.MongoException;
import com.mongodb.ServerAddress;
import com.mongodb.client.*;
import com.mongodb.client.model.*;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import org.bson.Document;
import org.bson.conversions.Bson;
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
        Document document = myCollection.find(eq("_id", ID)).first();
        if (document == null) {
            System.out.println("No such Document found");
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

    /**
     * Update a single Document
     * @author Jannis Cui
     * @param collection Collection to update in
     * @param queryid ID of to be updated
     * @param document Updated content
     */
    public void updateDocument(String collection, String queryid, Document document) {
        MongoCollection<Document> myCollection = currentDatabase.getCollection(collection);
        Document docquery = new Document().append("_id",  queryid);
        myCollection.updateOne(docquery, document);
    }

    /**
     * Update multiple Documents selected by a query
     * @author Jannis Cui
     * @param collection Collection to update in
     * @param query Condition to search for
     * @param updates What is to be updated
     */
    public void updateManyDocument(String collection, Bson query, Bson updates) {
        MongoCollection<Document> myCollection = currentDatabase.getCollection(collection);
        try {
            UpdateResult result = myCollection.updateMany(query, updates);
            System.out.println("Modified document count: " + result.getModifiedCount());
        } catch (MongoException me) {
            System.err.println("Unable to update due to an error: " + me);
        }
    }

    /**
     * Replace a single Document
     * @author Jannis Cui
     * @param collection Collection to update in
     * @param queryid ID of to be updated item
     * @param replacedocument Updated content
     */
    public void replaceDocument(String collection, String queryid, Document replacedocument) {
        MongoCollection<Document> myCollection = currentDatabase.getCollection(collection);
        Document docquery = new Document().append("_id",  queryid);
        try {
            UpdateResult result = myCollection.replaceOne(docquery, replacedocument);
            System.out.println("Modified document count: " + result.getModifiedCount());
            System.out.println("Upserted id: " + result.getUpsertedId()); // only contains a value when an upsert is performed
        }
        catch (MongoException me) {
            System.err.println("Unable to replace due to an error: " + me);
        }
    }

    /**
     * Delete a single Document
     * @author Jannis Cui
     * @param collection Collection to update in
     * @param queryid ID of the to be deleted item
     */
    public void deleteDocument(String collection, String queryid) {
        MongoCollection<Document> myCollection = currentDatabase.getCollection(collection);
        Bson query = eq("_id", queryid);
        try {
            DeleteResult result = myCollection.deleteOne(query);
            System.out.println("Deleted document count: " + result.getDeletedCount());
        } catch (MongoException me) {
            System.err.println("Unable to delete due to an error: " + me);
        }
    }

    /**
     * Delete multiple Documents specified by query
     * @author Jannis Cui
     * @param collection Collection to update in
     * @param query Condition to search for
     */
    public void deletemanyDocument(String collection, Bson query) {
        MongoCollection<Document> myCollection = currentDatabase.getCollection(collection);
        try {
            DeleteResult result = myCollection.deleteMany(query);
            System.out.println("Deleted document count: " + result.getDeletedCount());
        } catch (MongoException me) {
            System.err.println("Unable to delete due to an error: " + me);
        }
    }

    /**
     * Gives an estimateted count of documents in a collection
     * @author Jannis Cui
     * @param collection Collection to update in
     */
    public long countestimateDocument(String collection) {
        MongoCollection<Document> myCollection = currentDatabase.getCollection(collection);
        long estimatedcount = 0;
        try {
            estimatedcount = myCollection.estimatedDocumentCount();
        } catch (MongoException me) {
            System.err.println("An error occurred: " + me);
        }
        return estimatedcount;
    }

    /**
     * Gives an count of documents specified by query
     * @author Jannis Cui
     * @param collection Collection to update in
     */
    public long countDocument(String collection, Bson query) {
        MongoCollection<Document> myCollection = currentDatabase.getCollection(collection);
        long count = 0;
        try {
            count = myCollection.countDocuments(query);
        } catch (MongoException me) {
            System.err.println("An error occurred: " + me);
        }
        return count;
    }

    /**
     * Given a list of aggregates and the collection, apply said aggregate to collection and return
     * @author Jannis Cui
     * @param collection Collection to update in
     * @param aggregates List of Bson Aggregates that are to be applied
     * @return AggregateIterable
     */
    public AggregateIterable<Document> aggregateDocument(String collection, List<Bson> aggregates) {
        MongoCollection<Document> myCollection = currentDatabase.getCollection(collection);

        AggregateIterable<Document> output = myCollection.aggregate(aggregates);
        return output;
    }
}

