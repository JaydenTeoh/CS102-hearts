import java.util.ArrayList;

public class Round {
    private boolean heartsBroken;
    private int playerStartingFirst;
    private int numTricksPlayed;
    private Trick currentTrick;
    private Game belongsToGame;
    private ArrayList<Player> players;

    public Round(Player playerStartingFirst, Game belongsToGame){
        this.playerStartingFirst = playerStartingFirst;
        this.belongsToGame = game;
        // need to implement Game.getPlayers later on
        this.players = game.getPlayers();
    }

    public int getPlayerStartingFirst() {
        return playerStartingFirst;
    }

    public int getNumTricksPlayed() {
        return numTricksPlayed;
    }

    public boolean isHeartsBroken() {
        return heartsBroken;
    }

    public startNewTrick(int player){
        this.currentTrick = new Trick(new ArrayList<Card>(), )
    }


    // Need to add the static variables later on
    public void dealHands() {
        ArrayList<Hand> hands = new ArrayList<>();

        // create new deck, then populate 52 standard poker cards and randomise it (shuffle)
        Deck d = new Deck();
        d.populate();
        d.shuffle();

        // create 4 new hands
        for (int i = 0; i < NUM_PLAYERS; i++) {
            hands.add(new Hand());
        }

        // populate 4 hands from the shuffled deck
        for (int card = 0; card < MAX_NUMBER_OF_CARDS_PER_PLAYER; card++) {
            for (int handIndex = 0; handIndex < NUM_PLAYERS; handIndex++) {
                if (!d.isEmpty()) {
                    hands.get(handIndex).addCard(d.dealCard());
                }
            }
        }

        for (int i = 0; i < NUM_PLAYERS; i++) {
            players[i].setHand(hands[i]);
        }
    }
}
