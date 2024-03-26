package com.example.players;
import java.util.*;

import com.example.gameplay.*;
import com.example.pokercards.*;

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

    public List<Card> passCards() {
        // not implemented yet
        return null;
    }
}
