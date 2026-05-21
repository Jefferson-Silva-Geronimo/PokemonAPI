package repository;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    private static final String URL = "jdbc:h2:./pokemon_db";

    private DatabaseConnection() {
    }

    public static Connection get() throws SQLException {
        return DriverManager.getConnection(URL);
    }
}