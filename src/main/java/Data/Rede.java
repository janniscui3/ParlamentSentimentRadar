package Data;

import org.bson.Document;

import java.util.ArrayList;

/**
 * This class represents a single Rede.
 * It has all the Data required to identify a Rede, aswell as
 * its Redner. It also contains its own text, while storing
 * its comments in a different list.
 * Otherwise it has normal get and setter methods.
 *
 * @author Jannis Cui
 */
public class Rede {
    private final ArrayList<String> paragraphs;
    private final String rednerid;
    private final String redeid;
    private final String sitzungsnr;
    private final String tagesordnungspunkt;
    private final ArrayList<String> kommentare;

    public Rede(String rednerID, String redeID, String sitzung, String topid) {
        rednerid = rednerID;
        redeid = redeID;
        sitzungsnr = sitzung;
        tagesordnungspunkt = topid;
        paragraphs = new ArrayList<>();
        kommentare = new ArrayList<>();
    }

    public Rede(Document obj) {
        redeid = (String) obj.get("_id");
        rednerid = (String) obj.get("rednerid");
        sitzungsnr = (String) obj.get("sitzungsnr");
        tagesordnungspunkt = (String) obj.get("tagesordnungspunkt");
        paragraphs = (ArrayList<String>) obj.get("paragraphs:");
        kommentare = (ArrayList<String>) obj.get("comments:");
    }

    // Getter and Setter
    public Integer getkommentaresize() {
        return this.kommentare.size();
    }

    public String getRedeid() {
        return this.redeid;
    }

    public String getRednerid() {
        return this.rednerid;
    }

    public String getSitzungsnr() {
        return this.sitzungsnr;
    }

    public String getTagesordnungspunkt() {
        return this.tagesordnungspunkt;
    }

    public void addToParagraphs(String text) {
        this.paragraphs.add(text);
    }

    public void addToComments(String text) {
        this.kommentare.add(text);
    }

    public ArrayList<String> getparagraphs() {
        return this.paragraphs;
    }

    public ArrayList<String> getcomments() {
        return this.kommentare;
    }

}
