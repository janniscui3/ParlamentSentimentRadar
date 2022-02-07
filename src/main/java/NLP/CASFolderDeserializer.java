package NLP;

import de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.pos.POS;
import de.tudarmstadt.ukp.dkpro.core.api.ner.type.NamedEntity;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import org.apache.commons.io.FilenameUtils;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.hucompute.textimager.uima.type.Sentiment;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class CASFolderDeserializer {
    ArrayList<Sentence> sentencelist;
    ArrayList<POS> poslist;
    ArrayList<Token> tokenlist;
    ArrayList<NamedEntity> namedEntitieslist;

    public CASFolderDeserializer() {
        sentencelist = new ArrayList<>();
        poslist = new ArrayList<>();
        tokenlist = new ArrayList<>();
        namedEntitieslist = new ArrayList<>();
    }

    /**
     * Will read all XMLs from a file and get their Data and save it in the resources folder.
     *
     * @param filepath Filepath to read Data from
     * @throws ResourceInitializationException a
     * @throws IOException                     a
     */
    public void initialize(String filepath) throws ResourceInitializationException, IOException {
        File folder = new File(filepath);
        File[] fileslist = folder.listFiles();
        CASDeserializer readSingleXML = new CASDeserializer();
        File sentiments = new File("src/main/resources/Sentiments.txt");
        File tokens = new File("src/main/resources/Tokens.txt");
        File pos = new File("src/main/resources/POS.txt");
        File namedentities = new File("src/main/resources/namedentities.txt");

        sentiments.createNewFile();
        tokens.createNewFile();
        pos.createNewFile();
        namedentities.createNewFile();

        for (File file : fileslist) {
            try {
                if (file.isFile() && !(file.getName().equals("dbtplenarprotokoll.dtd"))) {
                    System.out.println("Parsing file: " + file.getName());
                    JCas jCas = readSingleXML.getjCasFromXML(file);

                    //Create content of Sentiments.txt
                    String redeid = FilenameUtils.getBaseName(file.getName());
                    FileWriter fr = new FileWriter(sentiments, true);
                    BufferedWriter br = new BufferedWriter(fr);
                    StringBuilder sb = new StringBuilder();
                    sb.append(redeid).append(":");
                    for (Sentence sentence : JCasUtil.select(jCas, Sentence.class)) {
                        for (Sentiment sentiment : JCasUtil.selectCovered(Sentiment.class, sentence)) {
                            sb.append(sentiment.getSentiment()).append(" ");
                        }
                    }
                    sb.append("\n");
                    br.write(sb.toString());
                    br.close();
                    fr.close();

                    //Create Content of Tokens.txt
                    fr = new FileWriter(tokens, true);
                    br = new BufferedWriter(fr);
                    for (Token token : JCasUtil.select(jCas, Token.class)) {
                        br.write(token.getLemmaValue() + "\n");
                    }
                    br.close();
                    fr.close();

                    //Create Content of POS.txt
                    fr = new FileWriter(pos, true);
                    br = new BufferedWriter(fr);
                    for (POS pos1 : JCasUtil.select(jCas, POS.class)) {
                        StringBuilder sb1 = new StringBuilder();
                        sb1.append(pos1.getPosValue()).append("\n");
                        br.write(sb1.toString());
                    }
                    br.close();
                    fr.close();

                    //Create Content of namedentities.txt
                    fr = new FileWriter(namedentities, true);
                    br = new BufferedWriter(fr);
                    for (NamedEntity namedEntity : JCasUtil.select(jCas, NamedEntity.class)) {
                        StringBuilder sb1 = new StringBuilder();
                        sb1.append(namedEntity.getValue()).append(":").append(namedEntity.getCoveredText()).append("\n");
                        br.write(sb1.toString());
                    }
                    br.close();
                    fr.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public ArrayList<Sentence> getSentencelist() {
        return sentencelist;
    }

    public ArrayList<POS> getPoslist() {
        return poslist;
    }

    public ArrayList<Token> getTokenlist() {
        return tokenlist;
    }

    public ArrayList<NamedEntity> getNamedEntitieslist() {
        return namedEntitieslist;
    }
}
