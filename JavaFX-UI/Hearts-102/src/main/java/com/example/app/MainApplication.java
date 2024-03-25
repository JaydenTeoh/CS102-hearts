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
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.DropShadow;
import javafx.scene.Cursor;
import javafx.util.Duration;
import java.util.concurrent.TimeUnit;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.animation.ScaleTransition;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.*;

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
    private List<Node> currentCardViewsInTrick;

    private Pane root = new Pane();
    private Game game;
    private Round round;
    private Label roundLabel;
    private int currentRound;

    private Parent createContent() {
        root.setPrefSize(WINDOW_WIDTH, WINDOW_HEIGHT);

        Region background = new Region();
        background.setPrefSize(1500, 800);
        background.setStyle("-fx-background-color: green");

        Button btn = new Button();
        btn.setText("Start");

        btn.setPrefSize(200, 50);
        btn.setStyle("-fx-font-size: 20px;");

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

        root.getChildren().addAll(background, btn);

        root.widthProperty().addListener((obs, oldVal, newVal) -> {
            btn.setLayoutX(newVal.doubleValue() / 2 - btn.getWidth() / 2);
        });

        root.heightProperty().addListener((obs, oldVal, newVal) -> {
            btn.setLayoutY(newVal.doubleValue() / 2 - btn.getHeight() / 2);
        });

        btn.layoutBoundsProperty().addListener((obs, oldVal, newVal) -> {
            btn.setLayoutX((WINDOW_WIDTH - newVal.getWidth()) / 2);
            btn.setLayoutY((WINDOW_HEIGHT - newVal.getHeight()) / 2);
        });

        return root;
    }

    private void startGame() {
        game = new Game();
        initializeRoundDisplay();
        currentRound = 0;

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

    private void startRound() {
        round = new Round(0, game);
        currentRound++;
        createAndAddAllScorePanes();
        updateScoresDisplay();
        updateRoundDisplay(currentRound);

        currentCardViewsInTrick = new ArrayList<>();
        round.dealHands();

        for (int i = 0; i < Game.NUM_PLAYERS; i++) {
            List<Card> hand = playerList.get(i).getHand().getCards();
            for (Card c : hand) {
                // set 2 of clubs to start first, as per game rules
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

        // updateScoresDisplay();
        round.startNewTrick();

        // Create Player Areas
        setupPlayerAreas(playerList, round);

        ObservableList<Node> cards = getCardViewsOfPlayer(0);
        disableCards(cards);

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
                Card card = (Card) node.getUserData();
                if (card.isSameAs(cardPlayed)) {
                    // Flip the card to face up if it's played by AI player
                    CardImageView cardView = (CardImageView) node;
                    cardView.setImage(true); // Flip the card to face up
                    moveCard(node, cardPlayed);
                    currentCardViewsInTrick.add(node);
                }
            }
        } else {
            enableCards(currentPlayer);
        }
    }

    private void updateScoresAfterCurrentTrickBackend(Trick currTrick) {
        currTrick.setNumPoints(); // this sets numPoints in trick based on the cards in it
        int pointsInCurrTrick = currTrick.getNumPoints();
        int winningCardIndexInTrick = currTrick.getWinningCardIndex();
        int shift = Game.NUM_PLAYERS - 1 - currentPlayer; // because current player is last player of trick
        int winnerIndexInPlayerList = winningCardIndexInTrick - shift;
        if (winnerIndexInPlayerList < 0) {
            winnerIndexInPlayerList += 4;
        }

        Player winner = playerList.get(winnerIndexInPlayerList);
        round.setPlayersPointsInCurrentRound(winner, pointsInCurrTrick);

        // displays transition of the player that won the trick
        animateTrickToPlayerArea(winnerIndexInPlayerList);
    }

    private void nextTurn() {
        Trick currTrick = round.getCurrentTrick();

        if (currTrick.getCardsInTrick().size() == 4) {
            System.out.println("------------------------");

            updateScoresAfterCurrentTrickBackend(currTrick);
            updateScoresDisplay();

            // start new trick
            for (Node cardView: currentCardViewsInTrick) {
                ((ImageView) cardView).setImage(null);
            }

            currentCardViewsInTrick = new ArrayList<>();

            try {
                TimeUnit.SECONDS.sleep(1); // Sleep for 5 seconds
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            round.startNewTrick();
            currentPlayer = round.getPlayerStartingFirst();

        } else {
            currentPlayer = game.getNextPlayer(currentPlayer);
        }

        // when all tricks have been played, start a new round
        if (currTrick.getCardsInTrick().size() == 4 && round.getNumTricksPlayed() == 12) {
            HashMap<Player, Integer> roundPoints = round.getPlayersPointsInCurrentRound();
            Iterator<Player> iter = roundPoints.keySet().iterator();

            while (iter.hasNext()) {
                Player p = iter.next();
                game.setPlayersPointsInCurrentGame(p, roundPoints.get(p));
            }

            // updateScoresAfterCurrentTrickBackend(currTrick);
            updateScoresDisplay();

            // make new background for next round
            root.getChildren().clear();
            Region background = new Region();
            background.setPrefSize(1500, 800);
            background.setStyle("-fx-background-color: green");
            root.getChildren().add(background);


            // start new round
            if (!game.isEnded()) {
                startRound();
                return;
            }

            // display final scores if game ended


        }

        System.out.println("Next Player: Player " + (currentPlayer + 1));

        // Check if player is Human or AI
        if (playerList.get(currentPlayer) instanceof AIPlayer) {
            Card cardPlayed = playerList.get(currentPlayer).playCard(round, round.getCurrentTrick());
            if (cardPlayed.isHeart() && !round.isHeartsBroken()) {
                round.setHeartsBroken(true);
            }
            ObservableList<Node> currentPlayerCardViews = getCardViewsOfPlayer(currentPlayer);
            for (Node node : currentPlayerCardViews) {
                Card card = (Card) node.getUserData();
                if (card.isSameAs(cardPlayed)) {
                    // Flip the card to face up if it's played by AI player
                    CardImageView cardView = (CardImageView) node;
                    cardView.setImage(true); // Flip the card to face up
                    moveCard(node, cardPlayed);
                    currentCardViewsInTrick.add(node);
                }
            }
        } else {
            enableCards(currentPlayer);
        }

        // round.startNewTrick();
    }

    // used to differentiate player panes and score panes
    private static boolean isNumeric(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private ObservableList<Node> getCardViewsOfPlayer(int id) {
        for (Node node : root.getChildren()) {
            if (node.getId() != null)
                if (isNumeric(node.getId()))
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
        System.out.println("\nPlayable Cards:");
        for (Card c : playableCards) {
            System.out.println(c);
        }
        System.out.println();
        for (Node child : root.getChildren()) {
            if ("0".equals(child.getId())) {
                child.toFront();
                break; // Exit the loop once the desired node is found and brought to front
            }
        }

        ObservableList<Node> cards = getCardViewsOfPlayer(currentPlayer);
        disableCards(cards);
        for (Node cardView : cards) {
            Card selectedCard = (Card) cardView.getUserData();

            if (!playableCards.contains(selectedCard)) {
                continue;
            }
            addHoverEffect((ImageView) cardView);
            cardView.setEffect(null);
            cardView.setOpacity(1);
            cardView.getStyleClass().add("card-active");
            cardView.setOnMouseClicked(event -> {
                disableCards(getCardViewsOfPlayer(currentPlayer));
                currentCardViewsInTrick.add(cardView);
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
            card.setOnMouseEntered(null);
            card.setOnMouseExited(null);
            ColorAdjust grayscale = new ColorAdjust();
            grayscale.setBrightness(-0.5); // Lower brightness by 50%
            grayscale.setContrast(-0.5); // Lower contrast by 50%
            card.setEffect(grayscale);
            TranslateTransition hoverTransition = new TranslateTransition(Duration.seconds(0.2), card);
            hoverTransition.setToY(0);
            hoverTransition.play();

        }
    }

    private void moveCard(Node cardView, Card cardPlayed) {
        TranslateTransition transition = new TranslateTransition(Duration.seconds(0.5), cardView);
        cardView.setOnMouseEntered(null);
        cardView.setOnMouseExited(null);
        cardView.setEffect(null); // Remove drop shadow effect when mouse exits
        cardView.setCursor(Cursor.DEFAULT); // Change cursor back to default
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
            transition.setToY((((PLAYER_AREA_HEIGHT / 2) - cardView.getLayoutY()) - CARD_HEIGHT / 2)+180);
            transition.setToX(500);
        } else if (playerNo == 3) { // Top player
            transition.setToY(180);
            transition.setToX(((PLAYER_AREA_WIDTH / 2) - cardView.getLayoutX()) - CARD_WIDTH / 2);
        } else if (playerNo == 4) { // Right player
            transition.setToY((((PLAYER_AREA_HEIGHT / 2) - cardView.getLayoutY()) - CARD_HEIGHT / 2)+180);
            transition.setToX(-350);
        }

        transition.setCycleCount(1);

        transition.setOnFinished(event -> {
            System.out.println("Card played: " + cardPlayed + " by Player " + (playerNo));

            // remove card from current player's hand
            playerList.get(currentPlayer).getHand().removeCard(cardPlayed);

            // add card to current trick
            round.getCurrentTrick().addCardToTrick(cardPlayed);
            nextTurn();

            // Bring the card to the front
            cardView.toFront();

            // Disable mouse interaction with the card
            cardView.setDisable(true);
        });

        transition.play();

    }

    private Pane createCardViewsOfPlayer(Pane playerArea, Player player) {
        try {
            String currentDirectory = System.getProperty("user.dir");
            List<Card> hand = player.getHand().getCards();
            int playerIndex = playerList.indexOf(player);

            for (int i = 0; i < hand.size(); i++) {
                Card card = hand.get(i);

                File faceUpFile = new File(currentDirectory + "/images/" + card.getFilename());
                Image faceUpImage = new Image(new FileInputStream(faceUpFile));
                File faceDownFile = new File(currentDirectory + "/face_down_image.png");
                Image faceDownImage = new Image(new FileInputStream(faceDownFile));
                CardImageView cardView = new CardImageView(faceUpImage, faceDownImage);
                if (player instanceof HumanPlayer) {
                    cardView.setImage(true);
                }
                cardView.setFitWidth(CARD_WIDTH);
                cardView.setFitHeight(CARD_HEIGHT);

                double xPos, yPos;
                if (playerIndex == 0) { // Bottom player
                    xPos = i *  (CARD_WIDTH + SPACING); // Normal horizontal spacing
                    yPos = 0; // Align with top edge
                } else if (playerIndex == 1) { // Left player
                    xPos = 30; // Align with left edge
                    yPos = i * 30; // Vertical spacing
                } else if (playerIndex == 2) { // Top player
                    xPos = i * (CARD_WIDTH + SPACING); // Normal horizontal spacing
                    yPos = 30; // Align with top edge
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
                    addHoverEffect(cardView);
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
            // final String playerAreaId = playerArea.getId();
            // playerArea.setOnMouseEntered(event -> {
            //     // Print the node's ID or any other identifying detail
            //     System.out.println("Mouse entered: " + playerAreaId);
            // });


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

    private void addHoverEffect(ImageView cardView) {
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

    private void setScorePaneLayout(Pane scorePane, double layoutX, double layoutY) {
        scorePane.setLayoutX(layoutX);
        scorePane.setLayoutY(layoutY);
    }

    private Pane createScoreArea(int playerIndex) {
        Pane scorePane = new Pane();
        scorePane.setPrefSize(90, 82);
        scorePane.setStyle("-fx-background-color: black; -fx-border-color: black;");

        // Assign an ID to the score pane based on the player's position
        scorePane.setId("scorePanePlayer" + (playerIndex + 1));

        // Create the score label
        Label scoreLabel = new Label();
        scoreLabel.setStyle("-fx-text-fill: white; -fx-font-size: 16px;");
        scoreLabel.setLayoutX(5);
        scoreLabel.setLayoutY(10);

        // Add the score label to the score pane
        scorePane.getChildren().add(scoreLabel);

        return scorePane;
    }

    private void createAndAddScorePane(double layoutX, double layoutY, int playerIndex) {
        Pane scorePane = createScoreArea(playerIndex);
        setScorePaneLayout(scorePane, layoutX, layoutY);
        root.getChildren().add(scorePane);
    }

    private void createAndAddAllScorePanes() {
        // is there anyway to get exact location where we should put or just hard code?
        double xPos, yPos;

        for (int i = 0; i < Game.NUM_PLAYERS; i++) {
            switch (i) {
                case 0:
                    xPos = WINDOW_WIDTH - 540;
                    yPos = WINDOW_HEIGHT - 250;
                    break;
                case 1:
                    xPos = 160;
                    yPos = 100;
                    break;
                case 2:
                    xPos = WINDOW_WIDTH - 540;
                    yPos = 168;
                    break;
                case 3:
                    xPos = WINDOW_WIDTH - 250;
                    yPos = WINDOW_HEIGHT - 183;
                    break;
                default:
                    xPos = 0;
                    yPos = 0;
                    break;
            }

            createAndAddScorePane(xPos, yPos, i);
        }
    }

    private void initializeRoundDisplay() {
        roundLabel = new Label("Round 1");
        roundLabel.setLayoutX(10);
        roundLabel.setLayoutY(10);
        roundLabel.setStyle("-fx-text-fill: white; -fx-font-size: 16px;");

        root.getChildren().add(roundLabel);
    }

    private void updateRoundDisplay(int currentRound) {
        if (roundLabel != null) {
            roundLabel.setText("Round " + currentRound);
        } else {
            // If roundLabel is null, it means it hasn't been initialized yet
            initializeRoundDisplay();
            roundLabel.setText("Round " + currentRound); // Update the label after initialization
        }
    }

    private void updateScoresDisplay() {
        HashMap<Player, Integer> pointsInCurrentRound = round.getPlayersPointsInCurrentRound();
        HashMap<Player, Integer> pointsInCurrentGame = game.getPlayersPointsInCurrentGame();

        for (int i = 0; i < playerList.size(); i++) {
            Player player = playerList.get(i);
            int roundPoints = pointsInCurrentRound.get(player);
            int gamePoints = pointsInCurrentGame.get(player);

            // Find the score pane for the current player
            Pane scorePane = (Pane) root.lookup("#scorePanePlayer" + (i + 1));
            if (scorePane != null) {
                // Update the score label text
                Label scoreLabel = (Label) scorePane.getChildren().get(0); // Assuming the score label is the first child
                scoreLabel.setText("Player " + (i + 1) + ":\nRound = " + roundPoints + "\nGame = " + gamePoints);
            }
        }
    }

    private void animateTrickToPlayerArea(int winnerPlayerIndex) {
        try {
            String currentDirectory = System.getProperty("user.dir");
            File file = new File(currentDirectory + "/fourCards.png");
            // Print out whether the file exists
            System.out.println("File exists: " + file.exists());

            // Load the image
            Image image = new Image(new FileInputStream(file));

            ImageView imageView = new ImageView(image);
            imageView.setPreserveRatio(true);

            // Set initial size and position of the image
            imageView.setFitWidth(50); // Initial width
            imageView.setFitHeight(50); // Initial height
            imageView.setLayoutX((root.getPrefWidth() - imageView.getFitWidth()) / 2);
            imageView.setLayoutY((root.getPrefHeight() - imageView.getFitHeight()) / 2);

            root.getChildren().add(imageView);

            // Animation to scale up
            ScaleTransition scaleUp = new ScaleTransition(Duration.seconds(1), imageView);
            scaleUp.setToX(2); // Double the width
            scaleUp.setToY(2); // Double the height

            // Animation to scale down
            ScaleTransition scaleDown = new ScaleTransition(Duration.seconds(1), imageView);
            scaleDown.setToX(1); // Original width
            scaleDown.setToY(1); // Original height

            TranslateTransition transition = new TranslateTransition(Duration.seconds(0.5), imageView);
            // Animation to move to player areadou
            switch (winnerPlayerIndex) {
                case 0: // Bottom player
                    transition.setToX(250);
                    transition.setToY(350);
                    break;
                case 1: // Left player
                    transition.setToX(-660);
                    transition.setToY(237);
                    break;
                case 2: // Top player
                    transition.setToX(250);
                    transition.setToY(-320);
                    break;
                case 3: // Right player
                    transition.setToX(660);
                    transition.setToY(237);
                    break;
                default:
                    break;
            }
            // Start the animation
            scaleUp.play();
            transition.play();
        } catch (FileNotFoundException e) {
            System.out.println("Error loading image: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch();
    }
}