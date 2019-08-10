/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package wekabayesnetwork.Options;

import java.io.File;

import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author akram
 */
@XmlRootElement(name = "testingOptions")
public class TestingOptions
{

    public String bayesCmd;
    public String bayesCmdName;
    public int parentCount;
    public String distanceName1;//corpus1 distance
    public String distanceName2;//corpus2 distance
    public String cmdOption;
    public File bayesnetFile;
    public File bayesnetConfFile;
    public File lmFile;
    public File conceptSrcFile;
    public File conceptTrgFile;
    public boolean binaryTrg;
    public File bayesMarginFile;
    public int bnClassIndexFinal;

    public TestingOptions()
    {
    }

    public TestingOptions(String bayesCmd,
                          String bayesCmdName,
                          int parentCount,
                          String distanceName1,
                          String distanceName2,
                          String cmdOption,
                          String bayesnetPath,
                          String bayesnetConfPath,
                          File lmFile,
                          File conceptSrcFile,
                          File conceptTrgFile,
                          File bayesMarginFile,
                          int bnClassIndexFinal,
                          boolean binaryTrg)
    {
        this.bayesCmd = bayesCmd;
        this.bayesCmdName = bayesCmdName;
        this.distanceName1 = distanceName1;
        this.distanceName2 = distanceName2;
        this.cmdOption = cmdOption;
        this.parentCount = parentCount;

        this.bayesnetFile = new File(bayesnetPath);
        this.bayesnetConfFile = new File(bayesnetConfPath);

        this.lmFile = lmFile;

        this.conceptSrcFile = conceptSrcFile;
        this.conceptTrgFile = conceptTrgFile;

        this.bayesMarginFile = bayesMarginFile;

        this.bnClassIndexFinal = bnClassIndexFinal;

        this.binaryTrg = binaryTrg;
    }
}
