package XMLReader;

import org.apache.commons.io.IOUtils;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.xml.sax.SAXException;

import java.io.*;
import java.net.URL;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * The WebCrawler class itself only has a method that will download a XML
 * from a given URL and save it in the resources/Bundestag90 folder.
 * In the Main method of this class we give the method the url of the bundestag XMLs and
 * iterate over every link.
 * @author Jannis Cui
 */
public class WebCrawler {

    /** This method will return a XML in the resources/Bundestag90 folder from a given URL.
     * @author Jannis Cui
     * @param url URL that contains the XML
     * @throws IOException Exceptions
     * @throws SAXException Exceptions
     * @throws ParserConfigurationException Exceptions
     * @throws TransformerException Exceptions
     */
    public void DownloadXMLFromURL(URL url) throws IOException, SAXException, ParserConfigurationException, TransformerException {
        // Create helper-strings
        String url1 = url.toString();
        String substr = url1.substring(url1.length() - 14);
        String path = "src/main/resources/Bundestag90/".concat(substr);

        File file = new File(path);
        if(!file.isFile()) {
            // Turn URL into Document
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = dbf.newDocumentBuilder();

            InputStream stream = url.openStream();
            org.w3c.dom.Document doc = docBuilder.parse(stream);

            // Convert document into file
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);

            FileWriter writer = new FileWriter(new File(path));
            StreamResult result = new StreamResult(writer);

            transformer.transform(source, result);
        }
        else {
            System.out.println("Das File " + substr + " existiert bereits.");
        }
    }

    /**
     * Method to get a abgeordnetens picture from his name
     * @author Sam Ghanei
     * @param name
     * @return
     * @throws IOException
     */
    public static String getPic(String name) throws IOException {
        String pic = "";

        try {
            Document document = Jsoup.connect("https://bilddatenbank.bundestag.de/search/picture-result?" +
                    "query=" +
                    name +
                    "&filterQuery%5Bereignis%5D%5B%5D=Portr%C3%A4t%2FPortrait&sortVal=3#group-1").get();
            Elements elements = document.select("img");
            pic = elements.get(2).absUrl("src");
        } catch (Exception e) {
            System.out.println("Fehler beim fetchen vom Bild");
        }
        System.out.println(pic);
        return pic;
    }
    /** Main method used to download all XMLS into resource folder.
     * Just run this class and it will download all the XML's
     * @author Jannis Cui
     * @param args
     */
    public static void main(String[] args) throws InterruptedException, IOException, ParserConfigurationException, SAXException, TransformerException {
        Scanner scanner = new Scanner(System.in);
        String choose = "";
        System.out.println("Welches Periode von Protokollen wollen sie downloaden? \n 1 für 20. Periode \n 2. für 19. Periode");
        choose = scanner.nextLine();
        int offset = 0;
        boolean working = true;
        Document doc;

        while (working) {
            try {
                if (choose.equals("1")) {
                    doc = Jsoup.connect("https://www.bundestag.de/ajax/filterlist/de/services/opendata/866354-866354?limit=10&noFilterSet=true&offset=".concat(Integer.toString(offset))).get();
                }
                else {
                    doc = Jsoup.connect("https://www.bundestag.de/ajax/filterlist/de/services/opendata/543410-543410?limit=10&noFilterSet=true&offset=".concat(Integer.toString(offset))).get();
                }
                Elements links = doc.select("a");

                if (links.isEmpty()) {
                    working = false;
                    System.out.println("Keine XML's mehr gefunden.");
                }
                for (Element element : links) {
                    System.out.println(element.attr("abs:href"));
                    URL currenturl = new URL(element.attr("abs:href"));
                    WebCrawler web = new WebCrawler();
                    web.DownloadXMLFromURL(currenturl);
                }
            }
            catch (HttpStatusException ex) {
                assert true;
            }
            offset += 10;
        }
    }
}
