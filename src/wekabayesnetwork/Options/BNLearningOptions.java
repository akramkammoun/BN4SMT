/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package wekabayesnetwork.Options;

import java.util.Locale;

/**
 *
 * @author akram
 */
public class BNLearningOptions
{

    public String structMethodName;//k2,tabu,
    public String scoreName;//BAYES,MDL,AIC
    public String paramterMethodName = "SimpleEstimator";//SimpleEstimator
    public double paramterValue = 0.5;// 0 at 1
    public int parentCount;
    public String cmdText;

    public BNLearningOptions(String structMethodName,
                             String scoreName,
                             int parentCount)
    {
        this.structMethodName = structMethodName;
        this.scoreName = scoreName;
        this.parentCount = parentCount;

        if (structMethodName.toLowerCase().equals("k2"))
        {
            this.cmdText = "-Q weka.classifiers.bayes.net.search.local.K2 -- "
                           + "-P " + this.parentCount + " -S " + this.scoreName + " -E "
                           + "weka.classifiers.bayes.net.estimate." + this.paramterMethodName + " -- -A " + this.paramterValue;
        }
        else if (structMethodName.toLowerCase().equals("hillclimber"))
        {
            this.cmdText = "-Q weka.classifiers.bayes.net.search.local.HillClimber -- "
                           + "-N -P " + this.parentCount + " -S " + this.scoreName + " -E "
                           + "weka.classifiers.bayes.net.estimate." + this.paramterMethodName + " -- -A " + this.paramterValue;
        }
        else if (structMethodName.toLowerCase().equals("tabusearch"))
        {
            this.cmdText = "-Q weka.classifiers.bayes.net.search.local.TabuSearch -- "
                           + "-L 2 -U 4 -N -P " + this.parentCount + " -S " + this.scoreName + " -E "
                           + "weka.classifiers.bayes.net.estimate." + this.paramterMethodName + " -- -A " + this.paramterValue;
        }
        else if (structMethodName.toLowerCase().equals("simulatedannealing"))
        {
            this.cmdText = "-Q weka.classifiers.bayes.net.search.local.SimulatedAnnealing -- "
                           + "-A 10.0 -U 200 -D 0.999 -R 1 -S " + this.scoreName + " -E "
                           + "weka.classifiers.bayes.net.estimate." + this.paramterMethodName + " -- -A " + this.paramterValue;
        }
        else if (structMethodName.toLowerCase().equals("tan"))
        {
            this.cmdText = "-Q weka.classifiers.bayes.net.search.local.TAN -- "
                           + "-S " + this.scoreName + " -E "
                           + "weka.classifiers.bayes.net.estimate." + this.paramterMethodName + " -- -A " + this.paramterValue;
        }

    }

    public BNLearningOptions(String cmdText)
    {
        this.cmdText = cmdText;
    }
}
