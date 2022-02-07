package NLP;

import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.metadata.AnalysisEngineMetaData;
import org.apache.uima.cas.CAS;
import org.apache.uima.cas.impl.XmiCasDeserializer;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.util.CasCreationUtils;

import java.io.File;
import java.io.FileInputStream;

public class CASDeserializer {
    JCas jCas;
    CASTextSerializer AE;
    AnalysisEngineMetaData metaData;
    CAS cas;

    //Constructor
    public CASDeserializer() throws ResourceInitializationException {
        //Initialize AnalysisEngineMetaData, which is needed to get TypeSystem for CAS
        AE = new CASTextSerializer();
        metaData = AE.getAnalysisMetaData();
    }

    //Methods
    public JCas getjCasFromXML(File file) throws UIMAException {
        try {
            cas = CasCreationUtils.createCas(metaData);
            FileInputStream is = new FileInputStream(file);
            XmiCasDeserializer.deserialize(is, cas);
            jCas = cas.getJCas();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jCas;
    }
}
