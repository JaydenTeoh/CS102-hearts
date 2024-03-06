import java.util.*;

public class Round {
    private boolean heartsBroken;
    private int playerStartingFirst;
    private int numTricksPlayed;
    private Trick currentTrick;
    private Game belongsToGame;
    private HashMap<Player, Integer> playersPointsInCurrentRound;


    public Round(int playerStartingFirst, Game belongsToGame){
        this.playerStartingFirst = playerStartingFirst;
        this.belongsToGame = belongsToGame;

        // need to implement Game.getPlayers later on
        currentTrick = null;

        // initialising the hashmap, every player starts with 0 points
        playersPointsInCurrentRound = new HashMap<>();
        for (Player p: belongsToGame.getPlayers()) {
            playersPointsInCurrentRound.put(p, 0);
        }
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

    // starts a new trick
    // if there was a previous trick, allocate points of the trick to the player who won it
    public void startNewTrick(){
        if (currentTrick != null) {
            playerStartingFirst = currentTrick.getWinner(); //ADD THIS LATER ON
            Player winnerOfTrick = belongsToGame.getPlayers().get(playerStartingFirst);
            int pointsInTrick = currentTrick.getNumPoints();
            int previousPoints = playersPointsInCurrentRound.get(winnerOfTrick);
            playersPointsInCurrentRound.replace(winnerOfTrick, pointsInTrick + previousPoints);
        }
        this.currentTrick = new Trick(belongsToGame.getPlayers());
    }


    // Need to add the static variables later on
    public void dealHands() {
        ArrayList<Hand> hands = new ArrayList<>();

        // create new deck, then populate 52 standard poker cards and randomise it (shuffle)
        Deck d = new Deck();
        d.populate();
        d.shuffle();

        // create 4 new hands
        for (int i = 0; i < belongsToGame.NUM_PLAYERS; i++) {
            hands.add(new Hand());
        }

        // populate 4 hands from the shuffled deck
        for (int card = 0; card < belongsToGame.MAX_NUMBER_OF_CARDS_PER_PLAYER; card++) {
            for (int handIndex = 0; handIndex < belongsToGame.NUM_PLAYERS; handIndex++) {
                if (!d.isEmpty()) {
                    hands.get(handIndex).addCard(d.dealCard());
                }
            }
        }

        for (int i = 0; i < belongsToGame.NUM_PLAYERS; i++) {
            players[i].setHand(hands[i]);
        }
    }

    public synchronized void playRound() {
        // async

        // Conditions: Player's hand is not empty (round is not over) and trick is finished
        // while (!belongsToGame.getPlayers().get(0).getHand().getCards().isEmpty() && currentTrick.getCardsInTrick().size() == Game.NUM_PLAYERS) {
        //     this.startNewTrick();
        //     // get server response for player's play card method * 4
        // }

        for (int i = 0; i < belongsToGame.MAX_NUMBER_OF_CARDS_PER_PLAYER; i++) {
            this.startNewTrick();
            // get server response for player's play card method * 4
        }

        // Implement PlayerDoesNotExistException later?
        for (Player p: belongsToGame.getPlayers()) {
            belongsToGame.addPoints(p, playersPointsInCurrentRound.get(p) % 26);
        }
    }

}