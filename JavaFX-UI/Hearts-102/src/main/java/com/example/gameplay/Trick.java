package com.example.gameplay;
import com.example.app.*;
import com.example.pokercards.*;

import java.util.ArrayList;
import java.util.List;

public class Trick {
    private int numPoints;
    private int trickNo;
    private boolean heartsBroken;
    private int playerStartingFirst;
    private List<Card> cardsInTrick;


    // Constructor
    public Trick(int trickNo, boolean heartsBroken, int playerStartingFirst) {
        cardsInTrick = new ArrayList<Card>();
        numPoints = 0; // Initialize numPoints to 0
        this.trickNo = trickNo;
        this.heartsBroken = heartsBroken;
        this.playerStartingFirst = playerStartingFirst;
    }

    // Method to add a card to the trick
    public void addCardToTrick(Card card) {
        cardsInTrick.add(card);
    }

    // Method to return the cards currently in the trick
    public List<Card> getCardsInTrick() {
        return cardsInTrick;
    }

    // Method to get the leading card of the trick
    public Card getLeadingCard() {
        if (!cardsInTrick.isEmpty()) {
            return cardsInTrick.get(0);
        } else {
            return null; // No cards in the trick, so no leading card
        }
    }

    // Method to set the leading suit of the trick
    public Suit getLeadingSuit() {
        if (!cardsInTrick.isEmpty()) {
            return cardsInTrick.get(0).getSuit();
        } else {
            return null; // No cards in the trick, so no leading suit
        }
    }

    // Method to get the index of the winning card of the trick (0 - 3, where 0 is the leading card)
    public int getWinningCardIndex() {
        Card leadingCard = this.getLeadingCard();
        Suit leadSuit = leadingCard.getSuit();
        int winningCardIndex = 0; // leading card is winnning card unless higher card of that suit found
        Rank highestRank = leadingCard.getRank();

        for (int i = 1; i < cardsInTrick.size(); i++) {
            Card card = cardsInTrick.get(i);
            if (card.getSuit() == leadSuit) {
                // Card is of the lead suit
                if (card.getRank().compareTo(highestRank) > 0) {
                    // This card is currently the highest of the lead suit
                    winningCardIndex = i;
                    highestRank = card.getRank();
                }
            }
        }

        return winningCardIndex;
    }


    // Method to calculate and set the number of points in the trick
    public void setNumPoints() {
        numPoints = 0;
        for (Card card : cardsInTrick) {
            if (card.getSuit().equals(Suit.HEARTS)) {
                numPoints++;
            } else if (card.isSameAs(Game.QUEEN_OF_SPADES)) {
                numPoints += 13;
            }
        }
    }

    // Method to get the number of points accumulated in the trick
    public int getNumPoints() {
        return numPoints;
    }

    public int getTrickNo() {
        return trickNo;
    }

    public void setTrickNo(int trickNo) {
        this.trickNo = trickNo;
    }

    public boolean isHeartsBroken() {
        return heartsBroken;
    }

    public int getWinner() {
        if (cardsInTrick.size() != 4) {
            return -1;
        }
        
        int winningCardIndex = getWinningCardIndex();
        return (winningCardIndex + playerStartingFirst) % Game.NUM_PLAYERS;
    }
}

