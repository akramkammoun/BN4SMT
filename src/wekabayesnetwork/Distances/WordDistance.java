/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package wekabayesnetwork.Distances;

import java.util.ArrayList;
import weka.core.FastVector;
import wekabayesnetwork.Word;

/**
 *
 * @author akram
 */
public class WordDistance extends Distance
{

    public WordDistance(int textLinesCount,
                        ArrayList<Word> wordsList,
                        short[][] wordSquareMat,
                        FastVector attributesDataSet,
                        int conceptCount)
    {
        super(textLinesCount,
              wordsList,
              wordSquareMat,
              attributesDataSet,
              conceptCount);
        this.distanceName = "word";
    }

    @Override
    public void buildDataSet()
    {
        this.conceptLists = new ArrayList<ArrayList<String>>();

        for (int i = 0; i < this.wordsList.size(); i++)
        {
            ArrayList<String> wordConcept = new ArrayList<String>();
            wordConcept.add(this.wordsList.get(i).text);
            this.conceptLists.add(wordConcept);
        }
    }
}
