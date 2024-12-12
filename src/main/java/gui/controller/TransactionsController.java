package gui.controller;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.GridPane;
import structure.Account;

// TODO: When adding new row all columns labels must have a padding of 10 for both tables
public class TransactionsController {

    @FXML
    public ComboBox<String> transactionsFilter;
    @FXML
    public ComboBox<String> recurringTransactionsFilter;
    @FXML
    public GridPane transactionsGrid;
    @FXML
    public GridPane recurringTransactionsGrid;

    @FXML
    public void homeButton(){

    }

    @FXML
    public void deleteButton(){

    }

    @FXML
    public void addTransaction(){

    }

    @FXML
    public void addRecurringTransaction(){

    }

    @FXML
    public void swapFilter(){

    }

    @FXML
    public void summaryButton(){

    }
    public void render(Account[] accounts){

    }
}
