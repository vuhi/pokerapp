package uc.edu.vuhi.pokerprojectapp.DTO;

import android.content.Context;
import android.support.test.InstrumentationRegistry;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class HandTest {

    private Context context;

    @Before
    public void setup() {
        context = InstrumentationRegistry.getContext();
    }

    @Test
    public void placeCardInHand() {
        Hand hand = new Hand();
        Card heartAceCard = givenHeartAceCard();
        hand.placeCardInHand(heartAceCard, 0);
        assertNotNull(hand.cards);
        assertEquals(hand.cards[0].getFace(), Card.Face.ace);
        assertEquals(hand.cards[0].getSuit(), Card.Suit.heart);
    }

    @Test
    public void getRankName() {

        Hand hand = new Hand();
        Card heartAceCard = givenHeartAceCard();
        Card clubAceCard = givenClubAceCard();
        Card diamonAceCard = givenDiamonAceCard();
        Card spadeTwoCard = givenSpadeTwoCard();
        Card heartThreeCard = givenHeartThreeCard();

        hand.placeCardInHand(heartAceCard, 0);
        hand.placeCardInHand(clubAceCard, 4);
        hand.placeCardInHand(diamonAceCard, 1);
        hand.placeCardInHand(spadeTwoCard, 3);
        hand.placeCardInHand(heartThreeCard, 2);
        hand.evaluateHand();

        assertNotNull(hand.getRankName());
        assertNotEquals("No Pair", hand.getRankName());
        assertEquals("Three of a Kind", hand.getRankName());
    }

    @Test
    public void getScore() {
        Hand hand = new Hand();
        Card heartAceCard = givenHeartAceCard();
        Card clubAceCard = givenClubAceCard();
        Card diamonAceCard = givenDiamonAceCard();
        Card spadeTwoCard = givenSpadeTwoCard();
        Card heartThreeCard = givenHeartThreeCard();

        hand.placeCardInHand(heartAceCard, 0);
        hand.placeCardInHand(clubAceCard, 4);
        hand.placeCardInHand(diamonAceCard, 1);
        hand.placeCardInHand(spadeTwoCard, 3);
        hand.placeCardInHand(heartThreeCard, 2);
        hand.evaluateHand();

        assertNotNull(hand.getScore());
        assertNotEquals(0, hand.getScore());
        assertNotEquals(1, hand.getScore());
        assertNotEquals(2, hand.getScore());
        assertNotEquals(3, hand.getScore());
        assertEquals(4, hand.getScore());
        assertTrue(hand.getScore() < 5);
    }


    private Card givenHeartAceCard() {
        return new Card(Card.Face.ace, Card.Suit.heart, context);
    }

    private Card givenClubAceCard() {
        return new Card(Card.Face.ace, Card.Suit.club, context);
    }

    private Card givenHeartThreeCard() {
        return new Card(Card.Face.three, Card.Suit.heart, context);
    }

    private Card givenSpadeTwoCard() {
        return new Card(Card.Face.two, Card.Suit.spade, context);
    }

    private Card givenDiamonAceCard() {
        return new Card(Card.Face.ace, Card.Suit.diamond, context);
    }
}