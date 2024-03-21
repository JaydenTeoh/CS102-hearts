package com.example.gameplay;
import com.example.players.*;
import com.example.exceptions.*;
import com.example.app.*;
import com.example.pokercards.*;

//Variables:
//numPoints: Stores the number of points accumulated in the trick.
//cardsInTrick: ArrayList storing the cards currently in the trick.
//leadingCardOfTrick: Stores the Card that was played first in a trick.
//winningCardOfTrick: Stores the Card that won the trick.

//Constructors:
//Trick(List<Card> cards, List<Player> players): Initializes a new Trick object with the specified list of cards and players. Sets up the trick's initial state, including cards in the trick, leading card, winning card, and calculates the number of points.
//Methods:
//addCardToTrick(Card card): Adds a card to the trick.
//getCardsInTrick(): ArrayList<Card>: Returns the cards currently in the trick.
//setLeadingCard(Card card): Sets the leading card of the trick.
//getLeadingCard(): Card: Returns the leading card of the trick.
//setWinningCard(Card card): Sets the winning card of the trick.
//getWinningCard(): Card: Returns the winning card of the trick.
//setNumPoints(): Calculates and sets the number of points in the trick.
//getNumPoints(): int: Returns the number of points accumulated in the trick.
//returnWinningCardIndex(): int: Return the index of the winning card in the cardsInTrick list

import java.util.ArrayList;
import java.util.List;

public class Trick {
    private int numPoints;
    private ArrayList<Card> cardsInTrick;

    // Constructor
    public Trick(ArrayList<Player> players) {
        this.cardsInTrick = new ArrayList<Card>();
        this.numPoints = 0; // Initialize numPoints to 0
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

    // Method to get the index of the winning card of the trick
    public int getWinningCardIndex() {
        Suit leadSuit = getLeadingCard().getSuit();
        int winningCardIndex = -1;
        Rank highestRank = null; // Initialize with a low value

        for (int i = 0; i < cardsInTrick.size(); i++) {
            Card card = cardsInTrick.get(i);
            if (card.getSuit() == leadSuit) {
                // Card is of the lead suit
                if (highestRank == null || card.getRank().compareTo(highestRank) > 0) {
                    // This card is currently the highest of the lead suit
                    winningCardIndex = i;
                    highestRank = card.getRank();
                }
            } else if (winningCardIndex == -1) {
                // No cards of lead suit played yet, consider this card as winning card
                winningCardIndex = i;
                highestRank = card.getRank();
            } else if (card.getSuit() != leadSuit && card.getRank().compareTo(highestRank) > 0) {
                // Card is not of the lead suit, but it has a higher rank
                winningCardIndex = i;
                highestRank = card.getRank();
            }
        }
        return winningCardIndex;
    }


    // Method to calculate and set the number of points in the trick
    private void setNumPoints() {
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
}

