package pl;

import bll.SegmentBO;
import dal.SegmentDAO;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.util.Map;

public class SegmentGUI extends Application {
    private TextField inputField;
    private TextArea resultArea;
    private final SegmentBO segmentBO;

    // Explicit main method to run the application
    public static void main(String[] args) {
        launch(args);
    }

    public SegmentGUI() {
        SegmentDAO segmentDAO = new SegmentDAO();
        this.segmentBO = new SegmentBO(segmentDAO);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Arabic Word Segmentation");

        // Input panel
        HBox inputPanel = new HBox(10);
        inputPanel.setPadding(new Insets(10));
        Label label = new Label("Enter Compound Word:");
        inputField = new TextField();
        inputField.setPrefWidth(300);
        Button fetchButton = new Button("Fetch Meanings");
        inputPanel.getChildren().addAll(label, inputField, fetchButton);

        // Result area
        resultArea = new TextArea();
        resultArea.setEditable(false);
        resultArea.setPrefHeight(300);

        // Layout
        BorderPane root = new BorderPane();
        root.setTop(inputPanel);
        root.setCenter(resultArea);

        // Button action
        fetchButton.setOnAction(event -> {
            String compoundWord = inputField.getText().trim();
            if (!compoundWord.isEmpty()) {
                displayMeanings(compoundWord);
            } else {
                showAlert("Validation Error", "Please enter a compound word.");
            }
        });

        // Set the scene
        Scene scene = new Scene(root, 600, 400);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void displayMeanings(String compoundWord) {
        try {
            Map<String, Map<String, String>> meanings = segmentBO.segmentAndFetchMeaning(compoundWord);
            StringBuilder result = new StringBuilder();

            for (Map.Entry<String, Map<String, String>> entry : meanings.entrySet()) {
                result.append("Word: ").append(entry.getKey()).append("\n");
                Map<String, String> wordMeanings = entry.getValue();
                result.append("  Persian: ").append(wordMeanings.getOrDefault("Persian", "N/A")).append("\n");
                result.append("  Urdu: ").append(wordMeanings.getOrDefault("Urdu", "N/A")).append("\n\n");
            }

            resultArea.setText(result.toString());
        } catch (Exception ex) {
            ex.printStackTrace();
            showAlert("Error", "An error occurred: " + ex.getMessage());
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
