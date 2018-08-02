package uc.edu.vuhi.pokerprojectapp.DTO;

import android.content.Context;

import java.security.SecureRandom;

public class Deck {

    private final Card[] deck;
    private int currentCard;
    private static final int NUMBER_OF_CARDS = 52;
    private static final SecureRandom generator = new SecureRandom();

    public Deck(Context context)
    {
        deck = new Card[NUMBER_OF_CARDS];
        currentCard = 0;
        int count = 0;

        for(Card.Suit suit: Card.Suit.values())
        {
            for (Card.Face face: Card.Face.values())
            {
                deck[count] = new Card(face, suit, context);
                ++count;
            }
        }
    }

    public void shuffle()
    {
        for (int first = 0; first < deck.length; first++)
        {
            int second = generator.nextInt(NUMBER_OF_CARDS);
            Card temp = deck[first];
            deck[first] = deck[second];
            deck[second] = temp;
        }
        currentCard = 0;
    }

    public Card deal()
    {
        if (currentCard < deck.length)
            return deck[currentCard++];
        else
            return null;
    }
}
