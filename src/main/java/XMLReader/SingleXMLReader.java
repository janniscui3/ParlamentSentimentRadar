package XMLReader;

import Data.Abgeordnete;
import Data.Rede;
import Data.Sitzung;
import Data.Tagespunkte;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

/**
 * Returns a ReadSingleXML Object, that gets a single XML
 * File as input and can do various operations on it, such as
 * returning a list of abgeordneten or reden from a given XML.
 *
 * @author Jannis Cui
 */
public class SingleXMLReader {
    HashMap<String, Abgeordnete> abgeordnetenliste = new HashMap<>();
    HashMap<String, Rede> redeliste = new HashMap<>();
    HashMap<String, Sitzung> sitzungsliste = new HashMap<>();
    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    File filename;
    String id;
    String vorname;
    String nachname;
    String fraktion;

    //Constructor

    /**
     * @param fname The file that is to be read
     */
    public SingleXMLReader(File fname) {
        filename = fname;
    }


    //Methods

    /**
     * @return a list of all abgeordneten in the given XML
     */
    public HashMap<String, Abgeordnete> makeAbgeordnetenliste() {
        try {
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(this.filename);


            // Find all elements with tag name redner
            NodeList list = doc.getElementsByTagName("redner");

            for (int i = 0; i < list.getLength(); i++) {
                // Get the i'th item out of the list and save it as node
                Node node = list.item(i);

                //If said Node is a ElementNode
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;
                    try {
                        fraktion = element.getElementsByTagName("fraktion").item(0).getTextContent();
                        // change fraktion name to uppercase and remove all whitespace, since there are duplicates in the XML, as well as remove non break spaces
                        fraktion = fraktion.toUpperCase();
                        fraktion = fraktion.replaceAll(" ", "");
                        fraktion = fraktion.replaceAll("\u00a0", "");
                        id = element.getAttribute("id");
                        vorname = element.getElementsByTagName("vorname").item(0).getTextContent();
                        nachname = element.getElementsByTagName("nachname").item(0).getTextContent();

                    }
                    // If a Fraktion doesn't exist:
                    catch (NullPointerException e) {
                        id = element.getAttribute("id");
                        vorname = element.getElementsByTagName("vorname").item(0).getTextContent();
                        nachname = element.getElementsByTagName("nachname").item(0).getTextContent();
                    }
                    finally {
                        // In some XML Files, there are id's that are the empty string. In particular in 6.xml
                        if (!("".equals(id))) {
                            // Create an instance of a representative(abgeordnete) and put it into the HashMap, if it isn't already in there.
                            if (!abgeordnetenliste.containsKey(id)) {
                                Abgeordnete temp = new Abgeordnete(vorname, nachname, id, fraktion);
                                abgeordnetenliste.put(id, temp);
                            }

                        }
                    }
                }
            }
        } catch (ParserConfigurationException | SAXException | IOException | NumberFormatException e) {
            e.printStackTrace();
        }
        return abgeordnetenliste;
    }

    /**
     * @return a list of all reden in a given XML
     */
    public HashMap<String, Rede> makeRedeliste() {
        try {
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(this.filename);

            // Get root element
            Element dbtplenarprotokoll = doc.getDocumentElement();
            String sitzungsnr = dbtplenarprotokoll.getAttribute("sitzung-nr");
            // Get all elements in the doc with tagname "rede"
            NodeList list = doc.getElementsByTagName("rede");

            //Iterate over all these elements
            for (int i = 0; i < list.getLength(); i++) {
                try {
                    Node rede = list.item(i);

                    // Grab the parent node, which is always a Tagesordnungspunkt
                    Node parent = list.item(i).getParentNode();

                    if (rede.getNodeType() == Node.ELEMENT_NODE) {
                        Element parentelement = (Element) parent;
                        Element redeelement = (Element) rede;
                        String redeid = redeelement.getAttribute("id");

                        // Get the talkers id
                        NodeList rednerliste = redeelement.getElementsByTagName("redner");
                        Element redner = (Element) rednerliste.item(0);
                        String rednerid = redner.getAttribute("id");

                        // Get tagesordnungspunkt id
                        String topicid = parentelement.getAttribute("top-id");
                        // Create a new object of Rede class
                        Rede temprede = new Rede(rednerid, redeid, sitzungsnr, topicid);

                        // Get all the elements
                        NodeList redeinhaltliste = redeelement.getElementsByTagName("*");

                        // Read the Data in a given j Rede
                        for (int j = 0; j < redeinhaltliste.getLength(); j++) {
                            Element redeinhaltelement = (Element) redeinhaltliste.item(j);
                            // Only choose paragraph elements that have class J_1, J or O
                            String classname = redeinhaltelement.getAttribute("klasse");
                            if (classname.equals("J") || classname.equals("O") || classname.equals("J_1")) {
                                temprede.addToParagraphs(redeinhaltelement.getTextContent());
                            }
                            // Check for comments
                            if (redeinhaltelement.getTagName().equals("kommentar")) {
                                temprede.addToComments(redeinhaltelement.getTextContent());
                            }
                        }
                        redeliste.put(redeid, temprede);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        } catch (ParserConfigurationException | SAXException | IOException | NumberFormatException e) {
            e.printStackTrace();
        }
        return redeliste;
    }

    /**
     * @return makes a single Sitzung out of a given XML
     */
    public HashMap<String, Sitzung> makeSitzungsliste() {
        try {
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(this.filename);

            // Get root node
            Element dbtplenarprotokoll = doc.getDocumentElement();

            // Get sitzung-nr from root node
            String sitzungsnr = dbtplenarprotokoll.getAttribute("sitzung-nr");
            String datum = dbtplenarprotokoll.getAttribute("sitzung-datum");
            String wahlperiode = dbtplenarprotokoll.getAttribute("wahlperiode");

            // Get sitzungsleiter from sitzungsbeginn
            String sitzungsleiter = "Nicht gefunden";
            try {
                NodeList sitzungsbeginn = doc.getElementsByTagName("sitzungsbeginn");
                Element sitzungsbeginnelement = (Element) sitzungsbeginn.item(0);
                NodeList sitzungsbeginnliste = sitzungsbeginnelement.getElementsByTagName("*");
                for (int i = 0; i < sitzungsbeginnliste.getLength(); i++) {
                    try {
                        Element sitzungsleiterelement = (Element) sitzungsbeginnliste.item(i);
                        String classname = sitzungsleiterelement.getAttribute("klasse");
                        if (classname.equals("N") || sitzungsleiterelement.getTagName().equals("name")) {
                            sitzungsleiter = sitzungsleiterelement.getTextContent();
                            sitzungsleiter = sitzungsleiter.replaceAll(":", "");
                        }

                    } catch (NullPointerException e) {
                        continue;
                    }
                }

            } catch (NullPointerException e) {
                e.printStackTrace();
            }

            Sitzung protokoll = new Sitzung(sitzungsnr, datum, sitzungsleiter, wahlperiode);

            NodeList tagespunkteliste = doc.getElementsByTagName("tagesordnungspunkt");

            for (int i = 0; i < tagespunkteliste.getLength(); i++) {

                Node node = tagespunkteliste.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element tagespunktelement = (Element) node;

                    NodeList tagesinhaltliste = tagespunktelement.getElementsByTagName("*");
                    String tagespunktid = tagespunktelement.getAttribute("top-id");

                    Tagespunkte temptagespunkt = new Tagespunkte(tagespunktid);

                    for (int j = 0; j < tagesinhaltliste.getLength(); j++) {
                        Element tagesinhaltelement = (Element) tagesinhaltliste.item(j);
                        String classname = tagesinhaltelement.getAttribute("klasse");
                        if (classname.equals("J") || classname.equals("O") || classname.equals("J_1")) {
                            temptagespunkt.addParagraph((tagesinhaltelement.getTextContent()));
                        }
                        if (tagesinhaltelement.getTagName().equals("Kommentar")) {
                            temptagespunkt.addParagraph((tagesinhaltelement.getTextContent()));
                            temptagespunkt.addKommentar((tagesinhaltelement.getTextContent()));
                        }
                        if (tagesinhaltelement.getTagName().equals("rede")) {
                            temptagespunkt.addRede(tagesinhaltelement.getAttribute("id"));
                        }
                    }
                    protokoll.addToTagespunktliste(tagespunktid, temptagespunkt);
                }
            }

            sitzungsliste.put(wahlperiode + "_" + sitzungsnr, protokoll);
        } catch (ParserConfigurationException | SAXException | IOException | NumberFormatException e) {
            e.printStackTrace();
        }
        return sitzungsliste;
    }

}
/*
                            Element redeinhaltelement = (Element) redeinhaltliste.item(j);
                            // Only choose paragraph elements that have class J_1, J or O
                            String classname = redeinhaltelement.getAttribute("klasse");
                            if(classname.equals("J") || classname.equals("O") || classname.equals("J_1")  ) {
                                System.out.println(redeinhaltelement.getTextContent());
                            }

                            if (parent.getNodeType() == Node.ELEMENT_NODE) {
                            Element parentelement = (Element) parent;
                            NodeList tagesinhaltliste = parentelement.getElementsByTagName("*");
                            String tagespunktid = parentelement.getAttribute("top-id");

                            for (int j = 0; j < tagesinhaltliste.getLength(); j++) {
                                Element tagesinhaltelement = (Element) tagesinhaltliste.item(j);
                                String classname = tagesinhaltelement.getAttribute("klasse");
                                if(classname.equals("J") || classname.equals("O") || classname.equals("J_1")) {
                                    System.out.println(tagesinhaltelement.getTextContent());
                                }
                            }
                            System.out.println(tagespunktid);
                        }
 */