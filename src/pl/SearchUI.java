package pl;

import bll.BLLFacade;
import bll.IBLLFacade;
import bll.SegmentBO;
import dal.SegmentDAO;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.List;
import java.util.Map;

public class SearchUI extends Application {
    private IBLLFacade bllFacade;
    private SegmentBO segmentBO;

    // Class-level variables
    private RadioButton searchByKeyButton;
    private RadioButton searchByValueButton;
    private RadioButton urduButton;
    private RadioButton persianButton;

    public SearchUI() {
        this.bllFacade = new BLLFacade();
        this.segmentBO = new SegmentBO(new SegmentDAO());
    }

    private TextField searchField;
    private TextArea searchResultArea;
    private ListView<String> suggestionsList;
    private ToggleGroup searchTypeGroup;
    private ToggleGroup languageGroup;

    @Override
    public void start(Stage primaryStage) {
        Scene scene = new Scene(createSearchWordPane(), 500, 500);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Dictionary Search with Segmentation");
        primaryStage.show();
    }

    private Pane createSearchWordPane() {
        VBox pane = new VBox(10);
        pane.setPadding(new Insets(10));

        searchField = new TextField();
        Button searchButton = new Button("Search");
        searchResultArea = new TextArea();
        searchResultArea.setEditable(false);
        searchResultArea.setPrefHeight(300);

        suggestionsList = new ListView<>();
        suggestionsList.setVisible(false);

        ObservableList<String> suggestions = FXCollections.observableArrayList();
        suggestionsList.setItems(suggestions);

        searchTypeGroup = new ToggleGroup();
        searchByKeyButton = new RadioButton("Search by Word"); // Moved to class-level
        searchByKeyButton.setToggleGroup(searchTypeGroup);
        searchByKeyButton.setSelected(true);

        searchByValueButton = new RadioButton("Search by Meaning"); // Moved to class-level
        searchByValueButton.setToggleGroup(searchTypeGroup);

        languageGroup = new ToggleGroup();
        urduButton = new RadioButton("Urdu"); // Moved to class-level
        urduButton.setToggleGroup(languageGroup);
        urduButton.setDisable(true);

        persianButton = new RadioButton("Persian"); // Moved to class-level
        persianButton.setToggleGroup(languageGroup);
        persianButton.setDisable(true);

        searchTypeGroup.selectedToggleProperty().addListener((obs, oldToggle, newToggle) -> {
            boolean isValueSearch = newToggle == searchByValueButton;
            urduButton.setDisable(!isValueSearch);
            persianButton.setDisable(!isValueSearch);

            if (!isValueSearch) {
                urduButton.setSelected(false);
                persianButton.setSelected(false);
            }
        });

        searchField.setOnKeyReleased((KeyEvent e) -> {
            String input = searchField.getText().trim();
            boolean searchByKey = searchTypeGroup.getSelectedToggle() == searchByKeyButton;

            String language = "";
            if (!searchByKey) {
                if (languageGroup.getSelectedToggle() == urduButton) {
                    language = "Urdu";
                } else if (languageGroup.getSelectedToggle() == persianButton) {
                    language = "Persian";
                }
            }

            if (!input.isEmpty()) {
                try {
                    suggestions.setAll(bllFacade.getSuggestions(input, searchByKey, language));
                    suggestionsList.setVisible(!suggestions.isEmpty());
                } catch (RuntimeException ex) {
                    handleDatabaseError(ex);
                }
            } else {
                suggestionsList.setVisible(false);
                suggestions.clear();
            }
        });

        suggestionsList.setOnMouseClicked(event -> {
            String selectedSuggestion = suggestionsList.getSelectionModel().getSelectedItem();
            if (selectedSuggestion != null) {
                searchField.setText(selectedSuggestion);
                suggestionsList.setVisible(false);
                suggestions.clear();
            }
        });

        searchButton.setOnAction(e -> searchWord());

        pane.getChildren().addAll(
                new Label("Select Search Type:"), searchByKeyButton, searchByValueButton,
                new Label("Select Language for Translation (if applicable):"), urduButton, persianButton,
                new Label("Enter Search Term:"), searchField, suggestionsList, searchButton, searchResultArea);
        return pane;
    }

    private void searchWord() {
        String searchQuery = searchField.getText().trim();
        StringBuilder result = new StringBuilder();

        try {
            boolean searchByKey = searchTypeGroup.getSelectedToggle() == searchByKeyButton;
            String language = "";
            if (!searchByKey) {
                language = languageGroup.getSelectedToggle() == urduButton ? "Urdu" : "Persian";
            }

            List<String> results = bllFacade.searchWord(searchQuery, searchByKey, language);

            if (results.isEmpty()) {
                result.append("No matching results found.\n\nAttempting segmentation...\n");
                Map<String, Map<String, String>> segmentedResults = segmentBO.segmentAndFetchMeaning(searchQuery);

                if (segmentedResults.isEmpty()) {
                    result.append("Segmentation yielded no results.");
                } else {
                    segmentedResults.forEach((word, translations) -> {
                        result.append("Word: ").append(word).append("\n");
                        result.append("  Persian: ").append(translations.getOrDefault("Persian", "N/A")).append("\n");
                        result.append("  Urdu: ").append(translations.getOrDefault("Urdu", "N/A")).append("\n\n");
                    });
                }
            } else {
                results.forEach(entry -> result.append(entry).append("\n"));
            }
        } catch (RuntimeException ex) {
            handleDatabaseError(ex);
        }

        searchResultArea.setText(result.toString());
    }

    private void handleDatabaseError(RuntimeException ex) {
        searchResultArea.setText("An error occurred while accessing the database. Please try again later.");
        ex.printStackTrace();
    }

    public static void main(String[] args) {
        launch(args);
    }

    public Node getSearchPane() {
        return createSearchWordPane();
    }
}
