/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package wekabayesnetwork;

/**
 *
 * @author akram
 */
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import weka.core.Attribute;
import weka.core.FastVector;

/**
 *
 * @author akram
 */
public class ConceptClustring
{

    public File textFile;//file in
    public String phraseDelimiter;
    public short[][] wordsBinMat;//max 32767
    public int wordsCount;
    public int textLinesCount;//max 2 147 483 647
    public ArrayList<Word> wordsList;
    public short[][] wordSquareMat;//max 32767
    public FastVector attributesDataSet;

    public ConceptClustring(String textFilePath,
                            String phraseDelimiter)
    {
        this.textFile = new File(textFilePath);
        this.phraseDelimiter = phraseDelimiter;

        this.textLinesCount = Functions.getFileLinesCount2(textFilePath);

    }

    //********* for all distances and similarity ********//
    //
    public void extractWords()
    {
        InputStreamReader is;
        BufferedReader br;

        this.wordsList = new ArrayList<Word>();

        try
        {
            is = new InputStreamReader(new FileInputStream(this.textFile),
                                       "UTF-8");
            br = new BufferedReader(is);

            String line;
            String[] wordsStringArray;

            //exctract words and thei count and also create attributes for weka
            while ((line = br.readLine()) != null)
            {
                //extract words in array and then put them into ArrayList without repetition
                wordsStringArray = line.split(this.phraseDelimiter);
                for (int i = 0; i < wordsStringArray.length; i++)
                {
                    Word word = new Word(wordsStringArray[i]);
                    if (!this.wordsList.contains(word))
                    {
                        this.wordsList.add(word);
                    }
                    else
                    {
                        this.wordsList.get(this.wordsList.indexOf(word)).count++;
                    }
                }
            }
            br.close();
            is.close();

            this.wordsCount = this.wordsList.size();

        }
        catch (IOException ex)
        {
            Logger.getLogger(ConceptClustring.class.getName()).log(Level.SEVERE,
                                                                   null,
                                                                   ex);
        }
    }
    //

    public void buildDataSetAttributes()
    {
        this.attributesDataSet = new FastVector();

        for (int i = 0; i < this.wordsCount; i++)
        {
            this.attributesDataSet.addElement(new Attribute(
                    this.wordsList.get(i).text));
        }

    }
    //

    public void textBinarizer()
    {

        String line;
        String[] wordsStringArray;
        int indexWord;
        int wordNum;
        int lineNum = 0;

        InputStreamReader is;
        BufferedReader br;

        try
        {
            is = new InputStreamReader(new FileInputStream(this.textFile),
                                       "UTF-8");
            br = new BufferedReader(is);


            this.wordsBinMat = new short[this.textLinesCount][];

            while ((line = br.readLine()) != null)
            {
                wordsStringArray = line.split(this.phraseDelimiter);

                this.wordsBinMat[lineNum] = new short[wordsStringArray.length];

                for (wordNum = 0; wordNum < wordsStringArray.length; wordNum++)
                {
                    indexWord = this.wordsList.indexOf(new Word(
                            wordsStringArray[wordNum]));
                    //put the index of each word of the line in his place
                    this.wordsBinMat[lineNum][wordNum] = (short) indexWord;
                }

                lineNum++;

            }

            br.close();
            is.close();

        }
        catch (IOException ex)
        {
            Logger.getLogger(ConceptClustring.class.getName()).log(Level.SEVERE,
                                                                   null,
                                                                   ex);
        }
    }
    //
//    public void textBinarizer2()
//    {
//
//        RandomAccessFile textRAF;
//        RandomAccessFile binTextRAF;
//        try
//        {
//            this.getBinaryTextFile().delete();
//
//            textRAF = new RandomAccessFile(this.textFile, "r");
//            binTextRAF = new RandomAccessFile(this.getBinaryTextFile(), "rw");
//            String line;
//            String[] wordsStringArray;
//            StringBuilder zeroLine = new StringBuilder();
//            StringBuilder binaryPhrase;
//            int indexWord;
//            int wordNum;
//            int lineNum = 0;
//
//            //intinitialize wordsBinMat[][]
////            this.wordsBinMat = new short[this.textLinesCount][];
//
//            int i;
//            for(i = 0; i < this.wordsList.size(); i++)
//            {
//                zeroLine = zeroLine.append("0,");
//            }
//            zeroLine.setCharAt(i*2 - 1, '\n');
//
//
//            while((line = textRAF.readLine()) != null)
//            {
//                binaryPhrase = new StringBuilder(zeroLine);
//
//                wordsStringArray = line.split(this.phraseDelimiter);
//
//                //intinitialize wordsBinMat[lineNum]
////                this.wordsBinMat[lineNum] = new short[wordsStringArray.length];
//
//                for( wordNum = 0; wordNum < wordsStringArray.length; wordNum++)
//                {
//                    indexWord = this.wordsList.indexOf(new Word(wordsStringArray[wordNum]));
//
//                    //put the index of each word of the line in his place
////                    this.wordsBinMat[lineNum][wordNum] = indexWord;
//
//                    binaryPhrase.setCharAt(indexWord*2, '1');
//                }
//
//                binTextRAF.writeBytes(binaryPhrase.toString());
//
//
//                lineNum++;
//            }
//
//            binTextRAF.close();
//            textRAF.close();
//        }
//        catch (Exception ex)
//        {
//            Logger.getLogger(ConceptClustring.class.getName()).log(Level.SEVERE, null, ex);
//        }
//    }
    //

    public void buildWordSquareMatrix()
    {

        this.wordSquareMat = new short[this.wordsCount][this.wordsCount];

        //init wordSquareMat with zeros
        for (int i = 0; i < this.wordsCount; i++)
        {
            for (int j = 0; j < this.wordsCount; j++) //                this.wordSquareMat[i][j] = 0;
            {
                this.wordSquareMat[i][j] = 0;
            }
        }



        //build wordXword matrix
        for (int i = 0; i < this.textLinesCount; i++)
        {
            for (int j = 0; j < this.wordsBinMat[i].length; j++)
            {
                for (int k = 0; k < this.wordsBinMat[i].length; k++) //                    this.wordSquareMat[this.wordsBinMat[i][j]][this.wordsBinMat[i][k]]++;
                {
                    this.wordSquareMat[this.wordsBinMat[i][j]][this.wordsBinMat[i][k]] =
                    (short) (this.wordSquareMat[this.wordsBinMat[i][j]][this.wordsBinMat[i][k]] + 1);
                }
            }
        }
    }
    //
    //******** for all distance and similarity **************//

    public String getWords()
    {
        String result = "";
        for (int i = 0; i < this.wordsCount; i++)
        {
            result += this.wordsList.get(i).text + " " + this.wordsList.get(i).count + "\n";
        }

        return result;
    }

    public void exportWordSquare(String fileOutPath)
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


            for (int i = 0; i < this.wordSquareMat.length; i++)
            {

                int j;
                for (j = 0; j < this.wordSquareMat[i].length - 1; j++)
                {
                    bw.write(this.wordSquareMat[i][j] + ",");
                }

                bw.write(this.wordSquareMat[i][j] + "\n");

            }

            bw.close();
            os.close();

        }
        catch (IOException ex)
        {
            Logger.getLogger(ConceptClustring.class.getName()).log(Level.SEVERE,
                                                                   null,
                                                                   ex);
        }
        catch (Exception ex)
        {
            Logger.getLogger(ConceptClustring.class.getName()).log(Level.SEVERE,
                                                                   null,
                                                                   ex);
        }
    }

    public void exportWordsAsConcept(String fileOutPath,
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

            //
            for (int i = 0; i < this.wordsList.size(); i++)
            {
                bw.write("c" + (i + 1) + "word" + lang + conceptDelimiter);
                bw.write(this.wordsList.get(i).text.toString() + "\n");
            }
            bw.close();
            os.close();
        }
        catch (IOException ex)
        {
            Logger.getLogger(ConceptClustring.class.getName()).log(Level.SEVERE,
                                                                   null,
                                                                   ex);
        }
        catch (Exception ex)
        {
            Logger.getLogger(ConceptClustring.class.getName()).log(Level.SEVERE,
                                                                   null,
                                                                   ex);
        }
    }
}
