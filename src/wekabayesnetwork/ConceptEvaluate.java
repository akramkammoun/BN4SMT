/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package wekabayesnetwork;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author akram
 */
public class ConceptEvaluate
{
    //E_j = 1 - (n_j / |ph_j|)

    public File corpusFile;
    public File conceptFile;
    public String corpusDelimiter = " ";
    public String conceptDelimiter = " ";
    public ArrayList<ArrayList<String>> conceptLists;
    private ArrayList<ConceptError> conceptError;
    public double wordsPositionError;
    public double wordsPositionPercentError;//E[E_j] = 1 - \sum_j (n_j) / \sum_j |ph_j|
    public double wordsCountError;
    public double wordsCountPercentError;
    public double wordsOccurenceCorpusCount;
    double entropyError = 0;
    double entropyErrorMax = 0;
    public double fMesure = 0;
    public double EMoyMesure = 0;//E[E_j] = 1 - 1/N \sum_j (n_j / |ph_j|)
    public double intraClass = 0;
    public double interClass = 0;
    public int wordsCount = 0;

    public ConceptEvaluate(String corpusFilePath,
                           String conceptFilePath)
    {
        this.corpusFile = new File(corpusFilePath);
        this.conceptFile = new File(conceptFilePath);

        this.conceptLists = Functions.fileToLists(this.conceptFile,
                                                  this.conceptDelimiter);

        entropyErrorMax = 0;
        entropyError = 0;
        fMesure = 0;
        EMoyMesure = 0;
    }

    public double wordsOccurenceTextCount()
    {
        int wordsCount = 0;

        InputStreamReader is;
        BufferedReader br;

        String line;

        try
        {

            is = new InputStreamReader(new FileInputStream(this.corpusFile),
                                       "UTF-8");
            br = new BufferedReader(is);

            while ((line = br.readLine()) != null)
            {
                wordsCount += line.split(this.corpusDelimiter).length;
            }

        }
        catch (IOException ex)
        {
            Logger.getLogger(Corpus.class.getName()).log(Level.SEVERE,
                                                         null,
                                                         ex);
        }

        return wordsCount;
    }

    public void conceptEvaluation()
    {
        this.conceptError = new ArrayList<ConceptError>();

        InputStreamReader is;
        BufferedReader br;

        String line;

        int errorEntropyTab[] = new int[Functions.getFileLinesCount(
                this.corpusFile.getAbsolutePath())];
        int errorEntropySum = 0;

        try
        {

            is = new InputStreamReader(new FileInputStream(this.corpusFile),
                                       "UTF-8");
            br = new BufferedReader(is);

            int corpusLine = 0;

            this.EMoyMesure = 0;

            while ((line = br.readLine()) != null)
            {
                corpusLine++;

                String[] lineStringArray = line.split(this.corpusDelimiter);

                int wordsFoundCount = 0;

                int conceptsFoundCount = 0;

                for (int conceptNum = 0; conceptNum < this.conceptLists.size()
                                         && wordsFoundCount != lineStringArray.length; conceptNum++)
                {

                    ConceptError conceptErrorTmp = new ConceptError();
                    conceptErrorTmp.corpusLine = corpusLine;
                    conceptErrorTmp.conceptLine = conceptNum + 1;
                    conceptErrorTmp.wordsRevoquedList = new ArrayList<String>();

                    ArrayList<String> wordsFoundedList = new ArrayList<String>();

                    boolean conceptFound = false;
                    for (int wordNum = 0; wordNum < lineStringArray.length; wordNum++)
                    {

                        //if the word exist in the concept
                        if (this.conceptLists.get(conceptNum).contains(
                                lineStringArray[wordNum]))
                        {
                            wordsFoundedList.add(lineStringArray[wordNum]);
                            wordsFoundCount++;


                            if (conceptFound == false)
                            {
                                conceptsFoundCount++;
                            }
                            conceptFound = true;
                        }
                    }

                    if (wordsFoundedList.size() >= 2)
                    {
                        conceptErrorTmp.wordUsed = wordsFoundedList.
                                get(wordsFoundedList.size() - 1);

                        for (int i = 0; i < wordsFoundedList.size() - 1; i++)
                        {
                            conceptErrorTmp.wordsRevoquedList.add(wordsFoundedList.get(
                                    i));
                        }

                        conceptErrorTmp.wordsRevoquedCount = wordsFoundedList.size() - 1;

                        this.conceptError.add(conceptErrorTmp);

                    }
                    //else there's isn't errors

//                    if(!wordsFoundedList.isEmpty())
//                    {
//                        errorEntropyTab[corpusLine - 1] = wordsFoundedList.size() - 1;
//                        errorEntropySum += errorEntropyTab[corpusLine - 1];
//                    }
//                    else
//                    {
//                        errorEntropyTab[corpusLine - 1] = 0;
//                    }
                }

                this.EMoyMesure += (double) conceptsFoundCount / lineStringArray.length;

                errorEntropyTab[corpusLine - 1] = conceptsFoundCount;
                errorEntropySum += errorEntropyTab[corpusLine - 1];


                double pi = (double) conceptsFoundCount / lineStringArray.length;
                this.entropyError += -1 * pi * Functions.log2(pi);
                this.entropyErrorMax += -1 * ((double) 1 / lineStringArray.length)
                                        * Functions.log2(
                        (double) 1 / lineStringArray.length);



            }

            this.EMoyMesure = 1 - ((double) 1 / Functions.getFileLinesCount(
                                   this.corpusFile.getAbsolutePath())) * this.EMoyMesure;

            //calcl entropy

//            this.entropyError = 0;
//
//            if(errorEntropySum == 0)
//                this.entropyError = 0;
//            else
//            {
//                for(int en = 0; en < errorEntropyTab.length; en++)
//                {
//                    double pi;
//                    if(errorEntropyTab[en] != 0)
//                    {
//                        pi = (double) errorEntropyTab[en]/errorEntropySum;
//                        this.entropyError += pi * Functions.log2(pi);
//                    }
//                }
//
//                this.entropyErrorMax = Functions.log2(Functions.getFileLinesCount(corpusFile.getAbsolutePath()));
//                this.entropyError *= -1;
//            }





            //find error statistiques:

            this.wordsCountError = 0;
            for (int i = 0; i < this.conceptError.size(); i++)
            {
                this.wordsCountError += this.conceptError.get(i).wordsRevoquedCount;
            }
            this.wordsOccurenceCorpusCount = wordsOccurenceTextCount();

            this.wordsPositionError = this.conceptError.size();
            this.wordsPositionPercentError = ((double) this.conceptError.size()
                                              / this.wordsOccurenceCorpusCount);

            this.wordsCountPercentError = ((double) this.wordsCountError
                                           / this.wordsOccurenceCorpusCount);


        }
        catch (IOException ex)
        {
            Logger.getLogger(Corpus.class.getName()).log(Level.SEVERE,
                                                         null,
                                                         ex);
        }
    }

    public void conceptsFMesure(String conceptRefFilePath,
                                int wordsCount)
    {

        ArrayList<ArrayList<String>> conceptRefList =
                                     Functions.fileToLists(new File(
                conceptRefFilePath),
                                                           this.conceptDelimiter);


        this.fMesure = 0;
//        int l = 0;

        for (int a = 0; a < conceptRefList.size(); a++)
        {
            double max = 0, pi;


            pi = (double) conceptRefList.get(a).size() / wordsCount;

            for (int k = 0; k < this.conceptLists.size(); k++)
            {
                int n = 0;
                double r, p, f;

//                for(int m = 1; m < this.conceptLists.get(k).size(); m++)
//                    for(int b = 1; b < conceptRefList.get(a).size(); b++)
//                        if(this.conceptLists.get(k).get(m).equals(conceptRefList.get(a).get(b)))
//                            n++;

                for (int m = 1; m < this.conceptLists.get(k).size(); m++)
                {
                    if (conceptRefList.get(a).contains(this.conceptLists.get(k).get(
                            m)))
                    {
                        n++;
                    }
                }

                r = (double) n / conceptRefList.get(a).size();
                p = (double) n / this.conceptLists.get(k).size();
                if ((r + k) > 0)
                {
                    f = (2 * r * p) / (r + p);
                }
                else
                {
                    f = 0;
                }

                if (f > max)
                {
                    max = f;
                }
//                l += n;
            }
            this.fMesure += (max * pi);
        }

        this.fMesure *= 100;
    }

    public void exportConceptEvaluationFull(String fileOutPath,
                                            String headText)
    {

        BufferedWriter bw;


        try
        {
            FileWriter fileOut = new FileWriter(fileOutPath,
                                                true);

            bw = new BufferedWriter(fileOut);

            if (!headText.isEmpty())
            {
                bw.write(headText + "\n");
            }

            bw.write(
                    "Corpus #\tConcept #\tWord used\tWords revoqued\tWords revoqued count\n");

            for (int i = 0; i < this.conceptError.size(); i++)
            {
                bw.write(
                        this.conceptError.get(i).corpusLine + "\t"
                        + this.conceptError.get(i).conceptLine + "\t"
                        + this.conceptError.get(i).wordUsed + "\t"
                        + Functions.listToString(
                        this.conceptError.get(i).wordsRevoquedList,
                                                 ",") + "\t"
                        + this.conceptError.get(i).wordsRevoquedCount + "\n");
            }

            bw.write(
                    "\t\t\t\t" + this.wordsCountError + "/" + this.wordsOccurenceCorpusCount + "="
                    + this.wordsCountPercentError
                    + "\tWords occurence count dosen't take into account percent (sum)\n");
//            bw.write("\t\t\t\t" + this.wordsPositionError + "\tWords occurence position ambiguity count\n");

            bw.write(
                    "\t\t\t\t" + this.wordsPositionError + "/" + this.wordsOccurenceCorpusCount + "="
                    + this.wordsPositionPercentError + "\tWords occurence ambiguity position percent\n");
            bw.write("\t\t\t\tFMesure\t" + this.fMesure + "\n");
            bw.write("\t\t\t\tinterclasse\t" + this.interClass + "\n");
            bw.write("\t\t\t\tintraclasse\t" + this.intraClass + "\n");
            bw.write("\t\t\t\tEntropy\t" + this.entropyError + "\n");
            bw.write("\t\t\t\tEntropy max\t" + this.entropyErrorMax + "\n\n");

            bw.write(
                    "\n-------\t--------\t--------\t-------\t--------\t--------\t\n\n");

            bw.close();
        }
        catch (IOException ex)
        {
            Logger.getLogger(TextProcessing.class.getName()).log(Level.SEVERE,
                                                                 null,
                                                                 ex);
        }

    }

    public void exportConceptEvaluationMinim(String fileOutPath,
                                             String headText)
    {
        BufferedWriter bw;

        try
        {
            FileWriter fileOut = new FileWriter(fileOutPath,
                                                true);

            bw = new BufferedWriter(fileOut);

            if (!headText.isEmpty())
            {
                bw.write(headText + "\n");
            }

            bw.write("Concepts count\t" + this.conceptLists.size() + "\n");
            bw.write("Words Count\t" + this.wordsCount + "\n");

            bw.write("Words occurence count dosen't take into account percent (sum)\t"
                     + this.wordsCountError + "/" + this.wordsOccurenceCorpusCount
                     + "=" + this.wordsCountPercentError + "\n");

            bw.write("Words occurence ambiguity position percent\t"
                     + this.wordsPositionError + "/" + this.wordsOccurenceCorpusCount
                     + "=" + this.wordsPositionPercentError + "\n");

            bw.write("FMesure\t" + this.fMesure + "\n");
//            bw.write("EMoyMesure\t" + this.EMoyMesure + "\n");
            bw.write("interclasse\t" + this.interClass + "\n");
            bw.write("intraclasse\t" + this.intraClass + "\n");
            bw.write("Entropy\t" + this.entropyError + "\n");
            bw.write("Entropy max\t" + this.entropyErrorMax + "\n\n");

            bw.write(
                    "-------\t--------\t--------\t-------\t--------\t--------\t\n\n");

            bw.close();
        }
        catch (IOException ex)
        {
            Logger.getLogger(TextProcessing.class.getName()).log(Level.SEVERE,
                                                                 null,
                                                                 ex);
        }
    }

    private class ConceptError
    {

        int corpusLine;
        int conceptLine;
        String wordUsed;
        ArrayList<String> wordsRevoquedList;
        int wordsRevoquedCount;
    }
}
