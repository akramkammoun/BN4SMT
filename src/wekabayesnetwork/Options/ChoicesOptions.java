/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package wekabayesnetwork.Options;

import java.io.File;
import java.util.ArrayList;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author akram
 */
@XmlRootElement(name = "choicesOptions")
public class ChoicesOptions
{

    public File textFile;
    public String lang;
    public int corpusLinesCountInit;//
    public int corpusLinesCountFinal;//
    public int wordsCountInit;//
    public int wordsCountFinal;
    public int minFreqWord;
    public int maxFreqWord;
    public int minWordsPhrase;
    public int maxWordsPhrase;
    public String phraseDelimiter;
    public String conceptDelimiter;
    public ArrayList<ConceptOptions> co;
    public boolean replaceDelim;
    public boolean textLowerCase;
    public boolean textTokenizer;
    public boolean textDelimiter;
    public boolean textNotAlphaDeleter;
    public boolean textStopWordsDeleter;
    public boolean textStemmer;
    public boolean textLineUnique;
    public boolean textFreqMinDeleter;
    public int splitConceptCount;
    public File conceptRef;
    public boolean conceptStemmed;

    public ChoicesOptions()
    {
    }

    public ChoicesOptions(TrainingOptions to)
    {
        this.textFile = to.textFile;
        this.lang = to.lang;
        this.wordsCountFinal = to.wordsCount;
        this.wordsCountInit = to.wordsCountInit;
        this.corpusLinesCountInit = to.corpusLinesCountInit;
        this.corpusLinesCountFinal = to.corpusLinesCountFinal;
        this.minFreqWord = to.minFreqWord;
        this.maxFreqWord = to.maxFreqWord;
        this.phraseDelimiter = to.phraseDelimiter;
        this.conceptDelimiter = to.conceptDelimiter;
        this.co = to.co;

        this.replaceDelim = to.replaceDelim;
        this.textLowerCase = to.textLowerCase;
        this.textTokenizer = to.textTokenizer;
        this.textDelimiter = to.textDelimiter;
        this.textNotAlphaDeleter = to.textNotAlphaDeleter;
        this.textStopWordsDeleter = to.textStopWordsDeleter;
        this.textStemmer = to.textStemmer;
        this.textLineUnique = to.textLineUnique;
        this.textFreqMinDeleter = to.textFreqMinDeleter;
        //
        this.minWordsPhrase = TrainingOptions.minWordsPhrase;
        this.maxWordsPhrase = TrainingOptions.maxWordsPhrase;
        //
        this.splitConceptCount = to.splitConceptCount;

        this.conceptRef = to.conceptRef;

        this.conceptStemmed = to.conceptStemmed;
    }
}
