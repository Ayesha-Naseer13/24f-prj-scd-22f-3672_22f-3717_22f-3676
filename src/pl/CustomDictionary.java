package pl;

import bll.CustomDictionaryBO;
import bll.ICustomDictionaryBO;
import dto.WordTranslation;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

import java.io.File;

public class CustomDictionary {

    private final ICustomDictionaryBO dictionaryBO;

    public CustomDictionary() {
        this.dictionaryBO = new CustomDictionaryBO(); 
    }

    public VBox getCustomDictionaryPane() {
        // TableView for displaying story and translations
    	TableView<WordTranslation> translationTable = new TableView<>();
    	translationTable.setPrefHeight(300);

    	// Define table columns
    	TableColumn<WordTranslation, String> arabicColumn = new TableColumn<>("Arabic");
    	arabicColumn.setCellValueFactory(new PropertyValueFactory<>("arabic"));
    	arabicColumn.setResizable(true); // Allow resizing
    	arabicColumn.setPrefWidth(100);  // Initial width, will resize dynamically

    	TableColumn<WordTranslation, String> urduColumn = new TableColumn<>("Urdu");
    	urduColumn.setCellValueFactory(new PropertyValueFactory<>("urdu"));
    	urduColumn.setResizable(true); 
    	urduColumn.setPrefWidth(100);  

    	TableColumn<WordTranslation, String> persianColumn = new TableColumn<>("Persian");
    	persianColumn.setCellValueFactory(new PropertyValueFactory<>("persian"));
    	persianColumn.setResizable(true); 
    	persianColumn.setPrefWidth(100);  

    	translationTable.getColumns().addAll(arabicColumn, urduColumn, persianColumn);

    	translationTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        ObservableList<WordTranslation> translations = FXCollections.observableArrayList();
        translationTable.setItems(translations);

        TextArea arabicStoryInput = new TextArea();
        arabicStoryInput.setPromptText("Enter Arabic story...");
        arabicStoryInput.setWrapText(true);

        Button importButton = new Button("Import Story");
        importButton.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Open Story File");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));

            File selectedFile = fileChooser.showOpenDialog(null);
            if (selectedFile != null) {
                String storyText = dictionaryBO.importStoryFromFile(selectedFile);
                if (storyText.startsWith("Error")) {
                    showAlert("Import Error", storyText);
                } else {
                    arabicStoryInput.setText(storyText);
                }
            }
        });
        // Translate button
        Button translateButton = new Button("Translate Story");
        translateButton.setOnAction(e -> {
            String storyText = arabicStoryInput.getText().trim();
            if (storyText.isEmpty()) {
                showAlert("Input Error", "Story text cannot be empty!");
                return;
            }

            try {
                translations.clear();
                ObservableList<WordTranslation> fetchedTranslations = dictionaryBO.getTranslationsForStory(storyText);

                if (fetchedTranslations == null || fetchedTranslations.isEmpty()) {
                    showAlert("No Translations", "No translations found for the provided story.");
                } else {
                    translations.addAll(fetchedTranslations);
                }
            } catch (Exception ex) {
                showAlert("Error", "An error occurred while processing the story: " + ex.getMessage());
            }
        });

       

        // Layout setup
        VBox layout = new VBox(10, arabicStoryInput , importButton, translateButton, translationTable);
        layout.setPadding(new Insets(10));
        return layout;
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
