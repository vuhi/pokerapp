package uc.edu.vuhi.pokerprojectapp.DTO;

import android.content.Context;
import android.support.test.InstrumentationRegistry;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class CardTest {
    private Context context;

    @Before
    public void setup() {
        context = InstrumentationRegistry.getContext();
    }

    @Test
    public void getFace() {
        assertEquals(givenHeartAceCard().getFace(), Card.Face.ace );
    }

    @Test
    public void getSuit() {
        assertEquals(givenHeartAceCard().getSuit(), Card.Suit.heart );
    }

    @Test
    public void getCardImageId() {
        int cardImageId = givenHeartAceCard().getCardImageId();
        int aceHeartId = this.context.getResources().getIdentifier("ace_heart", "drawable", this.context.getPackageName());
        assertEquals(cardImageId, aceHeartId);
    }

    private Card givenHeartAceCard() {
        return new Card(Card.Face.ace, Card.Suit.heart, context);
    }
}