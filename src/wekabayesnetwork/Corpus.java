/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package wekabayesnetwork;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import java.io.IOException;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author akram
 */
public final class Corpus
{
    //given by user

    public String lang;//language used
    public File corpusFile;
    public File conceptFile;
    public String distanceName;
    public String corpusDelimiter = " ";
    public String conceptDelimiter = " ";
    //attributes static
//    public static byte corpusCount = 0;//max 127
    //begin with 0 and after it can be incremented but not unincremented
    //take corpusCount value
    private int corpusLinesCount;//max 2 147 483 647
    private int conceptLinesCount;//max 32767
    InputStreamReader conceptISR;
    BufferedReader conceptBR;
    InputStreamReader corpusISR;
    BufferedReader corpusBR;
    public ArrayList<ArrayList<String>> conceptLists = new ArrayList<ArrayList<String>>();

    public Corpus(String corpusFilePath,
                  String conceptFilePath,
                  String distanceName,
                  String lang)
    {
        this.corpusFile = new File(corpusFilePath);
        this.conceptFile = new File(conceptFilePath);
        this.distanceName = distanceName;
        this.lang = lang;

        this.corpusLinesCount = Functions.getFileLinesCount(
                corpusFile.getAbsolutePath());
        this.conceptLinesCount = Functions.getFileLinesCount(
                conceptFile.getAbsolutePath());

        //increment the corpus count
//        Corpus.corpusCount++;
//        this.idCorpus = Corpus.corpusCount;

        this.conceptLists = getConceptList();
    }

    //********** begin get attributes *************//
    //
    //files paths
    public String getLang()
    {
        return this.lang;
    }
    //

    public File getCorpusFile()
    {
        return this.corpusFile;
    }
    //

    public File getConceptFile()
    {
        return this.conceptFile;
    }
    //
    //get numeric

    public int getCorpusLinesCount()
    {
        return this.corpusLinesCount;
    }
    //

    public int getConceptLinesCount()
    {
        return this.conceptLinesCount;
    }
    //
//    public static short getCorpusCount()
//    {
//        return Corpus.corpusCount;
//    }
    //

    public String getCorpusDelimiter()
    {
        return this.corpusDelimiter;
    }
    //

    public String getConceptDelimiter()
    {
        return this.conceptDelimiter;
    }
    //
    //********** end get attributes *************//

    //********* set attirbutes ***********//
    //
    public void setCorpusDelimiter(String value)
    {
        this.corpusDelimiter = value;
    }
    //

    public void setConceptDelimiter(String value)
    {
        this.conceptDelimiter = value;
    }
    //
    //********* set attirbutes ***********//

    //********* open close get RandomAccessFile **********//
    //
    public void openCorpusFile() throws IOException
    {
        this.corpusISR = new InputStreamReader(new FileInputStream(
                this.corpusFile),
                                               "UTF-8");
        this.corpusBR = new BufferedReader(this.corpusISR);
    }
    //

    public void closeCorpusFile() throws IOException
    {
        this.corpusBR.close();
        this.corpusISR.close();
    }
    //

    public BufferedReader getCorpusBR()
    {

        return this.corpusBR;
    }
    //
    //concept

    public void openConceptFile() throws IOException
    {
        this.conceptISR = new InputStreamReader(new FileInputStream(
                this.conceptFile),
                                                "UTF-8");
        this.conceptBR = new BufferedReader(this.conceptISR);
    }
    //

    public void closeConceptFile() throws IOException
    {
        this.conceptBR.close();
        this.conceptISR.close();
    }
    //

    public BufferedReader getConceptBR()
    {
        return this.conceptBR;
    }
    //
    //********* open close get RandomAccessFile **********//

    public ArrayList<ArrayList<String>> getConceptList()
    {
        if (!this.conceptLists.isEmpty())
        {
            return this.conceptLists;
        }

        ArrayList<ArrayList<String>> lists = new ArrayList<ArrayList<String>>();

        InputStreamReader is;
        BufferedReader br;
        String line;

        try
        {
            is = new InputStreamReader(new FileInputStream(this.conceptFile),
                                       "UTF-8");
            br = new BufferedReader(is);

            while ((line = br.readLine()) != null)
            {

                String[] concepts = line.split(this.conceptDelimiter);

                ArrayList<String> listNum = new ArrayList<String>();
                for (int i = 1; i < concepts.length; i++)
                {
                    listNum.add(concepts[i]);
                }
                lists.add(listNum);
            }


        }
        catch (IOException ex)
        {
            Logger.getLogger(Corpus.class.getName()).log(Level.SEVERE,
                                                         null,
                                                         ex);
        }
        catch (Exception ex)
        {
            Logger.getLogger(Corpus.class.getName()).log(Level.SEVERE,
                                                         null,
                                                         ex);
        }


        return lists;
    }
}
