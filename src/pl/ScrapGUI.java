package pl;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import bll.ScrapBO;

public class ScrapGUI extends Application {

    private ScrapBO dictionaryService;
    private TextField urlField;
    private Stage stage;

    public ScrapGUI() {
        dictionaryService = new ScrapBO();
    }

    @Override
    public void start(Stage primaryStage) {
        stage = primaryStage;
        createGUI();
    }

    private void createGUI() {
        stage.setTitle("Dictionary Scraper");

        // GridPane layout
        GridPane grid = new GridPane();
        grid.setVgap(10);
        grid.setHgap(10);
        grid.setPadding(new Insets(20));

        // Title Label
        Label titleLabel = new Label("Dictionary Scraper");
        titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        grid.add(titleLabel, 0, 0, 2, 1);

        // URL input
        Label urlLabel = new Label("Enter URL to scrape:");
        urlLabel.setStyle("-fx-font-size: 14px;");
        grid.add(urlLabel, 0, 1);

        urlField = new TextField();
        grid.add(urlField, 1, 1);

        // Scrape Button
        Button scrapeUrlButton = new Button("Scrape from URL");
        scrapeUrlButton.setStyle("-fx-background-color: #007bff; -fx-text-fill: white;");
        grid.add(scrapeUrlButton, 1, 2);

        // Action for scraping from URL
        scrapeUrlButton.setOnAction(e -> {
            String url = urlField.getText();
            if (!url.isEmpty()) {
                String resultMessage = dictionaryService.addWordFromUrl(url);
                showMessage(resultMessage);
            } else {
                showMessage("Please enter a valid URL.");
            }
        });

        // Set the scene
        Scene scene = new Scene(grid, 500, 300);
        stage.setScene(scene);
        stage.show();
    }

    private void showMessage(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Scrape Result");
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
