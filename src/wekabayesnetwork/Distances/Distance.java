/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package wekabayesnetwork.Distances;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import weka.clusterers.*;
import weka.core.FastVector;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSink;
import wekabayesnetwork.Functions;
import wekabayesnetwork.Word;

/**
 *
 * @author akram
 */
abstract public class Distance
{

    public RandomizableClusterer randomCluster;
    public HierarchicalClusterer hierarchicalClusterer;
    public EM em;
    int[] clusterInstanceTab;
    public Instances dataSet;
    public String distanceName;
    public int textLinesCount;//max 2 147 483 647
    public ArrayList<Word> wordsList = new ArrayList<Word>();
    public short[][] wordSquareMat;//max 32767
    public FastVector attributesDataSet = new FastVector();
    public int conceptCounts;
    public ArrayList<ArrayList<String>> conceptLists;
    public int[] conceptWordsCount;
    public double intraClass = 0;
    public double interClass = 0;
    public int wordsCountInertie = 0;

    public Distance(int textLinesCount,
                    ArrayList<Word> wordsList,
                    short[][] wordSquareMat,
                    FastVector attributesDataSet,
                    int conceptCount)
    {
        this.textLinesCount = textLinesCount;
        this.wordsList = wordsList;
        this.wordSquareMat = wordSquareMat;
        this.attributesDataSet = attributesDataSet;
        this.conceptCounts = conceptCount;
    }

    abstract public void buildDataSet();

//    public void runKmeansCluster(String options)
//    {
//
//        String[] optionsArray;
//
//        this.randomCluster = new SimpleKMeans();
//
//        try
//        {
//            optionsArray = weka.core.Utils.splitOptions(options);
//
//            this.randomCluster.setOptions(optionsArray);
//
//            //execute the algorithm
//            this.randomCluster.buildClusterer(this.dataSet);
//
//            this.conceptCounts = this.randomCluster.numberOfClusters();
//
//        }
//        catch (IOException ex)
//        {
//            Logger.getLogger(Distance.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        catch (Exception ex)
//        {
//            Logger.getLogger(Distance.class.getName()).log(Level.SEVERE, null, ex);
//        }
//    }
    //
    public void runClustring(Object clusterType,
                             String options)
    {
        String[] optionsArray = null;

        try
        {
            optionsArray = weka.core.Utils.splitOptions(options);
        }
        catch (Exception ex)
        {
            Logger.getLogger(Distance.class.getName()).log(Level.SEVERE,
                                                           null,
                                                           ex);
        }

        try
        {
            if (clusterType instanceof RandomizableClusterer)
            {
                this.randomCluster = (RandomizableClusterer) clusterType;

                this.randomCluster.setOptions(optionsArray);

                //execute the algorithme
                this.randomCluster.buildClusterer(this.dataSet);

                this.conceptCounts = this.randomCluster.numberOfClusters();

                this.clusterInstanceTab = new int[this.dataSet.numInstances()];
                for (int i = 0; i < this.dataSet.numInstances(); i++)
                {
                    clusterInstanceTab[i] = this.randomCluster.clusterInstance(this.dataSet.instance(
                            i));
                }
            }
            else if (clusterType instanceof HierarchicalClusterer)
            {
                this.hierarchicalClusterer = (HierarchicalClusterer) clusterType;

                this.hierarchicalClusterer.setOptions(optionsArray);

                //execute the algorithme
                this.hierarchicalClusterer.buildClusterer(this.dataSet);

                this.conceptCounts = this.hierarchicalClusterer.numberOfClusters();

                this.clusterInstanceTab = new int[this.dataSet.numInstances()];
                for (int i = 0; i < this.dataSet.numInstances(); i++)
                {
                    clusterInstanceTab[i] = this.hierarchicalClusterer.clusterInstance(this.dataSet.instance(
                            i));
                }
            }
            else if (clusterType instanceof EM)
            {
                this.em = (EM) clusterType;

                this.em.setOptions(optionsArray);

                //execute the algorithme
                this.em.buildClusterer(this.dataSet);

                this.conceptCounts = this.em.numberOfClusters();

                this.clusterInstanceTab = new int[this.dataSet.numInstances()];
                for (int i = 0; i < this.dataSet.numInstances(); i++)
                {
                    clusterInstanceTab[i] = this.em.clusterInstance(this.dataSet.instance(
                            i));
                }
            }
        }
        catch (IOException ex)
        {
            Logger.getLogger(Distance.class.getName()).log(Level.SEVERE,
                                                           null,
                                                           ex);
        }
        catch (Exception ex)
        {
            Logger.getLogger(Distance.class.getName()).log(Level.SEVERE,
                                                           null,
                                                           ex);
        }
    }
    //

    public void extractConcept()
    {
        this.conceptLists = new ArrayList<ArrayList<String>>();
        this.conceptWordsCount = new int[this.conceptCounts];

        //initialize concept list
        for (int i = 0; i < this.conceptCounts; i++)
        {
            this.conceptLists.add(new ArrayList<String>());

            this.conceptWordsCount[i] = 0;
        }


        //build the concept list
        for (int i = 0; i < this.dataSet.numInstances(); i++)
        {
            try
            {
                this.conceptLists.get(this.clusterInstanceTab[i]).add(this.wordsList.get(
                        i).text);
                this.conceptWordsCount[this.clusterInstanceTab[i]]++;
            }
            catch (Exception ex)
            {
                Logger.getLogger(Distance.class.getName()).log(Level.SEVERE,
                                                               null,
                                                               ex);
            }
        }

//        int sum = 0;
//        for(int i = 0; i < this.conceptCounts; i++)
//        {
//            sum+=this.conceptWordsCount[i];
//            System.out.println(this.conceptWordsCount[i]);
//        }
//        System.out.println(sum);
//        System.out.println(this.wordsList.size());
//
//        System.exit(-1);

    }
    //

    public void splitConcept(int clusterInstancesCount)
    {
        ArrayList<ArrayList<String>> result = new ArrayList<ArrayList<String>>();

        for (int i = 0; i < this.conceptLists.size(); i++)
        {

            int j;
            int clusterInstancesCountTmp = 1;
            ArrayList<String> concept = new ArrayList<String>();

            if (this.conceptLists.get(i).size() > clusterInstancesCount)
            {
                for (j = 0; j < this.conceptLists.get(i).size(); j++)
                {
                    concept.add(this.conceptLists.get(i).get(j));

                    if (clusterInstancesCount > clusterInstancesCountTmp)
                    {
                        clusterInstancesCountTmp++;
                    }
                    else
                    {
                        result.add(concept);
                        concept = new ArrayList<String>();
                        clusterInstancesCountTmp = 1;
                    }
                }

                if (!concept.isEmpty())
                {
                    result.add(concept);
                }
            }
            else
            {
                result.add(this.conceptLists.get(i));
            }
        }


        this.conceptLists = result;
        this.conceptCounts = result.size();
    }
    //

    public void reorderConcept()
    {
        ArrayList<ArrayList<String>> result = new ArrayList<ArrayList<String>>();

        for (int i = 0; i < this.wordsList.size(); i++)
        {
            boolean found = false;
            for (int j = 0; j < this.conceptLists.size() && found == false; j++)
            {
                if (this.conceptLists.get(j).contains(this.wordsList.get(i).text))
                {
                    if (!result.contains(this.conceptLists.get(j)))
                    {
                        result.add(this.conceptLists.get(j));
                        found = true;
                    }
                    else
                    {
                        found = true;
                    }
                }
            }
        }

        this.conceptLists = result;
    }

    public void stemToOrigWords(String origStemWordsPath)
    {
        ArrayList<ArrayList<String>> origStemWordsList =
                                     Functions.fileToLists(new File(
                origStemWordsPath),
                                                           "\t");

        ArrayList<ArrayList<String>> newTab = new ArrayList<ArrayList<String>>();

        int i = this.conceptLists.size() - 1;
        while (i >= 0)
        {
            ArrayList<String> wordsAdd = new ArrayList<String>();
            for (int j = 0; j < this.conceptLists.get(i).size(); j++)
            {
                String word = this.conceptLists.get(i).get(j);
//                System.out.println(word);
                for (int k = 0; k < origStemWordsList.size(); k++)
                {
                    //origStemWordsList.get(k).get(1): mot origine
                    //origStemWordsList.get(k).get(0): mot stemmed
//                    System.out.println(origStemWordsList.get(k).get(0));
//                    if(!origStemWordsList.get(k).isEmpty())
                    if (word.equals(origStemWordsList.get(k).get(0)))//word stemmed
                    {
                        String[] wordsSplited = origStemWordsList.get(k).get(1).split(
                                " ");
                        wordsAdd.addAll(Arrays.asList(wordsSplited));
                    }
                }
            }
            i--;
            newTab.add(wordsAdd);
        }
        this.conceptLists = newTab;
    }
    //

    public void exportConcept(String fileOutPath,
                              String conceptDelimiter,
                              String lang)
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


            for (int i = 0; i < this.conceptLists.size(); i++)
            {
                bw.write(
                        "c" + (i + 1) + this.distanceName + lang + conceptDelimiter);
                int j;
                for (j = 0; j < this.conceptLists.get(i).size() - 1; j++)
                {
                    bw.write(this.conceptLists.get(i).get(j) + conceptDelimiter);
                }

                bw.write(this.conceptLists.get(i).get(j) + "\n");
            }

            bw.close();
            os.close();

        }
        catch (IOException ex)
        {
            Logger.getLogger(Distance.class.getName()).log(Level.SEVERE,
                                                           null,
                                                           ex);
        }
        catch (Exception ex)
        {
            Logger.getLogger(Distance.class.getName()).log(Level.SEVERE,
                                                           null,
                                                           ex);
        }
    }

    public void exportDataSetArff(String fileOutPath)
    {
        try
        {
            DataSink.write(fileOutPath,
                           this.dataSet);
        }
        catch (Exception ex)
        {
            Logger.getLogger(Distance.class.getName()).log(Level.SEVERE,
                                                           null,
                                                           ex);
        }
    }

    public void calcInertie()
    {

        //


        //

        double[] gCentre = new double[this.wordsCountInertie];

        double[][] gConceptsCentre = new double[this.conceptCounts][this.wordsCountInertie];
        //intialize gConceptsCentre[i][j]
        for (int i = 0; i < this.conceptCounts; i++)
        {
            for (int j = 0; j < this.wordsCountInertie; j++)
            {
                gConceptsCentre[i][j] = 0;
            }
        }


        //gCentre and //gConcepts
        for (int i = 0; i < this.wordsCountInertie; i++)
        {
            gCentre[i] = 0;
            for (int j = 0; j < this.wordsCountInertie; j++)
            {
                if (i != j) //distances between two words i and j.
                {
                    gCentre[i] += this.dataSet.instance(j).value(i);

                    gConceptsCentre[clusterInstanceTab[i]][j] +=
                    this.dataSet.instance(i).value(j) * ((double) 1 / this.conceptWordsCount[clusterInstanceTab[i]]);
                }
            }
            gCentre[i] /= this.wordsCountInertie;
        }

        //clusterInstanceTab[i] = this.randomCluster.clusterInstance(this.dataSet.instance(i));
        //this.conceptLists.get(this.clusterInstanceTab[i]).add(this.wordsList.get(i).text);

        //interclass:

        this.interClass = 0;
        for (int i = 0; i < this.conceptCounts; i++)
        {
            for (int j = 0; j < this.wordsCountInertie; j++)
            {
                //euclidian
                this.interClass += Math.pow(gCentre[j] - gConceptsCentre[i][j],
                                            2);
            }
            this.interClass *= this.conceptWordsCount[i];
        }
        this.interClass /= this.conceptCounts;

        double[] intraConcept = new double[this.conceptCounts];
        for (int i = 0; i < this.conceptCounts; i++)
        {
            intraConcept[i] = 0;
        }

        for (int i = 0; i < this.wordsCountInertie; i++)
        {
            for (int j = 0; j < this.wordsCountInertie; j++)
            {
                intraConcept[clusterInstanceTab[i]] +=
                Math.pow(gConceptsCentre[clusterInstanceTab[i]][j]
                         - this.dataSet.instance(i).value(j),
                         2);
            }
        }

        for (int i = 0; i < this.conceptCounts; i++)
        {
            this.intraClass += intraConcept[i];
        }

        this.intraClass /= this.conceptCounts;


//        System.out.println(this.interClass);
//        System.out.println(this.intraClass);


//        for(int i = 0; i < this.conceptCounts; i++)
//        {
//            for(int j = 0; j < wordsList.size(); j++)
//            System.out.println(gConceptsCentre[i][j]);
//        }

//        System.exit(-1);

    }
}
