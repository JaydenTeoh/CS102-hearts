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

    @Override
    public Card playCard(Round round, Trick trick) {
        // not implemented yet
        return null;
    }

    @Override
    public void passCards(List<Card> cards, Player player) {
        hand.getCards().removeAll(cards);
        player.getHand().getCards().addAll(cards);
    }
}
