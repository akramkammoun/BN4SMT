/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package wekabayesnetwork.Options;

import java.io.File;
import java.util.ArrayList;
import javax.xml.bind.annotation.XmlRootElement;
import wekabayesnetwork.ConceptClustring;
import wekabayesnetwork.Corpus;
import wekabayesnetwork.Distances.Distance;
import wekabayesnetwork.TextProcessing;

/**
 *
 * @author akram
 */
@XmlRootElement(name = "trainingOptions")
public class TrainingOptions
{

    public static String id;
    public static int minWordsPhrase;
    public static int maxWordsPhrase;
    public static double conceptCountMoyen;
    public static int parentBNCount;
    public File textFile;
    public String phraseDelimiter;
    public String conceptDelimiter = " ";
    public String lang;
    public int wordsCount;
    public int minFreqWord;
    public int maxFreqWord;
    public TextProcessing tp;//
    public ConceptClustring cc;//
    public Corpus[] c;// textSrc, textPath = 2//
    public ArrayList<ConceptOptions> co = new ArrayList<ConceptOptions>();//imm, dkl
    public ArrayList<Distance> distancesList;//
    public boolean replaceDelim;
    public boolean textLowerCase;
    public boolean textTokenizer;
    public boolean textDelimiter;
    public boolean textNotAlphaDeleter;
    public boolean textStopWordsDeleter;
    public boolean textStemmer;
    public boolean textLineUnique;
    public boolean textFreqMinDeleter;
    public static boolean textCleaner;
    //for choicesOptions
    public int corpusLinesCountInit;//
    public int corpusLinesCountFinal;//
    public int wordsCountInit;//
    public int splitConceptCount;
    public File conceptRef;
    public boolean conceptStemmed;
    public static boolean bn4Layers;//BN as 4 layers
    public static boolean binarytrg;
    public static boolean compareDistancesSrcTrg;
    public static ArrayList<BNLearningOptions> bnLearningOptions = new ArrayList<BNLearningOptions>();
    public static ArrayList<ClusteringLearninigOptions> clusteringLearninigOptions =
                                                        new ArrayList<ClusteringLearninigOptions>();
    public ArrayList<String> distancesOrder = new ArrayList<String>();
}