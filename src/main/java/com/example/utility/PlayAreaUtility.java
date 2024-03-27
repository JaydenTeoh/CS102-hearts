package com.example.utility;

import java.util.List;
import com.example.players.Player;

import javafx.scene.Node;
import javafx.scene.layout.Pane;

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

            // create card views
            playerArea = CardViewUtility.createCardViewsOfPlayer(playerArea, playerList.get(i), playerList);
            playerArea.setId(i + "");

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
}