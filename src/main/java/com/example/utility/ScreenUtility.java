package com.example.utility;

import java.util.List;
import java.util.stream.Collectors;

import com.example.gameplay.Game;
import com.example.players.Player;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Popup;
import javafx.stage.Stage;

public class ScreenUtility {
    
    public static void showHowToPlayPopup(Pane root) {
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

    public static void updateBackgroundImage(String imageUrl, Region background) {
        BackgroundImage backgroundImage = new BackgroundImage(
                new Image(imageUrl, PlayAreaUtility.WINDOW_WIDTH, PlayAreaUtility.WINDOW_HEIGHT, false, true),
                BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.DEFAULT, BackgroundSize.DEFAULT);
        background.setBackground(new Background(backgroundImage));
    }


    public static void displayLeaderboard(Pane root, List<Player> playerList, Game game) {
        Pane leaderboardScreen = new Pane();
        leaderboardScreen.setPrefSize(PlayAreaUtility.WINDOW_WIDTH, PlayAreaUtility.WINDOW_HEIGHT);
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

        Scene leaderboardScene = new Scene(leaderboardScreen, PlayAreaUtility.WINDOW_WIDTH, PlayAreaUtility.WINDOW_HEIGHT);
        Stage primaryStage = (Stage) root.getScene().getWindow();
        primaryStage.setScene(leaderboardScene);
    }
}
