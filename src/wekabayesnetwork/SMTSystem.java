/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package wekabayesnetwork;

import wekabayesnetwork.MyWeka.MyMarginCalculator;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;
import wekabayesnetwork.Distances.*;
import wekabayesnetwork.MyWeka.MyEditableBayesNet;
import wekabayesnetwork.Options.*;

/**
 *
 * @author akram
 */
public class SMTSystem
{

    public static void dataTesting(String myProjectDir,
                                   String projectName,
                                   TestingOptionsList tol,
                                   String textInFilePath,
                                   String textEvalFilePath,
                                   TrainingOptions[] to)
    {

        String testDir = myProjectDir + "testing/";
        String usrDir = testDir + "usr/";
        String evalDir = myProjectDir + "evaluating/";
        String tmpDir = myProjectDir + "tmp/";


        //************ project *************//
        //
        (new File(myProjectDir)).mkdir();
        (new File(evalDir)).mkdir();
        (new File(testDir)).mkdir();
        (new File(usrDir)).mkdir();
        (new File(tmpDir)).mkdir();
        //
        //************ project ************//



        //************ text in eval ************//
        //
        String textInUsrFilePath = usrDir + (new File(textInFilePath)).getName() + ".in.txt";
        String textEvalUsrFilePath = usrDir + (new File(textEvalFilePath)).getName() + ".eval.txt";
        //
        Functions.copyFile(textInFilePath,
                           textInUsrFilePath);
        Functions.copyFile(textEvalFilePath,
                           textEvalUsrFilePath);
        //
        //
        //********************* text processing **************************//
        //
        String tpDir[] = new String[2];
        tpDir[0] = usrDir + "test/";
        tpDir[1] = usrDir + "eval/";
        to[0].textFile = new File(textInUsrFilePath);
        to[1].textFile = new File(textEvalUsrFilePath);
        System.out.println("text processing...");
        for (int i = 0; i < 2; i++)
        {
            to[i].tp = new TextProcessing(to[i].textFile,
                                          to[i].lang,
                                          " +");
            (new File(tpDir[i])).mkdir();
        }
        //



        if (TrainingOptions.textCleaner == true)
        {
            to[0].tp.textCleaner(
                    tpDir[0] + TrainingOptions.id + "." + to[0].lang + ".clean.txt",
                    tpDir[1] + TrainingOptions.id + "." + to[1].lang + ".clean.txt",
                    TrainingOptions.minWordsPhrase,
                    TrainingOptions.maxWordsPhrase,
                    to[1].tp);//first cleaning
        }
        //
        for (int i = 0; i < 2; i++)
        {
            String idText = TrainingOptions.id + "." + to[i].lang;

            if (to[i].replaceDelim == true)
            {
                to[i].tp.replaceDelim(tpDir[i] + idText + ".replacedelim.txt",
                                      to[i].phraseDelimiter,
                                      " ");
                to[i].phraseDelimiter = " ";
            }

            if (to[i].textLowerCase == true)
            {
                to[i].tp.textLowerCase(tpDir[i] + idText + ".lower.txt");//1
            }
            if (to[i].textTokenizer == true)
            {
                to[i].tp.textTokenizer(tpDir[i] + idText + ".token.txt");//2
            }
            if (to[i].textDelimiter == true)
            {
                to[i].tp.textDelimiter(
                        tpDir[i] + TrainingOptions.id + ".delim.txt");
            }

            if (to[i].textNotAlphaDeleter == true)
            {
                to[i].tp.textNotAlphaDeleter(tpDir[i] + idText + ".alpha.txt");//3
            }
            if (to[i].textStopWordsDeleter == true)
            {
                to[i].tp.textStopWordsDeleter(
                        tpDir[i] + idText + ".stopwords.txt");//4
            }
            if (to[i].textStemmer == true)
            {
                to[i].tp.textStemmer(tpDir[i] + idText + ".stem.txt",
                                     tpDir[i] + idText + ".words.origstem.txt");//5
            }
            if (to[i].textLineUnique == true)
            {
                to[i].tp.textLineUnique(tpDir[i] + idText + ".unique.txt");//6
            }//
            if (to[i].textFreqMinDeleter == true)
            {
                to[i].tp.textFreqMinDeleter(tpDir[i] + idText + ".freq.txt",
                                            to[i].minFreqWord,
                                            to[i].maxFreqWord);
            }

            if (tol.deleteUnknowenWords == true)
            {
                to[i].tp.deleteWords(tol.wordsFilePath[i],
                                     tpDir[i] + idText + ".delwords.txt");
            }
        }

        if (TrainingOptions.textCleaner == true)
        {
            //tp1 and tp2
            to[0].tp.textCleaner(
                    tpDir[0] + TrainingOptions.id + "." + to[0].lang + ".clean.txt",
                    tpDir[1] + TrainingOptions.id + "." + to[1].lang + ".clean.txt",
                    TrainingOptions.minWordsPhrase,
                    TrainingOptions.maxWordsPhrase,
                    to[1].tp);//second cleaning
        }

        to[0].tp.extractWords();
        System.out.println(to[0].tp.wordsList.size());
        to[1].tp.extractWords();
        System.out.println(to[1].tp.wordsList.size());


        textInUsrFilePath = to[0].tp.textFile.getAbsolutePath();
        textEvalUsrFilePath = to[1].tp.textFile.getAbsolutePath();
        ArrayList<String> textInList = Functions.fileToStrings(textInUsrFilePath);
        //
        //************ text in eval ************//


//                //for concept to words
//            File fileXml2 = tol.testingOptions.get(0).bayesnetFile;
//            File fileConfXml2 = tol.testingOptions.get(0).bayesnetConfFile;
//            File bayesNetMarginFile2 = tol.testingOptions.get(0).bayesMarginFile;
//            TranslatingLine tr2 = new TranslatingLine();
//            tr2.loadBayesNet(fileXml2.getAbsolutePath(), fileConfXml2.getAbsolutePath(),
//                bayesNetMarginFile2.getAbsolutePath());
//            tr2.conceptsToWordsTranslation("/home/akram/Bureau/bleu3/a", "/home/akram/Bureau/bleu3/b");
//            double a = TranslateEvaluating.evalFMesure(textEvalUsrFilePath, "/home/akram/Bureau/bleu3/b");
//            System.out.println(a);
//            TranslateEvaluating.evalBLEU(textInUsrFilePath,
//                textEvalUsrFilePath, "/home/akram/Bureau/bleu3/b", "/home/akram/Bureau/bleu3/");
//        //for concept to words
//
//        System.exit(-1);

        if (tol.useBN == true)
        {

            System.out.println("bayesnet evaluation...");

            String fileEval = evalDir + projectName + ".translate.eval.txt";
            (new File(fileEval)).delete();
            //
            //
            String outHead =
                   "line #\t"
                   + "line in\t"
                   + "classifier name\t"
                   + "parents count\t"
                   + "distance text source\t"
                   + "distance text target\t"
                   + "bayesnet mode\t"
                   + "set evidence all source\t"
                   + "search in all target\t"
                   + "line translated\t"
                   + "Line ordered\t"
                   + "ngram\t"
                   + "line evaluate\t"
                   + "words missed\t"
                   + "words missed count\t"
                   + "words furthermore\t"
                   + "words furthermore count\t"
                   + "words dosen't exist\t"
                   + "words dosen't exist count"
                   + //                + "\tbayesnet path"
                    //                + "\tbayesnet conf path"
                    "\n";
            BufferedWriter bw = null;
            //
            try
            {
                FileWriter fileOut = new FileWriter(fileEval);
                bw = new BufferedWriter(fileOut);
//            bw.write("text in path\t" + textInUsrFilePath + "\n");
//            bw.write("text eval path\t" + textEvalUsrFilePath + "\n\n");
                bw.write(outHead);

                bw.close();


            }
            catch (IOException ex)
            {
                Logger.getLogger(SMTSystem.class.getName()).log(Level.SEVERE,
                                                                null,
                                                                ex);
            }


            for (int j = 0; j < tol.testingOptions.size(); j++)
            {

                String evalFilePathConcept = usrDir + projectName + "."
                                             + tol.testingOptions.get(j).distanceName2 + ".textconcept.txt";

                if (!tol.testingOptions.get(j).distanceName2.equals("word") && tol.testingOptions.get(
                        j).binaryTrg == true)
                {
                    TranslatingLine.textToTextConcept(textEvalUsrFilePath,
                                                      tol.testingOptions.get(j).conceptTrgFile.getAbsolutePath(),
                                                      " ",
                                                      evalFilePathConcept);
                }
                else
                {
                    evalFilePathConcept = textEvalUsrFilePath;
                }

                ArrayList<String> textEvalList = Functions.fileToStrings(
                        evalFilePathConcept);

                File fileXml = tol.testingOptions.get(j).bayesnetFile;
                File fileConfXml = tol.testingOptions.get(j).bayesnetConfFile;
                File bayesNetMarginFile = tol.testingOptions.get(j).bayesMarginFile;

                boolean setEvidence[] = tol.setEvidence;
                boolean findInAllwords[] = tol.findInAllwords;

                for (int k = 0; k < setEvidence.length; k++)//setEvidence |null, false| = 2;
                {
                    for (int l = 0; l < findInAllwords.length; l++)//findAllwords |null, false| = 2;
                    {
                        String fullDir = testDir + "translating/";
                        (new File(fullDir)).mkdir();
                        //
                        String fullFileName = projectName + ".";

                        fullDir += tol.testingOptions.get(j).distanceName1
                                   + "." + tol.testingOptions.get(j).distanceName2 + "/";//distance dir
                        fullFileName += tol.testingOptions.get(j).distanceName1
                                        + "." + tol.testingOptions.get(j).distanceName2 + ".";

                        fullDir += tol.testingOptions.get(j).bayesCmdName + "/";//cmd name
                        fullFileName += tol.testingOptions.get(j).bayesCmdName + ".";

                        fullDir += tol.testingOptions.get(j).cmdOption + "/";//cmd option
                        fullFileName += tol.testingOptions.get(j).cmdOption + ".";

                        fullDir += setEvidence[k] + "." + findInAllwords[l] + "." + tol.alpha + "/";
                        fullFileName += setEvidence[k] + "." + findInAllwords[l] + "." + tol.alpha;

                        (new File(fullDir)).mkdirs();
                        String translateFilePath = fullDir + fullFileName + ".translate.txt";
                        (new File(translateFilePath)).delete();

                        System.out.println(fullDir);

                        for (int i = 0; i < textInList.size(); i++)
                        {

                            System.out.println((i + 1) + " ");

                            String bayesNetMarginPath;
                            String bayesNetGraphPath;
                            String bayesNetConfPath;
                            if (tol.inferenceAllWords == false)
                            {
                                //************ calc margins *************//
                                //
                                bayesNetMarginPath = tmpDir + "margin.bin";
                                bayesNetGraphPath = tmpDir + "graph.xml";
                                bayesNetConfPath = tmpDir + "graphconf.xml";
                                try
                                {
                                    MyEditableBayesNet subBN =
                                                       TranslatingLine.buildSubBn(textInList.get(
                                            i),
                                                                                  tol.testingOptions.get(
                                            j).conceptSrcFile.getAbsolutePath(),
                                                                                  fileXml.getAbsolutePath(),
                                                                                  fileConfXml.getAbsolutePath(),
                                                                                  bayesNetGraphPath,
                                                                                  bayesNetConfPath);

                                    MyMarginCalculator myMarginCalculator = new MyMarginCalculator();

                                    myMarginCalculator.calcMargins(subBN);


                                    //
                                    myMarginCalculator.m_root.m_parentSeparator = null;
                                    myMarginCalculator.m_root.m_fi = null;
                                    myMarginCalculator.m_root.m_MarginalP = null;
                                    myMarginCalculator.m_root.m_bayesNet.m_Distributions = null;
                                    //
                                    // serialize model
                                    weka.core.SerializationHelper.write(
                                            bayesNetMarginPath,
                                            myMarginCalculator);
                                    //
                                    myMarginCalculator = null;
                                }
                                catch (Exception ex)
                                {
                                    Logger.getLogger(SMTSystem.class.getName()).log(
                                            Level.SEVERE,
                                            null,
                                            ex);
                                }
                                //
                                //************ calc margins *************//
                            }
                            else
                            {
                                bayesNetGraphPath = fileXml.getAbsolutePath();
                                bayesNetConfPath = fileConfXml.getAbsolutePath();
                                bayesNetMarginPath = bayesNetMarginFile.getAbsolutePath();
                            }

                            TranslatingLine tr = new TranslatingLine();
                            tr.loadBayesNet(bayesNetGraphPath,
                                            bayesNetConfPath,
                                            bayesNetMarginPath);

                            tr.translateLine(textInList.get(i),
                                             " ",
                                             setEvidence[k],
                                             findInAllwords[l],
                                             tol.testingOptions.get(j).conceptSrcFile.getAbsolutePath(),
                                             tol.alpha,
                                             tol.setEvidenceZero);
                            //
//                        if(i==13)
//                        System.exit(-1);
                            ArrayList<String> lineEvalFormat;

                            lineEvalFormat = Functions.StringToList(
                                    textEvalList.get(i),
                                    " ");

                            tr.evaluateTranslation(lineEvalFormat);

                            tr.bayesNetMargin = null;

                            LanguageModel lm1 = new LanguageModel();
                            lm1.lmFile = tol.testingOptions.get(j).lmFile;
                            lm1.tmpDirPath = tmpDir;

                            String transOrder = "";

                            if (!tr.wordsTranslatedList.isEmpty())
                            {
                                transOrder = lm1.lineOrder(
                                        Functions.listToString(
                                        tr.wordsTranslatedList,
                                        " "),
                                        " ");
                            }


                            int countWordsTextEval = textEvalList.get(i).split(
                                    " ").length;

                            try
                            {
                                FileWriter fileOut = new FileWriter(fileEval,
                                                                    true);
                                bw = new BufferedWriter(fileOut);
                                String out =
                                       i + "\t"
                                       + textInList.get(i) + "\t"
                                       + tol.testingOptions.get(j).bayesCmdName + "\t"
                                       + tol.testingOptions.get(j).parentCount + "\t"
                                       + tol.testingOptions.get(j).distanceName1 + "\t"
                                       + tol.testingOptions.get(j).distanceName2 + "\t"
                                       + tol.testingOptions.get(j).cmdOption + "\t"
                                       + setEvidence[k] + "\t"
                                       + findInAllwords[l] + "\t"
                                       + Functions.listToString(
                                        tr.wordsTranslatedList,
                                        " ") + "\t"
                                       + transOrder + "\t"
                                       + lm1.ngramSerie + "\t"
                                       + textEvalList.get(i) + "\t"
                                       + Functions.listToString(
                                        tr.missedWordsList,
                                        " ") + "\t"
                                       + tr.missedWordsList.size() + "/" + countWordsTextEval + "\t"
                                       + Functions.listToString(
                                        tr.furthermoreWordsList,
                                        " ") + "\t"
                                       + tr.furthermoreWordsList.size() + "\t"
                                       + Functions.listToString(
                                        tr.wordsNotTranslatedList,
                                        " ") + "\t"
                                       + tr.wordsNotTranslatedList.size()
                                       + //                                "\t" + fileXml.getAbsolutePath() +
                                        //                                "\t" + fileConfXml.getAbsolutePath() +
                                        "\n";

                                bw.write(out);

                                bw.close();

//                            String fileLineOut = setEvidFindWordsPath + xmlFilePath + ".txt";
//                            BufferedWriter bw2;
//                            FileWriter fileOut2 = new FileWriter(fileLineOut);
//                            bw2 = new BufferedWriter(fileOut2);
//
//                            bw2.write(outHead);
//                            bw2.write(out);
//                            bw2.close();

                                BufferedWriter bw3;
                                FileWriter fileOut3 = new FileWriter(
                                        translateFilePath,
                                        true);
                                bw3 = new BufferedWriter(fileOut3);
                                bw3.write(transOrder + "\n");
                                bw3.close();

                            }
                            catch (IOException ex)
                            {
                                Logger.getLogger(SMTSystem.class.getName()).log(
                                        Level.SEVERE,
                                        null,
                                        ex);
                            }
//                        tr.exportBayesNetXml(setEvidFindWordsPath + xmlFilePath + ".xml");
//                        tr.deleteUnusedSourceNodes();
//                        tr.exportBayesNetXml(setEvidFindWordsPath + xmlFilePath + ".delunused.xml");
                        }
                        TranslateEvaluating.evalBLEU(textInUsrFilePath,
                                                     evalFilePathConcept,
                                                     translateFilePath,
                                                     fullDir);
                        //
                        double FMesure = TranslateEvaluating.evalFMesure(
                                translateFilePath,
                                evalFilePathConcept);
                        System.out.println("\nFMesure: " + FMesure);
                        try
                        {
                            BufferedWriter bw4;
                            FileWriter fileOut4 = new FileWriter(
                                    fullDir + "evaluation.txt",
                                    true);
                            bw4 = new BufferedWriter(fileOut4);
                            bw4.write("\nFMesure: " + FMesure + "\n");
                            bw4.close();
                        }
                        catch (Exception ex)
                        {
                            Logger.getLogger(SMTSystem.class.getName()).log(
                                    Level.SEVERE,
                                    null,
                                    ex);
                        }
                    }
                }
            }

        }

        //************ moses ************//
        //
        //in: fr
        //out: en
        if (tol.useMoses == true)
        {
            System.out.println("moses training...");

            String mosesDir = new File(myProjectDir).getAbsolutePath() + "/moses/";
            (new File(mosesDir)).mkdirs();
            String textOutMoses;
            textOutMoses = tol.textFileTrg.getAbsolutePath();

            String projectRoot = new File("").getAbsolutePath() + "/";
            Moses moses = new Moses(projectRoot,
                                    mosesDir,
                                    tol.textFileSrc.getAbsolutePath(),
                                    textOutMoses,
                                    tol.ibmModelCount);
            moses.execLM();
            moses.execMT();
            moses.execDecoder();

            MosesOptions mosesOptions = new MosesOptions(moses.projectRoot,
                                                         moses.mosesFile);
            tol.mosesOption = mosesOptions;
            System.out.println("");
        }
        //
        //************ moses ************//


        //************ moses ************//
        //
        //in: fr
        //out: en
        if (tol.useMoses == true)
        {
            System.out.println("moses evaluation...");
            String textRefMoses;

            textRefMoses = textEvalUsrFilePath;

            Moses moses = new Moses(tol.mosesOption.projectRoot,
                                    new File(tol.mosesOption.mosesFile));

            moses.FileTraduction(textInUsrFilePath);
            moses.traductionEvaluation(textRefMoses);
        }
        //
        //************ moses ************//


    }

    public static void dataTraining(String myProjectDir,
                                    TestingOptionsList tol,
                                    TrainingOptions[] to)
    {

        //*********** useroptions **********//
        //
        String trainDir = myProjectDir + "training/";
        String corpusDir = trainDir + "corpus/";
        String evalDir = myProjectDir + "evaluating/";
        String tmpDir = myProjectDir + "tmp/";
        String lmDir = trainDir + "languagemodel/";
        //
        //*********** useroptions **********//





        //************ project *************//
        //
        (new File(myProjectDir)).mkdir();
        (new File(trainDir)).mkdir();
        (new File(evalDir)).mkdir();
        (new File(tmpDir)).mkdir();
        (new File(lmDir)).mkdir();
        //
        //************ project ************//





        //********* project tree **************//
        //
        String myCorpusDir[] = new String[2];
        String tpDir[] = new String[2];
        String wordsDir[] = new String[2];
        String conceptDir[] = new String[2];
        (new File(corpusDir)).mkdir();
        for (int i = 0; i < 2; i++)
        {
            String idText = TrainingOptions.id + "." + to[i].lang;
            myCorpusDir[i] = corpusDir + idText + "/";
            tpDir[i] = myCorpusDir[i] + "textprocessing/";
            wordsDir[i] = myCorpusDir[i] + "words/";
            conceptDir[i] = myCorpusDir[i] + "concepts/";
            (new File(myCorpusDir[i])).mkdir();
        }
        //
        //********* project tree **************//






        //********************* text processing **************************//
        //
        System.out.println("text processing...");
        TextProcessing.tmpDir = tmpDir;
        File textFileOrig = to[1].textFile;
        for (int i = 0; i < 2; i++)
        {
            to[i].tp = new TextProcessing(to[i].textFile,
                                          to[i].lang,
                                          " +");
            (new File(tpDir[i])).mkdir();
            (new File(wordsDir[i])).mkdir();

            //just for choices Options
//            to[i].corpusLinesCountInit = Functions.getFileLinesCount(to[i].textFile.getAbsolutePath());
//            to[i].tp.extractWords();
//            to[i].wordsCountInit = to[i].tp.wordsCount;
//            to[i].tp.wordsList = null;
        }

        //
        if (TrainingOptions.textCleaner == true)
        {
            to[0].tp.textCleaner(
                    tpDir[0] + TrainingOptions.id + "." + to[0].lang + ".clean.txt",
                    tpDir[1] + TrainingOptions.id + "." + to[1].lang + ".clean.txt",
                    TrainingOptions.minWordsPhrase,
                    TrainingOptions.maxWordsPhrase,
                    to[1].tp);//first cleaning
        }
        //
        for (int i = 0; i < 2; i++)
        {
            String idText = TrainingOptions.id + "." + to[i].lang;

            if (to[i].replaceDelim == true)
            {
                to[i].tp.replaceDelim(tpDir[i] + idText + ".replacedelim.txt",
                                      to[i].phraseDelimiter,
                                      " ");
                to[i].phraseDelimiter = " ";
            }

            if (to[i].textLowerCase == true)
            {
                to[i].tp.textLowerCase(tpDir[i] + idText + ".lower.txt");//1
            }
            if (to[i].textTokenizer == true)
            {
                to[i].tp.textTokenizer(tpDir[i] + idText + ".token.txt");//2
            }
            if (to[i].textDelimiter == true)
            {
                to[i].tp.textDelimiter(
                        tpDir[i] + TrainingOptions.id + ".delim.txt");
            }

            if (to[i].textNotAlphaDeleter == true)
            {
                to[i].tp.textNotAlphaDeleter(tpDir[i] + idText + ".alpha.txt");//3
            }
            if (to[i].textStopWordsDeleter == true)
            {
                to[i].tp.textStopWordsDeleter(
                        tpDir[i] + idText + ".stopwords.txt");//4
            }
            if (to[i].textStemmer == true)
            {
                to[i].tp.textStemmer(tpDir[i] + idText + ".stem.txt",
                                     wordsDir[i] + idText + ".words.origstem.txt");//5
            }
            if (to[i].textLineUnique == true)
            {
                to[i].tp.textLineUnique(tpDir[i] + idText + ".unique.txt");//6
            }
            if (to[i].textFreqMinDeleter == true)
            {
                to[i].tp.textFreqMinDeleter(tpDir[i] + idText + ".freq.txt",
                                            to[i].minFreqWord,
                                            to[i].maxFreqWord);
            }

        }

        if (TrainingOptions.textCleaner == true)
        {
            //tp1 and tp2
            to[0].tp.textCleaner(
                    tpDir[0] + TrainingOptions.id + "." + to[0].lang + ".clean.txt",
                    tpDir[1] + TrainingOptions.id + "." + to[1].lang + ".clean.txt",
                    TrainingOptions.minWordsPhrase,
                    TrainingOptions.maxWordsPhrase,
                    to[1].tp);//second cleaning
        }
        //
        for (int i = 0; i < 2; i++)
        {
            to[i].corpusLinesCountFinal = Functions.getFileLinesCount(
                    to[i].tp.textFile.getAbsolutePath());
            String idText = TrainingOptions.id + "." + to[i].lang;
            to[i].textFile = to[i].tp.textFile;
            to[i].phraseDelimiter = to[i].tp.phraseOutDelimiter;
//            if(to[i].tp.wordsList.isEmpty())
            to[i].tp.extractWords();
            tol.wordsFilePath[i] = wordsDir[i] + idText + ".words.txt";
            to[i].tp.exportWords2(wordsDir[i] + idText + ".words.txt");
            to[i].tp.exportWords(wordsDir[i] + idText + ".words2.txt");
//            to[i].wordsFileIn = new File(wordsDir[i] + idText + ".words.txt")
            to[i].wordsCount = to[i].tp.wordsCount;
//            to[i].tp = null;
            System.out.println(
                    "  words count " + to[i].lang + ": " + to[i].wordsCount);
        }
        //
        //*********** text processing ****************//


        tol.testingOptions = new ArrayList<TestingOptions>();
        tol.textFileSrc = to[0].textFile;
        tol.textFileTrg = to[1].textFile;




        //*********** text processing for LM ****************//
        //
        TextProcessing tpLM = new TextProcessing(textFileOrig,
                                                 to[1].lang,
                                                 " ");
        //
        String idNameTextLM = lmDir + TrainingOptions.id + "." + to[1].lang;
        tpLM.textLowerCase(idNameTextLM + ".lower.txt");//1
        tpLM.textTokenizer(idNameTextLM + ".token.txt");//2
        tpLM.textNotAlphaDeleter(idNameTextLM + ".alpha.txt");//3
        //        tpLM.textFile = to[1].textFile;
        //
        //*********** text processing for LM ****************//




        if (tol.useBN == true)
        {



            //********** words Stemmer **************//
            //
            TextProcessing[] textConceptTP = new TextProcessing[to.length];
            String[] wordsStemmedPath = new String[to.length];

            for (int i = 0; i < 2; i++)
            {

                wordsStemmedPath[i] = conceptDir[i] + "words.stem.txt";
                //
                if (to[i].conceptStemmed == true)
                {
                    textConceptTP[i] = new TextProcessing(to[i].textFile,
                                                          to[i].lang,
                                                          to[i].phraseDelimiter);
                    wordsStemmedPath[i] = conceptDir[i] + "words.stem.txt";
                    //
                    (new File(conceptDir[i])).mkdirs();
                    textConceptTP[i].textStemmer2(conceptDir[i] + "stem.txt",
                                                  wordsStemmedPath[i]);
                    System.out.println(conceptDir[i] + "stem.txt");
                    textConceptTP[i].textLineUnique(
                            conceptDir[i] + "stem.uniq.txt");
                }
                else
                {
                    textConceptTP[i] = to[i].tp;
                }

                to[i].cc = new ConceptClustring(
                        textConceptTP[i].textFile.getAbsolutePath(),
                        to[i].phraseDelimiter);
                //
                System.out.println("    words extracting ...");
                if (to[i].conceptStemmed == true)
                {
                    System.out.print(
                            "      words stemmed: " + to[i].wordsCount + " -> ");
                    to[i].cc.extractWords();
                    System.out.println(to[i].cc.wordsCount);
                }
                else
                {
                    to[i].cc.wordsList = to[i].tp.wordsList;
                    to[i].cc.wordsCount = to[i].tp.wordsCount;
                }
            }
            //
            //********** words Stemmer **************//




            for (int i = 0; i < 2; i++)
            {
                for (int j = 0; j < to[i].distancesOrder.size(); j++)
                {
                    to[i].co.add(null);
                }
            }


            //********** word ***********//
            //
            for (int i = 0; i < 2; i++)
            {
                if (to[i].distancesOrder.contains("word") == true)
                {
                    (new File(conceptDir[i])).mkdirs();
                    String idText = TrainingOptions.id + "." + to[i].lang;
                    String lmDirConcept;
                    LanguageModel lm;
                    String lmFilePathTrg = "";
                    if (i == 1)
                    {
                        lmDirConcept = lmDir + idText + "/word/";
                        lm = new LanguageModel(tpLM.textFile.getAbsolutePath(),
                                               lmDirConcept);
                        lm.buildLanguageModel();
                        lmFilePathTrg = lm.lmFile.getAbsolutePath();
                    }
                    //

                    WordDistance distanceWord = new WordDistance(
                            to[i].cc.textLinesCount,
                            to[i].cc.wordsList,
                            null,
                            null,
                            1);

                    distanceWord.buildDataSet();

                    if (to[i].conceptStemmed == true)
                    {

                        distanceWord.stemToOrigWords(wordsStemmedPath[i]);

                        //to return the intial words list  to[i].tp.wordsList in to[i].distancesList.get(j).wordsList
                        distanceWord.wordsList = to[i].tp.wordsList;
                    }

                    if (to[i].splitConceptCount > 1)
                    {
                        System.out.println(
                                "concepts splited: " + to[i].splitConceptCount);
                        System.out.println(
                                "noeuds count before split: " + distanceWord.conceptLists.size());
                        distanceWord.splitConcept(to[i].splitConceptCount);
//                    to[i].distancesList.get(0).reorderConcept();
                        System.out.println(
                                "noeuds count after split: " + distanceWord.conceptLists.size());
                    }

                    String conceptFilePath = conceptDir[i] + idText + "." + distanceWord.distanceName + ".txt";
                    distanceWord.exportConcept(conceptFilePath,
                                               to[i].conceptDelimiter,
                                               to[i].lang);

                    int coOrder = to[i].distancesOrder.indexOf("word");
                    to[i].co.set(coOrder,
                                 new ConceptOptions(conceptFilePath,
                                                    "word",
                                                    to[i].cc.wordsCount,
                                                    lmFilePathTrg));
                }
            }
            //
            //********** word **********//





            //************* concepts ***************//
            //
            System.out.println("concepts clustring...");
            String evalMinim = evalDir + TrainingOptions.id + ".conceptsEvaluating.minim.txt";
            String evalFull = evalDir + TrainingOptions.id + ".conceptsEvaluating.full.txt";
            (new File(evalMinim)).delete();
            (new File(evalFull)).delete();
            //
            for (int i = 0; i < 2; i++)
            {
                if (to[i].distancesOrder.contains("entropie") == true || to[i].distancesOrder.contains(
                        "imm") == true
                    || to[i].distancesOrder.contains("dkl") == true)
                {

                    //
                    int conceptCount;
                    (new File(conceptDir[i])).mkdir();
                    //
                    String idText = TrainingOptions.id + "." + to[i].lang;
                    //
                    System.out.println(
                            "  " + to[i].lang + ": " + "concepts preparation (WordSquareMatrix) ...");
                    //
                    //
                    System.out.println("    build DataSet Attributes ...");
                    to[i].cc.buildDataSetAttributes();
                    System.out.println("    text Binarizer ...");
                    to[i].cc.textBinarizer();
                    System.out.println("    build Word Square Matrix ...");
                    to[i].cc.buildWordSquareMatrix();
                    to[i].cc.wordsBinMat = null;
                    //
                    //            to[i].co = new ArrayList<ConceptOptions>();
                    conceptCount = (int) (to[i].wordsCount / TrainingOptions.conceptCountMoyen);//for all distances

                    to[i].distancesList = new ArrayList<Distance>();
                    //
                    if (to[i].distancesOrder.contains("dkl") == true)
                    {
                        to[i].distancesList.add(new DKL(to[i].cc.textLinesCount,
                                                        to[i].cc.wordsList,
                                                        to[i].cc.wordSquareMat,
                                                        to[i].cc.attributesDataSet,
                                                        conceptCount));
                    }

                    if (to[i].distancesOrder.contains("imm") == true)
                    {
                        to[i].distancesList.add(new IMM(to[i].cc.textLinesCount,
                                                        to[i].cc.wordsList,
                                                        to[i].cc.wordSquareMat,
                                                        to[i].cc.attributesDataSet,
                                                        conceptCount));
                    }

                    if (to[i].distancesOrder.contains("entropie") == true)
                    {
                        to[i].distancesList.add(new Entropie(
                                to[i].cc.textLinesCount,
                                to[i].cc.wordsList,
                                to[i].cc.wordSquareMat,
                                to[i].cc.attributesDataSet,
                                conceptCount));
                    }





                    for (int j = 0; j < to[i].distancesList.size(); j++)
                    {
                        System.out.println(
                                "  " + to[i].distancesList.get(j).distanceName);
                        to[i].distancesList.get(j).buildDataSet();


                        ArrayList<Word> tmpWord = (ArrayList<Word>) to[i].distancesList.get(
                                j).wordsList.clone();

//                    ArrayList<ClusterType> clusterTypeList = ClusterType.buildClusterTypes(conceptCount);

                        for (int k = 0; k < TrainingOptions.clusteringLearninigOptions.size(); k++)
                        {
                            to[i].distancesList.get(j).wordsList = (ArrayList<Word>) tmpWord.clone();
                            ClusteringLearninigOptions clo = TrainingOptions.clusteringLearninigOptions.get(
                                    k);
                            clo.buildClusterOptions(conceptCount);

                            System.out.println("    run " + clo.clusterName);

                            double memUsed = Functions.getUsedMem();
                            long beginTime = System.currentTimeMillis();

                            to[i].distancesList.get(j).runClustring(
                                    clo.randomizableClusterer,
                                    clo.clusterOptions);

                            System.out.println(
                                    (float) (System.currentTimeMillis() - beginTime));
                            System.out.println(Functions.getUsedMem() - memUsed);

                            to[i].distancesList.get(j).extractConcept();

                            to[i].distancesList.get(j).wordsCountInertie = to[i].distancesList.get(
                                    j).wordsList.size();

                            if (to[i].conceptStemmed == true)
                            {
                                to[i].distancesList.get(j).stemToOrigWords(
                                        wordsStemmedPath[i]);
                                //to return the intial words list  to[i].tp.wordsList in to[i].distancesList.get(j).wordsList
                                to[i].distancesList.get(j).wordsList = to[i].tp.wordsList;

                                to[i].distancesList.get(j).wordsCountInertie = Functions.getFileLinesCount2(
                                        wordsStemmedPath[i]);
                            }

                            to[i].distancesList.get(j).calcInertie();

                            if (to[i].splitConceptCount > 1)
                            {
                                System.out.println(
                                        "concepts splited: " + to[i].splitConceptCount);
                                to[i].distancesList.get(j).splitConcept(
                                        to[i].splitConceptCount);
//                            to[i].distancesList.get(j).reorderConcept();
                            }

                            String distanceName = to[i].distancesList.get(j).distanceName
                                                  + "." + clo.clusterName + "." + clo.distanceName;
                            String conceptFilePath = conceptDir[i] + idText + "." + distanceName + ".txt";
                            to[i].distancesList.get(j).exportConcept(
                                    conceptFilePath,
                                    to[i].conceptDelimiter,
                                    to[i].lang);

                            //
//                        conceptFilePath = "/home/akram/NetBeansProjects/projects/" + distanceName + i + ".txt";
//                        conceptFilePath = "/home/akram/NetBeansProjects/projects/concepts_"
//                            + to[i].lang + "/split." + to[i].lang + ".imm.simplekmeans.EuclideanDistance." +
//                            to[i].splitConceptCount + ".txt";

                            String lmDirConcept;
                            LanguageModel lm;
                            String lmFilePathTrg = "";
                            if (i == 1)
                            {
                                lmDirConcept = lmDir + idText + "/" + distanceName + "/";
                                lm = new LanguageModel(
                                        to[1].textFile.getAbsolutePath(),
                                        lmDirConcept);
                                if (TrainingOptions.binarytrg == true)
                                {
                                    lm.tmpDirPath = tmpDir;
                                    lm.conceptFormatted(conceptFilePath,
                                                        to[i].conceptDelimiter,
                                                        lmDirConcept + idText + ".conceptlm." + distanceName + ".txt");
                                    lm.textToConceptText(
                                            lmDirConcept + idText + ".textconcept." + distanceName + ".txt");
                                }
                                lm.buildLanguageModel();
                                lmFilePathTrg = lm.lmFile.getAbsolutePath();

                            }
                            //

                            int coOrder = to[i].distancesOrder.indexOf(to[i].distancesList.get(
                                    j).distanceName);
                            to[i].co.set(coOrder,
                                         new ConceptOptions(conceptFilePath,
                                                            distanceName,
                                                            conceptCount,
                                                            lmFilePathTrg));

                            //evaluating

                            ConceptEvaluate ce = new ConceptEvaluate(
                                    to[i].textFile.getAbsolutePath(),
                                    conceptFilePath);

                            ce.interClass = to[i].distancesList.get(j).interClass;
                            ce.intraClass = to[i].distancesList.get(j).intraClass;
                            ce.wordsCount = to[i].distancesList.get(j).wordsList.size();

                            ce.conceptEvaluation();

                            if (to[i].conceptRef.isFile())
                            {
                                ce.conceptsFMesure(
                                        to[i].co.get(j).conceptFile.getAbsolutePath(),
                                        to[i].wordsCount);
                            }

                            System.out.println(idText
                                               + ":" + distanceName);
                            ce.exportConceptEvaluationFull(evalFull,
                                                           idText
                                                           + ":" + distanceName);

                            ce.exportConceptEvaluationMinim(evalMinim,
                                                            idText
                                                            + ":" + distanceName);
                            //evaluating
                        }



                        to[i].distancesList.set(j,
                                                null);//free memory
                    }

                    to[i].cc = null;
                    to[i].tp = null;
                }
            }
            //
            //************* concepts ***************//

//        System.exit(-1);



//        //********* evaluate concept ***********//
//        //
//        System.out.println("concept evaluating ...");
//        String evalMinim = evalDir + TrainingOptions.id + ".conceptsEvaluating.minim.txt";
//        String evalFull = evalDir + TrainingOptions.id + ".conceptsEvaluating.full.txt";
//        //
//        (new File(evalMinim)).delete();
//        (new File(evalFull)).delete();
//        //
//        for(int i = 0; i < 2; i++)//textfile
//        {
//            String idText = TrainingOptions.id + "." + to[i].lang;
//            for(int j = 0; j < to[i].co.size(); j++)//concept
//            {
//                System.out.println("  " +to[i].co.get(j).conceptFile.getAbsolutePath());
//                ConceptEvaluate ce = new ConceptEvaluate(to[i].textFile.getAbsolutePath()
//                    , to[i].co.get(j).conceptFile.getAbsolutePath());
//
//                ce.conceptEvaluation();
//
//                if(to[i].conceptRef.isFile())
//                    ce.conceptsFMesure(to[i].co.get(j).conceptFile.getAbsolutePath(), to[i].wordsCount);
//
//                ce.exportConceptEvaluationFull(evalFull, idText +
//                    ":" + to[i].co.get(j).distanceName);
//
//                ce.exportConceptEvaluationMinim(evalMinim, idText +
//                    ":" + to[i].co.get(j).distanceName);
//            }
//        }
//        //
//        //********* evaluate concept ***********//





            //********** define corpus *************//
            //
            for (int i = 0; i < 2; i++)//textfile
            {
                to[i].c = new Corpus[to[i].co.size()];

                for (int j = 0; j < to[i].co.size(); j++)//concept
                {
                    to[i].c[j] = new Corpus(to[i].textFile.getAbsolutePath(),
                                            to[i].co.get(j).conceptFile.getAbsolutePath(),
                                            to[i].co.get(j).distanceName,
                                            to[i].lang);

                    to[i].c[j].conceptDelimiter = to[i].conceptDelimiter;
                }
            }
            //
            //********** define corpus *************//






            //************* bayesnet ****************//
            //
            System.out.println("bayesnet processing ...");
            String bayesNetDir = trainDir + "bayesnet/";
//        TestingOptionsList tol = new TestingOptionsList();

            String distanceDir;
            String cmdsDir;
            String cmdsOptionsDir;

            ArrayList<String> cmdOptionsName = new ArrayList<String>();
            cmdOptionsName.add("normal");
//        cmdOptionsName.add("deletearcs");
//        cmdOptionsName.add("addarcs");

            tol.cmdsOptionsName = cmdOptionsName;

            (new File(bayesNetDir)).mkdir();

            int bayesClass = tol.bnClassIndexInit;


            for (int i = 0; i < to[0].co.size(); i++)//distance in
            {
                int j;
                String distanceName1 = to[0].co.get(i).distanceName;

                if (TrainingOptions.bn4Layers == true)
                {
                    distanceName1 += "." + to[0].co.get(i + 1).distanceName;//
                }
                for (j = 0; j < to[1].co.size(); j++)// distance out
                {
                    if (TrainingOptions.compareDistancesSrcTrg == false)
                    {
                        j = i;
                    }

                    if (TrainingOptions.bn4Layers == true)
                    {
                        distanceName1 += "." + to[1].co.get(j + 1).distanceName;//
                    }
                    File lmFilePath2 = new File(to[1].co.get(j).lmFilePath);

                    File conceptSrcFile = to[0].co.get(j).conceptFile;
                    File conceptTrgFile = to[1].co.get(j).conceptFile;

                    String distanceName2 = to[1].co.get(j).distanceName;

                    distanceDir = bayesNetDir + distanceName1 + "."
                                  + distanceName2 + "/";

                    (new File(distanceDir)).mkdir();

                    ArrayList<Corpus> corpusList = new ArrayList<Corpus>();

                    corpusList.add(to[0].c[i]);

                    if (TrainingOptions.bn4Layers == true)
                    {
                        corpusList.add(to[0].c[i + 1]);//
                        corpusList.add(to[1].c[j + 1]);//
                    }

                    corpusList.add(to[1].c[j]);

                    //index in the bayes net
                    int firstIndexSourceLang = 0;
                    int lastIndexSourceLang = corpusList.get(0).getConceptLinesCount() - 1;
                    int firstIndexTargetLang = corpusList.get(0).getConceptLinesCount();
                    int lastIndexTargetLang = corpusList.get(0).getConceptLinesCount()
                                              + corpusList.get(1).getConceptLinesCount() - 1;


                    if (TrainingOptions.bn4Layers == true)
                    {
                        firstIndexTargetLang += corpusList.get(1).getConceptLinesCount() +//
                                corpusList.get(2).getConceptLinesCount();//
                        lastIndexTargetLang += corpusList.get(2).getConceptLinesCount() +//
                                corpusList.get(3).getConceptLinesCount();//
                    }

                    String bayesNetArffPath = distanceDir + TrainingOptions.id + "."
                                              + distanceName1 + "." + distanceName2 + ".bayesnet.arff";

                    int bayesNetNoeudsCount = 0;
                    for (int c1 = 0; c1 < corpusList.size(); c1++)
                    {
                        bayesNetNoeudsCount += corpusList.get(c1).getConceptLinesCount();
                    }

                    System.out.println(
                            "  Bayes net nodes count: " + bayesNetNoeudsCount);
                    System.out.println("  Bayes net class index: " + bayesClass);

                    WekaBayesNet wbn;
                    wbn = new WekaBayesNet();
                    if (distanceName2.toLowerCase().equals("word"))////
                    {
                        wbn.buildDataSetWeka(corpusList,
                                             false);
                    }
                    else////
                    {
                        wbn.buildDataSetWeka(corpusList,
                                             TrainingOptions.binarytrg);////
                    }
                    wbn.exportDataSetArff(bayesNetArffPath);
//                wbn = new WekaBayesNet();

                    //count the bayesnet noeuds

//                int parentsCount = TrainingOptions.parentBNCount;

                    ArrayList<WekaBayesNet> cmdsBayes = new ArrayList<WekaBayesNet>();
                    for (int wbni = 0; wbni < TrainingOptions.bnLearningOptions.size(); wbni++)
                    {
                        cmdsBayes.add(new WekaBayesNet(TrainingOptions.bnLearningOptions.get(
                                i).cmdText,
                                                       TrainingOptions.bnLearningOptions.get(
                                i).structMethodName,
                                                       TrainingOptions.bnLearningOptions.get(
                                i).parentCount));
                    }



                    for (int k = 0; k < cmdsBayes.size(); k++)//commande (k2)
                    {

                        ArrayList<WekaBayesNet> wbnList = new ArrayList<WekaBayesNet>();

                        cmdsDir = distanceDir + cmdsBayes.get(k).cmdName + "/";

                        (new File(cmdsDir)).mkdir();

//                    wbn = new WekaBayesNet(cmdsBayes.get(k).cmdText, cmdsBayes.get(k).cmdName, parentsCount);
//                    wbn.loadDataArff(bayesNetArffPath);
                        String bayesNetXmlPath2 = TrainingOptions.id + "." + distanceName1 + "."
                                                  + distanceName2 + "." + cmdsBayes.get(
                                k).cmdName + "."
                                                  + cmdsBayes.get(k).parentsCount;
                        System.out.println("  " + bayesNetXmlPath2);
                        System.out.println("    Exec Bayes net");


                        double memUsed;
                        long beginTime;//millisecondes

                        memUsed = Functions.getUsedMem();
                        beginTime = System.currentTimeMillis();
                        //
                        //functions
                        //

                        wbn.execBayesNet(cmdsBayes.get(k).cmdText,
                                         cmdsBayes.get(k).cmdName,
                                         cmdsBayes.get(k).parentsCount,
                                         bayesClass);

                        System.out.println(
                                (float) (System.currentTimeMillis() - beginTime));
                        System.out.println(Functions.getUsedMem() - memUsed);



                        if (tol.deleteArcsNoeudClass == true)
                        {
                            //delete the last childs nodes in the bayes net
                            wbn.deleteNodeChildArcs(wbn.getClassIndex());
                            wbn.refreshCPTs();
                        }
//                    String bayesNetXmlTmp = tmpDir +
//                        TrainingOptions.id + "." + "bayesnet.xml";
//                    wbn.exportBayesNetXml(bayesNetXmlTmp);




                        //*********** normal ************//
                        wbnList.add(wbn);
                        //*********** normal ************//




                        //******** delete all parents of first nodes concept
//                    wbn = new WekaBayesNet(cmdsBayes.get(k).cmdText, cmdsBayes.get(k).cmdName, parentsCount);
//                    wbn.loadBayesNet(bayesNetXmlTmp);
//                    //
//                    wbn.deleteConceptParentArcs(firstIndexSourceLang, lastIndexSourceLang);
//                    wbn.refreshCPTs();
//                    wbnList.add(wbn);
                        //******** delete all parents of first nodes concept



                        //*********** add ************//
//                    wbn = new WekaBayesNet();
//                    wbn.loadBayesNet(bayesNetXmlTmp);
//                    //
//                    wbn.addArcsParentsToChilds(firstIndexSourceLang, lastIndexSourceLang
//                        , firstIndexTargetLang, lastIndexTargetLang);
//                    wbn.refreshCPTs();
//                    wbnList.add(wbn);
                        //********** add ************//


                        for (int l = 0; l < wbnList.size(); l++)//cmd Options
                        {

                            String bayesNetXml = TrainingOptions.id + "." + distanceName1 + "."
                                                 + distanceName2 + "." + cmdsBayes.get(
                                    k).cmdName + "."
                                                 + cmdsBayes.get(k).parentsCount + "." + cmdOptionsName.get(
                                    l);

                            cmdsOptionsDir = cmdsDir + cmdOptionsName.get(l) + "/";
                            (new File(cmdsOptionsDir)).mkdir();

                            String cmdText = wbnList.get(l).cmdText;
                            String cmdName = wbnList.get(l).cmdName;
                            int parentCount = cmdsBayes.get(k).parentsCount;
                            String bayestNetPath = cmdsOptionsDir + bayesNetXml + ".bayesnet.xml";
                            String bayesNetConfPath = cmdsOptionsDir + bayesNetXml + ".bayesnet.conf.xml";
                            String BayesNetMarginPath = cmdsOptionsDir + bayesNetXml + ".bayesnet.margin.bin";
                            String cmdOption = cmdOptionsName.get(l);




                            if (TrainingOptions.bn4Layers == false)
                            {
                                wbnList.get(l).bayesNetReorganization(
                                        new int[]
                                        {
                                            corpusList.get(0).getConceptLinesCount(),
                                            corpusList.get(1).getConceptLinesCount(), //                            corpusList.get(2).getConceptLinesCount(),//
                                        //                            corpusList.get(3).getConceptLinesCount()//
                                        });
                            }
                            else
                            {
                                wbnList.get(l).bayesNetReorganization(
                                        new int[]
                                        {
                                            corpusList.get(0).getConceptLinesCount(),
                                            corpusList.get(1).getConceptLinesCount(),
                                            corpusList.get(2).getConceptLinesCount(),
                                            corpusList.get(3).getConceptLinesCount()
                                        });
                            }


                            wbnList.get(l).exportBayesNetXml(bayestNetPath);
                            WekaBayesNet.exportConfBayesNet(bayesNetConfPath,
                                                            lastIndexSourceLang - firstIndexSourceLang + 1,
                                                            firstIndexSourceLang,
                                                            lastIndexSourceLang,
                                                            lastIndexTargetLang - firstIndexTargetLang + 1,
                                                            firstIndexTargetLang,
                                                            lastIndexTargetLang);
                            //            wbn.showBayesNet();

                            if (tol.inferenceAllWords == true)
                            {
                                System.out.println("    margins calculating...");
                                System.out.println(BayesNetMarginPath);
                                try
                                {
                                    wbnList.get(l).editBayesNet.m_nPositionX = null;
                                    wbnList.get(l).editBayesNet.m_nPositionY = null;

                                    MyMarginCalculator myMarginCalculator = new MyMarginCalculator();

                                    memUsed = Functions.getUsedMem();
                                    beginTime = System.currentTimeMillis();

                                    myMarginCalculator.calcMargins(
                                            wbnList.get(l).editBayesNet);

                                    System.out.println(
                                            (float) (System.currentTimeMillis() - beginTime));
                                    System.out.println(
                                            Functions.getUsedMem() - memUsed);


                                    //
                                    myMarginCalculator.m_root.m_parentSeparator = null;
                                    myMarginCalculator.m_root.m_fi = null;
                                    myMarginCalculator.m_root.m_MarginalP = null;
                                    myMarginCalculator.m_root.m_bayesNet.m_Distributions = null;
                                    //
                                    // serialize model
                                    weka.core.SerializationHelper.write(
                                            BayesNetMarginPath,
                                            myMarginCalculator);
                                    //
                                    myMarginCalculator = null;
                                }
                                catch (Exception ex)
                                {
                                    Logger.getLogger(SMTSystem.class.getName()).log(
                                            Level.SEVERE,
                                            null,
                                            ex);
                                }
                            }

                            tol.testingOptions.add(
                                    new TestingOptions(cmdText,
                                                       cmdName,
                                                       parentCount,
                                                       distanceName1,
                                                       distanceName2,
                                                       cmdOption,
                                                       bayestNetPath,
                                                       bayesNetConfPath,
                                                       lmFilePath2,
                                                       conceptSrcFile,
                                                       conceptTrgFile,
                                                       new File(
                                    BayesNetMarginPath),
                                                       wbnList.get(l).classIndex,
                                                       TrainingOptions.binarytrg));

                            if (TrainingOptions.bn4Layers == true)
                            {
                                i = j = 100;//to stop loops
                            }
                        }
                    }
                    if (TrainingOptions.compareDistancesSrcTrg == false)
                    {
                        j = to[1].co.size();//to stop loop
                    }
                }
            }

        }


        (new File(tol.testingOptionsFilePath)).delete();

        tol.encodeToFile(tol.testingOptionsFilePath);

        ChoicesOptionsList col = new ChoicesOptionsList();
        col.choicesOptions = new ArrayList<ChoicesOptions>();
        for (int t = 0; t < to.length; t++)
        {
            col.choicesOptions.add(new ChoicesOptions(to[t]));
        }
        col.id = TrainingOptions.id;
        col.conceptCountMoyen = TrainingOptions.conceptCountMoyen;
        col.parentBNCount = TrainingOptions.parentBNCount;
        col.binarytrg = TrainingOptions.binarytrg;
        col.bn4Layers = TrainingOptions.bn4Layers;
        col.textCleaner = TrainingOptions.textCleaner;
        col.compareDistancesSrcTrg = TrainingOptions.compareDistancesSrcTrg;
        //
        col.encodeToFile(myProjectDir + "choices.xml");

//        TrainingOptionsList trol = new TrainingOptionsList();
//        trol.trainingOptions = new ArrayList<TrainingOptions>();
//        trol.trainingOptions.add(to[0]);
//        trol.trainingOptions.add(to[1]);
//        trol.encodeToFile(myProjectDir + "training.xml");
        System.out.println("training finish");

    }
}