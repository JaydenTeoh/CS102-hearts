package com.example.utility;

import javafx.scene.control.Label;
import javafx.scene.layout.Pane;

public class RoundDisplayUtility {
    public static void initializeRoundDisplay(Pane root, Label roundLabel) {
        roundLabel.setLayoutX(10);
        roundLabel.setLayoutY(10);
        roundLabel.setStyle("-fx-text-fill: white; -fx-font-size: 16px;");

        root.getChildren().add(roundLabel);
    }

    public static void updateRoundDisplay(Pane root, Label roundLabel, int currentRound) {
        if (roundLabel != null) {
            roundLabel.setText("Round " + currentRound);
        } else {
            // If roundLabel is null, it means it hasn't been initialized yet
            initializeRoundDisplay(root, roundLabel);
            roundLabel.setText("Round " + currentRound);
        }
    }
}
