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
import javafx.animation.PauseTransition;
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
import java.util.List;
import javax.sound.sampled.Clip;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.sound.sampled.LineUnavailableException;

public class MainApplication extends Application {
    public static final double WINDOW_WIDTH = 1500;
    public static final double WINDOW_HEIGHT = 800;

    private int currentPlayer;
    private List<Player> playerList;
    private List<Node> currentCardViewsInTrick;

    private Pane root = new Pane();
    private Game game;
    private Round round;
    private Label roundLabel;

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
        // // initialise instance variable root here
        // root = new Pane();

        game = new Game();
        roundLabel = new Label("Round 1");

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
        game.incrementNumRounds();
        ScoreDisplayUtility.createAndAddAllScorePanes(root);

        // this adds previous round's score to game
        ScoreDisplayUtility.updateScoresDisplay(root, game, round, playerList);
        RoundDisplayUtility.initializeRoundDisplay(root, roundLabel);
        RoundDisplayUtility.updateRoundDisplay(root, roundLabel, game.getNumRounds());

        currentCardViewsInTrick = new ArrayList<>();
        round.dealHands();

        for (int i = 0; i < Game.NUM_PLAYERS; i++) {
            List<Card> hand = playerList.get(i).getHand().getCards();
            for (Card c : hand) {
                // set 2 of clubs to start first, as per game rules
                if (c.equals(Game.ROUND_STARTING_CARD)) {
                    round.setPlayerStartingFirst(i);
                }
            }
        }

        // Create Play Area
        Pane playArea = PlayAreaUtility.createPlayArea();
        playArea.setLayoutX((root.getPrefWidth() - playArea.getPrefWidth()) / 2);
        playArea.setLayoutY((root.getPrefHeight() - playArea.getPrefHeight()) / 2);
        root.getChildren().add(playArea);

        round.startNewTrick();

        // Create Player Areas
        PlayAreaUtility.setupPlayerAreas(playerList, round, root);

        CardViewUtility.disableCards(root);

        // Set Playable Cards to starting player
        currentPlayer = round.getPlayerStartingFirst();

        nextTurn();
    }

    private void processNextTrick(Trick currTrick) {
        System.out.println("------------------------");

        ScoreDisplayUtility.updateScoresAfterCurrentTrickBackend(root, currTrick, round, currentPlayer, playerList);
        ScoreDisplayUtility.updateScoresDisplay(root, game, round, playerList);

        // start new trick
        for (Node cardView: currentCardViewsInTrick) {
            ((ImageView) cardView).setImage(null);
        }

        currentCardViewsInTrick = new ArrayList<>();

        round.startNewTrick();
    }

    private void processNextRound() {
        HashMap<Player, Integer> roundPoints = round.getPlayersPointsInCurrentRound();
        Iterator<Player> iter = roundPoints.keySet().iterator();

        while (iter.hasNext()) {
            Player p = iter.next();
            game.setPlayersPointsInCurrentGame(p, roundPoints.get(p) % 26);
        }

        // ScoreDisplayUtility.updateScoresDisplay(root, game, round, playerList);

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

    private void nextTurn() {
        Trick currTrick = round.getCurrentTrick();

        if (currTrick.getCardsInTrick().size() == 4) {
            PauseTransition pause = new PauseTransition(Duration.seconds(1));

            pause.setOnFinished(event -> {
                processNextTrick(currTrick);
                currentPlayer = round.getPlayerStartingFirst();
                if (round.getNumTricksPlayed() == 13) {
                    // when all tricks have been played, start a new round
                    processNextRound();
                } else {
                    nextTurn();
                }
                
            });

            // Start the pause
            pause.play();
            return;

        } else if (currTrick.getCardsInTrick().size() != 0 || round.getNumTricksPlayed() != 0 ){
            currentPlayer = game.getNextPlayer(currentPlayer);
        }

        // Check if player is Human or AI
        if (playerList.get(currentPlayer) instanceof AIPlayer) {
            Card cardPlayed = playerList.get(currentPlayer).playCard(round, round.getCurrentTrick());
            if (cardPlayed.isHeart() && !round.isHeartsBroken()) {
                round.setHeartsBroken();
            }
            ObservableList<Node> currentPlayerCardViews = CardViewUtility.getCardViewsOfPlayer(root, currentPlayer);
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
            enableCards();
        }
    }

    public void enableCards() {
        ArrayList<Card> playableCards = playerList.get(0).getHand().getPlayableCards(round.getCurrentTrick());
        
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

        ObservableList<Node> cards = CardViewUtility.getCardViewsOfPlayer(root, 0);

        CardViewUtility.disableCards(root);
        for (Node cardView : cards) {
            Card selectedCard = (Card) cardView.getUserData();

            if (!playableCards.contains(selectedCard)) {
                continue;
            }
            CardViewUtility.addHoverEffect((ImageView) cardView);
            cardView.setEffect(null);
            cardView.setOpacity(1);
            cardView.getStyleClass().add("card-active");
            cardView.setOnMouseClicked(event -> {
                CardViewUtility.disableCards(root);
                Card cardPlayed = (Card) cardView.getUserData();
                // Move card to play area
                moveCard(cardView, cardPlayed);
            });
        }
    }

    private void moveCard(Node cardView, Card cardPlayed) {
        currentCardViewsInTrick.add(cardView);
        // shift this into a function?? idk but ill clean this up later
        if (cardPlayed.isHeart() && !round.isHeartsBroken()) {
            round.setHeartsBroken();
        }
        TranslateTransition transition = new TranslateTransition(Duration.seconds(0.5), cardView);
        CardViewUtility.disableHover((ImageView) cardView);
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
            transition.setToY(-(PlayAreaUtility.PLAYER_AREA_HEIGHT) + 90);
            transition.setToX(((PlayAreaUtility.PLAYER_AREA_WIDTH / 2) - cardView.getLayoutX()) - CardViewUtility.CARD_WIDTH / 2);
        } else if (playerNo == 2) { // Left player
            transition.setToY((((PlayAreaUtility.PLAYER_AREA_HEIGHT / 2) - cardView.getLayoutY()) - CardViewUtility.CARD_HEIGHT / 2)+180);
            transition.setToX(500);
        } else if (playerNo == 3) { // Top player
            transition.setToY(180);
            transition.setToX(((PlayAreaUtility.PLAYER_AREA_WIDTH / 2) - cardView.getLayoutX()) - CardViewUtility.CARD_WIDTH / 2);
        } else if (playerNo == 4) { // Right player
            transition.setToY((((PlayAreaUtility.PLAYER_AREA_HEIGHT / 2) - cardView.getLayoutY()) - CardViewUtility.CARD_HEIGHT / 2)+180);
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

    @Override
    public void start(Stage stage) throws IOException {
        try {
            Clip clip = MusicUtility.loadMusic();
            clip.start();
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
        
        Scene scene = new Scene(createContent());

        scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());

        stage.setScene(scene);
        stage.setWidth(WINDOW_WIDTH);
        stage.setHeight(WINDOW_HEIGHT);
        stage.setTitle("Hearts");
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}