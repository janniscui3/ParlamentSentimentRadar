package XMLReader;

import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

public class WebCrawler {
    public static void main(String[] args) throws InterruptedException, IOException {
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
    }
}
