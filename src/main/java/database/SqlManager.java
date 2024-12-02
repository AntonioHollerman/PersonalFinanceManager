package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SqlManager {
    private final Connection conn;
    public SqlManager() {
        try {
            conn = DriverManager.getConnection("jdbc:sqlite:C:\\JavaProjects\\PersonalFinanceManager\\resources\\finance.db");
        } catch (SQLException ignore){
            throw new RuntimeException("Fail to connect to database");
        }
    }
    
}
