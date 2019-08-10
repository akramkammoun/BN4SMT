/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package wekabayesnetwork;

import wekabayesnetwork.MyWeka.MyBIFReader;
import wekabayesnetwork.MyWeka.MyEditableBayesNet;
import java.io.FileNotFoundException;
import java.util.logging.Level;
import java.util.logging.Logger;
import weka.core.Utils;
import weka.core.Instances;
import java.io.File;
import weka.gui.graphvisualizer.GraphVisualizer;
import java.awt.BorderLayout;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import javax.swing.JFrame;
import weka.classifiers.bayes.net.BIFReader;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.SparseInstance;
import weka.core.converters.ArffLoader;
import weka.core.converters.ConverterUtils.DataSink;

/**
 *
 * @author akram
 */
public class WekaBayesNet
{

    public int classIndex;
    public MyEditableBayesNet editBayesNet;
    public Instances bayesNetDataSet;
    public static String idConcept = "c";
    public String cmdName;
    public String cmdText;
    public int parentsCount;
    public ArrayList<Corpus> corpusList;

//    public double[][] bayesMargin;
    public WekaBayesNet()
    {
    }

    public WekaBayesNet(String cmdText,
                        String cmdName,
                        int parentsCount)
    {
        this.cmdText = cmdText;
        this.cmdName = cmdName;
        this.parentsCount = parentsCount;
    }

    public void loadBayesNet(String bayesNetXMLPath)
    {
        try
        {
            MyBIFReader bifReader = new MyBIFReader();
            bifReader.processFile(bayesNetXMLPath);

            this.editBayesNet = new MyEditableBayesNet(bifReader);
        }
        catch (Exception ex)
        {
            Logger.getLogger(TranslatingLine.class.getName()).log(Level.SEVERE,
                                                                  null,
                                                                  ex);
        }

    }

    public void loadDataArff(String bayesNetArffFilePath)
    {

        File bayesNetArffFile = new File(bayesNetArffFilePath);

        ArffLoader dataArffLoader = new ArffLoader();

        try
        {
            dataArffLoader.setFile(bayesNetArffFile);

            this.bayesNetDataSet = dataArffLoader.getDataSet();
        }
        catch (IOException ex)
        {
            Logger.getLogger(WekaBayesNet.class.getName()).log(Level.SEVERE,
                                                               null,
                                                               ex);
        }
    }

    public void buildDataSetWeka(ArrayList<Corpus> corpusList,
                                 boolean binaryTrg)
    {
        this.corpusList = corpusList;
        FastVector attributes = new FastVector();
        StringBuffer relationSB = new StringBuffer("");

        ////to build @data
        short corpusReadedCount = 0;

        short corpusCount = (short) corpusList.size();

        try
        {
            //******** begin build @relation ligne **********/
            //
            for (short corpusNum = 0; corpusNum < corpusCount; corpusNum++)
            {
                String lineString = "corpus:" + corpusList.get(corpusNum).getLang()
                                    + "_" + corpusList.get(corpusNum).getCorpusLinesCount() + "_"
                                    + corpusList.get(corpusNum).getConceptLinesCount() + "|";

                relationSB = relationSB.append(lineString);
            }
            //
            relationSB.deleteCharAt(relationSB.length() - 1);
            //
            //******** end build @relation ligne **********/




            //******** begin build @attribute linges **********//
            //
            for (short corpusNum = 0; corpusNum < corpusCount; corpusNum++)
            {
                corpusList.get(corpusNum).openConceptFile();

                for (int conceptNum = 1; conceptNum <= corpusList.get(corpusNum).getConceptLinesCount();
                     conceptNum++)
                {

                    String[] attributeElementsArray = corpusList.get(corpusNum).
                            getConceptBR().readLine().split(corpusList.get(
                            corpusNum).conceptDelimiter);

//                    String attributeName = WekaBayesNet.idConcept + conceptNum
//                                           + corpusList.get(corpusNum).distanceName
//                                           + corpusList.get(corpusNum).getLang();
                    String attributeName;
                    
                    if("word".equals(corpusList.get(corpusNum).distanceName))
                    {
                        //each concept has a single word
                        //attributeName <- word
                        attributeName = attributeElementsArray[1];
                    }
                    else
                    {
                        attributeName = attributeElementsArray[0];
                    }

//                    System.out.println(attributeElementsArray.length);

                    FastVector labels = new FastVector();
                    labels.addElement("0");

                    if (binaryTrg == true && corpusNum == (corpusCount - 1))
                    {
                        labels.addElement(attributeName);
                    }
                    else
                    {
                        for (int i = 1; i < attributeElementsArray.length; i++)
                        {
                            labels.addElement(attributeElementsArray[i]);
                        }
                    }



                    attributes.addElement(new Attribute(attributeName,
                                                        labels));

                }

                corpusList.get(corpusNum).closeConceptFile();
            }
            this.bayesNetDataSet = new Instances(relationSB.toString(),
                                                 attributes,
                                                 0);
            relationSB = null;
            attributes = null;
            //
            //********* end build @attribute linges **********//


            //******* Open dataArffWekaFile on write mode and corpus and concepts files on read mode ***********//
            //
            for (short corpusNum = 0; corpusNum < corpusCount; corpusNum++)
            {
                corpusList.get(corpusNum).openCorpusFile();
            }
            //
            //*****end Open dataArffWekaFile on write mode and corpus and concepts files on read mode ***********//




            //******* begin build @data *********//
            //
            while (corpusReadedCount != corpusCount)
            {
                corpusReadedCount = 0;
                int conceptIndex = 0;
                double[] dataSetValues = new double[this.bayesNetDataSet.numAttributes()];
                //initialized with zeros.

                for (short corpusNum = 0; corpusNum < corpusCount; corpusNum++)
                {
                    if (corpusNum != 0)
                    {
                        conceptIndex += corpusList.get(corpusNum - 1).getConceptLinesCount();
                    }

                    String lineString = corpusList.get(corpusNum).getCorpusBR().readLine();



                    //if there is still a line in the corpus
                    if (lineString != null)
                    {
                        //split each line into words array
                        String[] lineStringArray = lineString.split(corpusList.
                                get(corpusNum).getCorpusDelimiter());
                        //
                        for (int wordNum = 0; wordNum < lineStringArray.length; wordNum++)
                        {

                            boolean wordFound = false;

                            for (int conceptNum = 0; conceptNum < corpusList.
                                    get(corpusNum).conceptLists.size() && wordFound == false;
                                 conceptNum++)
                            {
                                int indexConceptFound = corpusList.get(corpusNum).conceptLists.get(
                                        conceptNum).indexOf(
                                        lineStringArray[wordNum]);


                                if (indexConceptFound != -1)//founded
                                {
//                                    System.out.println("a");
                                    wordFound = true;

                                    if (binaryTrg == true && corpusNum == (corpusCount - 1))
                                    {
                                        dataSetValues[conceptNum + conceptIndex] = 1;//1 for the 0 added
                                    }
                                    else
                                    {
                                        dataSetValues[conceptNum + conceptIndex] = (double) indexConceptFound + 1;//1 for the concept added
                                    }
                                }
                                else
                                {
//                                    System.out.println(corpusList.get(corpusNum).conceptLists.get(conceptNum));
//                                    System.exit(-1);
                                }
                            }
                        }
                    } //if there isn't still a line in the corpus then build a line of 0 (0, 0, ..., 0);concepts Count
                    else
                    {
                        corpusReadedCount++;
                    }
                }

                this.bayesNetDataSet.add(new SparseInstance(1.0,
                                                            dataSetValues));

            }
            //
            //******* build @data *********//

            //delete the las instance cause it's null : {}
            this.bayesNetDataSet.delete(this.bayesNetDataSet.numInstances() - 1);


            //*********** close all files **************//
            //
            for (short corpusNum = 0; corpusNum < corpusCount; corpusNum++)
            {
                corpusList.get(corpusNum).closeCorpusFile();
            }
            //
            //*********** close all files **************//

        }
        catch (IOException ex)
        {
            Logger.getLogger(WekaBayesNet.class.getName()).log(Level.SEVERE,
                                                               null,
                                                               ex);
        }
        catch (Exception ex)
        {
            Logger.getLogger(WekaBayesNet.class.getName()).log(Level.SEVERE,
                                                               null,
                                                               ex);
        }
    }

    public void execBayesNet(String cmd,
                             String cmdName,
                             int parentsCount,
                             int classIndex)
    {
        try
        {
            this.cmdText = cmd;
            this.cmdName = cmdName;
            this.parentsCount = parentsCount;
            this.classIndex = classIndex;
            String[] optionsArray = Utils.splitOptions(cmd);
            //********* bash *************//
            //


            // uses the last attribute as class attribute
            if ((this.classIndex < 0) || (classIndex > this.bayesNetDataSet.numAttributes() - 1))
            {
                this.bayesNetDataSet.setClassIndex(
                        this.bayesNetDataSet.numAttributes() - 1);
                this.classIndex = this.bayesNetDataSet.numAttributes() - 1;
            }
            else
            {
                this.bayesNetDataSet.setClassIndex(this.classIndex);
            }
            //
            this.editBayesNet = new MyEditableBayesNet(this.bayesNetDataSet);
            this.editBayesNet.setOptions(optionsArray);
            this.editBayesNet.buildClassifier(this.bayesNetDataSet);
//            this.editBayesNet.setBIFFile(this.editBayesNet.getBIFFile() + ":" + options);

//            this.editBayesNet.estimateCPTs();
//            this.addArcsParentsToChilds(0,26,27,52);
            //
            //********* bash *************//




            //*********incremantal **********//
            //
//            Instances structure = loader.getStructure();
//            // uses the last attribute as class attribute
//            if (structure.classIndex() == -1)
//                structure.setClassIndex(structure.numAttributes() - 1);
//            // train NaiveBayes
//            BayesNet bayesNet = new BayesNet();
//            bayesNet.buildClassifier(structure);
//            Instance current;
//            while ((current = loader.getNextInstance(structure)) != null)
//                bayesNet.updateClassifier(current);
            //
            //*********incremantal **********//



            //********** eval ***********//
            //
//            Evaluation eval = new Evaluation(data);
//            eval.crossValidateModel(bayesNet, data, 10, new Random(1));
//            System.out.println(eval.toSummaryString("\nResults\n\n", false));
            //
            //********** eval ***********//

        }
        catch (Exception ex)
        {
            Logger.getLogger(WekaBayesNet.class.getName()).log(Level.SEVERE,
                                                               null,
                                                               ex);
        }
    }

    public void refreshCPTs()
    {
        try
        {
            this.editBayesNet.estimateCPTs();
        }
        catch (Exception ex)
        {
            Logger.getLogger(WekaBayesNet.class.getName()).log(Level.SEVERE,
                                                               null,
                                                               ex);
        }
    }

    public void deleteNodeChildArcs(int nodeParentId)
    {

        for (int nodeChildId = 0; nodeChildId < this.editBayesNet.getNrOfNodes(); nodeChildId++)
        {
            try
            {
                this.editBayesNet.deleteArc(nodeParentId,
                                            nodeChildId);
            }
            catch (Exception ex)
            {
                Logger.getLogger(WekaBayesNet.class.getName()).log(Level.SEVERE,
                                                                   null,
                                                                   ex);
            }
        }

        //to recalculate CPTs after deleting some arcs

    }

    public void deleteConceptParentArcs(int firstIndexChild,
                                        int lastIndexChild)
    {
        //firstIndex and lastIndex of concepts source
        int nodeCount = this.editBayesNet.getNrOfNodes();//count all nodes

        //nodeChildNum = node of given concept firstIndex < nodeChildNum < lastIndex
        //nodeChildNum = nodeParent (all nodes)

        for (int nodeChildNum = firstIndexChild; nodeChildNum <= lastIndexChild; nodeChildNum++)
        {
            for (int nodeParentNum = 0; nodeParentNum < nodeCount; nodeParentNum++)
            {
                try
                {
                    this.editBayesNet.deleteArc(nodeParentNum,
                                                nodeChildNum);

                }
                catch (Exception ex)
                {
                    //nothing to do
                }
            }
        }

        //recalculate CPTs after deleting some arcs


    }

    public void addArcsParentsToChilds(int firstIndexParent,
                                       int lastIndexParent,
                                       int firstIndexChild,
                                       int lastIndexChild)
    {
        for (int parentNum = firstIndexParent; parentNum <= lastIndexParent; parentNum++)
        {

            for (int childNum = firstIndexChild; childNum <= lastIndexChild; childNum++)
            {
                try
                {
                    this.editBayesNet.addArc(parentNum,
                                             childNum);
                }
                catch (Exception ex)
                {
                    //nothing to do
                }
            }
        }


    }

//    public void deleteConceptParentArcs(String idLangCorpus, int conceptCount)
//    {
//        //it delete all parents of given concepts
//
//        int nodeCount = this.editBayesNet.getNrOfNodes();//count all nodes
//        int nodeChildId;
//
//        for(int conceptNum = 1; conceptNum <= conceptCount; conceptNum++)
//        {
//            try
//            {
//                //exp: "C" + conceptNum + idConcept = C1ENG2
//                nodeChildId = this.editBayesNet.getNode(this.idConcept + conceptNum + idLangCorpus);
//            }
//            catch (Exception ex)
//            {
//                nodeChildId = -1;//event impossible, just to escape compilation error
//                Logger.getLogger(WekaBayesNet.class.getName()).log(Level.SEVERE, null, ex);
//            }
//
//            for(int nodeNum = 0; nodeNum < nodeCount; nodeNum++)
//            {
//                try
//                {
//                    //nodeNum : parent
//                    this.editBayesNet.deleteArc(nodeNum, nodeChildId);
//
//                }
//                catch (Exception ex)
//                {
//                    //nothing to do
//                }
//            }
//        }
//
//        //to recalculate CPTs after deleting some arcs
//        try
//        {
//            this.editBayesNet.estimateCPTs();
//        }
//        catch (Exception ex)
//        {
//            Logger.getLogger(WekaBayesNet.class.getName()).log(Level.SEVERE, null, ex);
//        }
//    }
    public void bayesNetReorganization(int[] conceptsLigneCount)
    {
        //each value of conceptsLigneCount contains
        int xPos;
        int yPos = 1;
        int nodeNum = 0;

        for (short conceptNum = 0; conceptNum < conceptsLigneCount.length; conceptNum++)
        {
            xPos = 1;
            for (int conceptLineNum = 0; conceptLineNum < conceptsLigneCount[conceptNum]; conceptLineNum++)
            {
                this.editBayesNet.setPosition(nodeNum,
                                              xPos,
                                              yPos);
                nodeNum++;
                xPos += 120;
                if (conceptLineNum < (int) (conceptsLigneCount[conceptNum] / 2))
                {
                    yPos += 10;
                }
                else
                {
                    yPos -= 10;
                }
            }
            yPos += 300;
        }
    }

    public void showBayesNet()
    {
        try
        {
            // display graph
            GraphVisualizer gv = new GraphVisualizer();
            gv.readBIF(this.editBayesNet.graph());
            JFrame jf = new JFrame("BayesNet graph");
            jf.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            jf.setSize(800,
                       600);
            jf.getContentPane().setLayout(new BorderLayout());
            jf.getContentPane().add(gv,
                                    BorderLayout.CENTER);
            jf.setVisible(true);
            // layout graph
//            gv.layoutGraph();
        }
        catch (Exception ex)
        {
            Logger.getLogger(WekaBayesNet.class.getName()).log(Level.SEVERE,
                                                               null,
                                                               ex);
        }

    }

    public void exportBayesNetXml(String fileOutPath)
    {

        File fileOut = new File(fileOutPath);
        OutputStreamWriter os;
        BufferedWriter bw;


        fileOut.delete();

        try
        {

            os = new OutputStreamWriter(new FileOutputStream(fileOut),
                                        "UTF-8");
            bw = new BufferedWriter(os);

            bw.write(this.editBayesNet.graph());

            bw.close();
            os.close();
        }
        catch (FileNotFoundException ex)
        {
            Logger.getLogger(WekaBayesNet.class.getName()).log(Level.SEVERE,
                                                               null,
                                                               ex);
        }
        catch (Exception ex)
        {
            Logger.getLogger(WekaBayesNet.class.getName()).log(Level.SEVERE,
                                                               null,
                                                               ex);
        }
    }

    public void exportDataSetArff(String fileOutPath)
    {
        File fileOut = new File(fileOutPath);

        try
        {
            // save as ARFF
            DataSink.write(fileOut.getAbsolutePath(),
                           this.bayesNetDataSet);
        }
        catch (Exception ex)
        {
            Logger.getLogger(WekaBayesNet.class.getName()).log(Level.SEVERE,
                                                               null,
                                                               ex);
        }
    }

    public static void exportConfBayesNet(String fileOutPath,
                                          int countSourceLang,
                                          int firstIndexSourceLang,
                                          int lastIndexSourceLang,
                                          int countTargetLang,
                                          int firstIndexTargetLang,
                                          int lastIndexTargetLang)
    {
        File fileOut = new File(fileOutPath);

        String confXMLContents =
               "<?xml version=\"1.0\"?>\n"
               + "<!-- DTD for the XMLBIF 0.3 format -->\n"
               + "<!DOCTYPE BIF [\n"
               + "\t<!ELEMENT BIF (CONF)>\n"
               + "\t\t<!ATTLIST BIF VERSION CDATA #REQUIRED>\n"
               + "\t<!ELEMENT MYOPTIONS ( SRCLANG, TRGLANG)>\n"
               + "\t<!ELEMENT SRCLANG ( NODESCOUNT, FIRSTINDEX, LASTINDEX)>\n"
               + "\t<!ELEMENT NODESCOUNT (#PCDATA)>\n"
               + "\t<!ELEMENT FIRSTINDEX (#PCDATA)>\n"
               + "\t<!ELEMENT LASTINDEX (#PCDATA)>\n"
               + "\t<!ELEMENT TRGETLANG ( NODESCOUNT, FIRSTINDEX, LASTINDEX)>\n"
               + "\t<!ELEMENT NODESCOUNT (#PCDATA)>\n"
               + "\t<!ELEMENT FIRSTINDEX (#PCDATA)>\n"
               + "\t<!ELEMENT LASTINDEX (#PCDATA)>\n"
               + "]>\n\n"
               + "<BIF VERSION=\"0.3\">\n"
               + "<CONF>\n"
               + "<SRCLANG>\n"
               + "<NODESCOUNT>" + countSourceLang + "</NODESCOUNT>\n"
               + "<FIRSTINDEX>" + firstIndexSourceLang + "</FIRSTINDEX>\n"
               + "<LASTINDEX>" + lastIndexSourceLang + "</LASTINDEX>\n"
               + "</SRCLANG>\n"
               + "<TRGLANG>\n"
               + "<NODESCOUNT>" + countTargetLang + "</NODESCOUNT>\n"
               + "<FIRSTINDEX>" + firstIndexTargetLang + "</FIRSTINDEX>\n"
               + "<LASTINDEX>" + lastIndexTargetLang + "</LASTINDEX>\n"
               + "</TRGLANG>\n"
               + "</CONF>\n"
               + "</BIF>\n";

        OutputStreamWriter os;
        BufferedWriter bw;


        fileOut.delete();

        try
        {
            os = new OutputStreamWriter(new FileOutputStream(fileOut),
                                        "UTF-8");
            bw = new BufferedWriter(os);

            bw.write(confXMLContents);

            bw.close();
            os.close();
        }
        catch (FileNotFoundException ex)
        {
            Logger.getLogger(WekaBayesNet.class.getName()).log(Level.SEVERE,
                                                               null,
                                                               ex);
        }
        catch (Exception ex)
        {
            Logger.getLogger(WekaBayesNet.class.getName()).log(Level.SEVERE,
                                                               null,
                                                               ex);
        }
    }

//    public void calculBayesMargin()
//    {
//        //calul marginal probabilities
//        MarginCalculator marginCalculator = new MarginCalculator();
//        try
//        {
//            marginCalculator.calcMargins(this.editBayesNet);
//        }
//        catch (Exception ex)
//        {
//            Logger.getLogger(TranslatingLine.class.getName()).log(Level.SEVERE, null, ex);
//        }
//
//        this.bayesMargin = new double[this.editBayesNet.getNrOfNodes()][];
//        for(int i = 0; i < this.editBayesNet.getNrOfNodes(); i++)
//        {
//            this.bayesMargin[i] = new double[this.editBayesNet.getParentCardinality(i)];
//            this.bayesMargin[i] = marginCalculator.getMargin(i);
//
//        }
//    }
    //******* get ********//
    //
    public MyEditableBayesNet getEditBayesNet()
    {
        return this.editBayesNet;
    }
    //

    public int getClassIndex()
    {
        return this.classIndex;
    }
    //

    public Instances getBayesNetDataSet()
    {
        return this.bayesNetDataSet;
    }
    //
    //******* get ********//

//    private int searchStringIntoConcept(String myString, BufferedReader fileBR)
//    {
//        String lineString;
//        int posStr = 1;
//
//        //on add the comma ',' for do not cherche a word that our myString may contains it
//        //exp: myString = go, if on don't add the ',' the program consider the word 'goes'
//        //is 'go'
//
//        myString = "," + myString + ",";
//
//        try
//        {
//            while ((lineString = fileBR.readLine()) != null)
//            {
//                lineString = "," + lineString + ",";
//                if(lineString.contains(myString) == true)
//                    return posStr;
//
//                posStr++;
//            }
//
//        }
//        catch (IOException ex)
//        {
//            Logger.getLogger(WekaBayesNet.class.getName()).log(Level.SEVERE, null, ex);
//        }
//
//        //string not founded into the file
//        return -1;
//    }
    public static ArrayList<WekaBayesNet> initBayesCmds(int parentsCount)
    {
        ArrayList<WekaBayesNet> result = new ArrayList<WekaBayesNet>();

        //weka.classifiers.bayes.net.search.local.K2
        //wekabayesnetwork.MyWeka.MyK2

        //local

        //blanket markov
        //-P 1 -S false
        //-P 1 -mbc -S true






//        result.add(new WekaBayesNet("-Q weka.classifiers.bayes.net.search.local.K2 -- " +
//            "-P " + parentsCount + " -S BAYES -E " +
//            "weka.classifiers.bayes.net.estimate.SimpleEstimator -- -A 0.5"
//            ,"local.K2.BAYES", parentsCount));
////
//        result.add(new WekaBayesNet("-Q weka.classifiers.bayes.net.search.local.K2 -- " +
//            "-P " + parentsCount + " -S MDL -E " +
//            "weka.classifiers.bayes.net.estimate.SimpleEstimator -- -A 0.5"
//            ,"local.K2.MDL", parentsCount));
//
//        result.add(new WekaBayesNet("-Q weka.classifiers.bayes.net.search.local.K2 -- " +
//            "-P " + parentsCount + " -S AIC -E " +
//            "weka.classifiers.bayes.net.estimate.SimpleEstimator -- -A 0.5"
//            ,"local.K2.AIC", parentsCount));



        //        result.add(new WekaBayesNet("-Q weka.classifiers.bayes.net.search.local.HillClimber -- " +
//            "-N -P " + parentsCount + " -S BAYES -E " +
//            "weka.classifiers.bayes.net.estimate.SimpleEstimator -- -A 0.5"
//            ,"local.HillClimber.Bayes", parentsCount));
//
//        result.add(new WekaBayesNet("-Q weka.classifiers.bayes.net.search.local.HillClimber -- " +
//            "-P " + parentsCount + " -S MDL -E " +
//            "weka.classifiers.bayes.net.estimate.SimpleEstimator -- -A 0.5"
//            ,"local.HillClimber.MDL", parentsCount));
//
//        result.add(new WekaBayesNet("-Q weka.classifiers.bayes.net.search.local.HillClimber -- " +
//            "-P " + parentsCount + " -S AIC -E " +
//            "weka.classifiers.bayes.net.estimate.SimpleEstimator -- -A 0.5"
//            ,"local.HillClimber.AIC", parentsCount));



//        result.add(new WekaBayesNet("-Q weka.classifiers.bayes.net.search.local.TabuSearch -- " +
//            "-L 2 -U 4 -N -P " + parentsCount + " -S BAYES -E " +
//            "weka.classifiers.bayes.net.estimate.SimpleEstimator -- -A 0.5"
//            ,"local.TabuSearch.Bayes", parentsCount));
//
//        result.add(new WekaBayesNet("-Q weka.classifiers.bayes.net.search.local.TabuSearch -- " +
//            "-L 2 -U 4 -N -P " + parentsCount + " -S MDL -E " +
//            "weka.classifiers.bayes.net.estimate.SimpleEstimator -- -A 0.5"
//            ,"local.TabuSearch.MDL", parentsCount));
//
//        result.add(new WekaBayesNet("-Q weka.classifiers.bayes.net.search.local.TabuSearch -- " +
//            "-L 2 -U 4 -N -P " + parentsCount + " -S AIC -E " +
//            "weka.classifiers.bayes.net.estimate.SimpleEstimator -- -A 0.5"
//            ,"local.TabuSearch.AIC", parentsCount));



//        result.add(new WekaBayesNet("-Q weka.classifiers.bayes.net.search.local.SimulatedAnnealing -- " +
//            "-A 10.0 -U 200 -D 0.999 -R 1 -S BAYES -E weka.classifiers.bayes.net.estimate.SimpleEstimator -- -A 0.5"
//            ,"SimulatedAnnealing.BAYES", parentsCount));
//
//        result.add(new WekaBayesNet("-Q weka.classifiers.bayes.net.search.local.SimulatedAnnealing -- " +
//            "-A 10.0 -U 200 -D 0.999 -R 1 -S MDL -E weka.classifiers.bayes.net.estimate.SimpleEstimator -- -A 0.5"
//            ,"SimulatedAnnealing.MDL", parentsCount));
//
//        result.add(new WekaBayesNet("-Q weka.classifiers.bayes.net.search.local.SimulatedAnnealing -- " +
//            "-A 10.0 -U 200 -D 0.999 -R 1 -S AIC -E weka.classifiers.bayes.net.estimate.SimpleEstimator -- -A 0.5"
//            ,"SimulatedAnnealing.AIC", parentsCount));
//
//
//
//        result.add(new WekaBayesNet("-Q weka.classifiers.bayes.net.search.local.TAN -- " +
//            "-S BAYES -E weka.classifiers.bayes.net.estimate.SimpleEstimator -- -A 0.5"
//            ,"local.TAN.Bayes", parentsCount));
//
//        result.add(new WekaBayesNet("-Q weka.classifiers.bayes.net.search.local.TAN -- " +
//            "-S MDL -E weka.classifiers.bayes.net.estimate.SimpleEstimator -- -A 0.5"
//            ,"local.TAN.MDL", parentsCount));
//
//        result.add(new WekaBayesNet("-Q weka.classifiers.bayes.net.search.local.TAN -- " +
//            "-S AIC -E weka.classifiers.bayes.net.estimate.SimpleEstimator -- -A 0.5"
//            ,"local.TAN.AIC", parentsCount));













////
////
////        //global
//        result.add(new WekaBayesNet("-Q weka.classifiers.bayes.net.search.global.K2 -- " +
//            "-P " + parentsCount + " -N -mbc -S LOO-CV -E " +
//            "weka.classifiers.bayes.net.estimate.SimpleEstimator -- -A 0.5"
//            ,"global.K2", parentsCount));
//
//        result.add(new WekaBayesNet("-Q weka.classifiers.bayes.net.search.global.TAN -- " +
//            "-mbc -S LOO-CV -E " +
//            "weka.classifiers.bayes.net.estimate.SimpleEstimator -- -A 0.5"
//            ,"global.TAN", -1));

//        result.add(new WekaBayesNet("-Q weka.classifiers.bayes.net.search.global.GeneticSearch -- " +
//            "-L 10 -A 100 -U 10 -R 1 -M -C -S LOO-CV -E " +
//            "weka.classifiers.bayes.net.estimate.SimpleEstimator -- -A 0.5"
//            ,"global.GeneticSearch", -1));

//        result.add(new WekaBayesNet("-Q weka.classifiers.bayes.net.search.local.GeneticSearch -- " +
//                "-L 10 -A 100 -U 10 -R 1 -M -C -S BAYES -E weka.classifiers.bayes.net.estimate.SimpleEstimator -- -A 0.5"
//                ,"local.GeneticSearch", -1));


        return result;
    }
}