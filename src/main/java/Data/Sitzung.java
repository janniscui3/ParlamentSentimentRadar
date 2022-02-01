package Data;

import org.bson.Document;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * This Class represents a single protocol.
 * It can be identifiert by its sitzungsnummer.
 * Also has a list of Tagespunkte contained in the Sitzung.
 *
 * @author Jannis Cui
 */
public class Sitzung {
    private String datum;
    private String legislaturperiode;
    private String titel;
    private String sitzungnummer;
    private String sitzungsleiter;
    private HashMap<String, Tagespunkte> tagespunkteliste = new HashMap<>();
    //Constructor

    public Sitzung(String title, String date, String sitzungsltr) {
        titel = title;
        datum = date;
        sitzungsleiter = sitzungsltr;
    }

    public Sitzung(Document obj) {
        titel = (String) obj.get("_id");
        datum = (String) obj.get("datum");
        sitzungsleiter = (String) obj.get("sitzungsleiter");
        ArrayList<Document> temp = (ArrayList<Document>) obj.get("tagesordnungen");
        for (Document document : temp) {
            String tagespunktid = (String) document.get("_id");
            Tagespunkte temptagespunkt = new Tagespunkte(document);
            tagespunkteliste.put(tagespunktid, temptagespunkt);
        }
    }

    //Getter and Setter
    public void addToTagespunktliste(String tagespunkttitel, Tagespunkte tagespunkt) {
        this.tagespunkteliste.put(tagespunkttitel, tagespunkt);
    }

    public ArrayList<String> getTextofTagespunkt(String tagespunkttitel) {
        return this.tagespunkteliste.get(tagespunkttitel).getparagraphs();
    }

    public HashMap<String, Tagespunkte> getTagespunkteliste() {
        return this.tagespunkteliste;
    }

    public String getDatum() {
        return this.datum;
    }

    public String getSitzungsleiter() {
        return this.sitzungsleiter;
    }


    public void setDatum(String datum) {
        this.datum = datum;
    }

    public String getTitel() {
        return titel;
    }

    public void setTitel(String titel) {
        this.titel = titel;
    }

    public String getSitzungnummer() {
        return sitzungnummer;
    }

    public void setSitzungnummer(String sitzungnummer) {
        this.sitzungnummer = sitzungnummer;
    }

    public void setTagespunkteliste(HashMap<String, Tagespunkte> tagespunkteliste) {
        this.tagespunkteliste = tagespunkteliste;
    }

    public void setSitzungsleiter(String sitzungsleiter) {
        this.sitzungsleiter = sitzungsleiter;
    }
}

