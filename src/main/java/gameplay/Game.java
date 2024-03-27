package gameplay;
import exceptions.*;
import app.*;
import pokercards.*;
import players.*;
import java.util.*;

public class Game {
    private int numRounds;
    private int currentNumPlayers;

    // this hashmap stores players and their points in the current game
    private HashMap<Player, Integer> playersPointsInCurrentGame;
    private ArrayList<Player> players;
    private Round round;

    public static final int MAX_POINTS = 50;
    public static final int NUM_PLAYERS = 4;
    public static final int MAX_NUMBER_OF_CARDS_PER_PLAYER = 13;

    public static final Card ROUND_STARTING_CARD = new Card(Suit.CLUBS, Rank.TWO);
    public static final Card QUEEN_OF_SPADES = new Card(Suit.SPADES, Rank.QUEEN);
    
    public Game() {
        numRounds = 0;
        currentNumPlayers = 0;
        playersPointsInCurrentGame = new HashMap<>();
        players = new ArrayList<>();
    }

    public Round getRound() {
        return round;
    }

    public void nextRound() {
        numRounds++;
        round = new Round(players);
    }

    public int getNumRounds() {
        return numRounds;
    }

    public ArrayList<Player> getPlayers() {
        return players;
    }

    public void addPlayer(Player p) throws PlayerException {
        if (currentNumPlayers < NUM_PLAYERS) {
            currentNumPlayers += 1;
            playersPointsInCurrentGame.put(p, 0);
            players.add(p);
        } else {
            throw new PlayerException("There can only be 4 players in one game, so we cannot add any more players.");
        }
    }

    public void removePlayer(Player p) {
        if (playersPointsInCurrentGame.containsKey(p)) {
            currentNumPlayers -= 1;
            playersPointsInCurrentGame.remove(p);
            players.remove(p);
        }
    }

    public HashMap<Player, Integer> getPlayersPointsInCurrentGame() {
        return playersPointsInCurrentGame;
    }

    public void setPlayersPointsInCurrentGame(Player p, int points) throws PlayerException {
        if (playersPointsInCurrentGame.containsKey(p)) {
            playersPointsInCurrentGame.put(p, playersPointsInCurrentGame.get(p) + points);
        } else {
            throw new PlayerException("We cannot add points because the player does not exist.");
        }
    }

    public boolean isEnded() {
        for (Integer value : playersPointsInCurrentGame.values()) {
            if (value >= MAX_POINTS) {
                return true;
            }
        }

        return false;
    }

    public int getNextPlayer(int currentPlayer) {
        int nextPlayer = 0;
        if (currentPlayer == this.getPlayers().size() - 1) {
            nextPlayer = 0;
        } else {
            nextPlayer = currentPlayer + 1;
        }

        return nextPlayer;
    }
}

