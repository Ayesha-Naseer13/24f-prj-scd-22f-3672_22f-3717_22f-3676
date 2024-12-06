package pl;

import java.util.List;

import bll.ViewBO;
import dto.Translation;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class ViewUI {
	private ViewBO viewBO;
    private FavouriteWordsUI favouriteWordsUI;
    private TableView<String> wordsTable;
    private BorderPane viewPane;

    public ViewUI() {
        this.viewBO = new ViewBO();
        this.favouriteWordsUI = new FavouriteWordsUI(); // Initialize the FavouriteWordsUI
        initializeView();
    }

    private void initializeView() {
        viewPane = new BorderPane();
        viewPane.setPadding(new Insets(10));

        // Buttons
        Button viewAllButton = new Button("View All Words");
        viewAllButton.setOnAction(e -> viewAllWords());

        Button viewDetailsButton = new Button("View Word Details");
        viewDetailsButton.setDisable(true);
        viewDetailsButton.setOnAction(e -> {
            String selectedWord = wordsTable.getSelectionModel().getSelectedItem();
            if (selectedWord != null) {
                viewWordDetails(selectedWord);
            }
        });

        Button addToFavouritesButton = new Button("Add to Favourites");
        addToFavouritesButton.setDisable(true);
        addToFavouritesButton.setOnAction(e -> {
            String selectedWord = wordsTable.getSelectionModel().getSelectedItem();
            if (selectedWord != null) {
                favouriteWordsUI.addToFavourites(selectedWord);
            }
        });

        Button viewFavouritesButton = new Button("View Favourite Words");
        viewFavouritesButton.setOnAction(e -> favouriteWordsUI.showFavouritesScreen());

        HBox topPanel = new HBox(viewAllButton, viewDetailsButton, addToFavouritesButton, viewFavouritesButton);
        topPanel.setSpacing(10);
        topPanel.setPadding(new Insets(10));

        // TableView to display words
        wordsTable = new TableView<>();
        TableColumn<String, String> wordColumn = new TableColumn<>("Word");
        wordColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue()));
        wordColumn.setMinWidth(200);
        wordsTable.getColumns().add(wordColumn);

        wordsTable.setPlaceholder(new Label("No words to display."));
        wordsTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            boolean isWordSelected = newValue != null;
            viewDetailsButton.setDisable(!isWordSelected);
            addToFavouritesButton.setDisable(!isWordSelected);
        });

        viewPane.setTop(topPanel);
        viewPane.setCenter(wordsTable);
    }

    public BorderPane getViewAllWordsPane() {
        return viewPane;
    }

    private void viewAllWords() {
        List<String> words = viewBO.viewAllWords();
        wordsTable.getItems().clear();
        wordsTable.getItems().addAll(words);
    }

    private void viewWordDetails(String word) {
        List<Translation> translations = viewBO.viewWordDetails(word);

        if (translations.isEmpty()) {
            showAlert(Alert.AlertType.INFORMATION, "Word Details", "No details found for the word: " + word);
        } else {
            Translation firstTranslation = translations.get(0);
            String urduMeaning = firstTranslation.getUrduMeaning();
            String persianMeaning = firstTranslation.getPersianMeaning();

            TableView<Translation> detailsTable = new TableView<>();
            TableColumn<Translation, String> stemColumn = new TableColumn<>("Stem Word");
            stemColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getStemWord()));

            TableColumn<Translation, String> posColumn = new TableColumn<>("Part of Speech");
            posColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getPartOfSpeech()));

            TableColumn<Translation, String> rootColumn = new TableColumn<>("Root Word");
            rootColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getRootWord()));

            detailsTable.getColumns().addAll(stemColumn, posColumn, rootColumn);
            detailsTable.getItems().addAll(translations);

            TextArea detailsArea = new TextArea();
            detailsArea.setText("Word: " + word + "\n" +
                                "Urdu Meaning: " + urduMeaning + "\n" +
                                "Persian Meaning: " + persianMeaning);
            detailsArea.setWrapText(true);
            detailsArea.setEditable(false);

            VBox dialogContent = new VBox(detailsArea, detailsTable);
            dialogContent.setSpacing(10);
            dialogContent.setPadding(new Insets(10));

            Dialog<Void> dialog = new Dialog<>();
            dialog.setTitle("Word Details");
            dialog.getDialogPane().setContent(dialogContent);
            dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
            dialog.showAndWait();
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
} 