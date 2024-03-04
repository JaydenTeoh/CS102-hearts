//Variables:
//numPoints: Stores the number of points accumulated in the trick.
//cardsInTrick: ArrayList storing the cards currently in the trick.
//playerHoldingTrick: Represents the player who currently holds the trick.
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
//setPlayerHoldingTrick(): Sets the player holding the trick based on the winning card.
//getPlayerHoldingTrick(): Player: Returns the player who holds the trick.

import java.util.ArrayList;
import java.util.List;

public class Trick {
    private int numPoints;
    private ArrayList<Card> cardsInTrick;
    private Player playerHoldingTrick;
    private Card leadingCardOfTrick;
    private Card winningCardOfTrick;
    private List<Player> players;

    // Constructor
    public Trick(List<Player> players) {
        this.cardsInTrick = new ArrayList<>();
        this.players = players;
        this.leadingCardOfTrick = null;
        this.winningCardOfTrick = null;
        setNumPoints();
        setPlayerHoldingTrick();
    }

    // Method to add a card to the trick
    public void addCardToTrick(Card card) {
        cardsInTrick.add(card);
    }

    // Method to return the cards currently in the trick
    public ArrayList<Card> getCardsInTrick() {
        return cardsInTrick;
    }

    // Method to set the leading card of the trick
    public void setLeadingCard(Card card) {
        this.leadingCardOfTrick = card;
    }

    public Card getLeadingCard() {
        return leadingCardOfTrick;
    }

    // Method to set the winning card of the trick
    public void setWinningCard(Card card) {
        this.winningCardOfTrick = card;
    }

    public Card getWinningCard() {
        return winningCardOfTrick;
    }

    // Method to calculate and return the number of points in the trick
    private void setNumPoints() {
        numPoints = 0;
        for (Card card : cardsInTrick) {
            if (card.isHeart() || card.isQueenOfSpades()) {
                numPoints++;
            }
        }
    }

    public int getNumPoints() {
        return numPoints;
    }

    // Method to set the player holding the trick
    private void setPlayerHoldingTrick() {
        for (Player player : players) {
            if (player.getHand().contains(winningCardOfTrick)) {
                playerHoldingTrick = player;
                return;
            }
        }
        // Handle the case where the winning card is not found in any player's hand
        playerHoldingTrick = null;
    }

    public Player getPlayerHoldingTrick() {
        return playerHoldingTrick;
    }
}

