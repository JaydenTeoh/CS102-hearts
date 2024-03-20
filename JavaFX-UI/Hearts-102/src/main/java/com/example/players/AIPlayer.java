package com.example.players;
import com.example.gameplay.*;
import com.example.exceptions.*;
import com.example.pokercards.*;


public class AIPlayer implements Player {
    public String name;
    private Hand hand;

    public AIPlayer(String name) {
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
        Hand currHand = getHand();
        /*
         * Case 1: First trick of the round
         */
        if (round.getNumTricksPlayed() == 0) {
            // must play starting card if have
            if (currHand.hasCard(Game.ROUND_STARTING_CARD)) {
                return Game.ROUND_STARTING_CARD;
            }
        }

        /*
         * Information 1: Not the first trick of the round
         * Case 2: Leading the trick (first player of the trick)
         */
        if (trick.getCardsInTrick().isEmpty()) {
            if (currHand.isAllHearts()) {
                return currHand.getLowest(Suit.HEARTS);
            }

            // if early on, short-suit yourself if possible in this order
            if (round.getNumTricksPlayed() < 4) {
                if (getHand().hasExactlyOne(Suit.DIAMONDS)) {
                    return getHand().getHighest(Suit.DIAMONDS);
                }
                if (getHand().hasExactlyOne(Suit.CLUBS)) {
                    return getHand().getHighest(Suit.CLUBS);
                }
            }

            // if you have a safeish card, lead with it
            if (getHand().getHighestSafe(Suit.SPADES, Rank.QUEEN) != null) {
                return getHand().getHighestSafe(Suit.SPADES, Rank.QUEEN);
            } else if (getHand().getHighestSafe(Suit.HEARTS, Rank.SIX) != null & round.isHeartsBroken()) {
                return getHand().getHighestSafe(Suit.HEARTS, Rank.SIX);
            } else if (getHand().getHighestSafe(Suit.CLUBS, Rank.SIX) != null) {
                return getHand().getHighestSafe(Suit.CLUBS, Rank.SIX);
            } else if (getHand().getHighestSafe(Suit.DIAMONDS, Rank.FIVE) != null) {
                return getHand().getHighestSafe(Suit.DIAMONDS, Rank.FIVE);
            }

            // none of the current cards are safe enough, increase safety allowance
            if (getHand().getHighestSafe(Suit.DIAMONDS, Rank.NINE) != null) {
                return getHand().getHighestSafe(Suit.DIAMONDS, Rank.NINE);
            } else if (getHand().getHighestSafe(Suit.CLUBS, Rank.NINE) != null) {
                return getHand().getHighestSafe(Suit.CLUBS, Rank.NINE);
            } else if (getHand().getHighestSafe(Suit.HEARTS, Rank.NINE) != null & round.isHeartsBroken()) {
                return getHand().getHighestSafe(Suit.HEARTS, Rank.NINE);
            }

            // only dangerous high cards, probably near end-game
            if (getHand().hasSuit(Suit.DIAMONDS)) {
                return getHand().getLowest(Suit.DIAMONDS);
            } else if (getHand().hasSuit(Suit.CLUBS)) {
                return getHand().getLowest(Suit.CLUBS);
            } else if (getHand().hasSuit(Suit.HEARTS) & round.isHeartsBroken()) {
                return getHand().getLowest(Suit.HEARTS);
            }
            // otherwise you only have spades
            return getHand().getLowest(Suit.SPADES);
        }


        /*
         * Information 1: Not the first trick of the round
         * Information 2: Not leading the trick
         * Case 3: We have to follow suit, let's check if we can avoid picking up points
         */
        if (currHand.hasSuit(trick.getLeadingSuit())) {
            Suit suitToFollow = trick.getLeadingSuit();
            Rank currentWinningRank = trick.getLeadingCard().getRank();
            Card highestFollowCard = getHand().getHighest(suitToFollow); // highest card of suit
            Card lowestFollowCard = getHand().getLowest(suitToFollow);
            Card highestSafeFollowCard = getHand().getHighestSafe(suitToFollow, currentWinningRank); // highest card of suit that is lower than winning rank
            // Case 4a: I only have one card of the suit, there is no choice available
            if (currHand.hasExactlyOne(trick.getLeadingSuit())) {
                return highestFollowCard; // highestFollowCard = lowestFollowCard
            }

            /*
             * Case 4b: Leading suit is Spades and I have Queen of Spades, 
             * have to be careful not to dump Queen of Spades on myself
             */
            if (trick.getLeadingSuit().compareTo(Suit.SPADES) == 0) {
                if (currHand.hasCard(Game.QUEEN_OF_SPADES)) {
                    // leading Card is ACE Spade or King Spade
                    if (trick.getLeadingCard().compareTo(Game.QUEEN_OF_SPADES) > 0) {
                        return Game.QUEEN_OF_SPADES; // let's dump our Queen of Spades on someone else
                    } else {
                        // we can't dump our Queen of Spades on someone else but at least we can clear
                        // a higher spade (King of Spades or Ace of Spades)
                        if (highestFollowCard.compareTo(Game.QUEEN_OF_SPADES) > 0) {
                            return highestFollowCard;
                        }
                        // Queen of Spades is our largest spades so we have to throw the biggest
                        // card after it to avoid dumping Queen of Spades on ourself
                        Card lowerCard = currHand.getHighestSafe(Suit.SPADES, Rank.QUEEN);
                    }
                }
            }

            // Case 4c: I am last player in the trick
            if (trick.getCardsInTrick().size() == Game.NUM_PLAYERS - 1) {
                // Case 4c(i): There are no points in the trick (just dump highest card)
                if (trick.getNumPoints() == 0) {
                    return highestFollowCard;
                } else { // Case 4c(ii): there are points in trick, we want to avoid eating those points if possible
                    if (highestSafeFollowCard != null) {
                        return highestSafeFollowCard; // we have a card that can save us
                    }
                    return highestFollowCard; // got to eat points, no choice
                }
            } else { // Case 4d: I am not the last player in the trick
                if (highestSafeFollowCard != null) {
                    return highestSafeFollowCard; // we play a card that is safe whenever possible
                }
                return lowestFollowCard;  // play lowest possible card
            }
        }

        /*
         * Information 1: Not the first trick of the round
         * Information 2: Not leading the trick
         * Information 3: We cannot follow suit (don't have cards)
         * Case 4: Let's try to throw dangerous cards / dump points on others
         */
        if (currHand.hasCard(Game.QUEEN_OF_SPADES)) { // first dump Queen of Spades if you have it
            return Game.QUEEN_OF_SPADES;
        }
        /*
         * dump ACE/King Of Spades. Yes, abit pointless if Queen of Spades 
         * was already played but we shall not add logic to keep track on 
         * whether Queen of Spades has been played for now
         */
        if (currHand.hasCard(Suit.SPADES, Rank.ACE) || currHand.hasCard(Suit.SPADES, Rank.KING)) {
            return currHand.getHighest(Suit.SPADES);
        }


        // short-suit the hand in this order
        if (currHand.hasExactlyOne(Suit.HEARTS)) {
            return currHand.getHighest(Suit.HEARTS);
        }
        if (currHand.hasExactlyOne(Suit.DIAMONDS)) {
            return currHand.getHighest(Suit.DIAMONDS);
        }
        if (currHand.hasExactlyOne(Suit.CLUBS)) {
            return currHand.getHighest(Suit.CLUBS);
        }
        if (currHand.hasExactlyOne(Suit.SPADES)) {
            return currHand.getHighest(Suit.SPADES);
        }

        // cannot short suit then dump highest heart if you have one
        if (currHand.hasSuit(Suit.HEARTS)) {
            return currHand.getHighest(Suit.HEARTS);
        }

        // no hearts, no queen of spades, then just dump highest card in this order
        if (currHand.getHighest(Suit.DIAMONDS) != null) {
            return currHand.getHighest(Suit.DIAMONDS);
        } else if (currHand.getHighest(Suit.CLUBS) != null) {
            return currHand.getHighest(Suit.CLUBS);
        } else {
            return currHand.getHighest(Suit.SPADES);
        }

        // throw new AIPlayerException("Edge case: AI player made 0 decision");
        //return null;
    }
}