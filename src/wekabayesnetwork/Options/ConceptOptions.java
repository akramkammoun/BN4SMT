/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package wekabayesnetwork.Options;

import java.io.File;

/**
 *
 * @author akram
 */
public class ConceptOptions
{

    public String distanceName;
    public File conceptFile;
    public int conceptCount;
    public String lmFilePath;

    private ConceptOptions()
    {
    }

    public ConceptOptions(String conceptFilePath,
                          String distanceName,
                          int conceptCount,
                          String lmFilePath)
    {
        this.distanceName = distanceName;
        this.conceptFile = new File(conceptFilePath);
        this.conceptCount = conceptCount;
        this.lmFilePath = lmFilePath;
    }
}
