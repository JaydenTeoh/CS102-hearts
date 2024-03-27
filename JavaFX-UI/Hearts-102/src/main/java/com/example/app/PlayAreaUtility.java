package com.example.app;

import java.util.List;
import java.util.stream.Collectors;

import com.example.gameplay.Game;
import com.example.players.Player;

import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class PlayAreaUtility {
    public static final double WINDOW_WIDTH = 1500;
    public static final double WINDOW_HEIGHT = 800;
    public static final int PLAYER_AREA_WIDTH = 600;
    public static final int PLAYER_AREA_HEIGHT = 250;
    public static final int PLAY_AREA_WIDTH = 400;
    public static final int PLAY_AREA_HEIGHT = 200;

    // Method to create a player area
    public static Pane createPlayerArea(int position) {
        Pane playerArea = new Pane();
        double width = (position == 1 || position == 3) ? PLAYER_AREA_HEIGHT : PLAYER_AREA_WIDTH;
        double height = (position == 1 || position == 3) ? PLAYER_AREA_WIDTH : PLAYER_AREA_HEIGHT;
        playerArea.setPrefSize(width, height);
        playerArea.setStyle("-fx-border-color: black; -fx-border-width: 0;");
        return playerArea;
    }

    // Method to position a player area
    public static void positionPlayerArea(Pane playerArea, int position) {
        double xPos = 0;
        double yPos = 0;
        switch (position) {
            case 0:
                xPos = (WINDOW_WIDTH - playerArea.getPrefWidth()) / 2;
                yPos = WINDOW_HEIGHT - playerArea.getPrefHeight();
                break;
            case 1:
                xPos = 0;
                yPos = (WINDOW_HEIGHT - playerArea.getPrefHeight()) / 2;
                break;
            case 2:
                xPos = (WINDOW_WIDTH - playerArea.getPrefWidth()) / 2;
                yPos = 0;
                break;
            case 3:
                xPos = WINDOW_WIDTH - playerArea.getPrefWidth();
                yPos = (WINDOW_HEIGHT - playerArea.getPrefHeight()) / 2;
                break;
        }
        playerArea.setLayoutX(xPos);
        playerArea.setLayoutY(yPos);
    }
    
    public static void setupPlayerAreas(List<Player> playerList, Pane root) {
        for (int i = 0; i < playerList.size(); i++) {
            int position = i;
            Pane playerArea = PlayAreaUtility.createPlayerArea(position);
            PlayAreaUtility.positionPlayerArea(playerArea, position);

            // Create Cards
            playerArea = CardViewUtility.createCardViewsOfPlayer(playerArea, playerList.get(i), playerList);
            playerArea.setId(i + "");
            // final String playerAreaId = playerArea.getId();
            // playerArea.setOnMouseEntered(event -> {
            //     // Print the node's ID or any other identifying detail
            //     System.out.println("Mouse entered: " + playerAreaId);
            // });

            root.getChildren().add(playerArea);
        }
    }

    public static Pane getPlayerArea(Pane root, int id){
        for (Node node : root.getChildren()) {
            if (node.getId() != null && node.getId().equals(id + "")) {
                Pane pane = (Pane) node;
                return pane;
            }
        }
        return new Pane();
    }

    public static void displayLeaderboard(Pane root, List<Player> playerList, Game game) {
        Pane leaderboardScreen = new Pane();
        leaderboardScreen.setPrefSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        leaderboardScreen.setStyle("-fx-background-color: green;");

        VBox leaderboardBox = new VBox(10);
        leaderboardBox.setAlignment(Pos.CENTER);
        
        // create a background for the leaderboard box
        leaderboardBox.setStyle("-fx-background-color: black; -fx-padding: 30; -fx-background-radius: 10;");
        
        Text title = new Text("Final Scores");
        title.setFill(Color.WHITE);
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");
        leaderboardBox.getChildren().add(title);

        // find lowest score player(s)
        int lowestScore = playerList.stream().mapToInt(p -> game.getPlayersPointsInCurrentGame().get(p)).min().orElse(Integer.MAX_VALUE);
        List<Player> winners = playerList.stream().filter(p -> game.getPlayersPointsInCurrentGame().get(p) == lowestScore).collect(Collectors.toList());
        
        // display the winner(s)
        Text winnersText = new Text("Winner" + (winners.size() > 1 ? "s: " : ": ") + "Player" + (winners.size() > 1 ? "s " : " ") + winners.stream().map(Player::getName).collect(Collectors.joining(", ")));
        winnersText.setFill(Color.WHITE);
        winnersText.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        leaderboardBox.getChildren().add(winnersText);

        // add text to show points
        for (Player player : playerList) {
            Text playerScore = new Text("Player " + player.getName() + ": " + game.getPlayersPointsInCurrentGame().get(player) + " points");
            playerScore.setFill(Color.WHITE);
            leaderboardBox.getChildren().add(playerScore);
        }

        leaderboardScreen.getChildren().add(leaderboardBox);

        // reposition
        leaderboardBox.layoutXProperty().bind(leaderboardScreen.widthProperty().subtract(leaderboardBox.widthProperty()).divide(2));
        leaderboardBox.layoutYProperty().bind(leaderboardScreen.heightProperty().subtract(leaderboardBox.heightProperty()).divide(2));

        Scene leaderboardScene = new Scene(leaderboardScreen, WINDOW_WIDTH, WINDOW_HEIGHT);
        Stage primaryStage = (Stage) root.getScene().getWindow();
        primaryStage.setScene(leaderboardScene);
    }

}