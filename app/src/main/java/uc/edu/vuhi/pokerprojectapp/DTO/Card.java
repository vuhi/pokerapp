package uc.edu.vuhi.pokerprojectapp.DTO;

import android.content.Context;

public class Card {

    public static enum Face
    {
        two, three, four, five, six, seven, eight, nine, ten,
        jack, queen, king, ace
    };

    public static enum Suit
    {
        club, diamond, heart, spade
    };

    private final Face face;
    private final Suit suit;
    private Context context;
    private int imageId;
    private boolean isDiscard = false;

    public Card()
    {
        this.face = null;
        this.suit = null;
    }

    public Card(Face face, Suit suit, Context context)
    {
        this.face = face;
        this.suit = suit;
        this.context = context;
        this.imageId = this.context.getResources().getIdentifier(face+"_"+suit, "drawable", this.context.getPackageName());
    }



    public Face getFace()
    {
        return face;
    }

    public Suit getSuit()
    {
        return suit;
    }

    public int getCardImageId()
    {
        return imageId;
    }

    public boolean getIsDiscard(){
        return isDiscard;
    }

    public void setIsDiscard(boolean bool){
        this.isDiscard = bool;
    }

    @Override
    public String toString()
    {
        return String.format("%s of %s", face,suit);
    }
}
