package uc.edu.vuhi.pokerprojectapp.DTO;

import android.net.Uri;

public class Card {

    public static enum Face
    {
        Two, Three, Four, Five, Six, Seven, Eight, Nine, Ten,
        Jack, Queen, King, Ace
    }
    public static enum Suit
    {
        Clubs, Diamonds, Hearts, Spades
    }

    private final Face face;
    private final Suit suit;
    //private Uri image;
    //private Uri cardbackimage = new ImageIcon(getClass().getResource("/cardimages/BlueBack.png"));

    public Card()
    {
        this.face = null;
        this.suit = null;
    }

    public Card(Face face, Suit suit)
    {
        this.face = face;
        this.suit = suit;
        //this.image = new ImageIcon(getClass().getResource("/cardimages/" + face + suit + ".png"));
    }

    public Face getFace()
    {
        return face;
    }

    public Suit getSuit()
    {
        return suit;
    }

    /*public ImageIcon getCardImage()
    {
        return image;
    }

    public ImageIcon getCardBackImage()
    {
        return cardbackimage;
    }*/

    @Override
    public String toString()
    {
        return String.format("%s of %s", face,suit);
    }

}
