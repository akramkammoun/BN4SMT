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
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

//stemmers:
import org.tartarus.snowball.*;

/**
 *
 * @author akram
 */
public class TextProcessing
{

    //gived by user
    public File textFile;
    public String textLang;
    public String phraseInDelimiter;
    public String phraseOutDelimiter = " ";
    public int wordsCount;
    public ArrayList<Word> wordsList;//initialized in extract words
    //for sentences filter
    public int minWords = 0;//get only
    public int maxWords = 0;//get only
    public int minFreq = 0;//get only
    public int maxFreq = 0;//get only
    private File stopWordsFile;//identified automatically by the textLang
    private String tokenDelimiter;//identified automatically by the textLang
    private SnowballStemmer stemmer = null;//identified automatically by the textLang
    //
    //used to delete the not alpha word in the text
    private String regex;
    public File wordsOrigStemFile;
    //tokens for texts delimiter not for words tokenizer
    public static String englishToken = "tokenizers/english.pickle";
    public static String frenshToken = "tokenizers/french.pickle";
    public static String spanishToken = "tokenizers/spanish.pickle";
    public static String dataDir = "data/";
    //stop words
    public static String stopWordsEnFilePath = "data/stopwords/stopwords_en.txt";
    public static String stopWordsFrFilePath = "data/stopwords/stopwords_fr.txt";
    public static String stopWordsEsFilePath = "data/stopwords/stopwords_es.txt";
    //scripts
    public static String textDelimiterScriptFilePath = "scripts/sentences_delimiter.py";
    public static String textCleanerScriptFilePath = "scripts/clean-corpus-n.perl";
    //
    //for tokens
    public static String textTokeinzerScriptFilePath = "scripts/tokenizer.perl";
    public static String tmpDir = "/tmp/";

    public TextProcessing(File textFile,
                          String textLang,
                          String phraseInDelimiter)
    {
        this.phraseInDelimiter = phraseInDelimiter;

        this.textFile = textFile;
        this.textLang = textLang;
        String stemLang = "";


        if (textLang.toLowerCase().equals("en"))
        {
            this.stopWordsFile = new File(TextProcessing.stopWordsEnFilePath);
            this.tokenDelimiter = TextProcessing.englishToken;
            stemLang = "english";
            this.regex = "([a-z])+";
//            this.stemmer = new PorterStemmer();
        }
        else if (textLang.toLowerCase().equals("fr"))
        {
            this.stopWordsFile = new File(TextProcessing.stopWordsFrFilePath);
            this.tokenDelimiter = TextProcessing.frenshToken;
            stemLang = "french";
            this.regex = "[a-zàâçèéêîôùû]+";
//            this.stemmer = new FrenchStemmer();
        }
        else if (textLang.toLowerCase().equals("es"))
        {
            this.stopWordsFile = new File(TextProcessing.stopWordsEsFilePath);
            this.tokenDelimiter = TextProcessing.spanishToken;
            stemLang = "spanish";
            this.regex = "([a-záéíóúñü])+";
//            this.stemmer = new SpanishStemmer();
        }
        else
        {
            System.out.print("language dosen't recongnized or not supported");
            System.exit(-1);
        }

        try
        {
            Class stemClass = Class.forName(
                    "org.tartarus.snowball.ext." + stemLang + "Stemmer");
            stemmer = (SnowballStemmer) stemClass.newInstance();
        }
        catch (ClassNotFoundException ex)
        {
            Logger.getLogger(TextProcessing.class.getName()).log(Level.SEVERE,
                                                                 null,
                                                                 ex);
        }
        catch (InstantiationException ex)
        {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE,
                                                       null,
                                                       ex);
        }
        catch (IllegalAccessException ex)
        {
            Logger.getLogger(TextProcessing.class.getName()).log(Level.SEVERE,
                                                                 null,
                                                                 ex);
        }
    }

    public void textCleaner(String fileOutPath,
                            String fileOutPathOtherInstance,
                            int minWords,
                            int maxWords,
                            TextProcessing otherInstance)
    {
        this.minWords = minWords;
        this.maxWords = maxWords;
        //algorithme

        File fileOut = new File(fileOutPath);
        File fileOutOtherInstance = new File(fileOutPathOtherInstance);

        fileOut.delete();
        fileOutOtherInstance.delete();

        String cmdArgs[] =
        {
            "/usr/bin/perl",
            TextProcessing.textCleanerScriptFilePath,
            this.textFile.getAbsolutePath(), //corpus1 in
            otherInstance.textFile.getAbsolutePath(), //corpus2 in
            fileOut.getAbsolutePath(), //corpus 1 out
            fileOutOtherInstance.getAbsolutePath(), //corpus2 out
            Integer.toString(minWords),
            Integer.toString(maxWords)
        };


        Functions.execExtProg(cmdArgs);

        //change files
        this.textFile = fileOut;
        otherInstance.textFile = fileOutOtherInstance;


    }

    public void textDelimiter(String fileOutPath)
    {
        File fileOut = new File(fileOutPath);

        fileOut.delete();

        String cmdArgs[] =
        {
            "/usr/bin/python",
            TextProcessing.textDelimiterScriptFilePath, //script
            this.textFile.getAbsolutePath(), //arg1 in
            fileOut.getAbsolutePath(), //arg2 out
            TextProcessing.dataDir, //arg3 data/
            this.tokenDelimiter
        }; //arg4 token

        Functions.execExtProg(cmdArgs);

        //change file
        this.textFile = fileOut;

    }

    public void textTokenizer(String fileOutPath)
    {
        File fileOut = new File(fileOutPath);

        fileOut.delete();

        String cmdArgsLine =
               "/bin/cat" + " "
               + this.textFile.getAbsolutePath() + " "
               + "|" + " "
               + TextProcessing.textTokeinzerScriptFilePath + " "
               + "-l" + " "
               + this.textLang + " "
               + "-w" + " "
               + TextProcessing.dataDir + " "
               + ">" + " "
               + fileOut.getAbsolutePath();

        //put the cmd into a file and then after execute it
        String tokenizerScriptTmp = TextProcessing.tmpDir + "tokenizer.sh";

        OutputStreamWriter os;
        BufferedWriter bw;


        (new File(tokenizerScriptTmp)).delete();
        try
        {

            os = new OutputStreamWriter(new FileOutputStream(tokenizerScriptTmp),
                                        "UTF-8");
            bw = new BufferedWriter(os);

            bw.write(cmdArgsLine);

            bw.close();
            os.close();

        }
        catch (IOException ex)
        {
            Logger.getLogger(TextProcessing.class.getName()).log(Level.SEVERE,
                                                                 null,
                                                                 ex);
        }

        String[] argsTab =
        {
            "/bin/sh", tokenizerScriptTmp
        };
        Functions.execExtProg(argsTab);

        //change file
        this.textFile = fileOut;
    }

    public void textStemmer(String stemmedFilePath,
                            String wordsOrigStemFilePath)
    {
        File stemmedFile = new File(stemmedFilePath);
        wordsOrigStemFile = new File(wordsOrigStemFilePath);

        InputStreamReader is;
        BufferedReader br;

        OutputStreamWriter os;
        BufferedWriter bw;

        OutputStreamWriter os2;
        BufferedWriter bw2;

        ArrayList<String> wordsOrigList = new ArrayList<String>();

        stemmedFile.delete();
        wordsOrigStemFile.delete();


        try
        {
            is = new InputStreamReader(new FileInputStream(this.textFile),
                                       "UTF-8");
            br = new BufferedReader(is);

            os = new OutputStreamWriter(new FileOutputStream(stemmedFile),
                                        "UTF-8");
            bw = new BufferedWriter(os);

            os2 = new OutputStreamWriter(new FileOutputStream(wordsOrigStemFile),
                                         "UTF-8");
            bw2 = new BufferedWriter(os2);

            String line = "";

            while ((line = br.readLine()) != null)
            {
                String[] lineArray = line.split(" ");
                int wordNum;
                String stemmerCurrent;

                for (wordNum = 0; wordNum < lineArray.length - 1; wordNum++)
                {
                    this.stemmer.setCurrent(lineArray[wordNum]);
                    this.stemmer.stem();
                    stemmerCurrent = this.stemmer.getCurrent();

                    bw.write(stemmerCurrent + " ");

                    if (!wordsOrigList.contains(lineArray[wordNum]))
                    {
                        wordsOrigList.add(lineArray[wordNum]);
                        bw2.write(
                                lineArray[wordNum] + " " + stemmerCurrent + "\n");
                    }
                }
                //we treat the las word alone

                this.stemmer.setCurrent(lineArray[wordNum]);
                this.stemmer.stem();
                stemmerCurrent = this.stemmer.getCurrent();
                bw.write(stemmerCurrent + "\n");
                if (!wordsOrigList.contains(lineArray[wordNum]))
                {
                    wordsOrigList.add(lineArray[wordNum]);
                    bw2.write(lineArray[wordNum] + " " + stemmerCurrent + "\n");
                }
            }

            br.close();
            is.close();

            bw2.close();
            os2.close();

            bw.close();
            os.close();

            this.textFile = stemmedFile;

        }
        catch (IOException ex)
        {
            Logger.getLogger(TextProcessing.class.getName()).log(Level.SEVERE,
                                                                 null,
                                                                 ex);
        }
    }

    public void textStemmer2(String stemmedFilePath,
                             String wordsOrigStemFilePath)
    {
        File stemmedFile = new File(stemmedFilePath);
        wordsOrigStemFile = new File(wordsOrigStemFilePath);

        InputStreamReader is;
        BufferedReader br;

        OutputStreamWriter os;
        BufferedWriter bw;

        OutputStreamWriter os2;
        BufferedWriter bw2;


        ArrayList<WordsOrigStem> wordsStemmedOrignsLists = new ArrayList<WordsOrigStem>();

        stemmedFile.delete();
        wordsOrigStemFile.delete();


        try
        {
            is = new InputStreamReader(new FileInputStream(this.textFile),
                                       "UTF-8");
            br = new BufferedReader(is);

            os = new OutputStreamWriter(new FileOutputStream(stemmedFile),
                                        "UTF-8");
            bw = new BufferedWriter(os);

            os2 = new OutputStreamWriter(new FileOutputStream(wordsOrigStemFile),
                                         "UTF-8");
            bw2 = new BufferedWriter(os2);

            String line = "";

            while ((line = br.readLine()) != null)
            {
                String[] lineArray = line.split(" ");
                int wordNum;
                String stemmerCurrent;

                ArrayList<String> phraseList = new ArrayList<String>();

                for (wordNum = 0; wordNum < lineArray.length; wordNum++)
                {
                    this.stemmer.setCurrent(lineArray[wordNum]);
                    this.stemmer.stem();
                    stemmerCurrent = this.stemmer.getCurrent();

                    phraseList.add(stemmerCurrent);

                    boolean wordOrigFound = false;

                    for (int i = 0; i < wordsStemmedOrignsLists.size() && wordOrigFound == false; i++)
                    {
                        if (wordsStemmedOrignsLists.get(i).wordStemmed.equals(
                                stemmerCurrent))
                        {
                            wordOrigFound = true;
                            boolean wordStemmedFound = false;

                            if (wordsStemmedOrignsLists.get(i).wordsOrigns.contains(
                                    lineArray[wordNum]))
                            {
                                wordStemmedFound = true;
                            }
                            else
                            {
                                wordsStemmedOrignsLists.get(i).wordsOrigns.add(
                                        lineArray[wordNum]);
                            }
                        }
                    }

                    if (wordOrigFound == false)
                    {
                        ArrayList<String> stemmedList = new ArrayList<String>();
                        stemmedList.add(lineArray[wordNum]);
                        wordsStemmedOrignsLists.add(
                                new WordsOrigStem(stemmerCurrent,
                                                  stemmedList));
                    }
                }
                bw.write(Functions.listToString(phraseList,
                                                " ") + "\n");

            }



            for (int i = 0; i < wordsStemmedOrignsLists.size(); i++)
            {
                bw2.write(wordsStemmedOrignsLists.get(i).wordStemmed + "\t");

                bw2.write(Functions.listToString(
                        wordsStemmedOrignsLists.get(i).wordsOrigns,
                                                 " ") + "\n");
            }

            br.close();
            is.close();

            bw2.close();
            os2.close();

            bw.close();
            os.close();

            this.textFile = stemmedFile;

        }
        catch (IOException ex)
        {
            Logger.getLogger(TextProcessing.class.getName()).log(Level.SEVERE,
                                                                 null,
                                                                 ex);
        }
    }

    public void replaceDelim(String fileOutPath,
                             String oldDelim,
                             String newDelim)
    {
        File fileOut = new File(fileOutPath);

        InputStreamReader is;
        BufferedReader br;

        OutputStreamWriter os;
        BufferedWriter bw;

        String line;

        fileOut.delete();

        try
        {

            is = new InputStreamReader(new FileInputStream(this.textFile),
                                       "UTF-8");
            br = new BufferedReader(is);

            os = new OutputStreamWriter(new FileOutputStream(fileOut),
                                        "UTF-8");
            bw = new BufferedWriter(os);


            while ((line = br.readLine()) != null)
            {
                bw.write(line.replaceAll(oldDelim,
                                         newDelim) + "\n");
            }

            br.close();
            is.close();

            bw.close();
            os.close();

            this.textFile = fileOut;
        }
        catch (IOException ex)
        {
            Logger.getLogger(TextProcessing.class.getName()).log(Level.SEVERE,
                                                                 null,
                                                                 ex);
        }
    }

    public void textLowerCase(String fileOutPath)
    {

        File fileOut = new File(fileOutPath);

        InputStreamReader is;
        BufferedReader br;

        OutputStreamWriter os;
        BufferedWriter bw;

        String line;

        fileOut.delete();

        try
        {

            is = new InputStreamReader(new FileInputStream(this.textFile),
                                       "UTF-8");
            br = new BufferedReader(is);

            os = new OutputStreamWriter(new FileOutputStream(fileOut),
                                        "UTF-8");
            bw = new BufferedWriter(os);


            while ((line = br.readLine()) != null)
            {
                bw.write(line.toLowerCase() + "\n");
            }

            br.close();
            is.close();

            bw.close();
            os.close();

            this.textFile = fileOut;
        }
        catch (IOException ex)
        {
            Logger.getLogger(TextProcessing.class.getName()).log(Level.SEVERE,
                                                                 null,
                                                                 ex);
        }

    }

    public void textStopWordsDeleter(String fileOutPath)
    {
        File fileOut = new File(fileOutPath);


        ArrayList<String> stopWordsList = new ArrayList<String>();
        ArrayList<String> notSWList;
        String[] wordsStringArray;
        String line;
        int wordNum;


        InputStreamReader is;
        BufferedReader br;

        InputStreamReader isSW;
        BufferedReader brSW;

        OutputStreamWriter os;
        BufferedWriter bw;

        fileOut.delete();

        try
        {
            is = new InputStreamReader(new FileInputStream(this.textFile),
                                       "UTF-8");
            br = new BufferedReader(is);

            isSW = new InputStreamReader(new FileInputStream(this.stopWordsFile),
                                         "UTF-8");
            brSW = new BufferedReader(isSW);

            os = new OutputStreamWriter(new FileOutputStream(fileOut),
                                        "UTF-8");
            bw = new BufferedWriter(os);





            //load stopWordsfile in stopWordsList
            while ((line = brSW.readLine()) != null)
            {
                stopWordsList.add(line);
            }

            while ((line = br.readLine()) != null)
            {
//                line = line.trim();
                wordsStringArray = line.split(this.phraseInDelimiter);
                notSWList = new ArrayList<String>();

                //locate the not stop words
                for (wordNum = 0; wordNum < wordsStringArray.length; wordNum++)
                {
                    if (!stopWordsList.contains(wordsStringArray[wordNum]))
                    {
                        notSWList.add(wordsStringArray[wordNum]);
                    }
                }

                //write the not stop words
                if (!notSWList.isEmpty())//in case when the li is empty or has only stopwords
                {
                    //write the not stop words
                    for (wordNum = 0; wordNum < notSWList.size() - 1; wordNum++)
                    {
                        bw.write(
                                notSWList.get(wordNum) + this.phraseOutDelimiter);
                    }
                    bw.write(notSWList.get(wordNum) + "\n");
                }
                else
                {
                    bw.write("\n");
                }

            }

            bw.close();
            os.close();

            brSW.close();
            isSW.close();

            br.close();
            is.close();

            this.textFile = fileOut;

        }
        catch (IOException ex)
        {
            Logger.getLogger(TextProcessing.class.getName()).log(Level.SEVERE,
                                                                 null,
                                                                 ex);
        }
    }

    public void textLineUnique(String fileOutPath)
    {

        File fileOut = new File(fileOutPath);

        InputStreamReader is;
        BufferedReader br;

        OutputStreamWriter os;
        BufferedWriter bw;

        fileOut.delete();

        try
        {
            is = new InputStreamReader(new FileInputStream(this.textFile),
                                       "UTF-8");
            br = new BufferedReader(is);

            os = new OutputStreamWriter(new FileOutputStream(fileOut),
                                        "UTF-8");
            bw = new BufferedWriter(os);

            String line = "";
            String[] wordsStringArray;
            ArrayList<String> wordsUniqueList;


            while ((line = br.readLine()) != null)
            {
                wordsUniqueList = new ArrayList<String>();
                //extract words in array and then put them into ArrayList without repetition
                wordsStringArray = line.split(this.phraseInDelimiter);
                for (int i = 0; i < wordsStringArray.length; i++)
                {

                    if (!wordsUniqueList.contains(wordsStringArray[i]))
                    {
                        wordsUniqueList.add(wordsStringArray[i]);
                    }
                }

                if (!wordsUniqueList.isEmpty())
                {
                    //put the unique words into the file
                    for (int i = 0; i < wordsUniqueList.size() - 1; i++)
                    {
                        bw.write(
                                wordsUniqueList.get(i) + this.phraseOutDelimiter);
                    }
                    bw.write(
                            wordsUniqueList.get(wordsUniqueList.size() - 1) + "\n");
                }
                else
                {
                    bw.write("\n");
                }
            }

            br.close();
            is.close();

            bw.close();
            os.close();

            this.textFile = fileOut;

        }
        catch (IOException ex)
        {
            Logger.getLogger(TextProcessing.class.getName()).log(Level.SEVERE,
                                                                 null,
                                                                 ex);
        }
    }

    public void textNotAlphaDeleter(String fileOutPath)
    {
        File fileOut = new File(fileOutPath);

        ArrayList<String> alphaWordsList;
        String[] wordsStringArray;
        String line;
        int wordNum;

        InputStreamReader is;
        BufferedReader br;

        OutputStreamWriter os;
        BufferedWriter bw;

        fileOut.delete();

        try
        {
            is = new InputStreamReader(new FileInputStream(this.textFile),
                                       "UTF-8");
            br = new BufferedReader(is);

            os = new OutputStreamWriter(new FileOutputStream(fileOut),
                                        "UTF-8");
            bw = new BufferedWriter(os);

            while ((line = br.readLine()) != null)
            {
                line = line.trim();
                wordsStringArray = line.split(this.phraseOutDelimiter);
                alphaWordsList = new ArrayList<String>();

                //locate the alpha words
                for (wordNum = 0; wordNum < wordsStringArray.length; wordNum++)
                {
                    if (wordsStringArray[wordNum].matches(this.regex))
                    {
                        alphaWordsList.add(wordsStringArray[wordNum]);
                    }
                }

                if (!alphaWordsList.isEmpty())//in case when the list is empty or has only not alphawords
                {
                    //write the alpha words
                    for (wordNum = 0; wordNum < alphaWordsList.size() - 1; wordNum++)
                    {
                        bw.write(
                                alphaWordsList.get(wordNum) + this.phraseOutDelimiter);
                    }
                    bw.write(alphaWordsList.get(wordNum) + "\n");
                }
                else
                {
                    bw.write("\n");
                }

            }

            br.close();
            is.close();

            bw.close();
            os.close();

            this.textFile = fileOut;
        }
        catch (IOException ex)
        {
            Logger.getLogger(TextProcessing.class.getName()).log(Level.SEVERE,
                                                                 null,
                                                                 ex);
        }
    }

    public void textFreqMinDeleter(String fileOutPath,
                                   int freqMin,
                                   int freqMax)
    {
        File fileOut = new File(fileOutPath);

        extractWords();

        this.minFreq = freqMin;
        this.maxFreq = freqMax;

        String line;
        String[] wordsStringArray;
        int indexWord;
        int wordNum;


        InputStreamReader is;
        BufferedReader br;

        OutputStreamWriter os;
        BufferedWriter bw;

        fileOut.delete();
        try
        {
            is = new InputStreamReader(new FileInputStream(this.textFile),
                                       "UTF-8");
            br = new BufferedReader(is);

            os = new OutputStreamWriter(new FileOutputStream(fileOut),
                                        "UTF-8");
            bw = new BufferedWriter(os);

            while ((line = br.readLine()) != null)
            {
                wordsStringArray = line.split(this.phraseInDelimiter);
                ArrayList<String> lineWordsAcceptedList = new ArrayList<String>();

                for (wordNum = 0; wordNum < wordsStringArray.length; wordNum++)
                {
                    Word word = new Word(wordsStringArray[wordNum]);
                    indexWord = this.wordsList.indexOf(word);

                    if (this.wordsList.get(indexWord).count >= freqMin
                        && this.wordsList.get(indexWord).count <= freqMax)
                    {
                        lineWordsAcceptedList.add(wordsStringArray[wordNum]);
                    }
                }

                if (!lineWordsAcceptedList.isEmpty())//in case when the list is empty or has only not acceptedWords
                {
                    //write the accepted words
                    for (wordNum = 0; wordNum < lineWordsAcceptedList.size() - 1; wordNum++)
                    {
                        bw.write(
                                lineWordsAcceptedList.get(wordNum) + this.phraseOutDelimiter);
                    }
                    bw.write(lineWordsAcceptedList.get(wordNum) + "\n");
                }
                else
                {
                    bw.write("\n");
                }
            }

            br.close();
            is.close();

            bw.close();
            os.close();

            this.textFile = fileOut;
        }
        catch (IOException ex)
        {
            Logger.getLogger(TextProcessing.class.getName()).log(Level.SEVERE,
                                                                 null,
                                                                 ex);
        }
    }

    public void deleteWords(String wordsFilePath,
                            String fileOutPath)
    {
        InputStreamReader is;
        BufferedReader br;

        File fileOut = new File(fileOutPath);

        OutputStreamWriter os;
        BufferedWriter bw;

        ArrayList<String> wordsDeleteList = Functions.fileToList(new File(
                wordsFilePath));

        fileOut.delete();

        try
        {
            is = new InputStreamReader(new FileInputStream(this.textFile),
                                       "UTF-8");
            br = new BufferedReader(is);

            os = new OutputStreamWriter(new FileOutputStream(fileOutPath),
                                        "UTF-8");
            bw = new BufferedWriter(os);

            String line = "";

            while ((line = br.readLine()) != null)
            {
                ArrayList<String> wordsExits = new ArrayList<String>();
                String[] lineArray = line.split(" ");

                for (int i = 0; i < lineArray.length; i++)
                {
                    if (wordsDeleteList.contains(lineArray[i]))
                    {
                        wordsExits.add(lineArray[i]);
                    }
                }
                bw.write(Functions.listToString(wordsExits,
                                                " ") + "\n");
            }


            br.close();
            is.close();

            bw.close();
            os.close();

            this.textFile = fileOut;
        }
        catch (IOException ex)
        {
            Logger.getLogger(Functions.class.getName()).log(Level.SEVERE,
                                                            null,
                                                            ex);
        }
    }

    public void extractWords()
    {
        InputStreamReader is;
        BufferedReader br;

        try
        {
            is = new InputStreamReader(new FileInputStream(this.textFile),
                                       "UTF-8");
            br = new BufferedReader(is);

            String line;
            String[] wordsStringArray;
            Word word;

            this.wordsList = new ArrayList<Word>();

            //exctract words and their count
            while ((line = br.readLine()) != null)
            {
                //extract words in array and then put them into ArrayList without repetition
                wordsStringArray = line.split(this.phraseInDelimiter);
                for (int i = 0; i < wordsStringArray.length; i++)
                {
                    word = new Word(wordsStringArray[i]);
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

            this.wordsCount = this.wordsList.size();

            br.close();
            is.close();

        }
        catch (IOException ex)
        {
            Logger.getLogger(TextProcessing.class.getName()).log(Level.SEVERE,
                                                                 null,
                                                                 ex);
        }
    }

//    public void deleteAllButTextFile()
//    {
//        ArrayList<File> allFileList = new ArrayList<File>();
//
//        allFileList.add(this.getAlphaFile());
//        allFileList.add(this.getCleandFile());
//        allFileList.add(this.getDelimitedFile());
//        allFileList.add(this.getEmptyCleanedFile());
//        allFileList.add(this.getFreqMinFile());
//        allFileList.add(this.getLowerCaseFile());
//        allFileList.add(this.getNotStopWordsFile());
//        allFileList.add(this.getStemmedFile());
//        allFileList.add(this.getTokenizedFile());
//        allFileList.add(this.getUniqueFile());
//
//        for(int i = 0; i < allFileList.size(); i++)
//        {
//            if(!allFileList.get(i).getName().equals(this.textFile.getName()))
//                allFileList.get(i).delete();
//        }
//    }
    public void exportWords(String fileOutPath)
    {

        File fileOut = new File(fileOutPath);

        fileOut.delete();

        OutputStreamWriter os;
        BufferedWriter bw;
        try
        {
            os = new OutputStreamWriter(new FileOutputStream(fileOut),
                                        "UTF-8");
            bw = new BufferedWriter(os);

//            bw.write("word\tcount\n");

            for (int i = 0; i < this.wordsList.size(); i++)
            {
                bw.write(this.wordsList.get(i).text + "\t"
                         + this.wordsList.get(i).count + "\n");
            }

            bw.close();
            os.close();

        }
        catch (IOException ex)
        {
            Logger.getLogger(TextProcessing.class.getName()).log(Level.SEVERE,
                                                                 null,
                                                                 ex);
        }
    }

    public void exportWords2(String fileOutPath)
    {
        //sans occurence mots
        File fileOut = new File(fileOutPath);

        fileOut.delete();

        OutputStreamWriter os;
        BufferedWriter bw;
        try
        {
            os = new OutputStreamWriter(new FileOutputStream(fileOut),
                                        "UTF-8");
            bw = new BufferedWriter(os);

//            bw.write("word\tcount\n");

            for (int i = 0; i < this.wordsList.size(); i++)
            {
                bw.write(this.wordsList.get(i).text + "\n");
            }

            bw.close();
            os.close();

        }
        catch (IOException ex)
        {
            Logger.getLogger(TextProcessing.class.getName()).log(Level.SEVERE,
                                                                 null,
                                                                 ex);
        }
    }

    class WordsOrigStem
    {

        public String wordStemmed;
        public ArrayList<String> wordsOrigns = new ArrayList<String>();

        public WordsOrigStem(String wordStemmed,
                             ArrayList<String> wordsOrigns)
        {
            this.wordStemmed = wordStemmed;
            this.wordsOrigns = wordsOrigns;
        }
    }
}
