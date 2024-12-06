package pl;

import bll.WordServiceBLO;
import dto.WordDTO;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class DictionaryAppGUI extends Application {

    private TextField wordField;
    private TextField persianField;
    private TextField urduField;
    private Label statusLabel;
    private WordServiceBLO wordService = new WordServiceBLO();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        wordService = new WordServiceBLO();
        primaryStage.setTitle("Dictionary App");
        primaryStage.show();
    }

    public Pane getAddWordPane() {
        return createAddWordPane();
    }

    public Pane getUpdateWordPane() {
        return createUpdateWordPane();
    }

    public Pane getDeleteWordPane() {
        return createDeleteWordPane();
    }

    // Add Word Pane
    private Pane createAddWordPane() {
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setVgap(10);
        grid.setHgap(10);

        wordField = new TextField();
        persianField = new TextField();
        urduField = new TextField();
        statusLabel = new Label("Enter word, Persian meaning, and Urdu meaning");

        Button submitButton = new Button("Submit");
        submitButton.setOnAction(e -> {
            String word = wordField.getText();
            String persianTranslation = persianField.getText();
            String urduTranslation = urduField.getText();

            // Create WordDTO instance with the provided values
            WordDTO newWord = new WordDTO(word, persianTranslation, urduTranslation);

            // Call addWord method from WordServiceBLO to save the word
            if (wordService.addWord(newWord)) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Word added successfully.");
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to add word.");
            }
            clearFields();
        });

        grid.add(statusLabel, 0, 0, 2, 1);
        grid.add(new Label("Word:"), 0, 1);
        grid.add(wordField, 1, 1);
        grid.add(new Label("Persian Meaning:"), 0, 2);
        grid.add(persianField, 1, 2);
        grid.add(new Label("Urdu Meaning:"), 0, 3);
        grid.add(urduField, 1, 3);
        grid.add(submitButton, 0, 4);

        return grid;
    }

    // Update Word Pane
    private Pane createUpdateWordPane() {
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setVgap(10);

        wordField = new TextField();
        statusLabel = new Label("Enter word to update meanings");

        Button submitButton = new Button("Submit");
        submitButton.setOnAction(e -> {
            String word = wordField.getText();
            WordDTO updatedWord = new WordDTO(word, null, null);

            ChoiceDialog<String> choiceDialog = new ChoiceDialog<>("Both", "Persian Meaning", "Urdu Meaning", "Both");
            choiceDialog.setTitle("Update Meaning");
            choiceDialog.setHeaderText("Select meaning to update:");
            choiceDialog.setContentText("Choose:");
            choiceDialog.showAndWait().ifPresent(choice -> {
                if ("Persian Meaning".equals(choice)) {
                    String persianTranslation = showInputDialog("Enter new Persian meaning:");
                    if (persianTranslation != null)
                        updatedWord.setPersianTranslation(persianTranslation);
                } else if ("Urdu Meaning".equals(choice)) {
                    String urduTranslation = showInputDialog("Enter new Urdu meaning:");
                    if (urduTranslation != null)
                        updatedWord.setUrduTranslation(urduTranslation);
                } else if ("Both".equals(choice)) {
                    String persianTranslation = showInputDialog("Enter new Persian meaning:");
                    if (persianTranslation != null)
                        updatedWord.setPersianTranslation(persianTranslation);
                    String urduTranslation = showInputDialog("Enter new Urdu meaning:");
                    if (urduTranslation != null)
                        updatedWord.setUrduTranslation(urduTranslation);
                }

                // Proceed if the word has updates
                if (updatedWord.getPersianTranslation() != null || updatedWord.getUrduTranslation() != null) {
                    if (wordService.updateWord(updatedWord)) {
                        showAlert(Alert.AlertType.INFORMATION, "Success", "Word updated successfully.");
                    } else {
                        showAlert(Alert.AlertType.ERROR, "Error", "Failed to update word.");
                    }
                } else {
                    showAlert(Alert.AlertType.ERROR, "Error", "No translation provided to update.");
                }
                clearFields();
            });
        });

        grid.add(statusLabel, 0, 0, 2, 1);
        grid.add(new Label("Word:"), 0, 1);
        grid.add(wordField, 1, 1);
        grid.add(submitButton, 0, 2);

        return grid;
    }

    // Delete Word Pane
    private Pane createDeleteWordPane() {
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setVgap(10);

        wordField = new TextField();
        statusLabel = new Label("Enter word to delete");

        Button submitButton = new Button("Submit");
        submitButton.setOnAction(e -> {
            String word = wordField.getText();
            if (wordService.deleteWord(word)) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Word deleted successfully.");
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to delete word.");
            }
            clearFields();
        });

        grid.add(statusLabel, 0, 0, 2, 1);
        grid.add(new Label("Word:"), 0, 1);
        grid.add(wordField, 1, 1);
        grid.add(submitButton, 0, 2);

        return grid;
    }

    private void clearFields() {
        wordField.clear();
        if (persianField != null)
            persianField.clear();
        if (urduField != null)
            urduField.clear();
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private String showInputDialog(String prompt) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setHeaderText(prompt);
        return dialog.showAndWait().orElse(null);
    }

    @Override
    public void stop() {
        wordService.closeService();
    }
}
