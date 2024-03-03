
import java.io.PrintStream;
import java.util.*;

public class Game {
    private int numRounds;
    private int numOfPlayers;

    // storing players and points
    private HashMap<Player, Integer> playersPoints;
    private ArrayList<Player> players;
    private static final int MAX_POINTS = 50;
    private static final int NUM_PLAYERS = 4;
    private static final int MAX_NUMBER_OF_CARDS_PER_PLAYER = 13;

    public Game() {
        numRounds = 0;
        numOfPlayers = 0;
        playersPoints = new HashMap<Player, Integer>();
    }

    public void addPlayer(Player p) throws TooManyPlayersException {
        if (numOfPlayers < NUM_PLAYERS) {
            numOfPlayers += 1;
            playersPoints.put(p, 0);
        } else {
            throw new TooManyPlayersException("There can only be 4 players in one game.");
        }

        return;
    }

    public void removePlayer(Player p) {
        if (playersPoints.containsKey(p)) {
            playersPoints.remove(p);
            numOfPlayers -= 1;
        }
    }

    public void addPoints(Player p, int points) {
        if (playersPoints.containsKey(p)) {
            playersPoints.put(p, playersPoints.get(p) + points);
        } else {
            throw new PlayerDoesNotExistException();
        }
    }

    public boolean isEnded() {
        for (Integer value : playersPoints.values()) {
            if (value >= MAX_POINTS) {
                return true;
            }
        }

        return false;
    }

    // public ArrayList<Hand> dealHands() {
    //     ArrayList<Hand> hands = new ArrayList<>();

    //     // create new deck, then populate 52 standard poker cards and randomise it (shuffle)
    //     Deck d = new Deck();
    //     d.populate();
    //     d.shuffle();

    //     // create 4 new hands
    //     for (int i = 0; i < NUM_PLAYERS; i++) {
    //         hands.add(new Hand());
    //     }

    //     // populate 4 hands from the shuffled deck
    //     for (int card = 0; card < MAX_NUMBER_OF_CARDS_PER_PLAYER; card++) {
    //         for (int handIndex = 0; handIndex < NUM_PLAYERS; handIndex++) {
    //             if (!d.isEmpty()) {
    //                 hands.get(handIndex).addCard(d.dealCard());
    //             }
    //         }
    //     }

    //     for (int i = 0; i < NUM_PLAYERS; i++) {
    //         players[i].setHand(hands[i]);
    //     }

    //     return;
    // }
}

