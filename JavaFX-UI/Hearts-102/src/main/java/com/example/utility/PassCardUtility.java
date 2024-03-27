package com.example.utility;

import java.util.List;

import com.example.functional.NextTurnAction;
import com.example.gameplay.Game;
import com.example.players.Player;
import com.example.pokercards.Card;

import javafx.animation.TranslateTransition;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

public class PassCardUtility {

    public static void initialisePassCardButton(Pane root, List<Player> playerList, Pane playArea, Button passCardbutton, List<CardImageView> cardViewsToPass, Game game, NextTurnAction nextTurnCallback) {
        passCardbutton.setText("Pass 3 cards");

        passCardbutton.setPrefSize(200, 50);
        passCardbutton.setStyle("-fx-font-size: 20px;");

        // Set the action event handler to call handleButtonClick method
        passCardbutton.setOnAction(event -> {
            if (cardViewsToPass.size() == 3) {
                System.out.println("Pass 3 cards");
                startPassingProcess(root, playerList, passCardbutton, cardViewsToPass, game, nextTurnCallback);
            } else {
                System.out.println("Please select 3 cards");
            }
        });

        double xPos = (playArea.getPrefWidth() - passCardbutton.getPrefWidth()) / 2;
        double yPos = (playArea.getPrefHeight() - passCardbutton.getPrefHeight()) / 2;

        passCardbutton.setLayoutX(xPos);
        passCardbutton.setLayoutY(yPos);

        playArea.getChildren().add(passCardbutton);
    }

    public static void selectCardsToPass(Pane root, List<CardImageView> cardViewsToPass) {
        ObservableList<Node> cards = CardViewUtility.getCardViewsOfPlayer(root, 0);
        for (Node cardView : cards) {
            cardView.setEffect(null);
            cardView.setOpacity(1);

            // In createCardViewsOfPlayer, there is a instanceof HumanPlayer -> add hover
            // effect, idk if removing it will break anything so I set these to null here
            // first
            cardView.setOnMouseEntered(null);
            cardView.setOnMouseExited(null);

            cardView.setOnMouseClicked(event -> {
                // Card card = (Card) cardView.getUserData();

                boolean cardActivated = cardViewsToPass.contains(cardView);

                if (cardViewsToPass.size() == 3 && !cardActivated) {
                    System.out.println("Max cards selected");
                } else {
                    double translateYDistance = cardActivated ? 0 : -50;

                    TranslateTransition transition = new TranslateTransition(Duration.seconds(0.35), cardView);
                    transition.setToY(translateYDistance);
                    transition.play();

                    if (cardActivated) {
                        cardViewsToPass.remove(cardView);
                    } else {
                        cardViewsToPass.add((CardImageView) cardView);
                    }
                }
            });
        }
    }

    private static void startPassingProcess(Pane root, List<Player> playerList, Button passCardbutton, List<CardImageView> cardViewsToPass, Game game, NextTurnAction nextTurnCallback) {
        CardViewUtility.processPlayerCards(0, playerList, cardViewsToPass, game.getNumRounds(), root, () -> {
            passCardbutton.setVisible(false);
            cardViewsToPass.clear();

            for (int i = 0; i < Game.NUM_PLAYERS; i++) {
                List<Card> hand = playerList.get(i).getHand().getCards();
                for (Card c : hand) {
                    // set 2 of clubs to start first, as per game rules
                    if (c.equals(Game.ROUND_STARTING_CARD)) {
                        game.getRound().setPlayerStartingFirst(i);
                    }
                }
            }
    
            nextTurnCallback.execute();
        });
    }

    public static void enablePassCardButton(Button passCardbutton, List<CardImageView> cardViewsToPass) {
        cardViewsToPass.clear();
        passCardbutton.setVisible(true);
    }

}
