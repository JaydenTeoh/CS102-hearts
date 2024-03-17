package gameplay;
import client.*;
import exceptions.*;
import app.*;
import pokercards.*;
import server.*;
import gameplay.players.*;

import java.io.PrintStream;
import java.util.*;
import javax.swing.ImageIcon;

public class Game {
    private int numRounds;
    private int currentNumPlayers;

    // this hashmap stores players and their points in the current game
    private HashMap<Player, Integer> playersPoints;
    private ArrayList<Player> players;

    public static final int MAX_POINTS = 50;
    public static final int NUM_PLAYERS = 4;
    public static final int MAX_NUMBER_OF_CARDS_PER_PLAYER = 13;
    public static final Card ROUND_STARTING_CARD = new Card(Suit.CLUBS, Rank.TWO, new ImageIcon(Card.getFilename(Suit.CLUBS, Rank.TWO)));
    public static final Card QUEEN_OF_SPADES = new Card(Suit.SPADES, Rank.QUEEN, new ImageIcon(Card.getFilename(Suit.SPADES, Rank.QUEEN)));

    public Game() {
        numRounds = 0;
        currentNumPlayers = 0;
        playersPoints = new HashMap<>();
        players = new ArrayList<>();
    }

    public int getNumRounds() {
        return numRounds;
    }

    public ArrayList<Player> getPlayers() {
        return players;
    }

    public void addPlayer(Player p) throws TooManyPlayersException {
        if (currentNumPlayers < NUM_PLAYERS) {
            currentNumPlayers += 1;
            playersPoints.put(p, 0);
            players.add(p);
        } else {
            throw new TooManyPlayersException("There can only be 4 players in one game.");
        }

        return;
    }

    public void removePlayer(Player p) {
        if (playersPoints.containsKey(p)) {
            currentNumPlayers -= 1;
            playersPoints.remove(p);
            players.remove(p);
        }

        return;
    }

    public void addPoints(Player p, int points) throws PlayerDoesNotExistException {
        if (playersPoints.containsKey(p)) {
            playersPoints.put(p, playersPoints.get(p) + points);
        } else {
            throw new PlayerDoesNotExistException("Player does not exist.");
        }

        return;
    }

    public boolean isEnded() {
        for (Integer value : playersPoints.values()) {
            if (value >= MAX_POINTS) {
                return true;
            }
        }

        return false;
    }
}

