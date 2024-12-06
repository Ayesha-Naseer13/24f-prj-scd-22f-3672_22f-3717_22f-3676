package dal;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DatabaseConfig {

    private static final Logger logger = LogManager.getLogger(DatabaseConfig.class);
    private static Connection connection;

    /**
     * Retrieves a singleton database connection.
     * 
     * @return Connection object
     * @throws SQLException if a database access error occurs
     */
    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            Properties dbProperties = loadDBProperties();
            String url = dbProperties.getProperty("db.url");
            String user = dbProperties.getProperty("db.user");
            String password = dbProperties.getProperty("db.password");

            try {
                logger.info("Attempting to establish a database connection to URL: {}", url);
                connection = DriverManager.getConnection(url, user, password);
                logger.info("Database connection established successfully.");
            } catch (SQLException e) {
                logger.error("Failed to establish a database connection.", e);
                throw e; // Re-throw to ensure calling methods handle this properly
            }
        }
        return connection;
    }

    /**
     * Closes the database connection if it's open.
     */
    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                logger.info("Database connection closed successfully.");
            } catch (SQLException e) {
                logger.error("Error closing the database connection: {}", e.getMessage(), e);
            }
        } else {
            logger.warn("Attempted to close a null or already closed database connection.");
        }
    }

    /**
     * Loads database properties from the "db.properties" file.
     * 
     * @return Properties object containing database configuration
     */
    private static Properties loadDBProperties() {
        Properties properties = new Properties();
        try (InputStream input = new FileInputStream("db.properties")) {
            logger.info("Loading database properties from file: db.properties");
            properties.load(input);
            logger.info("Database properties loaded successfully.");
        } catch (IOException e) {
            logger.error("Error loading database properties file: {}", e.getMessage(), e);
            throw new RuntimeException("Error loading database properties.", e);
        }
        return properties;
    }
}
