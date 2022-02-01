package Data;

import org.bson.Document;

import java.util.ArrayList;

/**
 * This class represents a single Tagespunkt
 * It contains a list of its text content(paragraphs), as well
 * as its contained comments.
 * It also has its containend Reden stored seperately.
 *
 * @author Jannis Cui
 */
public class Tagespunkte {
    private String thema;
    private String identifier;
    private ArrayList<String> paragraphs = new ArrayList<>();
    private ArrayList<String> redeliste = new ArrayList<>();
    private ArrayList<String> kommentare = new ArrayList<>();

    public Tagespunkte(String id) {
        identifier = id;
    }

    public Tagespunkte(Document obj) {
        identifier = (String) obj.get("_id");
        paragraphs = (ArrayList<String>) obj.get("paragraphs");
        redeliste = (ArrayList<String>) obj.get("rede");
        kommentare = (ArrayList<String>) obj.get("kommentare");
    }

    public void addParagraph(String text) {
        this.paragraphs.add(text);
    }

    public void addKommentar(String text) {
        this.kommentare.add(text);
    }

    public void addRede(String ID) {
        this.redeliste.add(ID);
    }

    public ArrayList<String> getparagraphs() {
        return this.paragraphs;
    }

    public ArrayList<String> getKommentare() {
        return this.kommentare;
    }

    public ArrayList<String> getRedeliste() {
        return redeliste;
    }
}
