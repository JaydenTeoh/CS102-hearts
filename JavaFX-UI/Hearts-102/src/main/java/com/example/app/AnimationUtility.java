package com.example.app;

import javafx.animation.*;
import javafx.scene.image.*;
import javafx.util.Duration;
import javafx.scene.layout.Pane;

import java.io.*;

public class AnimationUtility {
    public static void animateTrickToPlayerArea(int position, Pane root) {
        try {
        String currentDirectory = System.getProperty("user.dir");
            File file = new File(currentDirectory + "/images/" + "/fourCards.png");

            // Image of the 4 cards flipped
            Image image = new Image(new FileInputStream(file));
            ImageView imageView = new ImageView(image);
            imageView.setPreserveRatio(true);
            imageView.setFitWidth(50);
            imageView.setFitHeight(50);
            imageView.setLayoutX((root.getPrefWidth() - imageView.getFitWidth()) / 2);
            imageView.setLayoutY((root.getPrefHeight() - imageView.getFitHeight()) / 2);
            root.getChildren().add(imageView);

            // Animation to scale up and then scale down
            ScaleTransition scaleUp = new ScaleTransition(Duration.seconds(1), imageView);
            scaleUp.setToX(2); // Double the width
            scaleUp.setToY(2); // Double the height
            ScaleTransition scaleDown = new ScaleTransition(Duration.seconds(1), imageView);
            scaleDown.setToX(1); // Original width
            scaleDown.setToY(1); // Original height

            // Animation to move to each player's area
            TranslateTransition transition = new TranslateTransition(Duration.seconds(0.5), imageView);
            switch (position) {
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
}
