package gui;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class HelloController {
    @FXML
    private Label welcomeText;

    @FXML
    private void onHelloButtonClick() {
        welcomeText.setText("Welcome to JavaFX Application!");
    }
}