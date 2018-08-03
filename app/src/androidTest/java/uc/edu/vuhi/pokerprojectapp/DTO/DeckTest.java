package uc.edu.vuhi.pokerprojectapp.DTO;

import android.content.Context;
import android.support.test.InstrumentationRegistry;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class DeckTest {

    private Context context;

    @Before
    public void setup() {
        context = InstrumentationRegistry.getContext();
    }

    @Test
    public void shuffle() {
        Deck previousDeck = givenDeck();
        givenDeck().shuffle();
        Deck shuffleDeck = givenDeck();
        assertNotEquals(previousDeck, shuffleDeck);
    }

    @Test
    public void deal() {
        Deck deck = givenDeck();
        deck.shuffle();
        Card firstCard = deck.deal();
        Card secondCard = deck.deal();
        assertNotEquals(firstCard, secondCard);
    }

    private Deck givenDeck() {
        return new Deck(context);
    }
}