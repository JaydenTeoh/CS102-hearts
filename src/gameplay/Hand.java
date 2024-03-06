package gameplay;
import java.util.*;
import client.*;
import exceptions.*;
import app.*;
import pokercards.*;
import server.*;

public class Hand {
    private ArrayList<Card> cards;

    public Hand(ArrayList<Card> cards) {
        this.cards = cards;
    }

    public ArrayList<Card> getCards() {
        return cards;
    }

    public boolean hasSuit(Suit s) {
        for (Card c : cards) {
            if (c.getSuit() == s) {
                return true;
            }
        }
        
        return false;
    }

    public ArrayList<Card> getPlayableCards(Round round, Trick trick) {
        ArrayList<Card> playableCards = new ArrayList<Card>();

        // player has no card belonging to the trick's suit -> can play any card
        if (playableCards.isEmpty()) {
            return cards;
        }

        // if player is the first player of the trick, can play any card that isn't hearts if hearts is not yet broken. else, can play anything
        if (trick.getCardsInTrick().isEmpty()) {
            if (round.isHeartsBroken()) {
                return cards; // can play any card to start trick
            } 

            for (Card c : cards) {
                // can only play cards that are not of the Hearts suit
                if (c.getSuit().getName() != "Hearts") {
                    playableCards.add(c);
                }
            }

            return playableCards;
            }
        

        // if player isn't starting the trick, get leading card of trick
        Card leadingCardInTrick = trick.getLeadingCard();
        for (Card c : cards) {
            // if same suit (compareTo compares suits)
            if (c.getSuit().compareTo(leadingCardInTrick) == 0) {
                playableCards.add(c);
            }
        }

        return playableCards;
    }

    public void addCard(Card card) {
        cards.add(card);
        
        return;
    }
}
