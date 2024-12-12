package gui.controller;
// TODO: Preferred height start at 35 for ScrollPane, add 35 for up to 6 rows

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;

public class HomeController {
    @FXML
    private ScrollPane accountsScrollPane;

    @FXML
    private GridPane accountsGrid;

    @FXML
    private void initialize() {
        System.out.println("second");
    }

    @FXML
    public void selectButton(ActionEvent e){

    }

    @FXML
    public void addAccount(){

    }

    @FXML
    public void openAccounts(){

    }
}
