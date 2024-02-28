import java.util.ArrayList;

public class Player {
    public String name;
    private Hand hand;

    public Player(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Hand getHand() {
        return hand;
    }

    public void setHand(Hand hand) {
        this.hand = hand;
    }

    public Card playCard() {
        // not implemented yet
    }

    public ArrayList<Card> getPlayableCards(Trick trick) {
        ArrayList<Card> playableCards = new ArrayList<Card>();
        Card leadingCardInTrick = trick.getLeadingCard();

        for (Card c : hand.cards) {
            // if same suit
            if (c.getSuit().compareTo(leadingCardInTrick) == 0) {
                playableCards.add(c);
            }
        }

        // player has no card belonging to the trick's suit -> can play any card
        if (playableCards.isEmpty()) {
            return hand.cards;
        }

        return playableCards;
    }
}
