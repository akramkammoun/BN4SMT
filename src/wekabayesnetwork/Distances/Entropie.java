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
public class Entropie extends Distance
{

    public Entropie(int textLinesCount,
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
        this.distanceName = "entropie";
    }

    @Override
    public void buildDataSet()
    {
        //H(X,Y) = H(X) + H(Y|X)
        //       = - p_ij log p_ij
        this.dataSet = new Instances("IMM_Clustring",
                                     this.attributesDataSet,
                                     0);

        int wordsCount = this.wordsList.size();
        double pi, pj, pij;
        int iCount, jCount, ijCount;
        double[] values = new double[this.dataSet.numAttributes()];

        double hI, hJndI, hIJ;

        for (int i = 0; i < wordsCount; i++)
        {
//            iCount = Integer.parseInt(this.wordSquareMat[i][i]);
////            pi = (double) iCount / this.textLinesCount;

////            hI = (double) -1 * pi * Functions.log2(pi);
            for (int j = 0; j < wordsCount; j++)
            {

                ijCount = this.wordSquareMat[i][j];
                pij = (double) ijCount / (this.textLinesCount);

////                hJndI = (double) -1 * pij * Functions.log2(pij / pi);

////                hIJ = (double) hI + hJndI;

////                values[j] = (double) hIJ;
                if (pij != 0)
                {
                    values[j] = (double) -pij * Functions.log2(pij);
                }
                else
                {
                    values[j] = 0;
                }
//                System.out.print(values[j] + " ");
            }
//            System.out.println();
            this.dataSet.add(new SparseInstance(1.0,
                                                values));

//            for(int ll = 0; ll < wordsCount; ll++)
//            {
//                System.out.print(this.dataSet.instance(0).value(ll) + " ");
//            }
//
//            System.exit(-1);
        }
    }
}
