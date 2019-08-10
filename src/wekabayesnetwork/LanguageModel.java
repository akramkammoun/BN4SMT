/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package wekabayesnetwork;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author akram
 */
public class LanguageModel
{

    public File textFile;
    public File lmFile;
    public String lmDirPath;
    public String tmpDirPath = "/tmp/";
    public File conceptFormattedFile;
    public File textConceptFile;
    public String ngramSerie;
    public static int ngramCardMax = 3000;
    public static int wordsCountMaxOrder = 100;

    public LanguageModel(String textFilePath,
                         String lmDirPath)
    {
        this.textFile = new File(textFilePath);
        this.lmDirPath = lmDirPath;
        (new File(this.lmDirPath)).mkdirs();
    }

    public LanguageModel()
    {
    }

    public void buildLanguageModel()
    {
        String vocabPath = this.lmDirPath + "vocab";
        String countPath = this.lmDirPath + "counts";
        String lmPath = this.lmDirPath + "lm";

        this.lmFile = new File(lmPath);

        String cmd = "ngram-count -order 3 -text " + this.textFile + " -write-vocab "
                     + vocabPath + " -write " + countPath;

        Functions.execExtProg(cmd);

        cmd = "ngram-count -read " + countPath + " -vocab " + vocabPath + " -order 3"
              + " -lm " + lmPath + " -gt1min 1 -gt1max 4 -gt2min 1 -gt2max 4 -gt3min 1 -gt3max 4"
              + "";

        Functions.execExtProg(cmd);
    }

    public String lineOrder(String line,
                            String delimiter)
    {
        if (LanguageModel.wordsCountMaxOrder < line.length() || line.length() == 0)
        {
            return line;
        }

        String[] words = line.split(delimiter);

        String[] bestLine = null;
        String[] wordsTmp;

        int ngram;

        this.ngramSerie = "";
        do
        {
            wordsTmp = words;

            ngram = 0;
            boolean stop = false;

            int card = 1;

            for (int i = wordsTmp.length; i > 0 && stop == false; i--)
            {
                card *= i;

                if (card <= LanguageModel.ngramCardMax)
                {
                    ngram++;
                }
                else
                {
                    stop = true;
                }
            }
            ngramSerie += ngram + " ";


            String[][] wordsCombination = extractCombination(words,
                                                             ngram);

            bestLine = extractBestLine(wordsCombination);

            String bestLineStr = "";
            for (int i = 0; i < bestLine.length - 1; i++)
            {
                bestLineStr += bestLine[i] + " ";
            }

            bestLineStr += bestLine[bestLine.length - 1];


            words = new String[wordsTmp.length - ngram + 1];
            int k = 0;
            words[k++] = bestLineStr;

            ArrayList<String> wordsTmpList = new ArrayList<String>();
            wordsTmpList.addAll(Arrays.asList(wordsTmp));

            for (int i = 0; i < bestLine.length; i++)
            {
                boolean found = false;
                for (int j = 0; j < wordsTmpList.size() && found == false; j++)
                {
                    if (wordsTmpList.contains(bestLine[i]))
                    {
                        wordsTmpList.remove(bestLine[i]);
                        found = true;
                    }
                }
            }

            for (int i = 0; i < wordsTmpList.size(); i++)
            {
                words[k++] = wordsTmpList.get(i);
            }


            if (words.length < ngram)
            {
                ngram = words.length;
            }

        }
        while (words.length != 1);



        return words[0];
    }

    public String[] extractBestLine(String[][] wordsCombination)
    {

        String[] result = null;

        String orderFileTmp = this.tmpDirPath + "orderline.txt";
        String cmdFileTmp = this.tmpDirPath + "ordercmd.txt";

        (new File(orderFileTmp)).delete();

        try
        {
            FileWriter fstream = new FileWriter(orderFileTmp,
                                                true);
            BufferedWriter bw = new BufferedWriter(fstream);

            for (int i = 0; i < wordsCombination.length; i++)
            {
                int j = 0;
                for (j = 0; j < wordsCombination[i].length - 1; j++)
                {
                    bw.write(wordsCombination[i][j] + " ");
                }

                bw.write(wordsCombination[i][j] + "\n");
            }


            bw.close();


            //exec
            //ngram -lm lm -ppl p1 -debug 1 | grep "ppl=" | cut -d' ' -f6
            Runtime execCmd = Runtime.getRuntime();
            Process process;
            String cmdArgs = "ngram -order 3 -lm " + this.lmFile.getAbsolutePath()
                             + " -ppl " + orderFileTmp + " -debug 1 | grep ' ppl1= ' | cut -d' ' -f6";



            fstream = new FileWriter(cmdFileTmp);
            bw = new BufferedWriter(fstream);
            bw.write(cmdArgs);
            bw.close();

            String[] argsTab =
            {
                "/bin/sh", cmdFileTmp
            };

            BufferedReader cmdReader = null;
            //
            process = execCmd.exec(argsTab);
            //
            process.waitFor();
            //
            cmdReader = new BufferedReader(new InputStreamReader(
                    process.getInputStream()));
            //
            String cmdOut = "";
            int index = 1;
            int minIndex = 0;

            //0 is empty
            cmdOut = cmdReader.readLine();

            double minValue = 0;

            if (cmdOut.toLowerCase().equals("undefined"))
            {
                minValue = 1999999999;
            }
            else
            {
                minValue = Double.parseDouble(cmdOut);
            }

            while ((cmdOut = cmdReader.readLine()) != null)
            {
                double lineValue = 0;

                if (cmdOut.toLowerCase().equals("undefined"))
                {
                    continue;
                }

                if (!cmdOut.isEmpty())
                {
                    lineValue = Double.parseDouble(cmdOut);
                }

                if (lineValue < minValue)
                {
                    minValue = lineValue;
                    minIndex = index;
                }
                index++;
            }
            //
            cmdReader.close();

            result = wordsCombination[minIndex];


        }
        catch (Exception ex)
        {
            Logger.getLogger(LanguageModel.class.getName()).log(Level.SEVERE,
                                                                null,
                                                                ex);
        }

        return result;
    }

    public String[][] extractCombination(String[] elements,
                                         int ngram)
    {

        int card = 1;
        for (int i = elements.length; i > (elements.length - ngram); i--)
        {
            card *= i;
        }
//        System.out.println(card);
        int compt = 0;
        String[][] result = new String[card][ngram];



//        String[] elements = {"aaa", "b", "c", "d","e"};
        int[] indices;
        CombinationGenerator x = new CombinationGenerator(elements.length,
                                                          ngram);
//        StringBuffer combination;
        while (x.hasMore())
        {
//            combination = new StringBuffer ();
            indices = x.getNext();
            String[] elements2 = new String[indices.length];
            for (int i = 0; i < indices.length; i++)
            {
                elements2[i] = elements[indices[i]];
            }




            PermutationGenerator x2 = new PermutationGenerator(elements2.length);
            while (x2.hasMore())
            {
                int[] indices2;
//                permutation = new StringBuffer ();
                indices2 = x2.getNext();
                int i;
                for (i = 0; i < indices2.length; i++)
                {
                    result[compt][i] = elements2[indices2[i]];
//                    permutation.append (elements2[indices2[i]]);
//                    permutation.append (" ");
                }
                compt++;
//                permutation.append (elements2[indices2[i]]);
//                System.out.println(permutation.toString());

            }
        }

        return result;
    }

    public void conceptFormatted(String conceptFilePath,
                                 String conceptDelimiter,
                                 String conceptFormattedFilePath)
    {
        InputStreamReader is;
        BufferedReader br;

        OutputStreamWriter os;
        BufferedWriter bw;

        this.conceptFormattedFile = new File(conceptFormattedFilePath);

        (this.conceptFormattedFile).delete();

        try
        {
            is = new InputStreamReader(new FileInputStream(conceptFilePath),
                                       "UTF-8");
            br = new BufferedReader(is);

            os = new OutputStreamWriter(new FileOutputStream(
                    this.conceptFormattedFile),
                                        "UTF-8");
            bw = new BufferedWriter(os);

            String line;
            while ((line = br.readLine()) != null)
            {
                String[] lineSplitted = line.split(conceptDelimiter);

                for (int i = 1; i < lineSplitted.length; i++)
                {
                    bw.write(lineSplitted[0] + " 1 " + lineSplitted[i] + "\n");
                }
            }

            br.close();
            is.close();

            bw.close();
            os.close();
        }
        catch (Exception ex)
        {
            Logger.getLogger(LanguageModel.class.getName()).log(Level.SEVERE,
                                                                null,
                                                                ex);
        }
    }

    public void textToConceptText(String textConceptFilePath)
    {
        File textConceptFileTmp = new File(textConceptFilePath);

//        textConceptFileTmp.delete();

        String cmd = "replace-words-with-classes classes=" + this.conceptFormattedFile.getAbsolutePath()
                     + " " + this.textFile.getAbsolutePath() + " > " + textConceptFileTmp.getAbsolutePath();


        OutputStreamWriter os;
        BufferedWriter bw;


        String lmScriptTmp = this.tmpDirPath + "lm.sh";

        (new File(lmScriptTmp)).delete();
        try
        {

            os = new OutputStreamWriter(new FileOutputStream(lmScriptTmp),
                                        "UTF-8");
            bw = new BufferedWriter(os);

            bw.write(cmd);

            bw.close();
            os.close();

        }
        catch (Exception ex)
        {
            Logger.getLogger(LanguageModel.class.getName()).log(Level.SEVERE,
                                                                null,
                                                                ex);
        }



        this.textFile = textConceptFileTmp;
//        System.out.println(cmd);
        String[] argsTab =
        {
            "/bin/sh", lmScriptTmp
        };
        Functions.execExtProg(argsTab);
        //replace-words-with-classes classes=conceptFormatted.txt test.fr.clean.txt > aaa
    }
}
