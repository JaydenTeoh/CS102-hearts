package com.example.hearts102;

import javafx.animation.TranslateTransition;
import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class MainApplication extends Application {

    private static final double WINDOW_WIDTH = 1500;
    private static final double WINDOW_HEIGHT = 800;
    private static final int CARD_WIDTH = 100;
    private static final int CARD_HEIGHT = 140;

    private static final int PLAYER_AREA_WIDTH = 600;
    private static final int PLAYER_AREA_HEIGHT = 250;

    private static final int PLAY_AREA_WIDTH = 400;
    private static final int PLAY_AREA_HEIGHT = 200;
    private static final int SPACING = -65;

    private Pane root = new Pane();
    private Parent createContent() {

        root.setPrefSize(WINDOW_WIDTH, WINDOW_HEIGHT);

        Region background = new Region();
        background.setPrefSize(1500, 800);
        background.setStyle("-fx-background-color: green");

        // LEFT
        VBox leftVBox = new VBox(50);
        leftVBox.setAlignment(Pos.TOP_CENTER);

        Pane playArea = createPlayArea();
        playArea.setLayoutX((root.getPrefWidth() - playArea.getPrefWidth()) / 2);
        playArea.setLayoutY((root.getPrefHeight() - playArea.getPrefHeight()) / 2);

        root.getChildren().add(background);
        root.getChildren().add(playArea);

        // ADD BOTH STACKS TO ROOT LAYOUT
        setupPlayerAreas();

        Pane playerArea = new Pane();
        playerArea.setPrefSize(600, 150);
        playerArea.setLayoutX((WINDOW_WIDTH - playerArea.getPrefWidth()) / 2);
        playerArea.setLayoutY(WINDOW_HEIGHT - playerArea.getPrefHeight());

        // Call the method to create and position card placeholders
        createCardPlaceholders(playerArea, playArea);
        root.getChildren().add(playerArea);

        return root;
    }
    private void setupPlayerAreas() {
        String[] positions = {"TOP", "RIGHT", "BOTTOM", "LEFT"};
        for (String position : positions) {
            Pane playerArea = createPlayerArea(position);
            positionPlayerArea(playerArea, position);
            root.getChildren().add(playerArea);
        }
    }


    private void positionPlayerArea(Pane playerArea, String position) {
        switch (position) {
            case "TOP":
                playerArea.setLayoutX((WINDOW_WIDTH - playerArea.getPrefWidth()) / 2);
                playerArea.setLayoutY(0);
                break;
            case "RIGHT":
                playerArea.setLayoutX(WINDOW_WIDTH - playerArea.getPrefWidth());
                playerArea.setLayoutY((WINDOW_HEIGHT - playerArea.getPrefHeight()) / 2);
                break;
            case "BOTTOM":
                playerArea.setLayoutX((WINDOW_WIDTH - playerArea.getPrefWidth()) / 2);
                playerArea.setLayoutY(WINDOW_HEIGHT - playerArea.getPrefHeight());
                break;
            case "LEFT":
                playerArea.setLayoutX(0);
                playerArea.setLayoutY((WINDOW_HEIGHT - playerArea.getPrefHeight()) / 2);
                break;
        }
    }

    private Pane createPlayerArea(String position) {
        Pane playerArea = new Pane();

        if (position.equals("TOP") || position.equals("BOTTOM")) {
            playerArea.setPrefSize(PLAYER_AREA_WIDTH, PLAYER_AREA_HEIGHT);
        } else {
            playerArea.setPrefSize(PLAYER_AREA_HEIGHT, PLAYER_AREA_WIDTH);
        }
        playerArea.setStyle("-fx-border-color: black; -fx-border-width: 2;");
        return playerArea;
    }

    private Pane createPlayArea() {
        Pane playArea = new Pane();
        playArea.setPrefSize(PLAY_AREA_WIDTH, PLAY_AREA_HEIGHT);
        playArea.setStyle("-fx-background-color: beige; -fx-border-color: black;");
        return playArea;
    }

    private void createCardPlaceholders(Pane playerArea, Pane playArea) {
        try {
            String currentDirectory = System.getProperty("user.dir");
            File file = new File(currentDirectory + "/images/2c.gif");
            Image cardImage = new Image(new FileInputStream(file));

            for (int i = 0; i < 13; i++) {
                ImageView cardView = new ImageView(cardImage);
                cardView.setFitWidth(CARD_WIDTH);
                cardView.setFitHeight(CARD_HEIGHT);

                double xPos = i * (CARD_WIDTH + SPACING);

                cardView.setLayoutX(xPos);
                cardView.setLayoutY((playerArea.getPrefHeight() - CARD_HEIGHT*1.5));

                cardView.getStyleClass().add("card");

                cardView.setId(i+"");

                cardView.setOnMouseClicked(event -> {
                    System.out.println(cardView.getId());

                    cardView.getStyleClass().remove("card");

                    TranslateTransition transition = new TranslateTransition(Duration.seconds(0.5), cardView);
                    transition.setToY(-(PLAYER_AREA_HEIGHT) + 50);
                    transition.setToX(((PLAYER_AREA_WIDTH / 2) - xPos) - CARD_WIDTH / 2);
                    transition.setCycleCount(1);
                    transition.play();
                });

                playerArea.getChildren().add(cardView);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
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

    public static void main(String[] args) {
        launch();
    }
}