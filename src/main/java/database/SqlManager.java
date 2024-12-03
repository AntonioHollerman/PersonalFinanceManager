package database;

import database.enums.TransactionType;
import database.records.AccountRow;
import database.records.RecurringTransactionRow;
import database.records.TableToRecordAPI;
import database.records.TransactionRow;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SqlManager {
    private final Connection conn;
    protected SqlManager() {
        try {
            conn = DriverManager.getConnection("jdbc:sqlite:C:\\JavaProjects\\PersonalFinanceManager\\resources\\finance.db");
            createTables();
        } catch (SQLException ignore){
            throw new RuntimeException("Fail to connect to database");
        }
    }
    public void createTables() {
        String tables = """
            CREATE TABLE IF NOT EXISTS  account(
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                balance REAL NOT NULL,
                name TEXT NOT NULL,
                card TEXT,
                bank TEXT
            );
            CREATE TABLE IF NOT EXISTS recurring_transaction(
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                acc_id INTEGER REFERENCES account(id),
                start_date REAL NOT NULL,
                name TEXT NOT NULL,
                type TEXT NOT NULL,
                recurring_rate TEXT NOT NULL,
                amount REAL NOT NULL
            );
            CREATE TABLE IF NOT EXISTS user_transaction(
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                acc_id INTEGER REFERENCES account(id),
                date REAL NOT NULL,
                name TEXT,
                type TEXT,
                amount REAL NOT NULL
            );""";
        try (PreparedStatement ps = conn.prepareStatement(tables)){
            ps.execute();
        } catch (SQLException e){
            e.fillInStackTrace();
            throw new RuntimeException("Failed to create tables");
        }
    }
    public List<AccountRow> getAccounts(){
        String sql = """
                SELECT * FROM account;""";
        try (PreparedStatement ps = conn.prepareStatement(sql)){
            return TableToRecordAPI.toAccounts(ps.executeQuery());
        } catch (SQLException e){
            e.fillInStackTrace();
            throw new RuntimeException("Fail to get accounts");
        }
    }

    public List<TransactionRow> getTransactions(int[] acc_ids){
        StringBuilder sql = new StringBuilder("""
                SELECT * FROM user_transaction WHERE acc_id IN (""");
        for (int i=0; i < acc_ids.length; i++){
            if (i != acc_ids.length - 1){
                sql.append(acc_ids[i]).append(", ");
            } else {
                sql.append(acc_ids[i]).append(");");
            }
        }

        try (PreparedStatement ps = conn.prepareStatement(sql.toString())){
            return TableToRecordAPI.toTransactions(ps.executeQuery());
        } catch (SQLException e){
            e.fillInStackTrace();
            throw new RuntimeException("Fail to get transactions");
        }
    }

    public TransactionRow getTransaction(int tran_id){
        String sql = "SELECT * FROM user_transaction WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setInt(1, tran_id);
            return TableToRecordAPI.toTransactions(ps.executeQuery()).get(0);
        } catch (SQLException e){
            e.fillInStackTrace();
            throw new RuntimeException("Fail to get transaction id: " + tran_id);
        }
    }

    public List<RecurringTransactionRow> getRecurringTransactions(int[] acc_ids){
        StringBuilder sql = new StringBuilder("""
                SELECT * FROM recurring_transaction WHERE acc_id IN (""");
        for (int i=0; i < acc_ids.length; i++){
            if (i != acc_ids.length - 1){
                sql.append(acc_ids[i]).append(", ");
            } else {
                sql.append(acc_ids[i]).append(");");
            }
        }

        try (PreparedStatement ps = conn.prepareStatement(sql.toString())){
            return TableToRecordAPI.toRecurringTransactions(ps.executeQuery());
        } catch (SQLException e){
            e.fillInStackTrace();
            throw new RuntimeException("Fail to get recurring transactions");
        }
    }

    private void updateTableInfo(int id, String newValue, String column, String table){
        String sql = String.format("UPDATE %s SET %s = ? WHERE id = ?", table, column);

        try (PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setString(1, newValue);
            ps.setInt(2, id);
            ps.execute();
        } catch (SQLException e){
            e.fillInStackTrace();
            throw new RuntimeException("Fail to update Account Info");
        }
    }

    public void setAccountName(int acc_id, String newValue){
        updateTableInfo(acc_id, newValue, "name", "account");
    }

    public void setAccountCard(int acc_id, String newValue){
        updateTableInfo(acc_id, newValue, "card", "account");
    }

    public void setAccountBank(int acc_id, String newValue){
        updateTableInfo(acc_id, newValue, "bank", "account");
    }
    public void setTransactionName(int tran_id, String newValue){
        updateTableInfo(tran_id, newValue, "name", "user_transaction");
    }
    public void setTransactionType(int tran_id, String newValue){
        updateTableInfo(tran_id, newValue, "type", "user_transaction");
    }

    public void setRecurringTransactionName(int rec_id, String newValue){
        updateTableInfo(rec_id, newValue, "name", "recurring_transaction");
    }

    public void setRecurringTransactionType(int rec_id, String newValue){
        updateTableInfo(rec_id, newValue, "type", "recurring_transaction");
    }

    public void setRecurringTransactionRate(int rec_id, String newValue){
        updateTableInfo(rec_id, newValue, "recurring_rate", "recurring_transaction");
    }

    private void updateAccountBalance(int acc_id, float amount, TransactionType type){
        String sql = """
                UPDATE account
                SET balance = balance %s ?
                WHERE id = ?;""";
        sql = String.format(sql, type == TransactionType.DEPOSIT ? "+" : "-");

        try (PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setFloat(1, amount);
            ps.setInt(2, acc_id);
            ps.execute();
        } catch (SQLException e){
            e.fillInStackTrace();
            throw new RuntimeException("Fail to update account balance");
        }
    }

    public void setTransactionAmount(int tran_id, float newAmount){
        TransactionRow transaction = getTransaction(tran_id);
        TransactionType opposite = transaction.type() == TransactionType.DEPOSIT ?
                TransactionType.WITHDRAW : TransactionType.DEPOSIT;
        updateAccountBalance(transaction.acc_id(), transaction.amount(), opposite);
        updateAccountBalance(transaction.acc_id(), newAmount, transaction.type());

        String sql = "UPDATE user_transaction SET amount = ? WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setFloat(1, newAmount);
            ps.setInt(2, tran_id);
            ps.execute();
        } catch (SQLException e){
            e.fillInStackTrace();
            throw new RuntimeException("Fail to update transaction amount");
        }
    }

    public void setRecurringTransactionAmount(int rec_id, float newAmount){
        String sql = "UPDATE recurring_transaction SET amount = ? WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setFloat(1, newAmount);
            ps.setInt(2, rec_id);
            ps.execute();
        } catch (SQLException e){
            e.fillInStackTrace();
            throw new RuntimeException("Fail to update transaction amount");
        }
    }
}
