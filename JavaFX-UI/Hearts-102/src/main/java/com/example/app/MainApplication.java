package com.example.app;

import com.example.exceptions.PlayerException;
import com.example.gameplay.Game;
import com.example.gameplay.Round;
import com.example.gameplay.Trick;
import com.example.players.AIPlayer;
import com.example.players.HumanPlayer;
import com.example.players.Player;
import com.example.pokercards.Card;
import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import javafx.scene.effect.DropShadow;
import javafx.scene.Cursor;
import javafx.util.Duration;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class MainApplication extends Application {

    private static final double WINDOW_WIDTH = 1500;
    private static final double WINDOW_HEIGHT = 800;
    private static final int CARD_WIDTH = 95;
    private static final int CARD_HEIGHT = 140;

    private static final int PLAYER_AREA_WIDTH = 600;
    private static final int PLAYER_AREA_HEIGHT = 250;

    private static final int PLAY_AREA_WIDTH = 400;
    private static final int PLAY_AREA_HEIGHT = 200;
    private static final int SPACING = -65;

    private int currentPlayer;
    private List<Player> playerList;

    private Pane root = new Pane();
    private Game game;
    private Round round;

    private Parent createContent() {

        root.setPrefSize(WINDOW_WIDTH, WINDOW_HEIGHT);

        Region background = new Region();
        background.setPrefSize(1500, 800);
        background.setStyle("-fx-background-color: green");

        Button btn = new Button();
        btn.setText("Start  ");

        // Set the action event handler to call handleButtonClick method
        btn.setOnAction(event -> {
            FadeTransition fadeOut = new FadeTransition(Duration.seconds(0.4), root);
            fadeOut.setToValue(0); // Fade to transparent
            fadeOut.setOnFinished(event2 -> {
                // Fade in the new scene
                FadeTransition fadeIn = new FadeTransition(Duration.seconds(0.4), root);
                fadeIn.setFromValue(0); // Start from transparent
                fadeIn.setToValue(1); // Fade to opaque
                fadeIn.play();
                startGame();
                root.getChildren().remove(btn); // Removes the button from the root pane
            });
            fadeOut.play();
        });

        root.getChildren().add(background);
        root.getChildren().add(btn);

        return root;
    }

    private void startGame() {
        game = new Game();

        playerList = new ArrayList<>();
        try {
            game.addPlayer(new HumanPlayer("1"));
            game.addPlayer(new AIPlayer("2"));
            game.addPlayer(new AIPlayer("3"));
            game.addPlayer(new AIPlayer("4"));

            playerList = game.getPlayers();

            startRound();

        } catch (PlayerException e) {
            e.printStackTrace();
        }
    }

    private void startRound(){
        round = new Round(0, game);

        round.dealHands();

        for (int i = 0; i < Game.NUM_PLAYERS; i++) {
            List<Card> hand = playerList.get(i).getHand().getCards();
            for (Card c : hand) {
                // Hard code first player
                if (c.getRank().getName().equals("Two") && c.getSuit().getName().equals("Clubs")) {
                    round.setPlayerStartingFirst(i);
                }
            }
        }

        // Create Play Area
        Pane playArea = createPlayArea();
        playArea.setLayoutX((root.getPrefWidth() - playArea.getPrefWidth()) / 2);
        playArea.setLayoutY((root.getPrefHeight() - playArea.getPrefHeight()) / 2);
        root.getChildren().add(playArea);

        round.startNewTrick();

        // Create Player Areas
        setupPlayerAreas(playerList, round);

        // Set Playable Cards to starting player
        currentPlayer = round.getPlayerStartingFirst();

        // Start Turn
        // Check if player is Human or AI
        if (playerList.get(currentPlayer) instanceof AIPlayer) {
            Card cardPlayed = playerList.get(currentPlayer).playCard(round, round.getCurrentTrick());
            if (cardPlayed.isHeart() && !round.isHeartsBroken()) {
                round.setHeartsBroken(true);
            }
            ObservableList<Node> currentPlayerCardViews = getCardViewsOfPlayer(currentPlayer);
            for (Node node : currentPlayerCardViews) {
                if (((Card) node.getUserData()).isSameAs(cardPlayed)) {
                    moveCard(node, cardPlayed);
                }
            }
        } else {
            enableCards(currentPlayer);
        }
    }

    private void nextTurn() {
        System.out.println("Number of Tricks played: " + round.getNumTricksPlayed());
        if(round.getNumTricksPlayed() == 12){
            root.getChildren().clear();
            startRound();
        }

        if (round.getCurrentTrick().getCardsInTrick().size() == 4) {
            System.out.println("------------------------");
            round.startNewTrick();
            currentPlayer = round.getPlayerStartingFirst();

        } else {
            // // Get next player - possible to implement it in Game?
            // if (currentPlayer == game.getPlayers().size() - 1) {
                //     currentPlayer = 0;
            // } else {
                //     currentPlayer += 1;
            // }
            currentPlayer = game.getNextPlayer(currentPlayer);
        }

        System.out.println("Next Player: Player " + currentPlayer);

        // Check if player is Human or AI
        if (playerList.get(currentPlayer) instanceof AIPlayer) {
            Card cardPlayed = playerList.get(currentPlayer).playCard(round, round.getCurrentTrick());
            if (cardPlayed.isHeart() && !round.isHeartsBroken()) {
                round.setHeartsBroken(true);
            }
            ObservableList<Node> currentPlayerCardViews = getCardViewsOfPlayer(currentPlayer);
            for (Node node : currentPlayerCardViews) {
                if (((Card) node.getUserData()).isSameAs(cardPlayed)) {
                    moveCard(node, cardPlayed);
                }
            }
        } else {
            enableCards(currentPlayer);
        }

        // round.startNewTrick();
    }

    private ObservableList<Node> getCardViewsOfPlayer(int id) {
        for (Node node : root.getChildren()) {
            if (node.getId() != null)
                if (Integer.parseInt(node.getId()) == id) {
                    Pane pane = (Pane) node;
                    return pane.getChildren();
                }
        }

        // Empty List
        return FXCollections.observableArrayList();
    }

    private void enableCards(int currentPlayer) {
        Player player = playerList.get(currentPlayer);
        ArrayList<Card> playableCards = player.getHand().getPlayableCards(round, round.getCurrentTrick());
        System.out.println("Playable Cards");
        for (Card c: playableCards) {
            System.out.println(c);
        }
        ObservableList<Node> cards = getCardViewsOfPlayer(currentPlayer);
        for (Node cardView : cards) {
            Card selectedCard = (Card) cardView.getUserData();

            if (!playableCards.contains(selectedCard)) {
                continue;
            }
            cardView.getStyleClass().add("card-active");
            cardView.setOnMouseClicked(event -> {
                disableCards(getCardViewsOfPlayer(currentPlayer));

                Card cardPlayed = (Card) cardView.getUserData();
                // shift this into a function?? idk but ill clean this up later
                if (cardPlayed.isHeart() && !round.isHeartsBroken()) {
                    round.setHeartsBroken(true);
                }
                // Move card to play area
                moveCard(cardView, cardPlayed);
            });
        }
    }

    private void disableCards(ObservableList<Node> cards) {
        for (Node card : cards) {
            card.setOnMouseClicked(null);
            card.getStyleClass().remove("card-active");
        }
    }

    private void moveCard(Node cardView, Card cardPlayed) {
        TranslateTransition transition = new TranslateTransition(Duration.seconds(0.5), cardView);

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
            transition.setToY(-160);
            transition.setToX((((PLAYER_AREA_WIDTH / 2) - cardView.getLayoutX()) - CARD_WIDTH / 2)+270);
        } else if (playerNo == 3) { // Top player
            transition.setToY(180);
            transition.setToX(((PLAYER_AREA_WIDTH / 2) - cardView.getLayoutX()) - CARD_WIDTH / 2);
        } else if (playerNo == 4) { // Right player
            transition.setToY(-160);
            transition.setToX((((PLAYER_AREA_WIDTH / 2) - cardView.getLayoutX()) - CARD_WIDTH / 2)-620);
        }

        transition.setCycleCount(1);

        transition.setOnFinished(event -> {
            System.out.println("Card played: " + cardPlayed + " by Player " + (playerNo));
            playerList.get(currentPlayer).getHand().removeCard(cardPlayed);
            round.getCurrentTrick().addCardToTrick(cardPlayed);
            nextTurn();
        });

        transition.play();

        cardView.toFront(); // Bring the card to the front
        // Disable mouse interaction with the card
        cardView.setDisable(true);
    }

    private Pane createCardViewsOfPlayer(Pane playerArea, Player player) {
        try {
            String currentDirectory = System.getProperty("user.dir");
            List<Card> hand = player.getHand().getCards();

            for (int i = 0; i < hand.size(); i++) {
                Card card = hand.get(i);

                File file = new File(currentDirectory + "/images/" + card.getFilename());
                Image cardImage = new Image(new FileInputStream(file));
                ImageView cardView = new ImageView(cardImage);
                cardView.setFitWidth(CARD_WIDTH);
                cardView.setFitHeight(CARD_HEIGHT);

                double xPos = i * (CARD_WIDTH + SPACING);

                cardView.setLayoutX(xPos);
                cardView.setLayoutY((playerArea.getPrefHeight() - CARD_HEIGHT * 1.5));

                // cardView.setRotate(-90);

                // Determine if the card is playable
                boolean isPlayable = hand.contains(card);
                // Apply hover effect
                addHoverEffect(cardView, isPlayable);

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

    private Pane createPlayArea() {
        Pane playArea = new Pane();
        playArea.setPrefSize(PLAY_AREA_WIDTH, PLAY_AREA_HEIGHT);
        playArea.setStyle("-fx-background-color: beige; -fx-border-color: black;");
        return playArea;
    }

    private void setupPlayerAreas(List<Player> playerList, Round round) {
        for (int i = 0; i < playerList.size(); i++) {
            int position = i;
            Pane playerArea = createPlayerArea(position);
            positionPlayerArea(playerArea, position);

            // Create Cards
            playerArea = createCardViewsOfPlayer(playerArea, playerList.get(i));
            playerArea.setId(i + "");

            root.getChildren().add(playerArea);
        }
    }

    private Pane createPlayerArea(int position) {
        Pane playerArea = new Pane();

        if (position == 2 || position == 0) {
            playerArea.setPrefSize(PLAYER_AREA_WIDTH, PLAYER_AREA_HEIGHT);
        } else {
            playerArea.setPrefSize(PLAYER_AREA_HEIGHT, PLAYER_AREA_WIDTH);
        }
        playerArea.setStyle("-fx-border-color: black; -fx-border-width: 2;");
        return playerArea;
    }

    private void positionPlayerArea(Pane playerArea, int position) {
        double xPos = 0;
        double yPos = 0;
        // 0: Bottom, 1: Left, 2: Top, 3: Right
        switch (position) {
            case 0:
                xPos = (WINDOW_WIDTH - playerArea.getPrefWidth()) / 2;
                yPos = WINDOW_HEIGHT - playerArea.getPrefHeight();
                break;
            case 1:
                yPos = (WINDOW_HEIGHT - playerArea.getPrefHeight()) / 2;
                break;
            case 2:
                xPos = (WINDOW_WIDTH - playerArea.getPrefWidth()) / 2;
                break;
            case 3:
                xPos = WINDOW_WIDTH - playerArea.getPrefWidth();
                yPos = (WINDOW_HEIGHT - playerArea.getPrefHeight()) / 2;
                break;
        }
        playerArea.setLayoutX(xPos);
        playerArea.setLayoutY(yPos);
    }

    @Override
    public void start(Stage stage) throws IOException {
        Scene scene = new Scene(createContent());

        scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());

        stage.setScene(scene);
        stage.setWidth(WINDOW_WIDTH);
        stage.setHeight(WINDOW_HEIGHT);
        stage.setTitle("Hearts");
        stage.show();
    }

    private void addHoverEffect(ImageView cardView, boolean isPlayable) {
        if (isPlayable) {
            cardView.setOnMouseEntered(event -> {
                cardView.setEffect(new DropShadow()); // Apply drop shadow effect when mouse enters
                cardView.setCursor(Cursor.HAND); // Change cursor to hand

                // Translate animation to move the card up
                TranslateTransition hoverTransition = new TranslateTransition(Duration.seconds(0.2), cardView);
                hoverTransition.setToY(-20); // Adjust this value to change the hover distance
                hoverTransition.play();
            });

            cardView.setOnMouseExited(event -> {
                cardView.setEffect(null); // Remove drop shadow effect when mouse exits
                cardView.setCursor(Cursor.DEFAULT); // Change cursor back to default

                // Translate animation to move the card back down
                TranslateTransition hoverTransition = new TranslateTransition(Duration.seconds(0.2), cardView);
                hoverTransition.setToY(0);
                hoverTransition.play();
            });
        }
    }

    public static void main(String[] args) {
        launch();
    }
}