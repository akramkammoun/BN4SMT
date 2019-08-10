/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package wekabayesnetwork.Options;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;

/**
 *
 * @author akram
 */
@XmlRootElement(name = "testingOptionsList")
@XmlSeeAlso(
{
    TestingOptions.class
})
public class TestingOptionsList
{

    public String projectDir;
    public File textFileSrc;
    public File textFileTrg;
    public String[] wordsFilePath = new String[2];//src, trg
    public String testingOptionsFilePath;
    public double alpha;//parameter of translatingLine
    public int bnClassIndexInit;
    public boolean deleteArcsNoeudClass;
    public boolean setEvidence[];//true false
    public boolean findInAllwords[];//true false
    public boolean useMoses;
    public String ibmModelCount;
    public boolean useBN;
    public ArrayList<String> cmdsOptionsName;//.normal, deletearcs, addarcs
    public ArrayList<TestingOptions> testingOptions;
    public MosesOptions mosesOption;
    public boolean setEvidenceZero;
    public boolean deleteUnknowenWords;
    public boolean inferenceAllWords;

    public ArrayList<TestingOptions> getTestingOptions()
    {
        return this.testingOptions;
    }

    public void encodeToFile(String filePath)
    {

        try
        {
            // Create output streams.
            FileOutputStream fos = new FileOutputStream(filePath,
                                                        true);

            JAXBContext context = JAXBContext.newInstance(
                    TestingOptionsList.class);

            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT,
                                   true);

            marshaller.marshal(this,
                               new OutputStreamWriter(fos));

        }
        catch (IOException ex)
        {
            Logger.getLogger(TestingOptions.class.getName()).log(Level.SEVERE,
                                                                 null,
                                                                 ex);
        }
        catch (JAXBException ex)
        {
            Logger.getLogger(TestingOptions.class.getName()).log(Level.SEVERE,
                                                                 null,
                                                                 ex);
        }
    }

    public static TestingOptionsList decodeFormFile(String filePath)
    {
        TestingOptionsList tol = null;

        try
        {
            // Unmarshalles the given XML file to objects
            JAXBContext context;
            context = JAXBContext.newInstance(TestingOptionsList.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            tol = (TestingOptionsList) unmarshaller.unmarshal(new FileReader(
                    filePath));
        }
        catch (Exception ex)
        {
            Logger.getLogger(TestingOptions.class.getName()).log(Level.SEVERE,
                                                                 null,
                                                                 ex);
        }

        return tol;
    }
}
