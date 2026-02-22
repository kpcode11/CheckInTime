package com.example.studentmanager;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    // credentials are read from environment variables for security
    private static final String URL = System.getenv().getOrDefault("DB_URL", "jdbc:mysql://localhost:3306/userdb");
    private static final String USER = System.getenv().getOrDefault("DB_USER", "root");
    private static final String PASSWORD = System.getenv().getOrDefault("DB_PASS", "");

    public static Connection getConnection() throws SQLException {
        // Ensure that the MySQL JDBC driver is loaded
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("Unable to load MySQL JDBC driver", e);
        }

        // Establish and return the connection using environment values
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
