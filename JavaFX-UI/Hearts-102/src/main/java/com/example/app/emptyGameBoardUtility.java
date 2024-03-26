package com.example.app;

import javafx.scene.layout.Pane;

public class emptyGameBoardUtility {
    private static final double WINDOW_WIDTH = 1500;
    private static final double WINDOW_HEIGHT = 800;
    private static final int PLAYER_AREA_WIDTH = 600;
    private static final int PLAYER_AREA_HEIGHT = 250;
    private static final int PLAY_AREA_WIDTH = 400;
    private static final int PLAY_AREA_HEIGHT = 200;

    // Method to create a player area
    private Pane createPlayerArea(int position) {
        Pane playerArea = new Pane();
        double width = (position == 2 || position == 4) ? PLAYER_AREA_HEIGHT : PLAYER_AREA_WIDTH;
        double height = (position == 2 || position == 4) ? PLAYER_AREA_WIDTH : PLAYER_AREA_HEIGHT;
        playerArea.setPrefSize(width, height);
        playerArea.setStyle("-fx-border-color: black; -fx-border-width: 2;");
        return playerArea;
    }

    // Method to position a player area
    private void positionPlayerArea(Pane playerArea, int position) {
        double xPos = 0;
        double yPos = 0;
        switch (position) {
            case 0:
                xPos = (WINDOW_WIDTH - playerArea.getPrefWidth()) / 2;
                yPos = WINDOW_HEIGHT - playerArea.getPrefHeight();
                break;
            case 1:
                xPos = (WINDOW_WIDTH - playerArea.getPrefWidth()) / 9;
                yPos = (WINDOW_HEIGHT - playerArea.getPrefHeight()) / 2;
                break;
            case 2:
                xPos = (WINDOW_WIDTH - playerArea.getPrefWidth()) / 2;
                yPos = (WINDOW_HEIGHT - playerArea.getPrefHeight()) / 9;
                break;
            case 3:
                xPos = WINDOW_WIDTH - playerArea.getPrefWidth();
                yPos = (WINDOW_HEIGHT - playerArea.getPrefHeight()) / 2;
                break;
        }
        playerArea.setLayoutX(xPos);
        playerArea.setLayoutY(yPos);
    }

    // Method to setup player areas
    private void setupPlayerAreas(Pane gameBoard) {
        for (int i = 0; i < 4; i++) {
            int position = i;
            Pane playerArea = createPlayerArea(position);
            positionPlayerArea(playerArea, position);
            gameBoard.getChildren().add(playerArea);
        }
    }

    private void setupPlayArea(Pane gameBoard){
        Pane playArea = new Pane();
        playArea.setPrefSize(PLAY_AREA_WIDTH, PLAY_AREA_HEIGHT);
        playArea.setStyle("-fx-background-color: beige; -fx-border-color: black;");
        playArea.setLayoutX((WINDOW_WIDTH - PLAY_AREA_WIDTH) / 2);
        playArea.setLayoutY((WINDOW_HEIGHT - PLAY_AREA_HEIGHT) / 2);
        gameBoard.getChildren().add(playArea);
    }

    public Pane setUpGameBoard() {
        Pane gameBoard = new Pane();
        gameBoard.setPrefSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        gameBoard.setStyle("-fx-background-color: green;");
        setupPlayerAreas(gameBoard);
        return gameBoard;
    }
}
