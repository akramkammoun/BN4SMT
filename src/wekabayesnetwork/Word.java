/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package wekabayesnetwork;

/**
 *
 * @author akram
 */
public class Word
{

    public int count = 1;
    public String text;

    public Word(String text)
    {
        this.text = text;
    }

    @Override
    public boolean equals(Object wordInstance)
    {
        return (this.text.equals(((Word) wordInstance).text));
    }
}
