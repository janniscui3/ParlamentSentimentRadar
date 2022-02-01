package Data;

import org.bson.Document;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * This Class represents a faction, which is comprised of a name and a list of its representatives
 *
 * @author Jannis Cui
 */
public class Fraktion {
    private final String fraktionname;
    private final HashSet<String> abgeordnetenliste;

    public Fraktion(String name) {
        fraktionname = name;
        abgeordnetenliste = new HashSet<>();
    }

    public Fraktion(String name, HashSet<String> liste) {
        fraktionname = name;
        abgeordnetenliste = liste;
    }

    public Fraktion(Document obj) {
        fraktionname = (String) obj.get("_id");
        ArrayList<String> templist = (ArrayList<String>) obj.get("abgeordnete");
        abgeordnetenliste = new HashSet<>(templist);
    }

    public void addToListOfRepresentatives(String ID) {
        this.abgeordnetenliste.add(ID);
    }

    public Boolean isInParty(String ID) {
        return abgeordnetenliste.contains(ID);
    }

    public Integer getPartySize() {
        return abgeordnetenliste.size();
    }

    public String getName() {
        return this.fraktionname;
    }

    public HashSet<String> getAbgeordnetenliste() {
        return this.abgeordnetenliste;
    }
    //Getter and Setter

}
