package XMLReader;

import Data.*;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
/**
 * Returns a ReadSingleXML Object, that gets a single XML
 * File as input and can do various operations on it, such as
 * returning a list of abgeordneten or reden from a given XML.
 *
 * @author Sam Ghanei
 */
public class ReadFolderOfXML {
    //Fields
    HashMap<String, Abgeordnete> abgeordnetenliste = new HashMap<>();
    HashMap<String, Fraktion> fraktionliste = new HashMap<>();
    HashMap<String, Rede> redeliste = new HashMap<>();
    HashMap<String, Sitzung> sitzungliste = new HashMap<>();
    //Constructor

    //Methods
    public Boolean initialize(String filepath) {
        int countfiles = 0;
        try {
            File folder = new File(filepath);
            File[] fileslist = folder.listFiles();

            // Iterate over every file in the given directory
            for (File file : fileslist) {
                try {
                    if (file.isFile() && !(file.getName().equals("dbtplenarprotokoll.dtd"))) {
                        System.out.println("Parsing file: " + file.getName());
                        SingleXMLReader reader = new SingleXMLReader(file);
                        HashMap<String, Abgeordnete> templist = reader.makeAbgeordnetenliste();
                        abgeordnetenliste.putAll(templist);
                        HashMap<String, Rede> templist2 = reader.makeRedeliste();
                        redeliste.putAll(templist2);
                        HashMap<String, Sitzung> templist3 = reader.makeSitzungsliste();
                        sitzungliste.putAll(templist3);
                        countfiles++;
                    }
                } catch (Exception e) {
                    System.out.println("Das File/Ordner wurde nicht gefunden.");
                }

            }
            System.out.println("Files read:" + countfiles);

            // Listing every redner, as well as putting their ID's into their corresponding party's list
            for (String i : abgeordnetenliste.keySet()) {
                if (!fraktionliste.containsKey(abgeordnetenliste.get(i).getFraktionname())) {
                    Fraktion temp = new Fraktion(abgeordnetenliste.get(i).getFraktionname());
                    fraktionliste.put(abgeordnetenliste.get(i).getFraktionname(), temp);
                }
                fraktionliste.get(abgeordnetenliste.get(i).getFraktionname()).addToListOfRepresentatives(i);
            }

            // Get the ID from a given i rede and put it in the corresponding abgeordnete
            for (String i : redeliste.keySet()) {
                try {
                    abgeordnetenliste.get(redeliste.get(i).getRednerid()).addToRedeliste(redeliste.get(i).getRedeid());
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            }
            // Iterate over every speaker and get their avg character count
            for (String i : abgeordnetenliste.keySet()) {
                int anzahlderreden = 0;
                int charakteranzahl = 0;
                try {
                    ArrayList<String> templist = abgeordnetenliste.get(i).getRedeliste();
                    for (String j : templist) {
                        anzahlderreden++;
                        ArrayList<String> paragraphs3 = redeliste.get(j).getparagraphs();
                        for (String h : paragraphs3) {
                            charakteranzahl += h.length();
                        }
                    }
                } catch (NullPointerException e) {
                    e.printStackTrace();
                    continue;
                }
                if (anzahlderreden != 0) {
                    int avg = charakteranzahl / anzahlderreden;
                    abgeordnetenliste.get(i).setDurchschnittredelaenge(avg);
                }
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
            return true;
        }
        return false;
    }

    public HashMap<String, Abgeordnete> getAbgeordnetenliste() {
        return abgeordnetenliste;
    }

    public HashMap<String, Fraktion> getFraktionliste() {
        return fraktionliste;
    }

    public HashMap<String, Rede> getRedeliste() {
        return redeliste;
    }

    public HashMap<String, Sitzung> getSitzungliste() {
        return sitzungliste;
    }
}

