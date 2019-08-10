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
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Reader;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author akram
 */
public class TranslateEvaluating
{

    public static void evalBLEU(String textIn,
                                String textEval,
                                String textTranslate,
                                String evalDir)
    {
        String script = "scripts/scripts_moses/";
        //        new File(repertoir_travail + "/evaluation").mkdir();
        String evaluation_fichier = evalDir + "evaluation.txt";
        (new File(evaluation_fichier)).delete();
        evalDir += "bleu/";
        (new File(evalDir)).mkdir();


        Fichier trait = new Fichier();

        try
        {
            trait.prepar_fich_evaluation(textTranslate,
                                         evalDir + "corpus.traduction.prp",
                                         "tst");
            trait.prepar_fich_evaluation(textEval,
                                         evalDir + "corpus.ref.prp",
                                         "ref");
            trait.prepar_fich_evaluation(textIn,
                                         evalDir + "corpus.src.prp",
                                         "src");
        }
        catch (Exception e)
        {
            Logger.getLogger(TranslateEvaluating.class.getName()).log(
                    Level.SEVERE,
                                                                      null,
                                                                      e);
        }

        Process proc = null;
        try
        {
            proc = Runtime.getRuntime().exec("/bin/bash");
        }
        catch (Exception e)
        {
            Logger.getLogger(TranslateEvaluating.class.getName()).log(
                    Level.SEVERE,
                                                                      null,
                                                                      e);
        }

        if (proc != null)
        {
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    proc.getInputStream()));
            PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(
                    proc.getOutputStream())),
                                              true);
            String commande = script + "mteval-v11b.pl -r " + evalDir + "corpus.ref.prp -s "
                              + evalDir + "corpus.src.prp -t " + evalDir + "corpus.traduction.prp >& " + evaluation_fichier;
//            System.out.print(commande);
            out.println(commande);
            out.println("exit");
            try
            {
                proc.waitFor();
                out.close();
                //System.out.print(in);
            }
            catch (Exception e)
            {
                Logger.getLogger(TranslateEvaluating.class.getName()).log(
                        Level.SEVERE,
                                                                          null,
                                                                          e);
            }
        }
    }

    public static double evalFMesure(String textIn,
                                     String textRef)
    {
        ArrayList<ArrayList<String>> textInList = Functions.fileToLists(new File(
                textIn),
                                                                        " ");
        ArrayList<ArrayList<String>> textRefList = Functions.fileToLists(new File(
                textRef),
                                                                         " ");

        double rappelMoy = 0, precisionMoy = 0, effMoy;

        for (int i = 0; i < textInList.size(); i++)
        {
            int motsCorrectsTrouves = 0;
            for (int j = 0; j < textInList.get(i).size(); j++)
            {
                if (textRefList.get(i).contains(textInList.get(i).get(j)))
                {
                    motsCorrectsTrouves++;
                }
            }

            rappelMoy += ((double) motsCorrectsTrouves / textRefList.get(i).size());
            precisionMoy += ((double) motsCorrectsTrouves / textInList.get(i).size());
        }

        rappelMoy /= (double) textInList.size();
        precisionMoy /= (double) textInList.size();

        effMoy = (double) (2 * rappelMoy * precisionMoy) / (rappelMoy + precisionMoy);

        return effMoy;
    }
}

class Fichier
{

    public void copie_fichier(String source,
                              String destination)
    {

        String ligne;
        try
        {
            Reader fich_mot = new FileReader(source);
            BufferedReader br = new BufferedReader(fich_mot);
            PrintWriter out = new PrintWriter(new FileWriter(destination));
            while ((ligne = br.readLine()) != null)
            {
                out.println(ligne);
            }
            out.close();
        }
        catch (Exception e)
        {
            Logger.getLogger(TranslateEvaluating.class.getName()).log(
                    Level.SEVERE,
                                                                      null,
                                                                      e);
        }
    }

    public void prepar_fich_evaluation(String fichi_in,
                                       String fichier_out,
                                       String type)
    {

        (new File(fichier_out)).delete();

        String ligne;
        try
        {
            Reader fich_mot = new FileReader(fichi_in);
            BufferedReader br = new BufferedReader(fich_mot);
            PrintWriter out = new PrintWriter(new FileWriter(fichier_out));
            out.println(
                    "<" + type + "set setid=\"\" srclang=\"english\" trglang=\"french\">");
            out.println("<DOC docid=\"test\" sysid=\"eval\">");
            int i = 1;
            while ((ligne = br.readLine()) != null)
            {
                out.println("<seg id=\"" + i + "\">" + ligne + " </seg>");
                i++;
            }
            out.println("</DOC>");
            out.println("</" + type + "set>");
            out.close();
        }
        catch (Exception e)
        {
            Logger.getLogger(TranslateEvaluating.class.getName()).log(
                    Level.SEVERE,
                                                                      null,
                                                                      e);
        }
    }
}
