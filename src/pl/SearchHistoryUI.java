package pl;

import java.util.List;

import bll.BLLFacade;
import bll.IBLLFacade;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.control.Label;

public class SearchHistoryUI extends Application {

    private IBLLFacade bllFacade;

    public SearchHistoryUI() {
        this.bllFacade = new BLLFacade();
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Search History");

        Pane searchHistoryPane = createSearchHistoryPane(primaryStage);
        Scene scene = new Scene(searchHistoryPane, 400, 300);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public Pane getSearchHistoryPane(Stage primaryStage) {
        return createSearchHistoryPane(primaryStage);
    }

    private Pane createSearchHistoryPane(Stage primaryStage) {
        VBox pane = new VBox(10);
        pane.setPadding(new Insets(10));

        // Label for search history section
        Label label = new Label("Search History:");

        // ListView to display the history items
        ListView<String> historyListView = new ListView<>();

        // Button to fetch the search history
        Button fetchHistoryButton = new Button("Fetch Search History");

        fetchHistoryButton.setOnAction(e -> {
            try {
                // Fetch search history from the BLLFacade
                List<String> searchHistory = bllFacade.getSearchHistory();

                if (searchHistory.isEmpty()) {
                    showAlert("No search history found.", AlertType.INFORMATION);
                } else {
                    historyListView.getItems().clear();
                    historyListView.getItems().addAll(searchHistory);
                }
            } catch (Exception ex) {
                showAlert("Failed to fetch search history: " + ex.getMessage(), AlertType.ERROR);
            }
        });

        // Add components to the pane
        pane.getChildren().addAll(label, fetchHistoryButton, historyListView);
        return pane;
    }

    private void showAlert(String message, AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        SearchHistoryUI ui = new SearchHistoryUI();
        Application.launch(ui.getClass(), args);
    }
}