package NLP;

import Data.Rede;
import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.metadata.AnalysisEngineMetaData;
import org.apache.uima.cas.impl.XCASSerializer;
import org.apache.uima.cas.impl.XmiCasSerializer;
import org.apache.uima.fit.factory.AggregateBuilder;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.hucompute.textimager.fasttext.labelannotator.LabelAnnotatorDocker;
import org.hucompute.textimager.uima.gervader.GerVaderSentiment;
import org.hucompute.textimager.uima.spacy.SpaCyMultiTagger3;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;

public class CASReadText {
    private AnalysisEngine pAE;
    private JCas jCas;
    private XmiCasSerializer jcs;
    private XCASSerializer jbcds;

    //Constructor
    public CASReadText() {
        //  creating a Pipeline
        AggregateBuilder pipeline = new AggregateBuilder();

        // add different Engines to the Pipeline
        try {
            pipeline.add(createEngineDescription(SpaCyMultiTagger3.class,
                    SpaCyMultiTagger3.PARAM_REST_ENDPOINT, "http://spacy.prg2021.texttechnologylab.org"

            ));

            String sPOSMapFile = "src/main/resources/am_posmap.txt";

            pipeline.add(createEngineDescription(LabelAnnotatorDocker.class,
                    LabelAnnotatorDocker.PARAM_FASTTEXT_K, 100,
                    LabelAnnotatorDocker.PARAM_CUTOFF, false,
                    LabelAnnotatorDocker.PARAM_SELECTION, "text",
                    LabelAnnotatorDocker.PARAM_TAGS, "ddc3",
                    LabelAnnotatorDocker.PARAM_USE_LEMMA, true,
                    LabelAnnotatorDocker.PARAM_ADD_POS, true,
                    LabelAnnotatorDocker.PARAM_POSMAP_LOCATION, sPOSMapFile,
                    LabelAnnotatorDocker.PARAM_REMOVE_FUNCTIONWORDS, true,
                    LabelAnnotatorDocker.PARAM_REMOVE_PUNCT, true,
                    LabelAnnotatorDocker.PARAM_REST_ENDPOINT, "http://ddc.prg2021.texttechnologylab.org"
            ));

            pipeline.add(createEngineDescription(GerVaderSentiment.class,
                    GerVaderSentiment.PARAM_REST_ENDPOINT, "http://gervader.prg2021.texttechnologylab.org",
                    GerVaderSentiment.PARAM_SELECTION, "text,de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence"
            ));

            // create an AnalysisEngine for running the Pipeline.
            pAE = pipeline.createAggregate();
            jcs = new XmiCasSerializer(null);
        } catch (ResourceInitializationException e) {
            e.printStackTrace();
        }

    }

    /**
     * This method will analyse a given text and make a XML document out of it, named after its id
     * @author Jannis CUi
     * @param fulltext The text to be analysed
     * @param redeid   The files name
     * @throws UIMAException Not described yet
     * @throws IOException   Not described yet
     */
    public void analyseText(String fulltext, String redeid) throws UIMAException, IOException, SAXException {
        //Create a file to store CAS serialized as XML
        File file = new File("/home/muzisama1/Bundestag90CAS/" + redeid + ".xml");
        if (!file.exists()) {
            // Convert Text into CAS
            jCas = JCasFactory.createText(fulltext, "de");

            //Run through Pipeline
            SimplePipeline.runPipeline(jCas, pAE);
            System.out.println("Successfully analyzed text.");

            file.createNewFile();
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            XmiCasSerializer.serialize(jCas.getCas(), fileOutputStream);
        }
    }

    /**
     * @return Engine Meta data
     */
    public AnalysisEngineMetaData getAnalysisMetaData() {
        return pAE.getAnalysisEngineMetaData();
    }
}
