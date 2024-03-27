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
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.DropShadow;
import javafx.scene.Cursor;
import javafx.util.Duration;
import javafx.animation.ScaleTransition;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import javax.sound.sampled.Clip;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.sound.sampled.LineUnavailableException;
import javafx.scene.control.ChoiceBox;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.control.Label;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.paint.Color;


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
        // Declare background outside the method
        Region background = new Region();
        background.setPrefSize(1500, 800);

        // Create a label for "Hearts"
        Label heartsLabel = new Label("\uD83D\uDC9D  Hearts  \uD83D\uDC9E");
        heartsLabel.setFont(Font.font("Impact", FontWeight.BOLD, 72)); // Set font size and style
        heartsLabel.setTextFill(Color.WHITE); // Set text color

// Center horizontally
        heartsLabel.setLayoutX((WINDOW_WIDTH/2 - heartsLabel.prefWidth(-1))-200);

// Set vertical position
        heartsLabel.setLayoutY(300);

        // Background setup
        background.setPrefSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        background.setStyle("-fx-background-color: green");

        // Mode selection dropdown menu
        ChoiceBox<String> modeChoiceBox = new ChoiceBox<>();
        modeChoiceBox.getItems().addAll("Classic", "Casino", "School", "Holiday");
        modeChoiceBox.setValue("Classic"); // Default mode
        modeChoiceBox.setPrefSize(200, 50);
        modeChoiceBox.setLayoutX(WINDOW_WIDTH / 2 - 100);
        modeChoiceBox.setLayoutY(WINDOW_HEIGHT / 2 + 100); // Shifted downwards by 200 units

        // Apply font size and alignment styles
        String choiceBoxStyle = "-fx-font-size: 20px; -fx-alignment: center;";
        modeChoiceBox.setStyle(choiceBoxStyle);

        modeChoiceBox.setOnShown(event -> {
            // Apply CSS styling to adjust the width and center alignment of dropdown items
            modeChoiceBox.lookup(".choice-box").setStyle("-fx-pref-width: 200; -fx-alignment: center;");
        });

        // How to Play button
        Button howToPlayButton = new Button("How To Play");
        howToPlayButton.setPrefSize(200, 50);
        howToPlayButton.setLayoutX(WINDOW_WIDTH / 2 - 100);
        howToPlayButton.setLayoutY(WINDOW_HEIGHT / 2 + 175); // Shifted downwards by 200 units
        howToPlayButton.setStyle("-fx-font-size: 20px;");

        // Start button
        Button startButton = new Button("Start");
        startButton.setPrefSize(200, 50);
        startButton.setStyle("-fx-font-size: 20px;");
        startButton.setLayoutX(WINDOW_WIDTH / 2 - 100);
        startButton.setLayoutY(WINDOW_HEIGHT / 2 + 250); // Shifted downwards by 200 units
        startButton.setOnAction(event -> {
            FadeTransition fadeOut = new FadeTransition(Duration.seconds(0.4), root);
            fadeOut.setToValue(0);
            fadeOut.setOnFinished(event2 -> {
                FadeTransition fadeIn = new FadeTransition(Duration.seconds(0.4), root);
                fadeIn.setFromValue(0);
                fadeIn.setToValue(1);
                fadeIn.play();
                startGame();
                root.getChildren().removeAll(startButton, heartsLabel, modeChoiceBox, howToPlayButton); // Remove buttons
            });
            fadeOut.play();
        });

        // Listener for mode selection change
        // Listener for mode selection change
        modeChoiceBox.getSelectionModel().selectedItemProperty().addListener((obs, oldMode, newMode) -> {
            String currentDirectory = System.getProperty("user.dir");
            String updatedImageUrl;
            switch (newMode) {
                case "Casino":
                    updatedImageUrl = "file:" + currentDirectory + "/background1.jpg";
                    break;
                case "School":
                    updatedImageUrl = "file:" + currentDirectory + "/background3.jpeg";
                    break;
                case "Holiday":
                    updatedImageUrl = "file:" + currentDirectory + "/background4.jpeg";
                    break;
                default:
                    updatedImageUrl = ""; // Default or error case
                    background.setStyle("-fx-background-color: green"); // Green background for Classic mode
                    break;
            }
            updateBackgroundImage(updatedImageUrl, background);
        });

        // Add all components to root
        root.getChildren().addAll(background, heartsLabel, modeChoiceBox,howToPlayButton, startButton);

        return root;
    }

    private void updateBackgroundImage(String imageUrl, Region background) {
        BackgroundImage backgroundImage = new BackgroundImage(
                new Image(imageUrl, 1500, 800, false, true),
                BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.DEFAULT, BackgroundSize.DEFAULT);
        background.setBackground(new Background(backgroundImage));
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
        PlayAreaUtility.displayLeaderboard(root, playerList, game);
    }

    private void nextTurn() {
        Trick currTrick = round.getCurrentTrick();
        System.out.println();

        if (currTrick.getCardsInTrick().size() == 4) {
            PauseTransition pause = new PauseTransition(Duration.seconds(1));

            pause.setOnFinished(event -> {
                processNextTrick(currTrick);
                currentPlayer = round.getPlayerStartingFirst();
                System.out.println(currentPlayer);
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

        } else if (currTrick.getCardsInTrick().size() != 0){
            currentPlayer = game.getNextPlayer(currentPlayer);
        }

        // Check if player is Human or AI
        if (playerList.get(currentPlayer) instanceof AIPlayer AI) {
            Card leadingCard = round.getCurrentTrick().getLeadingCard();
            int roundNo = round.getNumTricksPlayed();
            boolean heartsBroken = round.isHeartsBroken();
            int trickSize = round.getCurrentTrick().getCardsInTrick().size();
            int trickPoints = round.getCurrentTrick().getNumPoints();
            Card cardPlayed = AI.playCard(leadingCard, roundNo, heartsBroken, trickSize, trickPoints);
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
                    CardViewUtility.moveCard(cardView, cardPlayed, currentCardViewsInTrick, playerList, () -> {
                        // remove Card from AI player's hand
                        playerList.get(currentPlayer).getHand().removeCard(cardPlayed);
                        // add card to current trick
                        round.getCurrentTrick().addCardToTrick(cardPlayed);
                        nextTurn();
                    });
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
                CardViewUtility.moveCard(cardView, cardPlayed, currentCardViewsInTrick, playerList, () -> {
                    // remove card from current player's hand
                    playerList.get(currentPlayer).getHand().removeCard(cardPlayed);
                    // add card to current trick
                    round.getCurrentTrick().addCardToTrick(cardPlayed);
                    nextTurn();
                });
            });
        }
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