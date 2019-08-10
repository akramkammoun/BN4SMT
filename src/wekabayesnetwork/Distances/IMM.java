/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package wekabayesnetwork.Distances;

import java.util.ArrayList;
import weka.core.FastVector;
import weka.core.Instances;
import weka.core.SparseInstance;
import wekabayesnetwork.Functions;
import wekabayesnetwork.Word;

/**
 *
 * @author akram
 */
public class IMM extends Distance
{

    public IMM(int textLinesCount,
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
        this.distanceName = "imm";
    }

    @Override
    public void buildDataSet()
    {
        this.dataSet = new Instances("IMM_Clustring",
                                     this.attributesDataSet,
                                     0);

        int wordsCount = this.wordsList.size();
        double pi, pj, pij;
        int iCount, jCount, ijCount;
        double[] values = new double[this.dataSet.numAttributes()];

        for (int i = 0; i < wordsCount; i++)
        {
            iCount = this.wordSquareMat[i][i];
            pi = (double) iCount / this.textLinesCount;

            for (int j = 0; j < wordsCount; j++)
            {
                jCount = this.wordSquareMat[j][j];
                pj = (double) jCount / this.textLinesCount;

                ijCount = this.wordSquareMat[i][j];
//                pij = (double) ijCount / (iCount + jCount - ijCount);
                pij = (double) ijCount / (this.textLinesCount);

                if (ijCount != 0)
                {
                    values[j] = (double) (Functions.log2(pij / (pi * pj)));
                }
                else
                {
                    values[j] = 0;
                }
            }
            this.dataSet.add(new SparseInstance(1.0,
                                                values));
        }
    }
}
