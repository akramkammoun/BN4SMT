/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package wekabayesnetwork;

import java.io.File;
import java.util.ArrayList;
import wekabayesnetwork.Options.*;

/**
 *
 * @author akram
 */
public class Main
{

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args)
    {

        double memUsed;
        long beginTime;//millisecondes





        //************* create project *************//
        //
        String projectName = "inriamobile";
        String projectDir = "../projects/" + projectName + "/";
        String testingOptionsFilePath = projectDir + "testing.xml";
        (new File(projectDir)).mkdirs();
        //
        //************* create project *************//



        //********* training ***********//
        //
        //TrainingOptions
        TrainingOptions[] to = new TrainingOptions[2];
        TrainingOptions.id = projectName;
        //
        //corpus 1
        to[0] = new TrainingOptions();
        to[0].lang = "fr";
        to[0].textFile = new File("/home/akram/Bureau/testfmbn/mobile");
        to[0].phraseDelimiter = " ";
        //
        //corpus 2
        to[1] = new TrainingOptions();
        to[1].lang = "en";
        to[1].textFile = new File("/home/akram/Bureau/testfmbn/empty");
        to[1].phraseDelimiter = " ";
        //
        //text processing
        TrainingOptions.textCleaner = false;//true
        TrainingOptions.minWordsPhrase = 1;
        TrainingOptions.maxWordsPhrase = 20;//10
        to[0].textFreqMinDeleter = to[1].textFreqMinDeleter = false;
        to[0].minFreqWord = to[1].minFreqWord = 1;//fixe 1
        to[0].maxFreqWord = to[1].maxFreqWord = 2;//fixe 1 ; 32767
        to[0].replaceDelim = to[1].replaceDelim = true;//true
        to[0].textLowerCase = to[1].textLowerCase = false;//true
        to[0].textTokenizer = to[1].textTokenizer = false;//true
        to[0].textDelimiter = to[1].textDelimiter = false;//false
        to[0].textNotAlphaDeleter = to[1].textNotAlphaDeleter = false;//true
        to[0].textStopWordsDeleter = to[1].textStopWordsDeleter = false;//true
        to[0].textStemmer = to[1].textStemmer = false;//false
        to[0].textLineUnique = to[1].textLineUnique = false;//true
        //
        //distances
        TrainingOptions.compareDistancesSrcTrg = false;
        //
        to[0].distancesOrder.add("word");//word,imm,dkl,entropie
//        to[0].distancesOrder.add("dkl");//word,imm,dkl,entropie
//        to[0].distancesOrder.add("entropie");//word,imm,dkl,entropie
        to[1].distancesOrder.add("word");
//        to[1].distancesOrder.add("dkl");
//        to[1].distancesOrder.add("entropie");
        //
        //concepts
        TrainingOptions.conceptCountMoyen = 8;//1
        to[0].splitConceptCount = to[1].splitConceptCount = 2; // >= 2
        to[0].conceptStemmed = to[1].conceptStemmed = false;//false;
        //
        ArrayList<ClusteringLearninigOptions> clusteringLearninigOptionses =
                                              new ArrayList<ClusteringLearninigOptions>(); //EuclideanDistance, ManhattanDistance
        clusteringLearninigOptionses.add(
                new ClusteringLearninigOptions("simplekmeans",
                                               "EuclideanDistance"));
//        clusteringLearninigOptionses.add(new ClusteringLearninigOptions("simplekmeans", "ManhattanDistance"));
        TrainingOptions.clusteringLearninigOptions = clusteringLearninigOptionses;
        //
        to[0].conceptRef = new File("");
        to[1].conceptRef = new File("");
        //
        //bayes network
        //Methods
        ArrayList<BNLearningOptions> bnLearningOptions = new ArrayList<BNLearningOptions>();
        bnLearningOptions.add(new BNLearningOptions("k2",
                                                    "BAYES",
                                                    10));
        TrainingOptions.bnLearningOptions = bnLearningOptions;
        //
//        TrainingOptions.parentBNCount = 2;//
        TrainingOptions.bn4Layers = false;//BN as 4 layers
        TrainingOptions.binarytrg = false;//out: words or concepts
        //
        //testingOptionsList
        TestingOptionsList tol = new TestingOptionsList();
        tol.projectDir = projectDir;
        tol.testingOptionsFilePath = testingOptionsFilePath;
        tol.bnClassIndexInit = 0;//0 ... n
        tol.deleteArcsNoeudClass = false;//true
        tol.alpha = 0;
        tol.setEvidence = new boolean[]
        {
            false
        };//false
        tol.findInAllwords = new boolean[]
        {
            false
        };//true
        tol.setEvidenceZero = true;//true
        //

        tol.useBN = true;
        tol.useMoses = false;
        tol.ibmModelCount = "5";
        tol.deleteUnknowenWords = true;
        tol.inferenceAllWords = false;
        //
        System.out.println("Training...");
        SMTSystem.dataTraining(projectDir,
                               tol,
                               to);
        //
        //********* training ***********//

        System.exit(-1);

        //********** testing *************//
        //
        System.out.println("Testing...");
        tol = TestingOptionsList.decodeFormFile(testingOptionsFilePath);
        String textInSplit = tol.textFileSrc.getAbsolutePath();
        String textEvalSplit = tol.textFileTrg.getAbsolutePath();
        //text processing
        TrainingOptions.textCleaner = true;//true
        TrainingOptions.minWordsPhrase = 1;
        TrainingOptions.maxWordsPhrase = 15;//10
        to[0].textFreqMinDeleter = to[1].textFreqMinDeleter = false;
        to[0].minFreqWord = to[1].minFreqWord = 1;//fixe 1
        to[0].maxFreqWord = to[1].maxFreqWord = 2;//fixe 1 ; 32767
        to[0].replaceDelim = to[1].replaceDelim = false;//true
        to[0].textLowerCase = to[1].textLowerCase = false;//true
        to[0].textTokenizer = to[1].textTokenizer = false;//true
        to[0].textDelimiter = to[1].textDelimiter = false;//false
        to[0].textNotAlphaDeleter = to[1].textNotAlphaDeleter = false;//true
        to[0].textStopWordsDeleter = to[1].textStopWordsDeleter = false;//true
        to[0].textStemmer = to[1].textStemmer = false;//false
        to[0].textLineUnique = to[1].textLineUnique = false;//true
//
        tol.alpha = 0;

        textInSplit = "/home/akram/Bureau/corpus_test.fr";
        textEvalSplit = "/home/akram/Bureau/corpus_eval.en";

//        Functions.splitFile(textInSplit, "/tmp/test.in", 0, 30);
//        Functions.splitFile(textEvalSplit, "/tmp/test.eval", 0, 30);
//        textInSplit = "/tmp/test.in";
//        textEvalSplit = "/tmp/test.eval";


//        tol.useBN = false;
//        tol.useMoses = true;
        memUsed = Functions.getUsedMem();
        beginTime = System.currentTimeMillis();
        //
        SMTSystem.dataTesting(projectDir,
                              projectName,
                              tol,
                              textInSplit,
                              textEvalSplit,
                              to);
        //
        System.out.println((float) (System.currentTimeMillis() - beginTime));
        System.out.println(Functions.getUsedMem() - memUsed);
        //
        //********** testing *************//


    }
}
