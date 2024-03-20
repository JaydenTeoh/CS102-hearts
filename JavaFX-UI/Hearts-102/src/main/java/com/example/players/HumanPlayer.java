package com.example.players;
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

    // public ArrayList<Card> getPlayableCards(Round round, Trick trick) {
    //     ArrayList<Card> playableCards = new ArrayList<Card>();

    //     // first player of the trick
    //     if (trick.getCardsInTrick().isEmpty()) {
    //         if (round.isHeartsBroken()) {
    //             return hand.cards; // can play any card to start trick
    //         } else {
    //             for (Card c : hand.cards) {
    //                 // can only play cards that are not of the Hearts suit
    //                 if (c.getSuit().getName() != "Hearts") {
    //                     playableCards.add(c);
    //                 }
    //             }
    //             return playableCards;
    //         }
    //     }

    //     // get suit and top card of trick
    //     Card leadingCardInTrick = trick.getLeadingCard();
    //     for (Card c : hand.cards) {
    //         // if same suit
    //         if (c.getSuit().compareTo(leadingCardInTrick) == 0) {
    //             playableCards.add(c);
    //         }
    //     }

    //     // player has no card belonging to the trick's suit -> can play any card
    //     if (playableCards.isEmpty()) {
    //         return hand.cards;
    //     }

    //     return playableCards;
    // }
}
