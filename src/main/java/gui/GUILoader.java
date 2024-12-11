package gui;

import gui.controller.EditAccountController;
import gui.controller.EditRecurringTransactionController;
import gui.controller.EditTransactionController;
import gui.controller.TransactionsController;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import structure.Account;
import structure.RecurringTransaction;
import structure.Transaction;

import java.io.IOException;
import java.net.URL;

public class GUILoader {
    public static Stage stage;
    private enum UI{
        HOME, TRANSACTIONS, SUMMARY_STATS, EDIT_ACCOUNT, EDIT_TRANSACTION, EDIT_RECURRING_TRANSACTION, NEW_RECURRING_TRANSACTION
    }
    private static URL getResource(UI ui) throws IOException {
        return switch (ui){
            case HOME -> GUILoader.class.getResource("gui\\home.fxml");
            case TRANSACTIONS -> GUILoader.class.getResource("gui\\transactions.fxml");
            case SUMMARY_STATS -> GUILoader.class.getResource("gui\\summary-stats.fxml");
            case EDIT_ACCOUNT -> GUILoader.class.getResource("gui\\edit-account.fxml");
            case EDIT_TRANSACTION -> GUILoader.class.getResource("gui\\edit-transaction.fxml");
            case EDIT_RECURRING_TRANSACTION -> GUILoader.class.getResource("gui\\edit-recurring-transaction.fxml");
            case NEW_RECURRING_TRANSACTION -> GUILoader.class.getResource("gui\\new-recurring-transaction.fxml");
        };
    }
    public static void swapHome() throws IOException{
        try{
            FXMLLoader loader = new FXMLLoader(getResource(UI.HOME));
            stage.setScene(loader.load());
        } catch (IOException e){
            throw new RuntimeException("Fail to swap scene to HOME", e);
        }
    }
    public static void swapTransactions(Account[] accounts) throws IOException{
        try{
            FXMLLoader loader = new FXMLLoader(getResource(UI.TRANSACTIONS));
            TransactionsController controller = loader.getController();
            controller.render(accounts);

            stage.setScene(loader.load());
        } catch (IOException e){
            throw new RuntimeException("Fail to swap scene to TRANSACTION", e);
        }
    }
    public static void swapSummaryStats() throws IOException{
        try{
            FXMLLoader loader = new FXMLLoader(getResource(UI.SUMMARY_STATS));
            stage.setScene(loader.load());
        } catch (IOException e){
            throw new RuntimeException("Fail to swap scene to SUMMARY_STATS", e);
        }
    }
    public static void swapNewAccount() {
        try{
            FXMLLoader loader = new FXMLLoader(getResource(UI.EDIT_ACCOUNT));
            stage.setScene(loader.load());
        } catch (IOException e){
            throw new RuntimeException("Fail to swap scene to NEW_ACCOUNT", e);
        }
    }
    public static void swapEditAccount(Account acc){
        try{
            FXMLLoader loader = new FXMLLoader(getResource(UI.EDIT_ACCOUNT));
            EditAccountController controller = loader.getController();
            controller.renderAccount(acc);

            stage.setScene(loader.load());
        } catch (IOException e){
            throw new RuntimeException("Fail to swap scene to EDIT_ACCOUNT", e);
        }
    }
    public static void swapNewTransaction(){
        try{
            FXMLLoader loader = new FXMLLoader(getResource(UI.EDIT_TRANSACTION));
            stage.setScene(loader.load());
        } catch (IOException e){
            throw new RuntimeException("Fail to swap scene to NEW_TRANSACTION", e);
        }
    }
    public static void swapEditTransaction(Transaction transaction){
        try{
            FXMLLoader loader = new FXMLLoader(getResource(UI.EDIT_TRANSACTION));
            EditTransactionController controller = loader.getController();
            controller.buildTransaction(transaction);

            stage.setScene(loader.load());
        } catch (IOException e){
            throw new RuntimeException("Fail to swap scene to EDIT_TRANSACTION", e);
        }
    }
    public static void swapNewRecurringTransaction(){
        try{
            FXMLLoader loader = new FXMLLoader(getResource(UI.NEW_RECURRING_TRANSACTION));
            stage.setScene(loader.load());
        } catch (IOException e){
            throw new RuntimeException("Fail to swap scene to NEW_RECURRING_TRANSACTION", e);
        }
    }
    public static void swapEditRecurringTransaction(RecurringTransaction recurringTransaction){
        try{
            FXMLLoader loader = new FXMLLoader(getResource(UI.EDIT_RECURRING_TRANSACTION));
            EditRecurringTransactionController controller = loader.getController();
            controller.renderRecurringTransaction(recurringTransaction);

            stage.setScene(loader.load());
        } catch (IOException e){
            throw new RuntimeException("Fail to swap scene to EDIT_RECURRING_TRANSACTION", e);
        }
    }
}
