/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package wekabayesnetwork.Options;

/**
 *
 * @author akram
 */
public class MosesOptions
{

    public String projectRoot;
    public String mosesFile;

    public MosesOptions()
    {
    }

    public MosesOptions(String projectRoot,
                        String mosesFile)
    {
        this.projectRoot = projectRoot;
        this.mosesFile = mosesFile;
    }
}
