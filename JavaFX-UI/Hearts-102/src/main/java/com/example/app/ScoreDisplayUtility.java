package com.example.app;

import java.util.*;

import com.example.gameplay.Game;
import com.example.players.Player;
import com.example.gameplay.Round;

import javafx.scene.control.Label;
import javafx.scene.layout.Pane;

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
                    xPos = MainApplication.WINDOW_WIDTH - 540;
                    yPos = MainApplication.WINDOW_HEIGHT - 250;
                    break;
                case 1:
                    xPos = 160;
                    yPos = 100;
                    break;
                case 2:
                    xPos = MainApplication.WINDOW_WIDTH - 540;
                    yPos = 168;
                    break;
                case 3:
                    xPos = MainApplication.WINDOW_WIDTH - 250;
                    yPos = MainApplication.WINDOW_HEIGHT - 183;
                    break;
                default:
                    xPos = 0;
                    yPos = 0;
                    break;
            }

            createAndAddScorePane(root, xPos, yPos, i);
        }
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
