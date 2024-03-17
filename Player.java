public abstract class Player {
    public String name; // player name
    private Hand hand; // cards in hand

    // constructor: set Player's name
    public Player(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public Hand getHand() {
        return hand;
    }

    public void setHand(Hand hand) {
        this.hand = hand;
    }

    public abstract Card playCard(Round round, Trick trick);
}
