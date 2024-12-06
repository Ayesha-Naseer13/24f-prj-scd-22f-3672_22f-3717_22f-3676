package pl;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MultilingualDictionaryApp extends Application {

    private DictionaryUI dic;
    private SearchUI search;
    private DictionaryAppGUI crud;
    private ViewUI view;
    private CustomDictionary customDictionary;
    private SearchHistoryUI searchHistoryUI;
    private ScrapGUI scrapGUI;
    private SegmentGUI segmentGUI;

    public MultilingualDictionaryApp() {
        search = new SearchUI();
        dic = new DictionaryUI();
        crud = new DictionaryAppGUI();
        view = new ViewUI();
        customDictionary = new CustomDictionary();
        searchHistoryUI = new SearchHistoryUI();
        scrapGUI = new ScrapGUI(); // Initialize ScrapGUI
        segmentGUI = new SegmentGUI(); // Initialize SegmentGUI
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Multilingual Dictionary");

        // Header with logo, title, and links
        HBox header = createHeader();
        header.setPadding(new Insets(15));
        header.setStyle("-fx-background-color: #343a40;");

        // Sidebar with navigation buttons
        VBox sidebar = new VBox(15);
        sidebar.setPadding(new Insets(20));
        sidebar.setStyle("-fx-background-color: #495057;");

        Button addWordBtn = createNavButton("Add Word");
        Button deleteWordBtn = createNavButton("Delete Word");
        Button updateWordBtn = createNavButton("Update Word");
        Button searchWordBtn = createNavButton("Search Word");
        Button viewAllWordsBtn = createNavButton("View All Words");
        Button importCSVBtn = createNavButton("Import CSV");
        Button customDictionaryBtn = createNavButton("Custom Dictionary");
        Button searchHistoryBtn = createNavButton("Search History");
        Button helpBtn = createNavButton("Help");
        Button scrapeDictionaryBtn = createNavButton("Scrape Dictionary"); // Button for ScrapGUI
        Button segmentDictionaryBtn = createNavButton("Segment Word"); // Button for SegmentGUI

        sidebar.getChildren().addAll(addWordBtn, deleteWordBtn, updateWordBtn, searchWordBtn, viewAllWordsBtn, importCSVBtn, customDictionaryBtn, searchHistoryBtn,scrapeDictionaryBtn,segmentDictionaryBtn, helpBtn);

        // Main content area
        StackPane contentArea = new StackPane();
        contentArea.setPadding(new Insets(20));

        // Footer with copyright notice
        HBox footer = createFooter();
        footer.setPadding(new Insets(10));
        footer.setStyle("-fx-background-color: #343a40; -fx-alignment: center;");

        // Layout Setup
        BorderPane root = new BorderPane();
        root.setTop(header);
        root.setLeft(sidebar);
        root.setCenter(contentArea);
        root.setBottom(footer);

        Scene scene = new Scene(root, 800, 600);
        scene.getStylesheets().add("file:/D:/eclipse_workspace/multilingual-dic/CSS/styles.css");

        // Button actions
        addWordBtn.setOnAction(e -> contentArea.getChildren().setAll(crud.getAddWordPane()));
        deleteWordBtn.setOnAction(e -> contentArea.getChildren().setAll(crud.getDeleteWordPane()));
        updateWordBtn.setOnAction(e -> contentArea.getChildren().setAll(crud.getUpdateWordPane()));
        searchWordBtn.setOnAction(e -> contentArea.getChildren().setAll(search.getSearchPane()));
        viewAllWordsBtn.setOnAction(e -> contentArea.getChildren().setAll(view.getViewAllWordsPane()));
        importCSVBtn.setOnAction(e -> contentArea.getChildren().setAll(dic.getImportCSVPane(primaryStage)));
        customDictionaryBtn.setOnAction(e -> contentArea.getChildren().setAll(customDictionary.getCustomDictionaryPane()));
        searchHistoryBtn.setOnAction(e -> contentArea.getChildren().setAll(searchHistoryUI.getSearchHistoryPane(primaryStage)));
       

        // Open ScrapGUI when the "Scrape Dictionary" button is clicked
        scrapeDictionaryBtn.setOnAction(e -> scrapGUI.start(new Stage()));

        // Open SegmentGUI when the "Segment Word" button is clicked
        segmentDictionaryBtn.setOnAction(e -> segmentGUI.start(new Stage()));
        helpBtn.setOnAction(e -> showHelpDialog());
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private HBox createHeader() {
        HBox header = new HBox(20);
        header.setPadding(new Insets(10));

        Label logoLabel = new Label("📖"); // Placeholder logo
        logoLabel.setStyle("-fx-font-size: 24px; -fx-text-fill: #ffffff;");

        Label titleLabel = new Label("Multilingual Dictionary");
        titleLabel.setStyle("-fx-font-size: 20px; -fx-text-fill: #ffffff;");

        header.getChildren().addAll(logoLabel, titleLabel);
        return header;
    }

    private HBox createFooter() {
        HBox footer = new HBox();
        Label footerLabel = new Label("© 2024 Multilingual Dictionary. All rights reserved.");
        footerLabel.setStyle("-fx-text-fill: #adb5bd;");
        footer.getChildren().add(footerLabel);
        return footer;
    }

    private Button createNavButton(String text) {
        Button button = new Button(text);
        button.setPrefWidth(150);
        button.setStyle("-fx-background-color: #6c757d; -fx-text-fill: #ffffff;");
        button.setOnMouseEntered(e -> button.setStyle("-fx-background-color: #adb5bd; -fx-text-fill: #343a40;"));
        button.setOnMouseExited(e -> button.setStyle("-fx-background-color: #6c757d; -fx-text-fill: #ffffff;"));
        return button;
    }

    private void showHelpDialog() {
        Alert helpAlert = new Alert(Alert.AlertType.INFORMATION);
        helpAlert.setTitle("Help");
        helpAlert.setHeaderText("Multilingual Dictionary - Help");
        helpAlert.setContentText("This application allows you to add, update, delete, and search words in multiple languages.\n\n"
                + "- Use the sidebar to navigate between functionalities.\n"
                + "- Add words with translations in Urdu and Persian.\n"
                + "- Import words from a CSV file.\n"
                + "- View all words currently in the dictionary.\n\n"
                + "For further assistance, contact support@multilingual-dic.com.");
        helpAlert.showAndWait();
    }
}
