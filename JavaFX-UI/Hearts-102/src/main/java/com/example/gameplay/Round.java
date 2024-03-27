package com.example.gameplay;
import com.example.players.*;
import java.util.*;
import com.example.exceptions.*;
import com.example.app.*;
import com.example.pokercards.*;

public class Round {
    private boolean heartsBroken;
    private int playerStartingFirst;
    private int numTricksPlayed;
    private Trick currentTrick;
    private HashMap<Player, Integer> playersPointsInCurrentRound;
    private ArrayList<Player> players;

    public Round(ArrayList<Player> players) {
        this.players = players;
        heartsBroken = false;

        currentTrick = null;

        // initialising the hashmap, every player starts with 0 points
        playersPointsInCurrentRound = new HashMap<>();

        for (Player p: players) {
            playersPointsInCurrentRound.put(p, 0);
        }
    }

    public int getPlayerStartingFirst() {
        return playerStartingFirst;
    }

    public void setPlayerStartingFirst(int playerStartingFirst) {
        this.playerStartingFirst = playerStartingFirst;
    }

    public int getNumTricksPlayed() {
        return numTricksPlayed;
    }

    public boolean isHeartsBroken() {
        return heartsBroken;
    }

    public void setHeartsBroken() {
        heartsBroken = true;
    }

    // starts a new trick
    public void startNewTrick() {
        if (currentTrick != null) {
            playerStartingFirst = currentTrick.getWinner();
            System.out.println("Winner of trick: Player " + (playerStartingFirst + 1));
            numTricksPlayed++;
        } 

        this.currentTrick = new Trick(numTricksPlayed, heartsBroken, playerStartingFirst);
    }

    public void dealHands() {
        ArrayList<Hand> hands = new ArrayList<>();

        // create new deck, then populate 52 standard poker cards and randomise it (shuffle)
        Deck d = new Deck();
        d.populate();
        d.shuffle();

        // create 4 new hands
        for (int i = 0; i < Game.NUM_PLAYERS; i++) {
            hands.add(new Hand(new ArrayList<Card>()));
        }

        // populate 4 hands from the shuffled deck
        for (int card = 0; card < Game.MAX_NUMBER_OF_CARDS_PER_PLAYER; card++) {
            for (int handIndex = 0; handIndex < Game.NUM_PLAYERS; handIndex++) {
                if (!d.isEmpty()) {
                    hands.get(handIndex).addCard(d.dealCard());
                }
            }
        }

        for (int i = 0; i < Game.NUM_PLAYERS; i++) {
            hands.get(i).sortCards();
            players.get(i).setHand(hands.get(i));
        }
    }

    public Trick getCurrentTrick() {
        return currentTrick;
    }

    // Added for testing
    public void setCurrentTrick(Trick currentTrick) {
        this.currentTrick = currentTrick;
    }

    public HashMap<Player, Integer> getPlayersPointsInCurrentRound() {
        return this.playersPointsInCurrentRound;
    }

    public void setPlayersPointsInCurrentRound(Player p, int points) throws PlayerException {
        if (playersPointsInCurrentRound.containsKey(p)) {
            playersPointsInCurrentRound.put(p, playersPointsInCurrentRound.get(p) + points);
        } else {
            throw new PlayerException("We cannot add points because the player does not exist.");
        }
    }
}
