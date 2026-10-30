package com.smartcanteen.database.connection;

import com.smartcanteen.common.exception.DatabaseException;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DBConnection {
    private static final String PROPERTIES_FILE = "db.properties";
    private static String url;
    private static String user;
    private static String password;

    static {
        loadProperties();
    }

    private DBConnection() {
        // Prevent instantiation
    }

    private static synchronized void loadProperties() {
        Properties props = new Properties();
        try (InputStream input = DBConnection.class.getClassLoader().getResourceAsStream(PROPERTIES_FILE)) {
            if (input == null) {
                throw new DatabaseException("Configuration file '" + PROPERTIES_FILE + "' not found in classpath.");
            }
            props.load(input);
            url = props.getProperty("db.url");
            user = props.getProperty("db.user");
            password = props.getProperty("db.password");

            // Optional driver loading
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
            } catch (ClassNotFoundException ignored) {
                // MySQL 8+ driver is automatically registered by JDBC ServiceLoader
            }
        } catch (IOException e) {
            throw new DatabaseException("Failed to load database properties: " + e.getMessage(), e);
        }
    }

    public static Connection getConnection() throws SQLException {
        if (url == null) {
            loadProperties();
        }
        return DriverManager.getConnection(url, user, password);
    }
}
