package com.example.app;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.*;

import com.example.gameplay.Game;
import com.example.players.Player;
import com.example.gameplay.Round;
import com.example.gameplay.Trick;

import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

public class ScoreDisplayUtility {

    public static void setScorePaneLayout(Pane scorePane, double layoutX, double layoutY) {
        scorePane.setLayoutX(layoutX);
        scorePane.setLayoutY(layoutY);
    }

    public static Pane createScoreArea(int playerIndex) {
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
    public static void createAndAddScorePane(Pane root, double layoutX, double layoutY, int playerIndex) {
        Pane scorePane = createScoreArea(playerIndex);
        setScorePaneLayout(scorePane, layoutX, layoutY);
        root.getChildren().add(scorePane);
    }

    public static void createAndAddAllScorePanes(Pane root) {
        // is there anyway to get exact location where we should put or just hard code?
        double xPos, yPos;

        for (int i = 0; i < Game.NUM_PLAYERS; i++) {
            switch (i) {
                case 0:
                    xPos = MainApplication.WINDOW_WIDTH / 2  - 75;
                    yPos = MainApplication.WINDOW_HEIGHT - 100;
                    break;
                case 1:
                    xPos = 100;
                    yPos = MainApplication.WINDOW_HEIGHT / 2 - 75;
                    break;
                case 2:
                    xPos = MainApplication.WINDOW_WIDTH / 2 -75;
                    yPos = 0;
                    break;
                case 3:
                    xPos = 1300;
                    yPos = MainApplication.WINDOW_HEIGHT / 2 - 75;
                    break;
                default:
                    xPos = 0;
                    yPos = 0;
                    break;
            }

            createAndAddScorePane(root, xPos, yPos, i);
        }
    }

    public static void animateTrickToPlayerArea(Pane root, int winnerPlayerIndex) {
        try {
            String currentDirectory = System.getProperty("user.dir");
            File file = new File(currentDirectory + "/images/" + "/fourCards.png");
            
            // // Print out whether the file exists
            // System.out.println("File exists: " + file.exists());

            // Load the image
            Image image = new Image(new FileInputStream(file));

            ImageView imageView = new ImageView(image);
            imageView.setPreserveRatio(true);

            // Set initial size and position of the image
            imageView.setFitWidth(35); // Initial width
            imageView.setFitHeight(35); // Initial height
            imageView.setLayoutX(750);
            imageView.setLayoutY(400);

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
                    transition.setToX(40);
                    transition.setToY(320);
                    break;
                case 1: // Left player
                    transition.setToX(-620);
                    transition.setToY(30);
                    break;
                case 2: // Top player
                    transition.setToX(40);
                    transition.setToY(-365);
                    break;
                case 3: // Right player
                    transition.setToX(585);
                    transition.setToY(30);
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

    public static void updateScoresAfterCurrentTrickBackend(Pane root, Trick currTrick, Round round, int currentPlayer, List<Player> playerList) {
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
        animateTrickToPlayerArea(root, winnerIndexInPlayerList);
    }

    public static void updateScoresDisplay(Pane root, Game game, Round round, List<Player> playerList) {
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
}
