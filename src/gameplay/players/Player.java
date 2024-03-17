
package gameplay.players;
import gameplay.*;
import java.util.ArrayList;
import client.*;
import exceptions.*;
import app.*;
import pokercards.*;
import server.*;

public interface Player {
    public String getName();

    public Hand getHand();

    public void setHand(Hand hand);

    public abstract Card playCard(Round round, Trick trick);

//     public ArrayList<Card> getPlayableCards(Round round, Trick trick) {
//         ArrayList<Card> playableCards = new ArrayList<Card>();

//         // first player of the trick
//         if (trick.getCardsInTrick().isEmpty()) {
//             if (round.isHeartsBroken()) {
//                 return hand.getCards(); // can play any card to start trick
//             } else {
//                 for (Card c : hand.getCards()) {
//                     // can only play cards that are not of the Hearts suit
//                     if (c.getSuit().getName() != "Hearts") {
//                         playableCards.add(c);
//                     }
//                 }
//                 return playableCards;
//             }
//         }

//         // get suit and top card of trick
//         Card leadingCardInTrick = trick.getLeadingCard();
//         for (Card c : hand.getCards()) {
//             // if same suit
//             if (c.getSuit().compareTo(leadingCardInTrick) == 0) {
//                 playableCards.add(c);
//             }
//         }

//         // player has no card belonging to the trick's suit -> can play any card
//         if (playableCards.isEmpty()) {
//             return hand.getCards();
//         }

//         return playableCards;
//     }
}
