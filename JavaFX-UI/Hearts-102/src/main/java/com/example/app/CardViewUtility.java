package com.example.app;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.example.gameplay.Round;
import com.example.players.AIPlayer;
import com.example.players.HumanPlayer;
import com.example.players.Player;
import com.example.pokercards.Card;

import javafx.animation.TranslateTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

public class CardViewUtility {
    public static final int CARD_WIDTH = 95;
    public static final int CARD_HEIGHT = 140;
    public static final int SPACING = -65;
    public static final int PLAYER_AREA_WIDTH = 600;
    public static final int PLAYER_AREA_HEIGHT = 250;

    public static ObservableList<Node> getCardViewsOfPlayer(Pane root, int id) {
        for (Node node : root.getChildren()) {
            if (node.getId() != null && node.getId().equals(id + "")) {
                Pane pane = (Pane) node;
                return pane.getChildren();
            }
        }
        // Empty List
        return FXCollections.observableArrayList();
    }

    public static void disableCards(Pane root) {
        ObservableList<Node> cards = getCardViewsOfPlayer(root, 0);

        for (Node node : cards) {
            if (node instanceof ImageView card) {
                disableHover(card);
                setGrayscale(card);
                card.setOnMouseClicked(null);
                card.setOnMouseEntered(null);
                card.setOnMouseExited(null);
                card.getStyleClass().remove("card-active");
            }
        }
    }

    public static void addHoverEffect(ImageView cardView) {
        cardView.setOnMouseEntered(event -> {
            enableHover(cardView);
        });

        cardView.setOnMouseExited(event -> {
            disableHover(cardView);
        });
    }

    public static void enableHover(ImageView cardView) {
        cardView.setEffect(new DropShadow()); // Apply drop shadow effect when mouse enters
        cardView.setCursor(Cursor.HAND); // Change cursor to hand

        // Translate animation to move the card up
        TranslateTransition hoverTransition = new TranslateTransition(Duration.seconds(0.2), cardView);
        hoverTransition.setToY(-20); // Adjust this value to change the hover distance
        hoverTransition.play();
    }

    public static void disableHover(ImageView cardView) {
        cardView.setEffect(null); // Remove drop shadow effect when mouse exits
        cardView.setCursor(Cursor.DEFAULT); // Change cursor back to default

        // Translate animation to move the card back down
        TranslateTransition hoverTransition = new TranslateTransition(Duration.seconds(0.2), cardView);
        hoverTransition.setToY(0);
        hoverTransition.play();
    }

    public static void setGrayscale(ImageView cardView) {
        ColorAdjust grayscale = new ColorAdjust();
        grayscale.setBrightness(-0.5); // Lower brightness by 50%
        grayscale.setContrast(-0.5); // Lower contrast by 50%
        cardView.setEffect(grayscale);
    }

    public static Pane createCardViewsOfPlayer(Pane playerArea, Player player, List<Player> playerList) {
        try {
            String currentDirectory = System.getProperty("user.dir");
            List<Card> hand = player.getHand().getCards();
            int playerIndex = playerList.indexOf(player);

            for (int i = 0; i < hand.size(); i++) {
                Card card = hand.get(i);

                File faceUpFile = new File(currentDirectory + "/images/" + card.getFilename());
                Image faceUpImage = new Image(new FileInputStream(faceUpFile));
                File faceDownFile = new File(currentDirectory + "/images/" + "/face_down_image.png");
                Image faceDownImage = new Image(new FileInputStream(faceDownFile));
                CardImageView cardView = new CardImageView(faceUpImage, faceDownImage);
                if (player instanceof HumanPlayer) {
                    cardView.setImage(true);
                }
                cardView.setFitWidth(CARD_WIDTH);
                cardView.setFitHeight(CARD_HEIGHT);

                double xPos, yPos;
                if (playerIndex == 0) { // Bottom player
                    xPos = i * (CARD_WIDTH + SPACING); // Normal horizontal spacing
                    yPos = 0; // Align with top edge
                } else if (playerIndex == 1) { // Left player
                    xPos = 0; // Align with left edge
                    yPos = i * 30; // Vertical spacing
                } else if (playerIndex == 2) { // Top player
                    xPos = i * (CARD_WIDTH + SPACING); // Normal horizontal spacing
                    yPos = 0; // Align with top edge
                } else { // Right player
                    xPos = 0; // Align with left edge
                    yPos = i * 30; // Vertical spacing
                }

                cardView.setLayoutX(xPos);
                cardView.setLayoutY(yPos);

                // cardView.setRotate(-90);
                // Rotate cards for left and right players
                if (playerIndex == 1 || playerIndex == 3) {
                    cardView.setRotate(90); // Rotate 90 degrees for left and right players
                }

                // Determine if the card is playable
                // boolean isPlayable = hand.contains(card);
                // Apply hover effect
                if (player instanceof HumanPlayer) {
                    CardViewUtility.addHoverEffect(cardView);
                }

                // cardView.setId(card.getRank().getSymbol()+card.getSuit().getSymbol());
                cardView.setId(i + "");

                cardView.setUserData(card);

                playerArea.getChildren().add(cardView);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return playerArea;
    }

    public static void moveCard(Node cardView, Card cardPlayed, List<Node> currentCardViewsInTrick,
            List<Player> playerList, Runnable callback) {
        currentCardViewsInTrick.add(cardView);
        TranslateTransition transition = new TranslateTransition(Duration.seconds(1), cardView);
        disableHover((ImageView) cardView);
        // Find the player who played the card
        Player playerNow = null;
        for (Player player : playerList) {
            if (player.getHand().getCards().contains(cardPlayed)) {
                playerNow = player;
                break;
            }
        }
        int playerNo = playerList.indexOf(playerNow) + 1; // Player number is the index in the list + 1

        // Adjust the transition based on the player number
        if (playerNo == 1) { // Bottom player
            transition.setToY(-(PLAYER_AREA_HEIGHT) + 90);
            transition.setToX(((PLAYER_AREA_WIDTH / 2) - cardView.getLayoutX()) - CARD_WIDTH / 2);
        } else if (playerNo == 2) { // Left player
            transition.setToY((((PLAYER_AREA_HEIGHT / 2) - cardView.getLayoutY()) - CARD_HEIGHT / 2) + 180);
            transition.setToX(500);
        } else if (playerNo == 3) { // Top player
            transition.setToY(180);
            transition.setToX(((PLAYER_AREA_WIDTH / 2) - cardView.getLayoutX()) - CARD_WIDTH / 2);
        } else if (playerNo == 4) { // Right player
            transition.setToY((((PLAYER_AREA_HEIGHT / 2) - cardView.getLayoutY()) - CARD_HEIGHT / 2) + 180);
            transition.setToX(-350);
        }

        transition.setCycleCount(1);

        transition.setOnFinished(event -> {
            System.out.println("Card played: " + cardPlayed + " by Player " + (playerNo));
            // Bring the card to the front
            cardView.toFront();
            // Disable mouse interaction with the card
            cardView.setDisable(true);
            callback.run();
        });
        transition.play();
    }

    public static void processPlayerCards(int currentPlayerIndex, List<Player> playerList,
            List<CardImageView> cardViewsToPass, int gameRound, Pane root, Runnable callback) {

        if (currentPlayerIndex > playerList.size() - 1) {
            for (int i = 0; i < playerList.size(); i++) {
                ObservableList<Node> playerCardViews = getCardViewsOfPlayer(root, i);
                for (int j = 0; j < playerCardViews.size(); j++) {
                    double xPos, yPos;
                    if (i == 0) { // Bottom player
                        xPos = j * (CARD_WIDTH + SPACING); // Normal horizontal spacing
                        yPos = 0; // Align with top edge
                    } else if (i == 1) { // Left player
                        xPos = 0; // Align with left edge
                        yPos = j * 30; // Vertical spacing
                    } else if (i == 2) { // Top player
                        xPos = j * (CARD_WIDTH + SPACING); // Normal horizontal spacing
                        yPos = 0; // Align with top edge
                    } else { // Right player
                        xPos = 0; // Align with left edge
                        yPos = j * 30; // Vertical spacing
                    }

                    playerCardViews.get(j).setLayoutX(xPos);
                    playerCardViews.get(j).setLayoutY(yPos);
                }
            }

            callback.run();
            return;
        }

        Player p = playerList.get(currentPlayerIndex);
        int nextPlayerIndex = currentPlayerIndex; // Initialize with current player's index

        switch (gameRound) {
            case 1: // Pass forward (to the right)
                nextPlayerIndex = (currentPlayerIndex + 1) % playerList.size();
                break;
            case 2: // Pass backward (to the left)
                nextPlayerIndex = (currentPlayerIndex - 1 + playerList.size()) % playerList.size();
                break;
            // case 3: // Pass across
            // nextPlayerIndex = (currentPlayerIndex + 2) % playerList.size();
            // break;
        }

        Player nextPlayer = playerList.get(nextPlayerIndex);
        List<Card> cardsToPass = new ArrayList<>();
        ObservableList<Node> currentPlayerCardViews = getCardViewsOfPlayer(root, currentPlayerIndex);

        if (p instanceof AIPlayer) {
            System.out.println("Player " + p.getName() + " passes to Player " + nextPlayer.getName());
            System.out.println("Player is an AI");

            cardViewsToPass.clear();

            cardsToPass.add((Card) currentPlayerCardViews.get(1).getUserData());
            cardsToPass.add((Card) currentPlayerCardViews.get(2).getUserData());
            cardsToPass.add((Card) currentPlayerCardViews.get(3).getUserData());

            p.getHand().removeCard((Card) currentPlayerCardViews.get(1).getUserData());
            p.getHand().removeCard((Card) currentPlayerCardViews.get(2).getUserData());
            p.getHand().removeCard((Card) currentPlayerCardViews.get(3).getUserData());

            nextPlayer.getHand().addCard((Card) currentPlayerCardViews.get(1).getUserData());
            nextPlayer.getHand().addCard((Card) currentPlayerCardViews.get(2).getUserData());
            nextPlayer.getHand().addCard((Card) currentPlayerCardViews.get(3).getUserData());

            currentPlayerCardViews.stream()
                    .filter(node -> node.getUserData() instanceof Card)
                    .map(node -> (CardImageView) node)
                    .filter(cardView -> cardsToPass.stream()
                            .anyMatch(card -> card.isSameAs((Card) cardView.getUserData())))
                    .forEach(cardViewsToPass::add);

            System.out.println("Cards to pass");
            for (Node n : cardViewsToPass) {
                Card c = (Card) n.getUserData();
                cardsToPass.add(c);
                System.out.println(c.getRank() + " of " + c.getSuit());
            }

            // Pass card
        } else {
            System.out.println("Player " + p.getName() + " passes to Player " + nextPlayer.getName());
            System.out.println("Player is a Human");
            System.out.println("Cards to pass");
            for (Node n : cardViewsToPass) {
                Card c = (Card) n.getUserData();
                cardsToPass.add(c);
                System.out.println(c.getRank() + " of " + c.getSuit());
            }

            // Get next player to pass
            p.passCards(cardsToPass, nextPlayer);
        }

        ObservableList<Node> nextPlayerCards = CardViewUtility.getCardViewsOfPlayer(root, nextPlayerIndex);

        // for(Node n : nextPlayerCards){
        // Card c = (Card) n.getUserData();
        // System.out.println(c.getRank()+" of "+c.getSuit());
        // }

        double xPos = nextPlayerCards.get(nextPlayerCards.size() - 1).getLayoutX();
        double yPos = nextPlayerCards.get(nextPlayerCards.size() - 1).getLayoutY();

        Set<CardImageView> animatingCards = new HashSet<>();

        for (int i = 0; i < cardViewsToPass.size(); i++) {
            CardImageView cardView = (CardImageView) cardViewsToPass.get(i);

            cardView.setImage(false); // Flip the card to face up

            if (nextPlayerIndex == 0 || nextPlayerIndex == 2) { // Bottom and Top player
                xPos += (CARD_WIDTH + SPACING);
                cardView.setLayoutY(0);
                cardView.setRotate(180);
                
            } else { // Left and Right player
                cardView.setLayoutX(0);
                cardView.setRotate(90);
                yPos += 30;
            }
            boolean isHuman = nextPlayer instanceof HumanPlayer;
            cardView.setImage(isHuman); // Flip the card to face up
            currentPlayerCardViews.remove(cardView);
            nextPlayerCards.add(cardView);

            if (animatingCards.contains(cardView)) {
                // Skip this card if it's already animating
                continue;
            }

            TranslateTransition translateTransition = new TranslateTransition(Duration.seconds(0.5), cardView);
            

            animatingCards.add(cardView);

            translateTransition.setToY(yPos);
            translateTransition.setToX(xPos);

            translateTransition.setCycleCount(1);

            translateTransition.setOnFinished(e -> {
                // Remove from animating set when finished
                animatingCards.remove(cardView);

                if (animatingCards.size() == 0) {
                    processPlayerCards(currentPlayerIndex + 1, playerList, cardViewsToPass, gameRound, root, callback);
                }
            });

            translateTransition.play();
        }
    }
}
