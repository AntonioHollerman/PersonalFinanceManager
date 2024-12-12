package gui.controller;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import structure.Transaction;

public class EditTransactionController {
    @FXML
    public Label titleLabel;
    @FXML
    public ComboBox<String> transactionType;
    @FXML
    public DatePicker dateEntry;
    @FXML
    public TextField descEntry;
    @FXML
    public TextField amountEntry;

    public void buildTransaction(Transaction transaction){

    }

    @FXML
    public void backButton(){

    }

    @FXML
    public void saveButton(){

    }
}
