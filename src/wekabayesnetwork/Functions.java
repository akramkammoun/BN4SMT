/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package wekabayesnetwork;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

/**
 *
 * @author akram
 */
public class Functions
{

    private Functions()
    {
    }
    public static double LOG10_2 = Math.log10(2);

    public static void copyFile(String fileInPath,
                                String fileOutPath)
    {
        File inputFile = new File(fileInPath);
        File outputFile = new File(fileOutPath);

        try
        {
            FileReader in = new FileReader(inputFile);
            FileWriter out = new FileWriter(outputFile);
            int c;

            while ((c = in.read()) != -1)
            {
                out.write(c);
            }

            in.close();
            out.close();
        }
        catch (IOException ex)
        {
            //happened with InputStreamReader
            Logger.getLogger(Functions.class.getName()).log(Level.SEVERE,
                                                            null,
                                                            ex);
        }

    }

    //return an integer that would be less than +2 147 483 647(int)
    public static int getFileLinesCount(String myfilePath)
    {
        Runtime execCmd = Runtime.getRuntime();
        Process process;
        String[] cmdArgs =
        {
            "/bin/sh", "-c", "wc -l " + myfilePath + " | cut -d' ' -f1"
        };
        BufferedReader cmdReader = null;

        int fileLinesCount = 0;

        try
        {
            process = execCmd.exec(cmdArgs);

            process.waitFor();

            cmdReader = new BufferedReader(new InputStreamReader(
                    process.getInputStream()));

            String cmdOut = "";
            //cmdOut has just one ligne which contains the corpus lines counts
            if ((cmdOut = cmdReader.readLine()) != null)
            {
                fileLinesCount = Integer.parseInt(cmdOut);
            }

            cmdReader.close();
        }
        catch (IOException ex)
        {
            //happened with InputStreamReader
            Logger.getLogger(Functions.class.getName()).log(Level.SEVERE,
                                                            null,
                                                            ex);
        }
        catch (InterruptedException ex)
        {
            Logger.getLogger(Functions.class.getName()).log(Level.SEVERE,
                                                            null,
                                                            ex);
        }
        catch (Exception ex)
        {
            Logger.getLogger(Functions.class.getName()).log(Level.SEVERE,
                                                            null,
                                                            ex);
        }

        return fileLinesCount;
    }

    public static int getFileLinesCount2(String myfilePath)
    {


        InputStreamReader is;
        BufferedReader br;
        String line;
        int lignCount = 0;

        try
        {
            is = new InputStreamReader(new FileInputStream(myfilePath),
                                       "UTF-8");
            br = new BufferedReader(is);

            while ((line = br.readLine()) != null)
            {
                lignCount++;
            }


        }
        catch (IOException ex)
        {
            Logger.getLogger(Functions.class.getName()).log(Level.SEVERE,
                                                            null,
                                                            ex);
        }
        catch (Exception ex)
        {
            Logger.getLogger(Functions.class.getName()).log(Level.SEVERE,
                                                            null,
                                                            ex);
        }

        return lignCount;
    }

    public static double log2(double num)
    {
        return (Math.log10(num) / Functions.LOG10_2);
    }

    public static String deleteExtension(String myString)
    {
        int pointIndex = myString.indexOf('.');

        if (pointIndex != -1)
        {
            return myString.substring(0,
                                      pointIndex);
        }
        else
        {
            return myString;
        }
    }

    public static void execExtProg(String args)
    {
        try
        {
            Runtime runtime = Runtime.getRuntime();
            final Process process = runtime.exec(args);

            process.waitFor();

            // Consommation de la sortie standard de l'application externe dans un Thread separe
            new Thread()
            {
                @Override
                public void run()
                {
                    try
                    {
                        BufferedReader reader = new BufferedReader(new InputStreamReader(
                                process.getInputStream()));
                        String line = "";
                        String out = "";
                        try
                        {
                            while ((line = reader.readLine()) != null)
                            {
                                out += line;
                            }
                        }
                        finally
                        {
                            reader.close();
                            System.out.println(out);
                        }
                    }
                    catch (IOException ex)
                    {
                        Logger.getLogger(Functions.class.getName()).log(
                                Level.SEVERE,
                                                                        null,
                                                                        ex);
                    }
                }
            }.start();


            // Consommation de la sortie d'erreur de l'application externe dans un Thread separe
            new Thread()
            {
                @Override
                public void run()
                {
                    try
                    {
                        BufferedReader reader = new BufferedReader(new InputStreamReader(
                                process.getErrorStream()));
                        String line = "";
                        String out = "";
                        try
                        {
                            while ((line = reader.readLine()) != null)
                            {
                                out += line;
                            }
                        }
                        finally
                        {
                            reader.close();
                            System.err.println(out);
                        }
                    }
                    catch (IOException ex)
                    {
                        Logger.getLogger(Functions.class.getName()).log(
                                Level.SEVERE,
                                                                        null,
                                                                        ex);
                    }
                }
            }.start();
        }
        catch (InterruptedException ex)
        {
            Logger.getLogger(Functions.class.getName()).log(Level.SEVERE,
                                                            null,
                                                            ex);
        }
        catch (IOException ex)
        {
            Logger.getLogger(Functions.class.getName()).log(Level.SEVERE,
                                                            null,
                                                            ex);
        }
    }

    public static void execExtProg(String args,
                                   String FilePathTmp)
    {

        OutputStreamWriter os;
        BufferedWriter bw;


        (new File(FilePathTmp)).delete();
        try
        {

            os = new OutputStreamWriter(new FileOutputStream(FilePathTmp),
                                        "UTF-8");
            bw = new BufferedWriter(os);

            bw.write(args);

            bw.close();
            os.close();

        }
        catch (IOException ex)
        {
            Logger.getLogger(Functions.class.getName()).log(Level.SEVERE,
                                                            null,
                                                            ex);
        }

        String[] argsTab =
        {
            "/bin/sh", FilePathTmp
        };
        Functions.execExtProg(argsTab);
    }

    public static void execExtProg(String[] args)
    {
        try
        {
            Runtime runtime = Runtime.getRuntime();
            final Process process = runtime.exec(args);

            process.waitFor();

            // Consommation de la sortie standard de l'application externe dans un Thread separe
            new Thread()
            {
                @Override
                public void run()
                {
                    try
                    {
                        BufferedReader reader = new BufferedReader(new InputStreamReader(
                                process.getInputStream()));
                        String line = "";
                        String out = "";
                        try
                        {
                            while ((line = reader.readLine()) != null)
                            {
                                out += line;
                            }
                        }
                        finally
                        {
                            reader.close();
                            System.out.println(out);
                        }
                    }
                    catch (IOException ex)
                    {
                        Logger.getLogger(Functions.class.getName()).log(
                                Level.SEVERE,
                                                                        null,
                                                                        ex);
                    }
                }
            }.start();


            // Consommation de la sortie d'erreur de l'application externe dans un Thread separe
            new Thread()
            {
                @Override
                public void run()
                {
                    try
                    {
                        BufferedReader reader = new BufferedReader(new InputStreamReader(
                                process.getErrorStream()));
                        String line = "";
                        String out = "";
                        try
                        {
                            while ((line = reader.readLine()) != null)
                            {
                                out += line;
                            }
                        }
                        finally
                        {
                            reader.close();
                            System.err.println(out);
                        }
                    }
                    catch (IOException ex)
                    {
                        Logger.getLogger(Functions.class.getName()).log(
                                Level.SEVERE,
                                                                        null,
                                                                        ex);
                    }
                }
            }.start();
        }
        catch (InterruptedException ex)
        {
            Logger.getLogger(Functions.class.getName()).log(Level.SEVERE,
                                                            null,
                                                            ex);
        }
        catch (IOException ex)
        {
            Logger.getLogger(Functions.class.getName()).log(Level.SEVERE,
                                                            null,
                                                            ex);
        }
    }

    public static int maxIndexDouble(double[] t)
    {
        double maximum = t[0];   // start with the first value
        int indexMax = 0;
        for (int i = 1; i < t.length; i++)
        {
            if (t[i] > maximum)
            {
                indexMax = i;
                maximum = t[i];   // new maximum
            }
        }
        return indexMax;
    }

    public static int maxIndexDouble(float[] t)
    {
        float maximum = t[0];   // start with the first value
        int indexMax = 0;
        for (int i = 1; i < t.length; i++)
        {
            if (t[i] > maximum)
            {
                indexMax = i;
                maximum = t[i];   // new maximum
            }
        }
        return indexMax;
    }

    public static int[] sortIndexTabDouble(double[] t)
    {

        int[] indexSortedTab = new int[t.length];

        double maximum;   // start with the first value
        int indexMax;

        for (int j = 0; j < t.length; j++)
        {
            maximum = t[0];
            indexMax = 0;

            for (int i = 1; i < t.length; i++)
            {
                if (t[i] > maximum)
                {
                    indexMax = i;
                    maximum = t[i];   // new maximum
                }
            }
            indexSortedTab[j] = indexMax;
            t[indexMax] = -1;
        }

        return indexSortedTab;
    }

    public static int[] sortIndexTabDouble(float[] t)
    {

        int[] indexSortedTab = new int[t.length];

        float maximum;   // start with the first value
        int indexMax;

        for (int j = 0; j < t.length; j++)
        {
            maximum = t[0];
            indexMax = 0;

            for (int i = 1; i < t.length; i++)
            {
                if (t[i] > maximum)
                {
                    indexMax = i;
                    maximum = t[i];   // new maximum
                }
            }
            indexSortedTab[j] = indexMax;
            t[indexMax] = -1;
        }

        return indexSortedTab;
    }

    public static ArrayList<ArrayList<String>> fileToLists(File textFile,
                                                           String delim)
    {
        ArrayList<ArrayList<String>> lists = new ArrayList<ArrayList<String>>();

        InputStreamReader is;
        BufferedReader br;
        String line;

        try
        {
            is = new InputStreamReader(new FileInputStream(textFile),
                                       "UTF-8");
            br = new BufferedReader(is);

            while ((line = br.readLine()) != null)
            {
                lists.add(new ArrayList(Arrays.asList(line.split(delim))));
            }


        }
        catch (IOException ex)
        {
            Logger.getLogger(Functions.class.getName()).log(Level.SEVERE,
                                                            null,
                                                            ex);
        }
        catch (Exception ex)
        {
            Logger.getLogger(Functions.class.getName()).log(Level.SEVERE,
                                                            null,
                                                            ex);
        }


        return lists;
    }

    public static ArrayList<String> fileToList(File textFile)
    {
        ArrayList<String> lists = new ArrayList<String>();

        InputStreamReader is;
        BufferedReader br;
        String line;

        try
        {
            is = new InputStreamReader(new FileInputStream(textFile),
                                       "UTF-8");
            br = new BufferedReader(is);

            while ((line = br.readLine()) != null)
            {
                lists.add(line);
            }
        }
        catch (IOException ex)
        {
            Logger.getLogger(Functions.class.getName()).log(Level.SEVERE,
                                                            null,
                                                            ex);
        }
        catch (Exception ex)
        {
            Logger.getLogger(Functions.class.getName()).log(Level.SEVERE,
                                                            null,
                                                            ex);
        }


        return lists;
    }

    public static ArrayList<String> fileToStrings(String textFilePath)
    {

        File textFile = new File(textFilePath);

        ArrayList<String> fileStringsList = new ArrayList<String>();

        InputStreamReader is;
        BufferedReader br;
        String line;

        try
        {
            is = new InputStreamReader(new FileInputStream(textFile),
                                       "UTF-8");
            br = new BufferedReader(is);

            while ((line = br.readLine()) != null)
            {
                fileStringsList.add(line);
            }
        }
        catch (IOException ex)
        {
            Logger.getLogger(Functions.class.getName()).log(Level.SEVERE,
                                                            null,
                                                            ex);
        }
        catch (Exception ex)
        {
            Logger.getLogger(Functions.class.getName()).log(Level.SEVERE,
                                                            null,
                                                            ex);
        }

        return fileStringsList;
    }

    public static String listToString(ArrayList array,
                                      String delim)
    {
        String textResult = "";

        if (array != null && !array.isEmpty())
        {
            int i;
            for (i = 0; i < array.size() - 1; i++)
            {
                textResult += array.get(i) + delim;
            }

            textResult += array.get(i);
        }

        return textResult;
    }

    public static ArrayList<String> StringToList(String line,
                                                 String delim)
    {
        ArrayList<String> result = new ArrayList<String>();

        String[] lineSplited = line.split(delim);
        result.addAll(Arrays.asList(lineSplited));

        return result;
    }

    public static void splitFile(String fileInPath,
                                 String fileOutPath,
                                 int firstLine,
                                 int lastLine)
    {

        InputStreamReader is;
        BufferedReader br;

        File fileOut = new File(fileOutPath);

        OutputStreamWriter os;
        BufferedWriter bw;

        fileOut.delete();


        try
        {
            is = new InputStreamReader(new FileInputStream(fileInPath),
                                       "UTF-8");
            br = new BufferedReader(is);

            os = new OutputStreamWriter(new FileOutputStream(fileOutPath),
                                        "UTF-8");
            bw = new BufferedWriter(os);

            String line = "";
            int lineCount = 0;

            while ((line = br.readLine()) != null && lineCount <= lastLine)
            {
                if (lineCount >= firstLine && lineCount <= lastLine)
                {
                    bw.write(line + "\n");
                }

                lineCount++;
            }


            br.close();
            is.close();

            bw.close();
            os.close();

        }
        catch (IOException ex)
        {
            Logger.getLogger(Functions.class.getName()).log(Level.SEVERE,
                                                            null,
                                                            ex);
        }
    }

    public static void stringToFile(String line,
                                    String fileOutPath,
                                    boolean append)
    {

        File fileOut = new File(fileOutPath);

        OutputStreamWriter os;
        BufferedWriter bw;

        try
        {
            os = new OutputStreamWriter(new FileOutputStream(fileOut,
                                                             append),
                                        "UTF-8");
            bw = new BufferedWriter(os);

            bw.write(line);

            bw.close();
            os.close();
        }
        catch (Exception e)
        {
            Logger.getLogger(Functions.class.getName()).log(Level.SEVERE,
                                                            null,
                                                            e);
        }

    }

    public static void stringListToFile(ArrayList<String> listIn,
                                        String fileOutPath,
                                        boolean append)
    {
        File fileOut = new File(fileOutPath);

        OutputStreamWriter os;
        BufferedWriter bw;

        try
        {
            os = new OutputStreamWriter(new FileOutputStream(fileOut,
                                                             append),
                                        "UTF-8");
            bw = new BufferedWriter(os);

            for (int i = 0; i < listIn.size(); i++)
            {
                bw.write(listIn.get(i) + "\n");
            }

            bw.close();
            os.close();
        }
        catch (Exception e)
        {
            Logger.getLogger(Functions.class.getName()).log(Level.SEVERE,
                                                            null,
                                                            e);
        }
    }

    public static void delWords(String filePath,
                                int wordsDelCount,
                                String fileOutPath)
    {
        InputStreamReader is;
        BufferedReader br;

        File fileOut = new File(fileOutPath);

        OutputStreamWriter os;
        BufferedWriter bw;

        fileOut.delete();

        try
        {
            is = new InputStreamReader(new FileInputStream(filePath),
                                       "UTF-8");
            br = new BufferedReader(is);

            os = new OutputStreamWriter(new FileOutputStream(fileOutPath),
                                        "UTF-8");
            bw = new BufferedWriter(os);

            String line = "";

            while ((line = br.readLine()) != null)
            {
                ArrayList<String> wordsExits = new ArrayList<String>();
                String[] lineArray = line.split(" ");

                for (int i = wordsDelCount; i < lineArray.length; i++)
                {
                    wordsExits.add(lineArray[i]);
                }
                bw.write(Functions.listToString(wordsExits,
                                                " ") + "\n");
            }


            br.close();
            is.close();

            bw.close();
            os.close();

        }
        catch (IOException ex)
        {
            Logger.getLogger(Functions.class.getName()).log(Level.SEVERE,
                                                            null,
                                                            ex);
        }
    }

    public static void serializeObject(Object objet,
                                       String outFilePath)
    {
        try
        {
            // ouverture d'un flux de sortie vers le fichier "personne.serial"
            FileOutputStream fos = new FileOutputStream(outFilePath);

            // création d'un "flux objet" avec le flux fichier
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            try
            {
                // sérialisation : écriture de l'objet dans le flux de sortie
                oos.writeObject(objet);
                // on vide le tampon
                oos.flush();
                System.out.println(objet + " a ete serialise");
            }
            finally
            {
                //fermeture des flux
                try
                {
                    oos.close();
                }
                finally
                {
                    fos.close();
                }
            }
        }
        catch (IOException ioe)
        {
            ioe.printStackTrace();
        }
    }

    public static Object deserialize(String inFilePath)
    {
        Object p = null;
        try
        {
            // ouverture d'un flux d'entrée depuis le fichier "personne.serial"
            FileInputStream fis = new FileInputStream(inFilePath);
            // création d'un "flux objet" avec le flux fichier
            ObjectInputStream ois = new ObjectInputStream(fis);
            try
            {
                // désérialisation : lecture de l'objet depuis le flux d'entrée
                p = ois.readObject();
            }
            finally
            {
                // on ferme les flux
                try
                {
                    ois.close();
                }
                finally
                {
                    fis.close();
                }
            }
        }
        catch (IOException ioe)
        {
            ioe.printStackTrace();
        }
        catch (ClassNotFoundException cnfe)
        {
            cnfe.printStackTrace();
        }
        if (p != null)
        {
            System.out.println(p + " a ete deserialise");
        }
        return null;
    }

    private static void updateTextArea(final String text,
                                       final JTextArea textArea)
    {
        SwingUtilities.invokeLater(new Runnable()
        {
            public void run()
            {
                textArea.append(text);
            }
        });
    }

    public static void redirectSystemStreams(final JTextArea textArea)
    {
        OutputStream out = new OutputStream()
        {
            @Override
            public void write(int b) throws IOException
            {
                updateTextArea(String.valueOf((char) b),
                               textArea);
            }

            @Override
            public void write(byte[] b,
                              int off,
                              int len) throws IOException
            {
                updateTextArea(new String(b,
                                          off,
                                          len),
                               textArea);
            }

            @Override
            public void write(byte[] b) throws IOException
            {
                write(b,
                      0,
                      b.length);
            }
        };

        System.setOut(new PrintStream(out,
                                      true));
        System.setErr(new PrintStream(out,
                                      true));
    }

    public static double getFreeMem()
    {
        return (double) Runtime.getRuntime().freeMemory() / 1048576;
    }

    public static double getTotalMem()
    {
        return (double) Runtime.getRuntime().totalMemory() / 1048576;
    }

    public static double getMaxMem()
    {
        return (double) Runtime.getRuntime().maxMemory() / 1048576;
    }

    public static double getUsedMem()
    {
        return getTotalMem() - getFreeMem();
    }

    static public boolean deleteDirectory(File path)
    {
        boolean resultat = true;

        if (path.exists())
        {
            File[] files = path.listFiles();
            for (int i = 0; i < files.length; i++)
            {
                if (files[i].isDirectory())
                {
                    resultat &= deleteDirectory(files[i]);
                }
                else
                {
                    resultat &= files[i].delete();
                }
            }
        }
        resultat &= path.delete();
        return (resultat);
    }
}
