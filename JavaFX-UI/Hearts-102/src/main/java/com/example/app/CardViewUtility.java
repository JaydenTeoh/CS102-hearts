package com.example.app;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import com.example.gameplay.Round;
import com.example.players.HumanPlayer;
import com.example.players.Player;
import com.example.pokercards.Card;

import javafx.animation.TranslateTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

public class CardViewUtility {
    public static ObservableList<Node> getCardViewsOfPlayer(Pane root, int id) {
        for (Node node : root.getChildren()) {
            if (node.getId() != null && node.getId().equals(id + "")) {
                Pane pane = (Pane) node;
                return pane.getChildren();
            }   
        }
        // Empty List
        return FXCollections.observableArrayList();
    }


    public static void disableCards(Pane root) {
        ObservableList<Node> cards = getCardViewsOfPlayer(root, 0);

        for (Node node : cards) {
            if (node instanceof ImageView card) {
                disableHover(card);
                setGrayscale(card);
                card.setOnMouseClicked(null);
                card.setOnMouseEntered(null);
                card.setOnMouseExited(null);
                card.getStyleClass().remove("card-active");

                
            }
        }
    }

    public static void addHoverEffect(ImageView cardView) {
        cardView.setOnMouseEntered(event -> {enableHover(cardView);});

        cardView.setOnMouseExited(event -> {disableHover(cardView);});
    }

    public static void enableHover(ImageView cardView) {
        cardView.setEffect(new DropShadow()); // Apply drop shadow effect when mouse enters
        cardView.setCursor(Cursor.HAND); // Change cursor to hand

        // Translate animation to move the card up
        TranslateTransition hoverTransition = new TranslateTransition(Duration.seconds(0.2), cardView);
        hoverTransition.setToY(-20); // Adjust this value to change the hover distance
        hoverTransition.play();
    }

    public static void disableHover(ImageView cardView) {
        cardView.setEffect(null); // Remove drop shadow effect when mouse exits
        cardView.setCursor(Cursor.DEFAULT); // Change cursor back to default

        // Translate animation to move the card back down
        TranslateTransition hoverTransition = new TranslateTransition(Duration.seconds(0.2), cardView);
        hoverTransition.setToY(0);
        hoverTransition.play();
    }

    public static void setGrayscale(ImageView cardView) {
        ColorAdjust grayscale = new ColorAdjust();
        grayscale.setBrightness(-0.5); // Lower brightness by 50%
        grayscale.setContrast(-0.5); // Lower contrast by 50%
        cardView.setEffect(grayscale);
    }
}
