package Database;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import Data.*;
import org.bson.Document;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Serializes Documents into my class structure, and serializes my class structure into documents.
 */
public class MongoDBCollectionHandler {
    HashMap<String, Abgeordnete> abgeordnetenliste;
    HashMap<String, Fraktion> fraktionliste;
    HashMap<String, Rede> redeliste;
    HashMap<String, Sitzung> sitzungliste;

    /** Needs to be given all data's to turn them into documents for the database.
     * @author Jannis Cui
     * @param abgeordnetenliste abgeordnetenliste
     * @param fraktionliste fraktionliste
     * @param redeliste redeliste
     * @param sitzungliste sitzungliste
     */
    //Constructor
    public MongoDBCollectionHandler(HashMap<String, Abgeordnete> abgeordnetenliste, HashMap<String, Fraktion> fraktionliste, HashMap<String, Rede> redeliste, HashMap<String, Sitzung> sitzungliste) {
        this.abgeordnetenliste = abgeordnetenliste;
        this.fraktionliste = fraktionliste;
        this.redeliste = redeliste;
        this.sitzungliste = sitzungliste;
    }

    /** Given a Hashmap of speakers, turn them all into Documents
     * @author Jannis Cui
     * @return List of Documents that contain data of speakers
     */
    public List<Document> createSpeakerCollection() {
        List<Document> speakercollection = new ArrayList<>();
        for (String i : abgeordnetenliste.keySet()) {
            Document temp = new Document()
                    .append("_id", i)
                    .append("vorname", abgeordnetenliste.get(i).getVorName())
                    .append("nachname", abgeordnetenliste.get(i).getNachName())
                    .append("fraktion", abgeordnetenliste.get(i).getFraktionname());

            ArrayList<String> redeids = abgeordnetenliste.get(i).getAllRedeIds();

            temp.append("rede", redeids);
            speakercollection.add(temp);
        }
        return speakercollection;
    }

    /** Given a Hashmap of factions, turn them all into Documents
     * @author Jannis Cui
     * @return List of Documents that contain data of factions
     */
    public List<Document> createFraktionCollection() {
        List<Document> factioncollection = new ArrayList<>();
        for (String i : fraktionliste.keySet()) {
            Document temp = new Document()
                    .append("_id", i)
                    .append("abgeordnete", fraktionliste.get(i).getAbgeordnetenliste());

            factioncollection.add(temp);
        }
        return factioncollection;
    }

    /** Given a Hashmap of speeches, turn them all into Documents
     * @author Jannis Cui
     * @return List of Documents that contain data of speeches
     */
    public List<Document> createSpeechCollection() {
        List<Document> speechcollection = new ArrayList<>();
        for (String i : redeliste.keySet()) {
            Document temp = new Document()
                    .append("_id", i)
                    .append("rednerid", redeliste.get(i).getRednerid())
                    .append("sitzungsnr", redeliste.get(i).getSitzungsnr())
                    .append("tagesordnungspunkt", redeliste.get(i).getTagesordnungspunkt())
                    .append("paragraphs:", redeliste.get(i).getparagraphs())
                    .append("comments:", redeliste.get(i).getcomments());

            speechcollection.add(temp);
        }
        return speechcollection;
    }

    /** Given a Hashmap of protocols, turn them all into Documents
     * @author Jannis Cui
     * @return List of Documents that contain data of protocols
     */
    public List<Document> createProtocolCollection() {
        List<Document> protocolcollection = new ArrayList<>();
        for (String i : sitzungliste.keySet()) {
            Document temp = new Document()
                    .append("_id", i)
                    .append("datum", sitzungliste.get(i).getDatum())
                    .append("sitzungsleiter", sitzungliste.get(i).getSitzungsleiter())
                    .append("legislaturperiode", sitzungliste.get(i).getLegislaturperiode());

            HashMap<String, Tagespunkte> tagespunkteliste = sitzungliste.get(i).getTagespunkteliste();
            List<Document> tagesordnungspunkte = new ArrayList<>();

            for (String j : tagespunkteliste.keySet()) {
                Document temp2 = new Document()
                        .append("_id", j)
                        .append("paragraphs", tagespunkteliste.get(j).getparagraphs())
                        .append("rede", tagespunkteliste.get(j).getRedeliste())
                        .append("kommentare", tagespunkteliste.get(j).getKommentare());

                tagesordnungspunkte.add(temp2);
            }
            temp.append("tagesordnungen", tagesordnungspunkte);
            protocolcollection.add(temp);
        }
        return protocolcollection;
    }


    /**
     * Given a mongocollection, build our class structure out of it.
     * @author Jannis Cui
     * @param mongoCollection MongCollection
     * @return Hashmap
     */
    public static HashMap<String, Abgeordnete> buildHashMapSpeaker(MongoCollection mongoCollection) {
        HashMap<String, Abgeordnete> abgeordneteHashMap = new HashMap<>();
        MongoCursor<Document> cursor = mongoCollection.find().iterator();
        try {
            while (cursor.hasNext()) {
                Document obj = cursor.next();
                String abgeordnetenid = (String) obj.get("_id");
                Abgeordnete temp = new Abgeordnete(obj);

                abgeordneteHashMap.put(abgeordnetenid, temp);
            }
        } finally {
            cursor.close();
        }
        return abgeordneteHashMap;
    }
    /**
     * Given a mongocollection, build our class structure out of it.
     * @author Jannis Cui
     * @param mongoCollection MongCollection
     * @return Hashmap
     */
    public static HashMap<String, Fraktion> buildHashMapFaction(MongoCollection mongoCollection) {
        HashMap<String, Fraktion> fraktionHashMap = new HashMap<>();
        MongoCursor<Document> cursor = mongoCollection.find().iterator();
        try {
            while (cursor.hasNext()) {
                Document obj = cursor.next();
                String faktionname = (String) obj.get("_id");

                Fraktion temp = new Fraktion(obj);
                fraktionHashMap.put(faktionname, temp);
            }
        } finally {
            cursor.close();
        }
        return fraktionHashMap;
    }
    /**
     * Given a mongocollection, build our class structure out of it.
     * @author Jannis Cui
     * @param mongoCollection MongCollection
     * @return Hashmap
     */
    public static HashMap<String, Rede> buildHashMapSpeech(MongoCollection mongoCollection) {
        HashMap<String, Rede> redeHashMap = new HashMap<>();
        MongoCursor<Document> cursor = mongoCollection.find().iterator();
        try {
            while (cursor.hasNext()) {
                Document obj = cursor.next();
                String redeid = (String) obj.get("_id");
                Rede temp = new Rede(obj);

                redeHashMap.put(redeid, temp);
            }
        } finally {
            cursor.close();
        }
        return redeHashMap;
    }
    /**
     * Given a mongocollection, build our class structure out of it.
     * @author Jannis Cui
     * @param mongoCollection MongCollection
     * @return Hashmap
     */
    public static HashMap<String, Sitzung> buildHashMapProtocol(MongoCollection mongoCollection) {
        HashMap<String, Sitzung> sitzungHashMap = new HashMap<>();
        MongoCursor<Document> cursor = mongoCollection.find().iterator();
        try {
            while (cursor.hasNext()) {
                Document obj = cursor.next();
                String sitzungsid = (String) obj.get("_id");
                Sitzung temp = new Sitzung(obj);

                sitzungHashMap.put(sitzungsid, temp);
            }
        } finally {
            cursor.close();
        }
        return sitzungHashMap;
    }
}
