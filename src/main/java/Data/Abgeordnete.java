package Data;

import Interface.Person_File_Impl;
import org.bson.Document;

import java.util.ArrayList;

/**
 * This Class represents a Abgeordneten.
 * It contains all necessary Data to identify a given
 * Abgeordneten, aswell as some auxiliary methods to
 * answer some of the questions.
 * Also contains a list of its own reden.
 *
 * @author Jannis Cui
 */
public class Abgeordnete extends Person_File_Impl {
    // Main Attributes
    private final String abgeordnetennummer;
    private String fraktionname;
    private Fraktion fraktion;
    private ArrayList<String> redeliste = new ArrayList<>();

    // Utility Attributes
    private Integer durchschnittredelaenge;


    //Constructor
    public Abgeordnete(String pName, String nName, String number) {
        super(pName, nName);
        abgeordnetennummer = number;
        durchschnittredelaenge = 0;
    }

    public Abgeordnete(String pName, String nName, String number, String faction) {
        super(pName, nName);
        abgeordnetennummer = number;
        fraktionname = faction;
        durchschnittredelaenge = 0;
    }

    public Abgeordnete(Document obj) {
        super((String) obj.get("vorname"), (String) obj.get("nachname"));
        abgeordnetennummer = (String) obj.get("_id");
        fraktionname = (String) obj.get("fraktion");
        redeliste = (ArrayList<String>) obj.get("rede");
    }

    //Getter and Setter
    public void addToRedeliste(String redeid) {
        this.redeliste.add(redeid);
    }

    public void setFraktion(Fraktion fraktion) {
        this.fraktion = fraktion;
        fraktion.addToListOfRepresentatives(this.abgeordnetennummer);
    }

    public void setDurchschnittredelaenge(Integer length) {
        this.durchschnittredelaenge = length;
    }

    public void setRedeListe(ArrayList<String> redeliste) {
        this.redeliste = redeliste;
    }

    public Integer getDurchschnittredelaenge() {
        return durchschnittredelaenge;
    }

    public Fraktion getFraktion() {
        if (fraktion != null) {
            return this.fraktion;
        } else {
            return null;
        }
    }

    public String getFraktionname() {
        return this.fraktionname;
    }

    public ArrayList<String> getRedeliste() {
        return redeliste;
    }

    // Other Methods
    public ArrayList<String> getAllRedeIds() {
        return redeliste;
    }
}
