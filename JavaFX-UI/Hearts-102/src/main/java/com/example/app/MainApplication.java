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
import javafx.animation.ParallelTransition;
import javafx.animation.PauseTransition;
import javafx.animation.RotateTransition;
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
import java.util.HashSet;
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

    private Pane root;
    private Game game;
    private Round round;
    private Label roundLabel;
    private int currentRound;
    private List<CardImageView> cardViewsToPass = new ArrayList<>();

    Button passCardbutton = new Button();

    private Parent createContent() {
        root = new Pane();
        root.setPrefSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        
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

        // Create Play Area
        Pane playArea = new Pane();
        playArea.setPrefSize(1000, 600);
        playArea.setLayoutX((root.getPrefWidth() - playArea.getPrefWidth()) / 2);
        playArea.setLayoutY((root.getPrefHeight() - playArea.getPrefHeight()) / 2);
        root.getChildren().add(playArea);

        initialisePassCardButton(playArea);

        round.startNewTrick();

        // Create Player Areas
        PlayAreaUtility.setupPlayerAreas(playerList, round, root);

        // CardViewUtility.disableCards(root);

        selectCardsToPass();
        // nextTurn();
    }

    private void startPassingProcess() {
        CardViewUtility.processPlayerCards(0, playerList, cardViewsToPass, game.getNumRounds(), root, () -> {
            passCardbutton.setVisible(false);
            cardViewsToPass.clear();

            for (int i = 0; i < Game.NUM_PLAYERS; i++) {
                List<Card> hand = playerList.get(i).getHand().getCards();
                for (Card c : hand) {
                    // set 2 of clubs to start first, as per game rules
                    if (c.equals(Game.ROUND_STARTING_CARD)) {
                        round.setPlayerStartingFirst(i);
                    }
                }
            }

            // Set Playable Cards to starting player
            currentPlayer = round.getPlayerStartingFirst();

            nextTurn();
        });
    }

    private void enablePassCardButton() {
        cardViewsToPass.clear();
        passCardbutton.setVisible(true);
    }

    private void initialisePassCardButton(Pane playArea) {
        passCardbutton.setText("Pass 3 cards");

        passCardbutton.setPrefSize(200, 50);
        passCardbutton.setStyle("-fx-font-size: 20px;");

        // Set the action event handler to call handleButtonClick method
        passCardbutton.setOnAction(event -> {
            if (cardViewsToPass.size() == 3) {
                System.out.println("Pass 3 cards");
                startPassingProcess();
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

    private void selectCardsToPass() {
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

    private void processNextTrick(Trick currTrick) {
        System.out.println("------------------------");

        ScoreDisplayUtility.updateScoresAfterCurrentTrickBackend(root, currTrick, round, currentPlayer, playerList);
        ScoreDisplayUtility.updateScoresDisplay(root, game, round, playerList);

        // start new trick
        for (Node cardView : currentCardViewsInTrick) {
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
            enablePassCardButton();
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

        } else if (currTrick.getCardsInTrick().size() != 0) {
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
                Label scoreLabel = (Label) scorePane.getChildren().get(0); // Assuming the score label is the first
                                                                           // child
                scoreLabel.setText("Player " + (i + 1) + ":\nRound = " + roundPoints + "\nGame = " + gamePoints);
            }
        }
    }

    private void animateTrickToPlayerArea(int winnerPlayerIndex) {
        try {
            String currentDirectory = System.getProperty("user.dir");
            File file = new File(currentDirectory + "/images/" + "/fourCards.png");
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