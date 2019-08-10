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
public class DKL extends Distance
{

    public DKL(int textLinesCount,
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
        this.distanceName = "dkl";
    }

    @Override
    public void buildDataSet()
    {
        this.dataSet = new Instances("DKL_Clustring",
                                     this.attributesDataSet,
                                     0);

        int wordsCount = this.wordsList.size();
        double pi, pj, pik, pjk;
        double dij, dji;
        int iCount, jCount, ikCount, jkCount;
        double[] values = new double[this.dataSet.numAttributes()];

        for (int i = 0; i < wordsCount; i++)
        {
            iCount = this.wordSquareMat[i][i];
//            iCount = this.wordSquareMat[i][i];
            pi = (double) iCount / this.textLinesCount;

            for (int j = 0; j < wordsCount; j++)
            {
                jCount = this.wordSquareMat[j][j];
//                jCount = this.wordSquareMat[j][j];
                pj = (double) jCount / this.textLinesCount;

                dij = 0;
                dji = 0;
                for (int k = 0; k < wordsCount; k++)
                {
                    ikCount = this.wordSquareMat[i][k];
//                    ikCount = this.wordSquareMat[i][k];
                    pik = (double) ikCount / (this.textLinesCount);

                    jkCount = this.wordSquareMat[j][k];
//                    jkCount = this.wordSquareMat[j][k];
                    pjk = (double) jkCount / (this.textLinesCount);

                    if (ikCount != 0 && jkCount != 0)
                    {
//                        dij += (double) (pik / pi) * Functions.log2((pik * pj) / (pjk * pi));
//                        dji += (double) (pjk / pj) * Functions.log2((pjk * pi) / (pik * pj));
                        dij += (double) (pik) * Functions.log2((pik) / (pjk));
                        dji += (double) (pjk) * Functions.log2((pjk) / (pik));
                    }
                }

//                dij += (double) (pi ) * Functions.log2((pi ) / (pj ));
//                dji += (double) (pj ) * Functions.log2((pj ) / (pi ));
                values[j] = (double) (dij + dji);

            }
            this.dataSet.add(new SparseInstance(1.0,
                                                values));
        }
    }
}
