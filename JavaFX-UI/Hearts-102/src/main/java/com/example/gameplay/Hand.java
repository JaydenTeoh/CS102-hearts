package com.example.gameplay;
import java.util.*;
import com.example.exceptions.*;
import com.example.app.*;
import com.example.pokercards.*;

public class Hand {
    private ArrayList<Card> cards;

    public Hand(ArrayList<Card> cards) {
        this.cards = cards;
    }

    public ArrayList<Card> getCards() {
        return cards;
    }


    public void sortCards() {
        Collections.sort(cards);
    }

    public boolean isAllHearts() {
        return !cards.stream().anyMatch(c -> c.getSuit().compareTo(Suit.HEARTS) != 0);
    }

    public void removeCard(Card toRemove) {
        Iterator<Card> iter = cards.iterator(); // since Card does not implement equals(), we have to iterate
        boolean hasRemoved = false;
        while(iter.hasNext()) {
            if (iter.next().isSameAs(toRemove)) {
                iter.remove();
                hasRemoved = true;
                break;
            }
        }
        /*
         * just a safety check to make sure our logic is not allowing Player to 
         * call removal of cards that are not even in hand
         */
        if (!hasRemoved) {
            throw new HandException("Hand does not have card: " + toRemove.toString());
        }
    }

    // Overloaded method, convenient if we don't want to intialise new Card
    public void removeCard(Suit s, Rank r) {
        Iterator<Card> iter = cards.iterator(); // since Card does not implement equals(), we have to iterate
        boolean hasRemoved = false;
        while(iter.hasNext()) {
            Card c = iter.next();
            if (c.getRank().compareTo(r) == 0 && c.getSuit().compareTo(s) == 0) {
                iter.remove();
                hasRemoved = true;
                break;
            }
        }
        /*
         * just a safety check to make sure our logic is not allowing Player to 
         * call removal of cards that are not even in hand
         */
        if (!hasRemoved) {
            throw new HandException("Hand does not have card: " + r.toString() + s.toString());
        }
    }


    public boolean hasSuit(Suit s) {
        return cards.stream().anyMatch(c -> c.getSuit().compareTo(s) == 0);
    }

    public boolean hasExactlyOne(Suit s) {
        long count = cards.stream().filter(c -> c.getSuit().compareTo(s) == 0).count();
        return count == 1;
    }

    public long howManyOfSuit(Suit s) {
        return cards.stream().filter(c -> c.getSuit().compareTo(s) == 0).count();
    }

    public List<Card> getAll(Suit s) {
        return cards.stream().filter(c -> c.getSuit().compareTo(s) == 0).toList();
    }

    public boolean hasCard(Card card) {
        return cards.stream().anyMatch(c -> c.isSameAs(card));
    }
    
    // Overloaded method, convenient if we don't want to intialise new Card
    public boolean hasCard(Suit s, Rank r) {
        return cards.stream().anyMatch(c -> c.getRank().compareTo(r) == 0 && c.getSuit().compareTo(s) == 0);
    }

    public Card getHighest(Suit suit) {
        Card highestOfSuit = null;
        for (Card card : cards) {
            if (card.getSuit().compareTo(suit) == 0) {
                if (highestOfSuit == null) {
                    highestOfSuit = card;
                } else if (highestOfSuit.compareTo(card) < 0) {
                    highestOfSuit = card;
                }
            }
        }
        return highestOfSuit;
    }

    /*
     * returns the highest card matching the given suit 
     * which is also below the given rank
     */
    public Card getHighestSafe(Suit suit, Rank rank) {
        Card highestSafe = null;
        for (Card card : cards) {
        if (card.getSuit().compareTo(suit) == 0 & rank.compareTo(card.getRank()) > 0) {
            if (highestSafe == null) {
                highestSafe = card;
            } else if (highestSafe.getRank().compareTo(card.getRank()) < 0) {
                highestSafe = card;
            }
        }
        }
        return highestSafe;
    }

    public Card getLowest(Suit suit) {
        Card highestOfSuit = null;
        for (Card card : cards) {
            if (card.getSuit().compareTo(suit) == 0) {
                if (highestOfSuit == null) {
                    highestOfSuit = card;
                } else if (highestOfSuit.compareTo(card) > 0) {
                    highestOfSuit = card;
                }
            }
        }
        return highestOfSuit;
    }

    public ArrayList<Card> getPlayableCards(Trick trick) {
        ArrayList<Card> playableCards = new ArrayList<Card>();
        int trickNo = trick.getTrickNo();
        boolean heartsBroken = trick.isHeartsBroken();

        // if it's the first trick and the player has the starting card
        if (trickNo == 0) {
            if (cards.contains(Game.ROUND_STARTING_CARD)) {
                playableCards.add(Game.ROUND_STARTING_CARD);
                return playableCards;
            }
        }

        // if player is the first player of the trick, can play any card that isn't hearts if hearts is not yet broken. else, can play anything
        if (trick.getCardsInTrick().isEmpty()) {
            if (heartsBroken) {
                return cards; // can play any card to start trick
            } 

            for (Card c : cards) {
                // can only play cards that are not of the Hearts suit
                if (c.getSuit().compareTo(Suit.HEARTS) != 0) {
                    playableCards.add(c);
                }
            }

            return playableCards;
            }
        

        // if player isn't starting the trick, get leading card of trick
        Card leadingCardInTrick = trick.getLeadingCard();
        for (Card c : cards) {
            // if same suit (compareTo compares suits)
            if (c.getSuit().compareTo(leadingCardInTrick.getSuit()) == 0) {
                playableCards.add(c);
            }
        }

        // player has no card belonging to the trick's suit -> can play any card
        if (playableCards.isEmpty()) {
            return cards;
        }

        return playableCards;
    }

    public void addCard(Card card) {
        cards.add(card);

    }

    // for AI player to decide whether to dump spades
    public boolean poorSpadesHand() {
        int goodSpades = 0;
        int badSpades = 0;

        for (Card c : cards) {
            if (c.equals(Game.QUEEN_OF_SPADES)) {
                badSpades += 2;
            } else if (c.equals(Suit.SPADES, Rank.ACE) || c.equals(Suit.SPADES, Rank.KING)) {
                // King and Ace spades can help cover the Queen
                if (this.hasCard(Game.QUEEN_OF_SPADES)) {
                    goodSpades++;
                } else {
                    badSpades++;
                }
            } else if (c.getSuit().equals(Suit.SPADES)) {
                goodSpades += 1;
            }
        }

        // no risky spades, i.e. A/K/Q Spades
        if (badSpades == 0) {
            return false; 
        }

        // too few good spades cards covering the queen, hard to bleed out spades
        return goodSpades - badSpades < 2;
    }
}
