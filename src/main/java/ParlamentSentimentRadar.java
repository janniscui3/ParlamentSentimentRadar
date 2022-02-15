
import Data.*;
import Database.MongoDBCollectionHandler;
import Database.MongoDBConnectionHandler;
import NLP.CASFolderDeserializer;
import NLP.CASFolderSerializer;
import XMLReader.ReadFolderOfXML;
import it.unimi.dsi.fastutil.Hash;
import org.bson.Document;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static Utilities.Util.sortByValueDescending;

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
        loop: while(true) {
            while (init) {
                System.out.println("Wählen sie den Pfad des Ordners an, in dem die XML Dateien sind, oder schreibe skip: ");
                filepath = scanner.nextLine();
                if (filepath.equals("skip")) {
                    init = false;
                    break;
                }
                ReadFolderOfXML reader = new ReadFolderOfXML();
                init = reader.initialize(filepath);
                abgeordnetenliste = reader.getAbgeordnetenliste();
                fraktionliste = reader.getFraktionliste();
                redeliste = reader.getRedeliste();
                sitzungliste = reader.getSitzungliste();
                collectionBuilder = new MongoDBCollectionHandler(abgeordnetenliste, fraktionliste, redeliste, sitzungliste);
            }

            System.out.println("Wählen sie, was sie machen wollen.");
            System.out.println("Drücke 12, um die Protokollen in die MongoDB hochzuladen (DAUERT LANGE).");
            System.out.println("Drücke 13, um die Reden in die MongoDB hochzuladen.");
            System.out.println("Drücke 14, um die Abgeordneten in die MongoDB hochzuladen.");
            System.out.println("Drücke 15, um die Fraktionen in die MongoDB hochzuladen.");
            System.out.println("Drücke 16, um die Reden analysieren zu lassen.");
            System.out.println("Drücke 17, um ein Ordner von CASXML Dateien hochzuladen(DAUERT).");
            System.out.println("Drücke 18, um ein Ordner von CASXML Dateien einzulesen und ihre Werte in den Resources zu speichern.");
            System.out.println("Drücke 19, um die Fraktionenliste, Abgeordnetenliste von der Datenbank herunterzuladen.");
            System.out.println("Drücke 20, um die Protokolle + Reden von der Datenbank herunterzuladen(DAUERT LANGE).");
            System.out.println("Drücke 21, um die häufigsten Tokens zu kriegen und sie in die Datenbank hochzuladen.");
            System.out.println("Drücke 22, um die häufigsten NamedEntities zu kriegen und sie in die Datenbank hochzuladen.");
            System.out.println("Drücke 23, um die häufigsten Parts of Speech zu kriegen und sie in die Datenbank hochzuladen.");
            System.out.println("Drücke 24, um die Sentimentverteilung pro Redner zu kriegen (Alle reden müssen geladen sein).");
            System.out.println("Drücke 25, um die Sentimentverteilung pro Partei zu kriegen (Alle reden müssen geladen sein).");
            System.out.println("Drücke 26, um die allgemeine Sentimentverteilung zu kriegen und sie hochzuladen.");
            String choose = scanner.nextLine();

            try {
                //
                switch (choose) {
                    // To exit, just break the main loop.
                    case "1":
                        System.out.println("Exiting.");
                        break loop;
                    case "12":
                        connectionHandler.addManyDocumentsToCollection(collectionBuilder.createProtocolCollection(), "protocol", false);
                        break;
                    case "13":
                        connectionHandler.addManyDocumentsToCollection(collectionBuilder.createSpeechCollection(), "speech", false);
                        break;
                    case "14":
                        connectionHandler.addManyDocumentsToCollection(collectionBuilder.createSpeakerCollection(), "speaker", false);
                        break;
                    case "15":
                        connectionHandler.addManyDocumentsToCollection(collectionBuilder.createFraktionCollection(), "faction", false);
                        break;
                    case "16":
                        CASFolderSerializer.AnalyseAllTexts(redeliste);
                        break;
                    case "17":
                        System.out.println("Gebe den Ordner an, der die CASXML Dateien hat: ");
                        String path = scanner.nextLine();
                        CASFolderSerializer.UploadCASXMLToMongoDB(path, connectionHandler);
                        break;
                    case "18":
                        CASFolderDeserializer casReadEntireFolder = new CASFolderDeserializer();
                        System.out.println("Gebe den Ordner an, der die CASXML Dateien hat: ");
                        path = scanner.nextLine();
                        casReadEntireFolder.initialize(path);
                        break;
                    case "19":
                        fraktionliste = MongoDBCollectionHandler.buildHashMapFaction(connectionHandler.getCollection("faction"));
                        abgeordnetenliste = MongoDBCollectionHandler.buildHashMapSpeaker(connectionHandler.getCollection("speaker"));
                        System.out.println("Alles fertig geladen.");
                        break;
                    case "20":
                        sitzungliste = MongoDBCollectionHandler.buildHashMapProtocol(connectionHandler.getCollection("protocol"));
                        redeliste = MongoDBCollectionHandler.buildHashMapSpeech(connectionHandler.getCollection("speech"));
                        break;
                    case "21":
                        // Create a Hashmap containing every token as key and increment its value by one every time we find it in the txt
                        HashMap<String, Integer> tokencounter = new HashMap<>();
                        try(BufferedReader br = new BufferedReader(new FileReader("src/main/resources/Tokens.txt"))) {
                            for(String line; (line = br.readLine()) != null; ) {
                                if(tokencounter.containsKey(line)) {
                                    int counter = tokencounter.get(line);
                                    counter++;
                                    tokencounter.put(line, counter);
                                }
                                else {
                                    if (!line.startsWith("#")) {
                                        tokencounter.put(line,1);
                                    }
                                }
                            }
                        }
                        // Sort Descendingly
                        Map<String, Integer> tokencounter1 = sortByValueDescending(tokencounter);

                        // Create Document
                        Document tempdoc = new Document()
                                .append("_id", "tokens");

                        List<Document> temptokens = new ArrayList<>();

                        for (String i: tokencounter1.keySet()) {
                            Document tempdoc1 = new Document();
                            tempdoc1.append("count", Integer.toString(tokencounter1.get(i)));
                            tempdoc1.append("token", i);
                            temptokens.add(tempdoc1);
                        }

                        tempdoc.append("result", temptokens);
                        tempdoc.append("success", "true");

                        // Add to Database
                        connectionHandler.replaceDocument("statistics", "tokens", tempdoc);
                        break;
                    case "22":
                        // Create a Hashmap containing every namedentity as key and increment its value by one every time we find it in the txt
                        HashMap<String, Integer> namedentitiescounter = new HashMap<>();
                        HashMap<String, String> namedentitiesgroup = new HashMap<>();
                        try(BufferedReader br = new BufferedReader(new FileReader("src/main/resources/namedentities.txt"))) {
                            for(String line; (line = br.readLine()) != null; ) {
                                String[] result = line.split(":");
                                if (result.length == 2) {
                                    if(namedentitiescounter.containsKey(result[1])) {
                                        int counter = namedentitiescounter.get(result[1]);
                                        counter++;
                                        namedentitiescounter.put(result[1], counter);
                                    }
                                    else {
                                        if (!line.startsWith("#")) {
                                            namedentitiescounter.put(result[1],1);
                                            namedentitiesgroup.put(result[1],result[0]);
                                        }
                                    }
                                }
                            }
                        }

                        Map<String, Integer> namedentitiescounter1 = sortByValueDescending(namedentitiescounter);
                        ArrayList<String> PERlist = new ArrayList<>();
                        ArrayList<String> ORGlist = new ArrayList<>();
                        ArrayList<String> MISClist = new ArrayList<>();
                        ArrayList<String> LOClist = new ArrayList<>();

                        for (String i: namedentitiescounter1.keySet()) {
                            if (namedentitiesgroup.get(i).equals("PER")) {
                                PERlist.add(i);
                            }
                            else if (namedentitiesgroup.get(i).equals("ORG")) {
                                ORGlist.add(i);
                            }
                            else if (namedentitiesgroup.get(i).equals("MISC")) {
                                MISClist.add(i);
                            }
                            else if (namedentitiesgroup.get(i).equals("LOC")) {
                                LOClist.add(i);
                            }
                        }

                        // TODO: Refactor this
                        // Create Document
                        Document tempnameddoc = new Document()
                                .append("_id", "namedentities");

                        List<Document> resultnamed = new ArrayList<>();

                        // Persons
                        Document tempdocperson = new Document();
                        List<Document> tempdoclistpersons = new ArrayList<>();
                        for (String i : PERlist) {
                            Document tempdoc1 = new Document();
                            tempdoc1.append("count", namedentitiescounter1.get(i));
                            tempdoc1.append("element", i);
                            tempdoclistpersons.add(tempdoc1);
                        }
                        tempdocperson.append("persons", tempdoclistpersons);
                        resultnamed.add(tempdocperson);

                        // Organisations
                        Document tempdocorg = new Document();
                        List<Document> tempdoclistorg = new ArrayList<>();
                        for (String i : ORGlist) {
                            Document tempdoc1 = new Document();
                            tempdoc1.append("count", namedentitiescounter1.get(i));
                            tempdoc1.append("element", i);
                            tempdoclistorg.add(tempdoc1);
                        }
                        tempdocorg.append("organisations", tempdoclistorg);
                        resultnamed.add(tempdocorg);

                        // Location
                        Document tempdocloc = new Document();
                        List<Document> tempdoclistloc = new ArrayList<>();
                        for (String i : LOClist) {
                            Document tempdoc1 = new Document();
                            tempdoc1.append("count", namedentitiescounter1.get(i));
                            tempdoc1.append("element", i);
                            tempdoclistloc.add(tempdoc1);
                        }
                        tempdocloc.append("locations", tempdoclistloc);
                        resultnamed.add(tempdocloc);


                        tempnameddoc.append("result", resultnamed);
                        tempnameddoc.append("success", "true");

                        connectionHandler.replaceDocument("statistics", "namedentities", tempnameddoc);
                        break;
                    case "23":
                        // Create a Hashmap containing every POS as key and increment its value by one every time we find it in the txt
                        HashMap<String, Integer> partofspeechcounter = new HashMap<>();
                        try(BufferedReader br = new BufferedReader(new FileReader("src/main/resources/POS.txt"))) {
                            for(String line; (line = br.readLine()) != null; ) {
                                if(partofspeechcounter.containsKey(line)) {
                                    int counter = partofspeechcounter.get(line);
                                    counter++;
                                    partofspeechcounter.put(line, counter);
                                }
                                else {
                                    if (!line.startsWith("#")) {
                                        partofspeechcounter.put(line,1);
                                    }
                                }
                            }
                        }
                        Map<String, Integer> partofspeechcounter1 = sortByValueDescending(partofspeechcounter);

                        // Create Document
                        Document tempPOSdoc = new Document()
                                .append("_id", "POS");

                        List<Document> tempPOS = new ArrayList<>();

                        for (String i: partofspeechcounter1.keySet()) {
                            Document tempdoc1 = new Document();
                            tempdoc1.append("count", Integer.toString(partofspeechcounter1.get(i)));
                            tempdoc1.append("POS", i);
                            tempPOS.add(tempdoc1);
                        }

                        tempPOSdoc.append("result", tempPOS);
                        tempPOSdoc.append("success", "true");
                        // Add to Database
                        connectionHandler.replaceDocument("statistics", "POS", tempPOSdoc);
                        break;
                    case "24":
                        break;
                    case "25":
                        // Create a Hashmap containing every redesentiment as key and increment its value by one every time we find it in the txt
                        HashMap<String, ArrayList<Float>> redesentiment = new HashMap<>();
                        try(BufferedReader br = new BufferedReader(new FileReader("src/main/resources/Sentiments.txt"))) {
                            for(String line; (line = br.readLine()) != null; ) {
                                String[] result = line.split(":");
                                if(result.length == 2) {
                                    String[] sentiments = result[1].split(" ");
                                    ArrayList<Float> floatlist = new ArrayList<>();
                                    for (String i: sentiments) {
                                        floatlist.add(Float.parseFloat(i));
                                    }
                                    redesentiment.put(result[0], floatlist);
                                }
                            }
                        }

                        for (String i: fraktionliste.keySet()) {
                            int possentimentcount = 0;
                            int neusentimentcount = 0;
                            int negsentimentcount = 0;
                            for (String j: fraktionliste.get(i).getAbgeordnetenliste()) {
                                for(String k: abgeordnetenliste.get(j).getRedeliste()) {
                                    if(redesentiment.containsKey(k)) {
                                        for(Float l: redesentiment.get(k)) {
                                            if (l > 0) {
                                                possentimentcount++;
                                            }
                                            else if(l == 0) {
                                                neusentimentcount++;
                                            }
                                            else if(l < 0) {
                                                negsentimentcount++;
                                            }
                                        }
                                    }
                                }
                            }
                            int totalcount = possentimentcount + neusentimentcount + negsentimentcount;
                            float percentpos = (possentimentcount * 100.0f)/totalcount;
                            float percentneu = (neusentimentcount * 100.0f)/totalcount;
                            float percentneg = (negsentimentcount * 100.0f)/totalcount;
                            if(totalcount > 0) {
                                System.out.println("Partei: " + fraktionliste.get(i).getName() + " Positiv: " + percentpos  + " Neutral: " + percentneu + " Negativ: " + percentneg);
                            }
                        }
                    case "26":
                        HashMap<String, Integer> redesentiments = new HashMap<>();
                        try(BufferedReader br = new BufferedReader(new FileReader("src/main/resources/Sentiments.txt"))) {
                            for(String line; (line = br.readLine()) != null; ) {
                                String[] result = line.split(":");
                                if(result.length == 2) {
                                    String[] sentiments = result[1].split(" ");
                                    for (String j : sentiments) {
                                        if (redesentiments.containsKey(j)) {
                                            int counter = redesentiments.get(j);
                                            counter++;
                                            redesentiments.put(j, counter);
                                        }
                                        else {
                                            redesentiments.put(j, 1);
                                        }
                                    }
                                }
                            }
                        }
                        Map<String, Integer> redesentiments1 = sortByValueDescending(redesentiments);

                        // Create document

                        Document tempsentimentdoc = new Document()
                                .append("_id", "sentiment");

                        List<Document> tempsentimentdoclist = new ArrayList<>();

                        for(String i: redesentiments1.keySet()) {
                            Document tempdoc1 = new Document();

                            tempdoc1.append("sentiment", i);
                            tempdoc1.append("count", redesentiments1.get(i));

                            tempsentimentdoclist.add(tempdoc1);
                        }

                        tempsentimentdoc.append("result", tempsentimentdoclist);
                        tempsentimentdoc.append("success", "true");

                        connectionHandler.replaceDocument("statistics", "sentiment", tempsentimentdoc);
                        break;
                    case "27":
                        break;
                    case "28":
                        break;
                    case "29":
                        break;
                    case "30":
                        break;
                    default:
                        System.out.println("Falsche Eingabe.");
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

