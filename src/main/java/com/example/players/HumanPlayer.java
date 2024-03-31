package com.example.players;

import com.example.gameplay.*;

public class HumanPlayer implements Player {
    public String name;
    private Hand hand;

    public HumanPlayer(String name) {
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
}
