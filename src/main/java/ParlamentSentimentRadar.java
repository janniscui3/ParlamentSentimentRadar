
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
            System.out.println("Drücke 1, um das Programm zu beenden.");
            System.out.println("Drücke 2, um eine Liste aller Redner zu kriegen.");
            System.out.println("Drücke 3, um nach den Vornamen zu filtern.");
            System.out.println("Drücke 4, um eine Liste der Parteien und ihren Abgeordneten zu kriegen.");
            System.out.println("Drücke 5, um mit der Sitzungsnummer und der Nummerindex den Text auszugeben.");
            System.out.println("Drücke 6, um den durchschnittlichen Redebeitrag zu bekommen.");
            System.out.println("Drücke 7, um den durchschnittlichen Redebeitrag je abgeordneten zu bekommen.");
            System.out.println("Drücke 8, um den durchschnittlichen Redebeitrag je Partei zu bekommen.");
            System.out.println("Drücke 9, um die Redebeitrage mit den meisten Zurufen zu kriegen.");
            System.out.println("Drücke 10, um die Menge an Zurufen pro Partei/Abgeordneten zu kriegen.");
            System.out.println("Drücke 11, um eine Liste von abgeordneten zu kriegen, die eine Sitzung geleitet haben");
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
            System.out.println("Drücke 22, um die häufigsten NamedEntities zu kriegen.");
            System.out.println("Drücke 23, um die häufigsten Parts of Speech zu kriegen.");
            System.out.println("Drücke 24, um die Sentimentverteilung pro Redner zu kriegen (Alle reden müssen geladen sein).");
            System.out.println("Drücke 25, um die Sentimentverteilung pro Partei zu kriegen (Alle reden müssen geladen sein).");
            String choose = scanner.nextLine();

            try {
                //
                switch (choose) {
                    // To exit, just break the main loop.
                    case "1":
                        System.out.println("Exiting.");
                        break loop;

                    // To print a list of all redners, iterate over every key in abgeordnetenliste and access it's value's name + party
                    case "2":
                        for (String i : abgeordnetenliste.keySet()) {
                            System.out.println("ID: " + i + " Name: " + abgeordnetenliste.get(i).getVorName() + " " + abgeordnetenliste.get(i).getNachName() + " Partei: " + abgeordnetenliste.get(i).getFraktionname());
                        }
                        System.out.println("Anzahl abgeordneten: " + abgeordnetenliste.size());
                        break;
                    case "3":
                        System.out.println("Wähle den Vornamen aus: ");
                        String name = scanner.nextLine();
                        //Copy all data from abgeordnetenliste to a new temporary list, on which we will filter
                        HashMap<String, Abgeordnete> temporary = new HashMap<>(abgeordnetenliste);

                        try {
                            // If the values name isnt equal to the first name, remove it.
                            temporary.values().removeIf(o -> !o.getVorName().equals(name));
                            for (String i : temporary.keySet()) {
                                System.out.println("ID: " + i + " Name: " + abgeordnetenliste.get(i).getVorName() + " " + abgeordnetenliste.get(i).getNachName() + " Partei: " + abgeordnetenliste.get(i).getFraktionname());
                            }
                            if (temporary.isEmpty()) {
                                System.out.println("Eine solche Person existiert nicht.");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    case "4":
                        // Iterate over every faction and create a list containing its abgeordneten.
                        for (String i : fraktionliste.keySet()) {
                            ArrayList<String> temp = new ArrayList<>();
                            for (String x : fraktionliste.get(i).getAbgeordnetenliste()) {
                                temp.add(abgeordnetenliste.get(x).getVorName() + " " + abgeordnetenliste.get(x).getNachName());
                            }
                            System.out.println("Partei: " + i + ": " + temp);
                        }

                        break;
                    case "5":
                        // Find given Sitzung in Hashmap Sitzungsliste and then return its content by calling getTextofTagespunkt method.
                        System.out.println("Geben sie die Sitzungsnummer + Wahlperiode im format wahlperiode_sitzungsnr an: ");
                        String sitzungsnr = scanner.nextLine();
                        if (!sitzungliste.containsKey(sitzungsnr)) {
                            System.out.println("Diese Sitzung existiert nicht.");
                            break;
                        }
                        System.out.println("Folgende Tagespunkte existieren in dieser Sitzung: ");
                        HashMap<String, Tagespunkte> temptagespunktliste = sitzungliste.get(sitzungsnr).getTagespunkteliste();

                        for (String i : temptagespunktliste.keySet()) {
                            System.out.println(i);
                        }

                        System.out.println("Geben sie den Nummernindex des Tagesordnungspunkt an: ");
                        String nummerindex = scanner.nextLine();
                        if (!sitzungliste.get(sitzungsnr).getTagespunkteliste().containsKey("Tagesordnungspunkt " + nummerindex)) {
                            System.out.println("Dieses Nummerindex des Tagesordnungspunkt existiert nicht.");
                            break;
                        }

                        ArrayList<String> paragraphs = sitzungliste.get(sitzungsnr).getTextofTagespunkt("Tagesordnungspunkt " + nummerindex);
                        for (String i : paragraphs) {
                            System.out.println(i);
                        }
                        break;
                    case "6":
                        // Iterate over every rede and gets its text content and add it together, then divide by amount of reden.
                        int anzahlderreden = redeliste.size();
                        int charakteranzahl = 0;
                        for (String i : redeliste.keySet()) {
                            ArrayList<String> paragraphs2 = redeliste.get(i).getparagraphs();
                            for (String j : paragraphs2) {
                                charakteranzahl += j.length();
                            }
                        }
                        System.out.println(charakteranzahl / anzahlderreden);
                        break;
                    case "7":
                        // Since we set avg length earlier, we can just access it now
                        for (String i : abgeordnetenliste.keySet()) {
                            System.out.println("Name: " + abgeordnetenliste.get(i).getVorName() + " " + abgeordnetenliste.get(i).getNachName() + ", Länge " + abgeordnetenliste.get(i).getDurchschnittredelaenge());
                        }
                        break;
                    case "8":
                        // get average redelaenge from every abgeordneten in a given fraktion, add them all up and divide by amount of abgeordneten
                        for (String i : fraktionliste.keySet()) {
                            charakteranzahl = 0;
                            int anzahlderredner = 0;
                            HashSet<String> templiste = fraktionliste.get(i).getAbgeordnetenliste();
                            for (String j : templiste) {
                                anzahlderredner++;
                                charakteranzahl += abgeordnetenliste.get(j).getDurchschnittredelaenge();
                            }

                            System.out.println("Partei: " + i + " Länge: " + charakteranzahl / anzahlderredner);
                        }
                        break;
                    case "9":
                        // Idea is to have a Treemap and use treemap to sort descendingly.
                        TreeMap<Integer, ArrayList<String>> redelistezurufe = new TreeMap<>(Collections.reverseOrder());

                        // Iterate over every rede and make its amount of comments the key of hashmap
                        for (String i : redeliste.keySet()) {
                            ArrayList<String> array = new ArrayList<>();
                            int kommentargroese = redeliste.get(i).getkommentaresize();
                            if (!redelistezurufe.containsKey(kommentargroese)) {
                                redelistezurufe.put(kommentargroese, array);
                            }
                            redelistezurufe.get(kommentargroese).add(i);
                        }

                        int count = 0;
                        System.out.println("Geben sie an, wieviele sie wollen: ");
                        int maxcount = Integer.parseInt(scanner.nextLine());

                        // Iterate over the Hashmaps keys and print out its attributes
                        for (Integer i : redelistezurufe.keySet()) {
                            if (count == maxcount) {
                                break;
                            }
                            String sitzungsnr1 = redeliste.get(redelistezurufe.get(i).get(0)).getSitzungsnr();
                            System.out.println("Zurufe: " + i + " " + redeliste.get(redelistezurufe.get(i).get(0)).getTagesordnungspunkt() + " Sitzungs-nr: " + sitzungsnr1 + " Datum: " + sitzungliste.get(sitzungsnr1).getDatum() + " Anzahl: " + redelistezurufe.get(i).size());
                            count++;
                        }
                        break;
                    case "10":
                        System.out.println("Noch nicht implementiert");
                        break;
                    case "11":
                        HashMap<String, Integer> sitzungsleiterzaehler = new HashMap<>();
                        for (String i : sitzungliste.keySet()) {
                            String sitzungsleiter = sitzungliste.get(i).getSitzungsleiter();
                            if (sitzungsleiterzaehler.containsKey(sitzungsleiter)) {
                                int count2 = sitzungsleiterzaehler.get(sitzungsleiter);
                                sitzungsleiterzaehler.put(sitzungsleiter, count2 + 1);
                            } else {
                                sitzungsleiterzaehler.put(sitzungsleiter, 1);
                            }
                        }

                        for (String i : sitzungsleiterzaehler.keySet()) {
                            System.out.println("Sitzungsleiter: " + i + " ;geleitet: " + sitzungsleiterzaehler.get(i));
                        }
                        break;
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
                                    tokencounter.put(line,1);
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

                        // Add to Database
                        connectionHandler.addDocumentToCollection(tempdoc, "statistics");
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
                                        namedentitiescounter.put(result[1],1);
                                        namedentitiesgroup.put(result[1],result[0]);
                                    }
                                }
                            }
                        }

                        Map<String, Integer> namedentitiescounter1 = sortByValueDescending(namedentitiescounter);
                        count = 0;
                        int percount = 0;
                        int orgcount = 0;
                        int misccount= 0;
                        int loccount = 0;
                        ArrayList<String> PERlist = new ArrayList<>();
                        ArrayList<String> ORGlist = new ArrayList<>();
                        ArrayList<String> MISClist = new ArrayList<>();
                        ArrayList<String> LOClist = new ArrayList<>();
                        System.out.println("Geben sie an, wieviele sie wollen: ");
                        maxcount = Integer.parseInt(scanner.nextLine());
                        maxcount = maxcount * 4;
                        for (String i: namedentitiescounter1.keySet()) {
                            if (count == maxcount) {
                                break;                            }
                            else if (namedentitiesgroup.get(i).equals("PER") && percount < maxcount/4 ) {
                                PERlist.add(i);
                                percount++;
                            }
                            else if (namedentitiesgroup.get(i).equals("ORG") && orgcount < maxcount/4) {
                                ORGlist.add(i);
                                orgcount++;
                            }
                            else if (namedentitiesgroup.get(i).equals("MISC") && misccount < maxcount/4) {
                                MISClist.add(i);
                                misccount++;
                            }
                            else if (namedentitiesgroup.get(i).equals("LOC") && loccount < maxcount/4) {
                                LOClist.add(i);
                                loccount++;
                            }
                            count = orgcount + percount + misccount + loccount;
                        }

                        for (String i: PERlist) {
                            System.out.println("Namedentity: " + i + " Value: " + namedentitiescounter1.get(i) + " Type: " + namedentitiesgroup.get(i));
                        }
                        for (String i: ORGlist) {
                            System.out.println("Namedentity: " + i + " Value: " + namedentitiescounter1.get(i) + " Type: " + namedentitiesgroup.get(i));
                        }
                        for (String i: MISClist) {
                            System.out.println("Namedentity: " + i + " Value: " + namedentitiescounter1.get(i) + " Type: " + namedentitiesgroup.get(i));
                        }
                        for (String i: LOClist) {
                            System.out.println("Namedentity: " + i + " Value: " + namedentitiescounter1.get(i) + " Type: " + namedentitiesgroup.get(i));
                        }
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
                                    partofspeechcounter.put(line,1);
                                }
                            }
                        }
                        Map<String, Integer> partofspeechcounter1 = sortByValueDescending(partofspeechcounter);
                        for (String i: partofspeechcounter1.keySet()) {
                            System.out.println("POS: " + i + " Value: " + partofspeechcounter1.get(i));
                        }
                    case "24":
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
                        // Iterate over every abgeordnete and get their reden, then find their sentiments in the redesentiment hashmap.
                        for (String i: abgeordnetenliste.keySet()) {
                            int possentimentcount = 0;
                            int neusentimentcount = 0;
                            int negsentimentcount = 0;
                            for(String j: abgeordnetenliste.get(i).getRedeliste()) {
                                if(redesentiment.containsKey(j)) {
                                    for(Float k: redesentiment.get(j)) {
                                        if (k > 0) {
                                            possentimentcount++;
                                        }
                                        else if(k == 0) {
                                            neusentimentcount++;
                                        }
                                        else if(k < 0) {
                                            negsentimentcount++;
                                        }
                                    }
                                }
                            }
                            int totalcount = possentimentcount + neusentimentcount + negsentimentcount;
                            float percentpos = (possentimentcount * 100.0f)/totalcount;
                            float percentneu = (neusentimentcount * 100.0f)/totalcount;
                            float percentneg = (negsentimentcount * 100.0f)/totalcount;
                            if(totalcount > 0) {
                                System.out.println("Abgeordneter: " + abgeordnetenliste.get(i).getVorName() + " " + abgeordnetenliste.get(i).getNachName() + " Positiv: " + percentpos  + " Neutral: " + percentneu + " Negativ: " + percentneg);
                            }
                        }
                        break;
                    case "25":
                        // Create a Hashmap containing every redesentiment as key and increment its value by one every time we find it in the txt
                        redesentiment = new HashMap<>();
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

