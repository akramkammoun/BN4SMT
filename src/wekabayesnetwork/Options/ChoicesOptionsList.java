/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package wekabayesnetwork.Options;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;

/**
 *
 * @author akram
 */
@XmlRootElement(name = "ChoicesOptionsList")
@XmlSeeAlso(
{
    ChoicesOptions.class
})
public class ChoicesOptionsList
{

    public String id;
    public boolean textCleaner;
    public double conceptCountMoyen;
    public int wordsPercent;
    public int parentBNCount;
    public boolean binarytrg;//
    public boolean bn4Layers;//BN as 4 layers//
    public boolean compareDistancesSrcTrg;
    public ArrayList<ChoicesOptions> choicesOptions;

    public ArrayList<ChoicesOptions> getChoicesOptions()
    {
        return this.choicesOptions;
    }

    public void encodeToFile(String filePath)
    {

        try
        {
            // Create output streams.
            FileOutputStream fos = new FileOutputStream(filePath,
                                                        false);

            JAXBContext context = JAXBContext.newInstance(
                    ChoicesOptionsList.class);

            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT,
                                   true);

            marshaller.marshal(this,
                               new OutputStreamWriter(fos));

        }
        catch (IOException ex)
        {
            Logger.getLogger(ChoicesOptions.class.getName()).log(Level.SEVERE,
                                                                 null,
                                                                 ex);
        }
        catch (JAXBException ex)
        {
            Logger.getLogger(ChoicesOptions.class.getName()).log(Level.SEVERE,
                                                                 null,
                                                                 ex);
        }
    }
}
