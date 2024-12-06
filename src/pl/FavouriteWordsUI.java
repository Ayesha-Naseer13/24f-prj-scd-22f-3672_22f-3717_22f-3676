package pl;

import java.util.List;

import bll.FavouriteWordsBO;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import main.WordNotFoundException;

public class FavouriteWordsUI {
    private FavouriteWordsBO favouriteWordsBO;

    public FavouriteWordsUI() {
        this.favouriteWordsBO = new FavouriteWordsBO();
    }

    public void addToFavourites(String word) {
        try {
            boolean success = favouriteWordsBO.addToFavourite(word);
            if (success) {
                showAlert(Alert.AlertType.INFORMATION, "Favourite Words", word + " added to favourites.");
            } else {
                showAlert(Alert.AlertType.WARNING, "Favourite Words", word + " is already in favourites.");
            }
        } catch (WordNotFoundException e) {
            showAlert(Alert.AlertType.ERROR, "Error", e.getMessage());
        } catch (RuntimeException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "An unexpected error occurred: " + e.getMessage());
        }
    }

    public void showFavouritesScreen() {
        Stage favouritesStage = new Stage();
        favouritesStage.setTitle("Favourite Words");

        TableView<String[]> favouriteWordsTable = new TableView<>();
        TableColumn<String[], String> wordColumn = new TableColumn<>("Word");
        wordColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue()[1]));

        TableColumn<String[], String> urduColumn = new TableColumn<>("Urdu Meaning");
        urduColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue()[2]));

        TableColumn<String[], String> persianColumn = new TableColumn<>("Persian Meaning");
        persianColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue()[3]));

        favouriteWordsTable.getColumns().addAll(wordColumn, urduColumn, persianColumn);

        // Load favorite words
        try {
            List<String[]> favourites = favouriteWordsBO.getFavouriteWords();
            favouriteWordsTable.getItems().addAll(favourites);
        } catch (RuntimeException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load favourites: " + e.getMessage());
        }

        Button removeButton = new Button("Remove Selected");
        removeButton.setOnAction(e -> {
            String[] selectedFavourite = favouriteWordsTable.getSelectionModel().getSelectedItem();
            if (selectedFavourite != null) {
                try {
                    int wordId = Integer.parseInt(selectedFavourite[0]);
                    favouriteWordsBO.removeFromFavourite(wordId);
                    favouriteWordsTable.getItems().remove(selectedFavourite);
                    showAlert(Alert.AlertType.INFORMATION, "Favourite Words", selectedFavourite[1] + " removed from favourites.");
                } catch (RuntimeException ex) {
                    showAlert(Alert.AlertType.ERROR, "Error", "Failed to remove from favourites: " + ex.getMessage());
                }
            } else {
                showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a word to remove.");
            }
        });

        VBox layout = new VBox(new VBox(favouriteWordsTable), removeButton);
        layout.setSpacing(10);
        layout.setPadding(new Insets(10));

        favouritesStage.setScene(new Scene(layout));
        favouritesStage.show();
    }

    private void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
