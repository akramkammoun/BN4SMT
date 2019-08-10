/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package wekabayesnetwork;

import java.util.ArrayList;
import weka.clusterers.*;

/**
 *
 * @author akram
 */
public class ClusterType
{

    public String clusterName;
    public Object randomizableClusterer;
    public String clusterOptions;
    public int clusterNumber;
    public AbstractClusterer abstractCluster;

    public ClusterType(String clusterName,
                       Object randomizableClusterer,
                       String clusterOptions,
                       int clusterNumber)
    {
        this.clusterName = clusterName;
        this.randomizableClusterer = randomizableClusterer;
        this.clusterOptions = clusterOptions;
        this.clusterNumber = clusterNumber;
    }

    public static ArrayList<ClusterType> buildClusterTypes(int clusterNumber)
    {
        ArrayList<ClusterType> clusterTypesList = new ArrayList<ClusterType>();

//        clusterTypesList.add(new ClusterType("EM_" + clusterNumber,
//            new EM(), "-I 100 -N " + clusterNumber + " -M 1.0E-6 -S 100", clusterNumber));
//
//        clusterTypesList.add(new ClusterType("FarthestFirst_" + clusterNumber,
//            new FarthestFirst(), "-N " + clusterNumber + " -S 1", clusterNumber));
////
//        clusterTypesList.add(new ClusterType("sIB_" + clusterNumber,
//            new sIB(), "-I 100 -M 0 -N " + clusterNumber + " -R 5 -S 1", clusterNumber));
//
//

        //****** kmeans ********//
//        clusterTypesList.add(new ClusterType("SimpleKMeans_Euclidean_" + clusterNumber,
//            new SimpleKMeans(),  "-N " + clusterNumber +" -A \"weka.core.EuclideanDistance "
//            + "-R first-last\" -I 500 -O -S 10", clusterNumber));
//        //
//        clusterTypesList.add(new ClusterType("SimpleKMeans_Manhattan_" + clusterNumber,
//            new SimpleKMeans(),  "-N " + clusterNumber + " -A \"weka.core.ManhattanDistance "
//            + "-R first-last\" -I 500 -O -S 10", clusterNumber));
        //****** kmeans ********//
//
//
//
//        //****** XMeans ********//
//        //-L: min cluster
//        //-H: max cluster
//        clusterTypesList.add(new ClusterType("XMeans_Euclidean_" + clusterNumber,
//            new XMeans(), "-I 1 -M 1000 -J 1000 " + " -H " + clusterNumber + " -B 1.0 -C 0.5"
//            + " -D \"weka.core.EuclideanDistance -R first-last\" -S 10", clusterNumber));
//        //
//        clusterTypesList.add(new ClusterType("XMeans_Manhattan_" + clusterNumber,
//            new XMeans(), "-I 1 -M 1000 -J 1000 " + " -H " + clusterNumber + " -B 1.0 -C 0.5"
//            + " -D \"weka.core.ManhattanDistance -R first-last\" -S 10", clusterNumber));
//        //****** XMeans ********//
//
//
//
//        //****** HierarchicalClusterer ******//
//        clusterTypesList.add(new ClusterType("HierarchicalClusterer_Euclidean_" + clusterNumber,
//            new HierarchicalClusterer(), "-N " + clusterNumber + " -L WARD -P "
//            + "-A \"weka.core.EuclideanDistance -R first-last\"", clusterNumber));
        //
//        clusterTypesList.add(new ClusterType("HierarchicalClusterer_Manhattan_" + clusterNumber,
//            new HierarchicalClusterer(), "-N " + clusterNumber + " -L WARD -P "
//            + "-A \"weka.core.ManhattanDistance -R first-last\"", clusterNumber));
//        //****** HierarchicalClusterer ******//


        return clusterTypesList;
    }
}
