
import Data.*;
import Database.MongoDBCollectionHandler;
import Database.MongoDBConnectionHandler;
import XMLReader.ReadFolderOfXML;
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
            System.out.println("Drücke 21, um die häufigsten Tokens zu kriegen.");
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

