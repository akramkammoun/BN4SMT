/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package wekabayesnetwork;

import wekabayesnetwork.MyWeka.MyMarginCalculator;
import com.sun.org.apache.xerces.internal.parsers.DOMParser;
import java.io.BufferedReader;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import weka.core.FastVector;
import wekabayesnetwork.MyWeka.MyBIFReader;
import wekabayesnetwork.MyWeka.MyEditableBayesNet;

/**
 *
 * @author akram
 */
public class TranslatingLine
{

    public ArrayList<String> wordsTranslatedList;
    public ArrayList<String> wordsNotTranslatedList;//dosen't exist in the training corpus
    public ArrayList<String> furthermoreWordsList;//evaluation
    public ArrayList<String> missedWordsList;//evaluation
    public ArrayList<Integer> nodesSourceIndexFound;
    private ArrayList<ParentNodes> parentNodes;
    public String phraseInDelimiter = " ";
    public String paramNull = "0";
    public int firstIndexTarget;//first index concept langage target
    public int lastIndexTarget;//last index concept langage target
    public int firstIndexSource;//first index concept langage source
    public int lastIndexSource;//last index concept langage source
    public String textIn;
    public boolean setEvidenceAll;
    public boolean searchInAll;
    public String lineEval;
    public String bayesNetMarginPath;
//    public double[][] bayesMargin;
    MyMarginCalculator bayesNetMargin = null;

    public TranslatingLine()
    {
    }

    public void loadBayesNet(String bayesNetXMLPath,
                             String bayesNetXmlConfPath,
                             String bayesNetMarginPath)
    {
        this.bayesNetMarginPath = bayesNetMarginPath;
        try
        {
//            MyBIFReader bifReader = new MyBIFReader();
//            bifReader.processFile(bayesNetXMLPath);
//
//            this.editBayesNet = new MyEditableBayesNet(bifReader);


            //sources nodes and target sources
            DOMParser parser = new DOMParser();
            parser.parse(bayesNetXmlConfPath);

            Document doc = parser.getDocument();

            NodeList nodes;

            //source nodes
            nodes = doc.getElementsByTagName("SRCLANG");
            this.firstIndexSource = Integer.valueOf(nodes.item(0).
                    getChildNodes().item(3).getTextContent());
            this.lastIndexSource = Integer.valueOf(nodes.item(0).
                    getChildNodes().item(5).getTextContent());

            //target nodes
            nodes = doc.getElementsByTagName("TRGLANG");
            this.firstIndexTarget = Integer.valueOf(nodes.item(0).
                    getChildNodes().item(3).getTextContent());
            this.lastIndexTarget = Integer.valueOf(nodes.item(0).
                    getChildNodes().item(5).getTextContent());


        }
        catch (Exception ex)
        {
            Logger.getLogger(TranslatingLine.class.getName()).log(Level.SEVERE,
                                                                  null,
                                                                  ex);
        }

    }

    public void translateLine(String line,
                              String delim,
                              boolean setEvidenceAllWords,
                              boolean findInAllTargetWords,
                              String conceptFilePath,
                              double alpha,
                              boolean setEvidenceZero)
    {
        this.textIn = line;
        this.setEvidenceAll = setEvidenceAllWords;
        this.searchInAll = findInAllTargetWords;

        this.wordsTranslatedList = new ArrayList<String>();
        this.wordsNotTranslatedList = new ArrayList<String>();


        this.nodesSourceIndexFound = new ArrayList<Integer>();


        this.parentNodes = new ArrayList<ParentNodes>();

        String[] wordsStringArray = line.split(delim);

        System.out.println(line);

        if ((this.lastIndexTarget - this.firstIndexTarget <= 0)
            || (this.lastIndexSource - this.firstIndexSource <= 0))
        {
            return;
        }

        //calul marginal probabilities
        try
        {
            bayesNetMargin = (MyMarginCalculator) weka.core.SerializationHelper.read(
                    this.bayesNetMarginPath);
//            this.editBayesNet = bayesNetMargin.m_root.m_bayesNet;
        }
        catch (Exception ex)
        {
            Logger.getLogger(TranslatingLine.class.getName()).log(Level.SEVERE,
                                                                  null,
                                                                  ex);
        }


        ArrayList<ArrayList<String>> conceptsWordsLists = new ArrayList<ArrayList<String>>();

        ArrayList<ArrayList<String>> conceptsLists = Functions.fileToLists(new File(
                conceptFilePath),
                                                                           " ");

        ArrayList<String> wordsExitsList = new ArrayList<String>();

        for (int i = 0; i < conceptsLists.size(); i++)
        {
            ArrayList<String> conceptsWordsListTmp = new ArrayList<String>();

//            conceptsLists.get(i).remove(0);

            for (int wordNumb = 0; wordNumb < wordsStringArray.length; wordNumb++)
            {
                if (conceptsLists.get(i).contains(wordsStringArray[wordNumb]))
                {
                    conceptsWordsListTmp.add(wordsStringArray[wordNumb]);
                    wordsExitsList.add(wordsStringArray[wordNumb]);
                }
            }

            if (!conceptsWordsListTmp.isEmpty())
            {
                conceptsWordsLists.add(conceptsWordsListTmp);
            }
        }

        conceptsLists = null;

        //search words which dosen't exists.
        for (int wordNumb = 0; wordNumb < wordsStringArray.length; wordNumb++)
        {
            if (!wordsExitsList.contains(wordsStringArray[wordNumb]))
            {
                this.wordsTranslatedList.add(wordsStringArray[wordNumb]);
//                System.out.print(wordsStringArray[wordNumb]);
                this.wordsNotTranslatedList.add(wordsStringArray[wordNumb]);
            }
        }
        wordsExitsList = null;


        //if there isn't any words to translate
        if (!conceptsWordsLists.isEmpty())
        {
            System.out.println(conceptsWordsLists.toString());

            //set evidence
            //find the "in" words in the values of nodes
            for (int wordNum = 0; wordNum < conceptsWordsLists.size(); wordNum++)
            {
                //find the word
                boolean found = false;

                //find in sources nodes
                for (int nodeNum = this.firstIndexSource; nodeNum <= this.lastIndexSource
                                                          && found == false; nodeNum++)
                {
                    String[] valuesNode = this.bayesNetMargin.m_root.m_bayesNet.getValues(
                            nodeNum);
//                double[] fMarginP = new double[valuesNode.length];

                    //find in all values of the given node
                    for (int valueNum = 0; valueNum < valuesNode.length && found == false; valueNum++)
                    {
                        //word is find
                        //the first word in concept
                        if (valuesNode[valueNum].equals(conceptsWordsLists.get(
                                wordNum).get(0)))
                        {
                            found = true;
                            try
                            {
                                if (!this.nodesSourceIndexFound.contains(nodeNum))
                                {
                                    ArrayList<Integer> valueNumList = new ArrayList<Integer>();
                                    valueNumList.add(valueNum);

                                    for (int valueNum2 = 0; valueNum2 < valuesNode.length; valueNum2++)
                                    {
                                        if (conceptsWordsLists.get(wordNum).contains(
                                                valuesNode[valueNum2]))
                                        {
                                            valueNumList.add(valueNum2);
                                        }
                                    }

                                    bayesNetMargin.setEvidence(nodeNum,
                                                               valueNumList,
                                                               setEvidenceZero);


                                    this.nodesSourceIndexFound.add(nodeNum);

                                    if (!this.parentNodes.contains(new ParentNodes(
                                            nodeNum)))
                                    {
                                        this.parentNodes.add(
                                                new ParentNodes(
                                                nodeNum,
                                                this.bayesNetMargin.m_root.m_bayesNet.getChildren(
                                                nodeNum)));
                                    }
                                }
                            }
                            catch (Exception ex)
                            {
                                Logger.getLogger(TranslatingLine.class.getName()).log(
                                        Level.SEVERE,
                                                                                      null,
                                                                                      ex);
                            }
                        }
                    }
                }
                if (setEvidenceAllWords == true)
                {
                    //set evidence at words that dosen't exist in langage source
                    for (int nodeNum = this.firstIndexSource; nodeNum <= this.lastIndexSource; nodeNum++)
                    {
                        if (!this.nodesSourceIndexFound.contains(nodeNum))
                        {
                            try
                            {
                                ArrayList<Integer> valuesListNulle = new ArrayList<Integer>();
                                valuesListNulle.add(0);

                                bayesNetMargin.setEvidence(nodeNum,
                                                           valuesListNulle,
                                                           setEvidenceZero);

                            }
                            catch (Exception ex)
                            {
                                Logger.getLogger(TranslatingLine.class.getName()).log(
                                        Level.SEVERE,
                                                                                      null,
                                                                                      ex);
                            }
                        }
                    }
                }
            }

            //select words
            try
            {
                if (findInAllTargetWords == true)
                {
                    //find in all nodes of target language
                    for (int nodeNum = this.firstIndexTarget; nodeNum <= this.lastIndexTarget; nodeNum++)
                    {
                        String[] valuesNode = this.bayesNetMargin.m_root.m_bayesNet.getValues(
                                nodeNum);

//                    if(((float) 1/(valuesNode.length) + alpha) > 1)
                        for (int ip = 1; ip < valuesNode.length; ip++)
                        {

//                        System.out.println(valuesNode[ip] +": " + bayesNetMargin.getMargin(nodeNum)[ip]);
                            if (bayesNetMargin.getMargin(nodeNum)[ip] > (((float) 1 / (valuesNode.length)) + alpha))
                            {

                                if (!this.wordsTranslatedList.contains(
                                        valuesNode[ip]))
                                {
                                    this.wordsTranslatedList.add(valuesNode[ip]);
                                }
                            }
                        }
                    }
                }
                else
                {
                    for (int nodeParentNum = 0; nodeParentNum < this.nodesSourceIndexFound.size(); nodeParentNum++)
                    {
                        FastVector nodeChildrenFV = this.bayesNetMargin.m_root.m_bayesNet.
                                getChildren(this.nodesSourceIndexFound.get(
                                nodeParentNum));

                        for (int nodeChildNum = 0; nodeChildNum < nodeChildrenFV.size(); nodeChildNum++)
                        {

                            int nodeNum = (Integer) nodeChildrenFV.elementAt(
                                    nodeChildNum);

                            //to evoid to take on count the sources nodes. Cause it can be happen that
                            //the nodeNum is a source node
                            if (nodeNum >= this.firstIndexTarget && nodeNum <= this.lastIndexTarget)
                            {
                                String[] valuesNode = this.bayesNetMargin.m_root.m_bayesNet.getValues(
                                        nodeNum);

                                for (int ip = 1; ip < valuesNode.length; ip++)
                                {
                                    if (bayesNetMargin.getMargin(nodeNum)[ip] > (((float) 1 / (valuesNode.length)) + alpha))
                                    {
                                        this.wordsTranslatedList.add(
                                                valuesNode[ip]);
                                    }
                                }
                            }
                        }
                    }
                }
            }
            catch (Exception ex)
            {
                Logger.getLogger(TranslatingLine.class.getName()).log(
                        Level.SEVERE,
                                                                      null,
                                                                      ex);
            }

        }
    }

    public void conceptsToWordsTranslation(String conceptsOutFilePath,
                                           String translatedFilePath)
    {
        ArrayList<ArrayList<String>> conceptsOutList =
                                     Functions.fileToLists(new File(
                conceptsOutFilePath),
                                                           " ");

        ArrayList<String> translatedTextList = new ArrayList<String>();

        //calul marginal probabilities
        try
        {
            bayesNetMargin = (MyMarginCalculator) weka.core.SerializationHelper.read(
                    this.bayesNetMarginPath);
        }
        catch (Exception ex)
        {
            Logger.getLogger(TranslatingLine.class.getName()).log(Level.SEVERE,
                                                                  null,
                                                                  ex);
        }



        for (int i = 0; i < conceptsOutList.size(); i++)
        {
            ArrayList<String> translatedLineList = new ArrayList<String>();

            for (int j = 0; j < conceptsOutList.get(i).size(); j++)
            {
                boolean wordFound = false;

                for (int nodeNum = this.firstIndexTarget; nodeNum <= this.lastIndexTarget
                                                          && wordFound == false; nodeNum++)
                {

                    if (this.bayesNetMargin.m_root.m_bayesNet.getNodeName(
                            nodeNum).
                            equals(conceptsOutList.get(i).get(j)))
                    {
                        wordFound = true;

                        String[] valuesNode = this.bayesNetMargin.m_root.m_bayesNet.getValues(
                                nodeNum);

                        float[] margin = bayesNetMargin.getMargin(nodeNum);


                        for (int k = 1; k < margin.length; k++)
                        {
                            translatedLineList.add(valuesNode[k]);
                        }

                    }
                }
            }

            translatedTextList.add(Functions.listToString(translatedLineList,
                                                          " "));

        }

        Functions.stringListToFile(translatedTextList,
                                   translatedFilePath,
                                   false);
    }

    public void evaluateTranslation(ArrayList<String> words)
    {

        this.lineEval = Functions.listToString(words,
                                               " ");

        this.missedWordsList = new ArrayList<String>();
        this.furthermoreWordsList = new ArrayList<String>();


        //find missed words
        for (int i = 0; i < words.size(); i++)
        {
            if (!this.wordsTranslatedList.contains(words.get(i)))
            {
                this.missedWordsList.add(words.get(i));
            }
        }

        //find furthermore words
        for (int i = 0; i < this.wordsTranslatedList.size(); i++)
        {
            if (!words.contains(this.wordsTranslatedList.get(i)))
            {
                this.furthermoreWordsList.add(this.wordsTranslatedList.get(i));
            }
        }
    }

    public void deleteUnusedSourceNodes()
    {
        ArrayList<Integer> unusedNodes = new ArrayList<Integer>();
        FastVector nodes = new FastVector();

        for (int i = this.firstIndexSource; i <= this.lastIndexSource; i++)
        {
            if (!this.parentNodes.contains(new ParentNodes(i)))
            {
                unusedNodes.add(i);
                nodes.addElement(i);
            }
        }

        bayesNetMargin.m_root.m_bayesNet.deleteSelection(nodes);
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

            bw.write(bayesNetMargin.m_root.m_bayesNet.toXMLBIF03());

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

    public void exportEvaluate(String fileOutPath,
                               String classifierName,
                               int parentCount,
                               String distance1Name,
                               String distance2Name,
                               String bayesNetMode,
                               String bayesNetPath,
                               String textInPath,
                               String textEvalPath)
    {

        BufferedWriter bw;

        try
        {
            FileWriter fileOut = new FileWriter(fileOutPath,
                                                true);
            bw = new BufferedWriter(fileOut);

            bw.write("text in path\t" + textInPath + "\n");
            bw.write("text eval path\t" + textEvalPath + "\n");

            bw.write("line in\t");


            bw.close();
        }
        catch (IOException ex)
        {
            Logger.getLogger(TextProcessing.class.getName()).log(Level.SEVERE,
                                                                 null,
                                                                 ex);
        }
    }

//    public static ArrayList<String> lineToConcept(ArrayList<String> line, String conceptPath, String conceptDelimiter)
//    {
//        ArrayList<String> result = new ArrayList<String>();
//
//        ArrayList<ArrayList<String>> conceptList =
//            Functions.fileToLists(new File(conceptPath), conceptDelimiter);
//
//        for(int i = 0; i < line.size(); i++)
//        {
//            boolean found = false;
//            for(int j = 0; j < conceptList.size() && found == false; j++)
//            {
//                boolean indexFound = conceptList.get(j).contains(line.get(i));
//
//                if(indexFound == true)
//                {
//                    found = true;
//                    if(!result.contains(conceptList.get(j).get(0)))
//                        result.add(conceptList.get(j).get(0));//concept name
//                }
//
//            }
//        }
//
//        return result;
//    }
    public static String lineToConcepts(String line,
                                        String conceptFilePath)
    {
        ArrayList<String> result = new ArrayList<String>();

        ArrayList<ArrayList<String>> conceptList =
                                     Functions.fileToLists(new File(
                conceptFilePath),
                                                           " ");

        ArrayList<String> lineList = Functions.StringToList(line,
                                                            " ");


        for (int k = 0; k < lineList.size(); k++)//word
        {
            boolean found = false;

            for (int j = 0; j < conceptList.size() && found == false; j++)
            {
                boolean indexFound = conceptList.get(j).contains(lineList.get(k));

                if (indexFound == true)
                {
                    found = true;
                    if (!result.contains(conceptList.get(j).get(0)))
                    {
                        result.add(conceptList.get(j).get(0));//concept name
                    }
                }
            }
        }

        return Functions.listToString(result,
                                      " ");
    }

    public static void textToTextConcept(String textFilePath,
                                         String conceptFilePath,
                                         String conceptDelimiter,
                                         String fileOutPath)
    {
        File fileOut = new File(fileOutPath);
        fileOut.delete();

        ArrayList<String> strList = Functions.fileToStrings(textFilePath);

        String textConcepts = "";

        for (int i = 0; i < strList.size(); i++)
        {
            textConcepts += TranslatingLine.lineToConcepts(strList.get(i),
                                                           conceptFilePath) + "\n";
        }

        Functions.stringToFile(textConcepts,
                               fileOutPath,
                               false);
    }

    public static int getConceptCount(String conceptName,
                                      String conceptsPath)
    {

        InputStreamReader is;
        BufferedReader br;

        String line;
        try
        {
            is = new InputStreamReader(new FileInputStream(conceptsPath),
                                       "UTF-8");
            br = new BufferedReader(is);

            while ((line = br.readLine()) != null)
            {
                String lineTab[] = line.split(" ");

                if (lineTab[0].equals(conceptName))
                {
                    return (lineTab.length - 1);
                }
            }
        }
        catch (Exception ex)
        {
            Logger.getLogger(TranslatingLine.class.getName()).log(Level.SEVERE,
                                                                  null,
                                                                  ex);
        }

        return -1;
    }

    public static MyEditableBayesNet buildSubBn(String line,
                                                String conceptFilePath,
                                                String bnXmlPath,
                                                String bayesNetXmlConfPath,
                                                String bnXmlPathNew,
                                                String bayesNetXmlConfPathNew)
    {

        ArrayList<String> lineList =
                          Functions.StringToList(
                TranslatingLine.lineToConcepts(line,
                                               conceptFilePath),
                                                 " ");


        int firstIndexSource = 0;
        int lastIndexSource = 0;
        int firstIndexTarget = 0;
        int lastIndexTarget = 0;

        try
        {
            //sources nodes and target sources
            DOMParser parser = new DOMParser();
            parser.parse(bayesNetXmlConfPath);

            Document doc = parser.getDocument();

            NodeList nodes;

            //source nodes
            nodes = doc.getElementsByTagName("SRCLANG");
            firstIndexSource = Integer.valueOf(nodes.item(0).
                    getChildNodes().item(3).getTextContent());
            lastIndexSource = Integer.valueOf(nodes.item(0).
                    getChildNodes().item(5).getTextContent());

            //target nodes
            nodes = doc.getElementsByTagName("TRGLANG");
            firstIndexTarget = Integer.valueOf(nodes.item(0).
                    getChildNodes().item(3).getTextContent());
            lastIndexTarget = Integer.valueOf(nodes.item(0).
                    getChildNodes().item(5).getTextContent());

        }
        catch (Exception ex)
        {
            Logger.getLogger(TranslatingLine.class.getName()).log(Level.SEVERE,
                                                                  null,
                                                                  ex);
        }


        MyBIFReader bifReader = new MyBIFReader();
        MyEditableBayesNet editBayesNetLine = null;
        try
        {
            bifReader.processFile(bnXmlPath);

            editBayesNetLine = new MyEditableBayesNet(bifReader);

            FastVector nodesSourceDel = new FastVector();

            for (int i = firstIndexSource; i <= lastIndexSource; i++)
            {

                //search source nodes
                if (!lineList.contains(editBayesNetLine.getNodeName(i)))
                {
                    nodesSourceDel.addElement(i);
                }
            }

            editBayesNetLine.deleteSelection(nodesSourceDel);

            //delete target nodes which hadn't any arcs
            nodesSourceDel = new FastVector();
            int targetNodesSize = 0;
            //
            for (int i = lineList.size(); i < editBayesNetLine.getNrOfNodes(); i++)
            {
                if (editBayesNetLine.getChildren(i).size() == 0 && editBayesNetLine.getParentSet(
                        i).getNrOfParents() == 0)
                {
                    nodesSourceDel.addElement(i);
                }
                else
                {
                    targetNodesSize++;
                }
            }
            //
            editBayesNetLine.deleteSelection(nodesSourceDel);

            //export graph
            Functions.stringToFile(editBayesNetLine.graph(),
                                   bnXmlPathNew,
                                   false);

            //export configuration graph
            int nodesSourceSizeNew = lineList.size();
            int firstIndexSourceNew = firstIndexSource;
            int lastIndexSourceNew = firstIndexSource + lineList.size() - 1;
            //
            int nodesTargetSizeNew = targetNodesSize;
            int firstIndexTargetNew = editBayesNetLine.getNrOfNodes() - nodesTargetSizeNew;
            int lastIndexTargetNew = editBayesNetLine.getNrOfNodes() - 1;

            WekaBayesNet.exportConfBayesNet(bayesNetXmlConfPathNew,
                                            nodesSourceSizeNew,
                                            firstIndexSourceNew,
                                            lastIndexSourceNew,
                                            nodesTargetSizeNew,
                                            firstIndexTargetNew,
                                            lastIndexTargetNew);
        }
        catch (Exception ex)
        {
            Logger.getLogger(TranslatingLine.class.getName()).log(Level.SEVERE,
                                                                  null,
                                                                  ex);
        }

        return editBayesNetLine;
    }

//    public void setMargin()
//    {
//        for(int i = 0; i < this.editBayesNet.getNrOfNodes(); i++)
//        {
//            this.editBayesNet.setMargin(i, this.bayesNetMargin.getMargin(i));
//        }
//
//    }
//    public void exportMarginBayesNetXml(File fileOut)
//    {
//        OutputStreamWriter os;
//        BufferedWriter bw;
//
//
//        fileOut.delete();
//
//        try
//        {
//
//            os = new OutputStreamWriter(new FileOutputStream(fileOut), "UTF-8");
//            bw = new BufferedWriter(os);
//
//            bw.write(this.bayesNetMargin.toXMLBIF03());
//
//            bw.close();
//            os.close();
//        }
//        catch (FileNotFoundException ex)
//        {
//            Logger.getLogger(WekaBayesNet.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        catch(Exception ex)
//        {
//            Logger.getLogger(WekaBayesNet.class.getName()).log(Level.SEVERE, null, ex);
//        }
//    }
    private class ParentNodes
    {

        public int indexParentNode;
        public FastVector indexChildsNode;

        public ParentNodes(int indexParentNode,
                           FastVector indexChildsNode)
        {
            this.indexParentNode = indexParentNode;
            this.indexChildsNode = indexChildsNode;
        }

        public ParentNodes(int indexParentNode)
        {
            this.indexParentNode = indexParentNode;
        }

        @Override
        public boolean equals(Object instance)
        {
            return (this.indexParentNode == ((ParentNodes) instance).indexParentNode);
        }
    }
}