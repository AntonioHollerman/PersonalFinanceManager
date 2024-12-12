package gui.controller;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;

public class NewRecurringTransactionController {
    @FXML
    public ComboBox<String> transactionType;
    @FXML
    public ComboBox<String> frequencyType;
    @FXML
    public DatePicker dateEntry;
    @FXML
    public TextField descEntry;
    @FXML
    public TextField amountEntry;

    @FXML
    public void backButton(){

    }

    @FXML
    public void saveButton(){

    }
}
