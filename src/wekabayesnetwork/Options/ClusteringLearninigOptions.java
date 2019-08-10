/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package wekabayesnetwork.Options;

import weka.clusterers.*;

/**
 *
 * @author akram
 */
public class ClusteringLearninigOptions
{

    public String clusterName;
    public String distanceName;
    public int clusterNumber;
    public Object randomizableClusterer;
    public String clusterOptions;

    public ClusteringLearninigOptions(String clusterName,
                                      String distanceName)
    {
        this.clusterName = clusterName;
        this.distanceName = distanceName;
    }

    public ClusteringLearninigOptions(String clusterName,
                                      String distanceName,
                                      int clusterNumber)
    {
        this.clusterName = clusterName;
        this.clusterNumber = clusterNumber;
        this.distanceName = distanceName;
    }

    public void buildClusterOptions(int clusterNumber)
    {
        this.clusterNumber = clusterNumber;
        if (clusterName.toLowerCase().equals("simplekmeans"))
        {
            this.clusterOptions = "-N " + clusterNumber + " -A \"weka.core." + distanceName
                                  + " -R first-last\" -I 500 -O -S 10";

            this.randomizableClusterer = new SimpleKMeans();
        }
        else if (clusterName.toLowerCase().equals("xmeans"))
        {
            this.clusterOptions = "-I 1 -M 1000 -J 1000 " + " -H " + clusterNumber + " -B 1.0 -C 0.5"
                                  + " -D \"weka.core." + distanceName + " -R first-last\" -S 10";

            this.randomizableClusterer = new XMeans();
        }
        else if (clusterName.toLowerCase().equals("hierarchicalclusterer"))
        {
            this.clusterOptions = "-N " + clusterNumber + " -L WARD -P "
                                  + "-A \"weka.core." + distanceName + " -R first-last\"";

            this.randomizableClusterer = new HierarchicalClusterer();
        }
        else if (clusterName.toLowerCase().equals("em"))
        {
            this.clusterOptions = "-I 100 -N " + clusterNumber + " -M 1.0E-6 -S 100";

            this.randomizableClusterer = new EM();
        }
        else if (clusterName.toLowerCase().equals("farthestfirst"))
        {
            this.clusterOptions = "-N " + clusterNumber + " -S 1";

            this.randomizableClusterer = new FarthestFirst();
        }
        else if (clusterName.toLowerCase().equals("sib"))
        {
            this.clusterOptions = "-I 100 -M 0 -N " + clusterNumber + " -R 5 -S 1";

            this.randomizableClusterer = new sIB();
        }
    }
}
