/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package wekabayesnetwork;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author akram
 */
public class Moses
{

    String projectJavaRoot;
    String projectRoot;
    String textInTrainFile;
    String textOutTrainFile;
    String lmFile;
    String textInTradFile;
    String textOutEvalFile;
    String textTraductedFile;
    String ibmModel;
    String script;
    String tmpDir;
    public String mtDir;
    public String mertWorkDir;
    public String mosesFile;

    //training
    public Moses(String projectJavaRoot,
                 String projectRoot,
                 String textInTrainFile,
                 String textOutTrainFile,
                 String ibmModel)
    {
        this.projectJavaRoot = projectJavaRoot;
        this.script = projectJavaRoot + "scripts/scripts_moses/";
        this.projectRoot = projectRoot;
        this.tmpDir = this.projectRoot + "tmp/";
        this.textInTrainFile = textInTrainFile;
        this.textOutTrainFile = textOutTrainFile;
        this.ibmModel = ibmModel;

        Functions.deleteDirectory(new File(projectRoot));
        //************ copies files ************//
        //
        try
        {
            (new File(this.projectRoot)).mkdirs();
            (new File(this.projectRoot + "corpus/")).mkdir();
            (new File(this.tmpDir)).mkdir();
            Functions.copyFile(this.textInTrainFile,
                               this.projectRoot + "corpus/corpus.fr");
            Functions.copyFile(this.textOutTrainFile,
                               this.projectRoot + "corpus/corpus.en");
        }
        catch (Exception e)
        {
            Logger.getLogger(Moses.class.getName()).log(Level.SEVERE,
                                                        null,
                                                        e);
        }
        //
        //************ copies files ************//
    }

    //traduction
    public Moses(String projectRoot,
                 File mosesFile)
    {
        System.out.print("projectRoot");
        System.out.print(mosesFile.getAbsolutePath());
        this.projectRoot = projectRoot;

        this.mosesFile = mosesFile.getAbsolutePath() + "/";
        this.tmpDir = this.projectRoot + "tmp/";
    }

    public void execLM()
    {
        //**************** language Model *************//
        //
        String lmPath = this.projectRoot + "lm/";
        (new File(lmPath)).mkdir();
        LanguageModel lm = new LanguageModel(this.textOutTrainFile,
                                             lmPath);
        lm.tmpDirPath = this.tmpDir;
        lm.buildLanguageModel();
        this.lmFile = lm.lmFile.getAbsolutePath();
        //
        //**************** language Model *************//
    }

    public void execMT()
    {
        String commande;

        //***************** MT *****************//
        //
        this.mtDir = this.projectRoot + "mt/";
        (new File(this.mtDir)).mkdir();
        (new File(this.mtDir + "corpus/")).mkdir();
        try
        {
            (new File(this.mtDir + "/corpus/fr.vcb.classes")).createNewFile();
            (new File(this.mtDir + "/corpus/fr.vcb.classes.cats")).createNewFile();
            (new File(this.mtDir + "/corpus/en.vcb.classes")).createNewFile();
            (new File(this.mtDir + "/corpus/en.vcb.classes.cats")).createNewFile();
        }
        catch (IOException ex)
        {
            Logger.getLogger(Moses.class.getName()).log(Level.SEVERE,
                                                        null,
                                                        ex);
        }
        commande = this.script + "training/train-model.perl" + " --root-dir=" + this.mtDir
                   + " --f=fr --e=en --lm=0:3:" + this.lmFile
                   + " --final-alignment-model=" + this.ibmModel + " --scripts-root-dir="
                   + this.script + " --corpus=" + this.projectRoot + "corpus/corpus";
        //
        Functions.execExtProg(commande,
                              this.tmpDir + "mt.sh");
        //
        //***************** MT *****************//
    }

    public void execDecoder()
    {
        String commande;

        //***************** moses.ini *****************//
        //
        this.mertWorkDir = this.mtDir + "mert1/";
        commande = this.script
                   + "training/mert-moses.pl " + "--rootdir=" + this.script
                   + " --working-dir=" + this.mertWorkDir + " --mertdir=" + this.script + "mert"
                   + " " + this.projectRoot + "corpus/corpus.fr " + this.projectRoot
                   + "corpus/corpus.en --nbest=100 /usr/bin/moses " + this.mtDir + "model/moses.ini " + "--lambdas=\"d:0.6,0-1.2 "
                   + "lm:0.5,0-1 tm:0.2,0-0.5;0.2,0-0.5;0.2,0-0.5;0.2,0-0.5;0.2,0-0.5 w:-1,-1.5-0.5\"";

//        Functions.execExtProg(commande, this.tmpDir + "moses.sh");
        //
        //***************** moses.ini *****************//

        System.out.println("commande moses:");
        System.out.println(commande);
        try
        {
            System.out.println("push ok");
            System.in.read();
        }
        catch (IOException ex)
        {
            Logger.getLogger(Moses.class.getName()).log(Level.SEVERE,
                                                        null,
                                                        ex);
        }

        //***************** filter-model-given-input *****************//
        //
        this.mosesFile = mtDir + "out/";
        commande = this.script + "training/filter-model-given-input.pl "
                   + this.mosesFile + " "
                   + this.mertWorkDir + "moses.ini "
                   + this.textInTrainFile + "";

        Functions.execExtProg(commande,
                              this.tmpDir + "filter-model-given-input.sh");
        //
        //***************** filter-model-given-input *****************//


//        System.out.println("commande filter:");
//        System.out.println(commande);
//        try {
//            System.out.println("push ok");
//            System.in.read();
//        } catch (IOException ex) {
//            Logger.getLogger(Moses.class.getName()).log(Level.SEVERE, null, ex);
//        }

    }

    public void FileTraduction(String textInTradFile)
    {
        this.textInTradFile = textInTradFile;
        this.textTraductedFile = this.projectRoot + "text.moses.traducted";
        String commande;

        commande = "moses -f "
                   + this.mosesFile + "moses.ini < "
                   + this.textInTradFile + " > " + this.textTraductedFile;

//        Functions.execExtProg(commande, this.tmpDir + "traduction.sh");

        System.out.println("commande traduction:");
        System.out.println(commande);
        try
        {
            System.out.println("push ok");
            System.in.read();
        }
        catch (IOException ex)
        {
            Logger.getLogger(Moses.class.getName()).log(Level.SEVERE,
                                                        null,
                                                        ex);
        }

    }

    public void traductionEvaluation(String textOutEvalFile)
    {

        this.textOutEvalFile = textOutEvalFile;

        TranslateEvaluating.evalBLEU(this.textInTradFile,
                                     textOutEvalFile,
                                     this.textTraductedFile,
                                     this.projectRoot);

        //
        double Fmesure = TranslateEvaluating.evalFMesure(this.textTraductedFile,
                                                         this.textOutEvalFile);
        System.out.print(Fmesure);
        try
        {
            BufferedWriter bw4;
            FileWriter fileOut4 = new FileWriter(
                    this.projectRoot + "evaluation.txt",
                                                 true);
            bw4 = new BufferedWriter(fileOut4);
            bw4.write("\nFMesure: " + Fmesure + "\n");
            bw4.close();
        }
        catch (Exception ex)
        {
            Logger.getLogger(Moses.class.getName()).log(Level.SEVERE,
                                                        null,
                                                        ex);
        }
        //
        //***************** Evaluation *****************//

    }
}

class traitemant
{

    public void table_tarduction(String tab1,
                                 String tab2,
                                 String tabfinal)
    {
        try
        {
            PrintWriter out = new PrintWriter(new FileWriter(tabfinal));
            Reader fich_tab1 = new FileReader(tab1);
            BufferedReader br = new BufferedReader(fich_tab1);
            String ligne;

            while ((ligne = br.readLine()) != null)
            {
                boolean trouve = false;
                Reader fich_tab2 = new FileReader(tab2);
                BufferedReader br2 = new BufferedReader(fich_tab2);
                String ligne2;
                while ((ligne2 = br2.readLine()) != null && trouve == false)
                {
                    StringTokenizer st = new StringTokenizer(ligne,
                                                             "|||");
                    StringTokenizer st2 = new StringTokenizer(ligne2,
                                                              "|||");
                    if (st.hasMoreTokens() && st2.hasMoreTokens())
                    {
                        if (st.nextElement().toString().equalsIgnoreCase(
                                st2.nextElement().toString())
                            && st.nextElement().toString().equalsIgnoreCase(
                                st2.nextElement().toString()))
                        {
                            ligne = ligne2;
                            trouve = true;
                        }
                    }
                }
                out.println(ligne);
            }
        }
        catch (Exception e)
        {
            Logger.getLogger(Moses.class.getName()).log(Level.SEVERE,
                                                        null,
                                                        e);
        }

    }
}
