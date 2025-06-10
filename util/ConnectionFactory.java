package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author lucas
 */

public class ConnectionFactory {
    private static final String URL = "jdbc:postgresql://localhost:5432/DBNAME";
    private static final String USER = "USER";
    private static final String PASSWORD = "PASSWORD";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
