package pl;

import java.io.File;
import java.util.List;

import bll.BLLFacade;
import bll.IBLLFacade;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class DictionaryUI extends Application {

    private IBLLFacade bllFacade;

    public DictionaryUI() {
        this.bllFacade = new BLLFacade();
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Multilingual Dictionary - Import CSV");

        Pane importCSVPane = createImportCSVPane(primaryStage);
        Scene scene = new Scene(importCSVPane, 400, 200);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public Pane getImportCSVPane(Stage primaryStage) {
        return createImportCSVPane(primaryStage);
    }
    private Pane createImportCSVPane(Stage primaryStage) {
        VBox pane = new VBox(10);
        pane.setPadding(new Insets(10));

        Label label = new Label("Import Words from CSV:");
        Button importButton = new Button("Choose CSV File to Import");

        importButton.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
            File selectedFile = fileChooser.showOpenDialog(primaryStage);

            if (selectedFile != null) {
                try {
                    List<String> duplicates = bllFacade.importCSV(selectedFile.getAbsolutePath());
                    if (duplicates.isEmpty()) {
                        showAlert("CSV imported successfully with no duplicates!", AlertType.INFORMATION);
                    } else {
                        showAlert("CSV imported with duplicates. Duplicate words: " + String.join(", ", duplicates), AlertType.WARNING);
                    }
                } catch (Exception ex) {
                    showAlert("Failed to import CSV: " + ex.getMessage(), AlertType.ERROR);
                }
            } else {
                showAlert("No file selected.", AlertType.INFORMATION);
            }
        });

        pane.getChildren().addAll(label, importButton);
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
        DictionaryUI ui = new DictionaryUI();
        Application.launch(ui.getClass(), args);
    }
}

