// Variables:
// int numPoints: Stores the number of points accumulated in the trick.
// ArrayList<Card> cardsInTrick: Stores the cards currently in the trick.
// Player playerHoldingTrick: Represents the player who currently holds the trick.
// Card leadingCardOfTrick: Stores the Card that was played first in a trick.

// Constructors:
// Trick(ArrayList<Cards>): Initializes a new Trick object

// Methods:
// getPlayerHoldingTrick(): Returns the player who played the winning card.
// getNumPoints(): Returns the number of points accumulated in the trick.
// getCardsInTrick(): Returns the cards currently in the trick.
// getLeadingCard(): Return the leading card


import java.util.ArrayList;
import java.util.List;

public class Trick {
    private List<Card> cardsPlayed;  
    private int currentPlayerIndex;  
    private List<Player> players;  // List of players in the game
    private boolean isFirstTrick;  // Flag to track if it's the first trick
 
    private int numPoints;
    private ArrayList<Card> cardsInTrick;
    private Player playerHoldingTrick;

    public Trick(List<Player> players, boolean isFirstTrick) {
        cardsPlayed = new ArrayList<>();
        currentPlayerIndex = 0;
        this.players = players;
        this.isFirstTrick = isFirstTrick;
        
        numPoints = 0;
        cardsInTrick = new ArrayList<>();
        playerHoldingTrick = null;
    }

    // Method for a player to play a card
    public void playCard(Player player, Card card) {
        if (isValidPlay(player, card)) {
            cardsPlayed.add(card);
            player.removeCardFromHand(card);
            cardsInTrick.add(card);
            currentPlayerIndex = (currentPlayerIndex + 1) % 4;
        }
    }

    // Method to validate if the card being played is valid
    private boolean isValidPlay(Player player, Card card) {
        if (isFirstTrick) {
            if (player.equals(players.get(0)) && card.getRank() == Rank.TWO && card.getSuit() == Suit.CLUBS) {
                return true;  // First player must play the 2 of clubs in the first trick
            } else {
                return false; // Other players cannot play in the first trick until 2 of clubs is played
            }
        } else {
            // Implement logic for subsequent tricks (checking hearts, Queen of Spades, etc.)
            return true;
        }
    }

    // Method to determine the winning card in the trick
    public Card getWinningCard() {
    // Initialize the winning card as the first card played
    Card winningCard = cardsPlayed.get(0);
    
    // Initialize the initial suite (if not first trick)
    Suit initialSuite = cardsPlayed.get(0).getSuit();

    // Iterate through the cards to find the winning card
    for (int i = 1; i < cardsPlayed.size(); i++) {
        Card currentCard = cardsPlayed.get(i);
        
        // Check if the current card is of the initial suite
        if (currentCard.getSuit() == initialSuite) {
            // If the current card is higher than the winning card and not a heart or queen of spades,
            // or if the winning card is not of the initial suite, update the winning card
            if (currentCard.compareTo(winningCard) > 0 && !currentCard.isHeart() && !currentCard.isQueenOfSpades()) {
                winningCard = currentCard;
            }
        } else {
            // If the current card is of a different suite and the winning card is not of the initial suite,
            // update the winning card if the current card is higher than the winning card
            if (winningCard.getSuit() != initialSuite && currentCard.compareTo(winningCard) > 0) {
                winningCard = currentCard;
                }
            }
        }
    }

    // Method to get the player who played the winning card
    public Player getPlayerHoldingTrick() {
        // Implement this method based on the rules of the Hearts game
        // Determine which player played the winning card and return that player
        return null;  // Placeholder return value
    }

    // Method to check if the trick is complete
    public boolean isTrickComplete() {
        return cardsPlayed.size() == 4;
    }

    // Method to return the number of points in the trick
    public int getNumPoints() {
        return numPoints;
    }

    // Method to return the cards in the trick
    public ArrayList<Card> getCardsInTrick() {
        return cardsInTrick;
    }

    // Method to return the player holding the trick
    public Player getPlayerHoldingTrick() {
        return playerHoldingTrick;
    }

}
