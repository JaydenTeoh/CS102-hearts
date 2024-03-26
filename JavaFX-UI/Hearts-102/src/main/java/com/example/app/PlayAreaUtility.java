package com.example.app;

import java.util.List;

import com.example.gameplay.Round;
import com.example.players.Player;

import javafx.scene.layout.Pane;

public class PlayAreaUtility {
    private static final double WINDOW_WIDTH = 1500;
    private static final double WINDOW_HEIGHT = 800;
    private static final int PLAYER_AREA_WIDTH = 600;
    private static final int PLAYER_AREA_HEIGHT = 250;
    private static final int PLAY_AREA_WIDTH = 400;
    private static final int PLAY_AREA_HEIGHT = 200;

    public static Pane createPlayArea() {
        Pane playArea = new Pane();
        playArea.setPrefSize(PLAY_AREA_WIDTH, PLAY_AREA_HEIGHT);
        playArea.setStyle("-fx-background-color: beige; -fx-border-color: black;");
        return playArea;
    }

    // Method to create a player area
    public static Pane createPlayerArea(int position) {
        Pane playerArea = new Pane();
        double width = (position == 1 || position == 3) ? PLAYER_AREA_HEIGHT : PLAYER_AREA_WIDTH;
        double height = (position == 1 || position == 3) ? PLAYER_AREA_WIDTH : PLAYER_AREA_HEIGHT;
        playerArea.setPrefSize(width, height);
        playerArea.setStyle("-fx-border-color: black; -fx-border-width: 2;");
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

    public static void setupPlayArea(Pane gameBoard){
        Pane playArea = new Pane();
        playArea.setPrefSize(PLAY_AREA_WIDTH, PLAY_AREA_HEIGHT);
        playArea.setStyle("-fx-background-color: beige; -fx-border-color: black;");
        playArea.setLayoutX((WINDOW_WIDTH - PLAY_AREA_WIDTH) / 2);
        playArea.setLayoutY((WINDOW_HEIGHT - PLAY_AREA_HEIGHT) / 2);
        gameBoard.getChildren().add(playArea);
    }

    public static void setupPlayerAreas(List<Player> playerList, Round round, Pane root) {
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
}