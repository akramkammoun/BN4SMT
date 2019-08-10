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
@XmlRootElement(name = "trainingOptionsList")
@XmlSeeAlso(
{
    TrainingOptions.class
})
public class TrainingOptionsList
{

    public ArrayList<TrainingOptions> trainingOptions;

    public ArrayList<TrainingOptions> getTrainingOptions()
    {
        return this.trainingOptions;
    }

    public void encodeToFile(String filePath)
    {

        try
        {
            // Create output streams.
            FileOutputStream fos = new FileOutputStream(filePath,
                                                        true);

            JAXBContext context = JAXBContext.newInstance(
                    TrainingOptionsList.class);

            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT,
                                   true);

            marshaller.marshal(this,
                               new OutputStreamWriter(fos));

        }
        catch (IOException ex)
        {
            Logger.getLogger(TrainingOptions.class.getName()).log(Level.SEVERE,
                                                                  null,
                                                                  ex);
        }
        catch (JAXBException ex)
        {
            Logger.getLogger(TrainingOptions.class.getName()).log(Level.SEVERE,
                                                                  null,
                                                                  ex);
        }
    }
}
