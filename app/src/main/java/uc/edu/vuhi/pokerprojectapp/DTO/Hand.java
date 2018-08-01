package uc.edu.vuhi.pokerprojectapp.DTO;

import java.util.Arrays;

public class Hand {

    public static enum Rank
    {
        NoPair, LowPair, HighPair, TwoPair, ThreeOfAKind, Straight,
        Flush, FullHouse, FourOfAKind, StraightFlush;
    };
    private final int HAND_SIZE = 5;
    public Card[] cards = new Card[HAND_SIZE];
    public Rank rank;
    private String rankName;
    private int score;
    private int flushCount, straightCount, count1, count2;
    private Card pair1HighCard, pair2HighCard;

    public int getScore(){
        return score;
    }

    public String getRankName(){
        return rankName;
    }

    public boolean hasAnyDisCard()
    {
        for (Card card: cards) {
            if(card.getIsDiscard()){
                return true;
            }
        }
        return false;
    }

    public void placeCardInHand(Card card, int position)
    {
        cards[position] = card;
    }

    public void evaluateHand()
    {
        getCardCounts();
        if (straightCount == 5 && flushCount == 5)
        {
            rank = Rank.StraightFlush;
            rankName = "Straight Flush";
            score = 15;
        }
        else if (flushCount == 5)
        {
            rank = Rank.Flush;
            rankName = "Flush";
            score = 8;
        }
        else if (straightCount == 5)
        {
            rank = Rank.Straight;
            rankName = "Straight";
            score = 6;
        }
        else if (count1 == 4)
        {
            rank = Rank.FourOfAKind;
            rankName = "Four of a Kind";
            score = 12;
        }
        else if ((count1 == 2 && count2 == 3) || (count1 == 3 && count2 == 2))
        {
            rank = Rank.FullHouse;
            rankName = "Full House";
            score = 10;
        }
        else if (count1 == 3)
        {
            rank = Rank.ThreeOfAKind;
            rankName = "Three of a Kind";
            score = 4;
        }
        else if (count1 == 2 && count2 == 2)
        {
            rank = Rank.TwoPair;
            rankName = "Two Pair";
            score = 2;
        }
        else if (count1 == 2)
        {
            if (pair1HighCard.getFace().compareTo(Card.Face.ten) > 0)
            {
                rank = Rank.HighPair;
                rankName = "High Pair";
                score = 1;
            }
            else
            {
                rank = Rank.LowPair;
                rankName = "Low Pair";
                score = -1;
            }
        }
        else
        {
            rank = Rank.NoPair;
            rankName = "No Pair";
            score = -1;
        }
    }

    private void getCardCounts()
    {
        Boolean useC1 = false, useC2 = false;
        // initialize counts
        flushCount = 1;
        straightCount = 1;
        count1 = 1;
        count2 = 1;

        Arrays.sort(cards, (Card c1, Card c2) -> c1.getFace().compareTo(c2.getFace()));

        for (int i = 0; i < HAND_SIZE - 1; i++)
        {
            // check for flush
            if (cards[i].getSuit() == cards[i+1].getSuit())
                flushCount++;
            // check for straight
            if ((cards[i].getFace().ordinal() == cards[i+1].getFace().ordinal()-1))
            {
                straightCount = straightCount + 1;
                if ((straightCount == 4) &&
                        (cards[4].getFace().equals(Card.Face.ace) && cards[0].getFace().equals(Card.Face.two)))
                    straightCount = 5;
            }
            // count pairs and triplets
            if (cards[i].getFace().equals(cards[i+1].getFace()))
            {
                if (! (useC1 || useC2))
                    useC1 = true;
                if (useC1)
                {
                    count1 += 1;
                    pair1HighCard = cards[i];
                }
                else
                {
                    count2 += 1;
                    pair2HighCard = cards[i];
                }
            }
            else
            {
                if (useC1)
                {
                    useC1 = false;
                    useC2 = true;
                }
            }

        }

    }
}
