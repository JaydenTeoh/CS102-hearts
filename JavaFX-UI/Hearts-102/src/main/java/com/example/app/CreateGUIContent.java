package com.example.app;

import javafx.scene.Parent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.control.Label;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.paint.Color;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;
import javafx.animation.RotateTransition;
import javafx.animation.Animation;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundSize;
import javafx.geometry.Insets;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;
import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class CreateGUIContent {
    public Label createHeartsLabel(double WINDOW_WIDTH) {
        // Create a label for "Hearts"
        Label heartsLabel = new Label("\uD83D\uDC9D  Hearts  \uD83D\uDC9E");
        heartsLabel.setFont(Font.font("Impact", FontWeight.BOLD, 72)); // Set font size and style
        heartsLabel.setTextFill(Color.WHITE); // Set text color

        // Center horizontally
        heartsLabel.setLayoutX((WINDOW_WIDTH / 2 - heartsLabel.prefWidth(-1)) - 200);

        // Set vertical position
        heartsLabel.setLayoutY(300);

        return heartsLabel;
    }

    public ChoiceBox<String> createModeChoiceBox(double WINDOW_WIDTH, double WINDOW_HEIGHT, String currentDirectory, Region background) {
        ChoiceBox<String> modeChoiceBox = new ChoiceBox<>();
        modeChoiceBox.getItems().addAll("Classic", "Casino", "School", "Holiday");
        modeChoiceBox.setValue("Classic"); // Default mode
        modeChoiceBox.setPrefSize(200, 50);
        modeChoiceBox.setLayoutX(WINDOW_WIDTH / 2 - 100);
        modeChoiceBox.setLayoutY(WINDOW_HEIGHT / 2 + 100); // Shifted downwards by 200 units

        // Apply font size and alignment styles
        String choiceBoxStyle = "-fx-font-size: 20px; -fx-alignment: center;";
        modeChoiceBox.setStyle(choiceBoxStyle);

        modeChoiceBox.setOnShown(event -> {
            // Apply CSS styling to adjust the width and center alignment of dropdown items
            modeChoiceBox.lookup(".choice-box").setStyle("-fx-pref-width: 200; -fx-alignment: center;");
        });

        // Listener for mode selection change
        modeChoiceBox.getSelectionModel().selectedItemProperty().addListener((obs, oldMode, newMode) -> {
            String updatedImageUrl;
            switch (newMode) {
                case "Casino":
                    updatedImageUrl = "file:" + currentDirectory + "/background1.jpg";
                    break;
                case "School":
                    updatedImageUrl = "file:" + currentDirectory + "/background3.jpeg";
                    break;
                case "Holiday":
                    updatedImageUrl = "file:" + currentDirectory + "/background4.jpeg";
                    break;
                default:
                    updatedImageUrl = ""; // Default or error case
                    // Set background color for Classic mode
                    background.setStyle("-fx-background-color: green");
                    break;
            }
            updateBackgroundImage(updatedImageUrl, background);
        });

        return modeChoiceBox;
    }

    public void updateBackgroundImage(String imageUrl, Region background) {
        BackgroundImage backgroundImage = new BackgroundImage(
                new Image(imageUrl, 1500, 800, false, true),
                BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.DEFAULT, BackgroundSize.DEFAULT);
        background.setBackground(new Background(backgroundImage));
    }


    public void rotateCard(ImageView card, boolean clockwise, double duration) {
        RotateTransition rotateTransition = new RotateTransition(Duration.seconds(duration), card);
        rotateTransition.setByAngle(clockwise ? 360 : -360); // Rotate clockwise or counterclockwise
        rotateTransition.setCycleCount(Animation.INDEFINITE);
        rotateTransition.setAutoReverse(true);
        rotateTransition.play();
    }

    public ImageView createCardImage(String imagePath, double width, double height) {
        Image image = new Image(imagePath);
        ImageView imageView = new ImageView(image);
        imageView.setPreserveRatio(true);
        imageView.setFitWidth(width); // Set the width
        imageView.setFitHeight(height); // Set the height
        // Set the pivot point for rotation to the center of the card
        imageView.setTranslateX(width / 2);
        imageView.setTranslateY(height / 2);
        return imageView;
    }

    public Button createHowToPlayButton(double WINDOW_WIDTH, double WINDOW_HEIGHT, Pane root) {
        Button howToPlayButton = new Button("How To Play");
        howToPlayButton.setPrefSize(200, 50);
        howToPlayButton.setLayoutX(WINDOW_WIDTH / 2 - 100);
        howToPlayButton.setLayoutY(WINDOW_HEIGHT / 2 + 175); // Shifted downwards by 200 units
        howToPlayButton.setStyle("-fx-font-size: 20px;");
        // Set action for the How to Play button
        howToPlayButton.setOnAction(event -> {
            showHowToPlayPopup(root);
        });
        return howToPlayButton;
    }

    private void showHowToPlayPopup(Pane root) {
        // Create a new popup
        Popup popup = new Popup();

        // Creating labels for the title
        Label title = new Label("How to Play Hearts");
        title.setStyle("-fx-font-size: 32px; -fx-font-weight: bold; -fx-text-fill: #333;");

        // Rules
        String rulesText = "Rules:\n" +
                "- Ace is high, 2 is low, there is no trump suit\n\n" +
                "- Players pass 3 cards from their hand to an opponent\n" +
                "  (tip: pass high cards, which will often win tricks)\n\n" +
                "- Player with the 2 of clubs starts the hand;\n" +
                "  other players must follow the suit being played;\n" +
                "  the highest card in the leading suit wins\n\n" +
                "- Player who won the last trick starts the next hand;\n " +
                "  continue until out of cards";

        // Create labels for the rules
        Label rulesLabel = new Label(rulesText);
        rulesLabel.setStyle("-fx-font-size: 20px; -fx-text-fill: #555;");

        // Information from the second image about goal and scoring
        String goalAndScoringText =
                "Goal: Player with the lowest score wins\n\n" +
                        "Tips: Avoid winning tricks with hearts / Q of spades\n\n" +
                        "Scoring: Hearts are 1 pts each, Q of spades is 13 pts";

        // Create a label for the goal and scoring
        Label goalAndScoringLabel = new Label(goalAndScoringText);
        goalAndScoringLabel.setStyle("-fx-font-size: 20px; -fx-text-fill: #555;");

        // Adding a little styling to our VBox
        VBox content = new VBox(10); // spacing between children is 10
        content.setPadding(new Insets(30));
        content.setStyle("-fx-background-color: #FFF59D; -fx-border-color: #FFA000; -fx-border-width: 2px;");
        content.setPrefSize(520, 650);

        // Add the title, rules, and goal and scoring to the VBox
        content.getChildren().addAll(title, rulesLabel, goalAndScoringLabel);

        // Set the content and show the popup
        popup.getContent().add(content);
        popup.setAutoHide(true); // Close the popup when clicking outside of it
        popup.show(root.getScene().getWindow(), 950, 150); //location
    }
}
