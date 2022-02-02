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

import java.nio.file.Path;
import java.nio.file.Paths;

public class WebCrawler {

    public void DownloadXMLFromURL(URL url) throws IOException, SAXException, ParserConfigurationException, TransformerException {
        // Turn URL into Document
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = dbf.newDocumentBuilder();

        InputStream stream = url.openStream();
        org.w3c.dom.Document doc = docBuilder.parse(stream);

        // Create helper-strings
        String url1 = url.toString();
        String substr = url1.substring(url1.length() - 14);
        String path = "src/main/resources/Bundestag90/".concat(substr);

        // Convert document into file
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        DOMSource source = new DOMSource(doc);

        FileWriter writer = new FileWriter(new File(path));
        StreamResult result = new StreamResult(writer);

        transformer.transform(source, result);
    }

    public static void main(String[] args) throws InterruptedException, IOException, ParserConfigurationException, SAXException, TransformerException {
        int offset = 0;
        try {
            Document doc = Jsoup.connect("https://www.bundestag.de/ajax/filterlist/de/services/opendata/866354-866354?limit=10&noFilterSet=true&offset=0").get();
            Elements links = doc.select("a");

            for (Element element : links) {
                System.out.println(element.attr("abs:href"));
            }
        }
        catch (HttpStatusException ex) {
            assert true;
        }

        URL currenturl = new URL("https://www.bundestag.de/resource/blob/879944/86559dfdad1e7304d92fae71974ad18d/20015-data.xml");
        WebCrawler web = new WebCrawler();
        web.DownloadXMLFromURL(currenturl);
    }
}
