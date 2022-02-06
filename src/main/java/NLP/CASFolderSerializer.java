package NLP;

import Data.Rede;
import Database.MongoDBConnectionHandler;
import org.apache.commons.io.FilenameUtils;
import org.apache.uima.UIMAException;
import org.bson.Document;
import org.xml.sax.SAXException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CASFolderSerializer {
    /** Gets a Redeliste and analyses it and creates CAS in a given folder.
     * @param redeliste The Redeliste to be analysed.
     * @throws IOException
     * @throws UIMAException
     * @throws SAXException
     */
    public static void AnalyseAllTexts(HashMap<String, Rede> redeliste) throws IOException, UIMAException, SAXException {
        int count = 0;
        int amountofreden = redeliste.size();
        CASTextSerializer analyser = new CASTextSerializer();

        for (String i : redeliste.keySet()) {
            StringBuilder fulltext = new StringBuilder();
            ArrayList<String> paragraphs = redeliste.get(i).getparagraphs();
            for (String j : paragraphs) {
                fulltext.append(j);
            }
            String str = fulltext.toString();
            analyser.analyseText(str, i);
            count++;
            System.out.println(count + ":" + amountofreden);
        }
    }

    /** Uploads CAS to a given MongoDB
     * @param filepath Files to be uploaded in a filepath
     * @param mongoDBConnectionHandler The connectionhandler to a database
     */
    public static void UploadCASXMLToMongoDB(String filepath, MongoDBConnectionHandler mongoDBConnectionHandler) {
        List<Document> listofdocuments = new ArrayList<>();
        File folder = new File(filepath);
        File[] fileslist = folder.listFiles();

        for (File file : fileslist) {
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                if (file.isFile()) {
                    String redeid = FilenameUtils.getBaseName(file.getName());

                    StringBuilder sb = new StringBuilder();
                    String line = br.readLine();
                    while (line != null) {
                        sb.append(line);
                        sb.append(System.lineSeparator());
                        line = br.readLine();
                    }
                    String everything = sb.toString();


                    Document doc = new Document()
                            .append("_id", redeid)
                            .append("xmlstring", everything);

                    listofdocuments.add(doc);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        mongoDBConnectionHandler.addManyDocumentsToCollection(listofdocuments, "CAS", false);
    }
}
