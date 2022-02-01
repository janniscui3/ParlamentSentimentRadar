
import Data.*;
import Database.MongoDBCollectionHandler;
import Database.MongoDBConnectionHandler;
import it.unimi.dsi.fastutil.Hash;
import org.bson.Document;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Main Class, from which everything is handled.
 * Contains an rudimentary UI using the console.
 *
 * @author Jannis Cui
 */
public class ParlamentSentimentRadar {
    public static void main(String[] args) throws InterruptedException {
        Scanner scanner = new Scanner(System.in);
        MongoDBConnectionHandler connectionHandler = new MongoDBConnectionHandler();
        HashMap<String, Abgeordnete> abgeordnetenliste = new HashMap<>();
        HashMap<String, Fraktion> fraktionliste = new HashMap<>();
        HashMap<String, Rede> redeliste = new HashMap<>();
        HashMap<String, Sitzung> sitzungliste = new HashMap<>();
        MongoDBCollectionHandler collectionBuilder = null;
        String filepath;
        boolean init = true;
        int countfiles = 0;

        TimeUnit.SECONDS.sleep(2);

        Document temp = new Document()
                .append("_id", "abc")
                .append("datum", "abcd")
                .append("sitzungsleiter", "afde");

        connectionHandler.addDocumentToCollection(temp, "speeches");
    }
}

