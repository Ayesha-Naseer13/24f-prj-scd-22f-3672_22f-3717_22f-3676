package dal;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SearchHistoryDAO implements ISearchHistoryDAO {
    @Override
    public List<String> getSearchHistory() {
        List<String> history = new ArrayList<>();
        String query = "SELECT search_term FROM search_history ORDER BY search_date DESC";

        // Only manage the PreparedStatement and ResultSet here
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try (Connection conn = DatabaseConfig.getConnection()) { // Use try-with-resources for connection
            conn.setAutoCommit(false); // Explicitly manage transactions
            stmt = conn.prepareStatement(query);
            rs = stmt.executeQuery();

            while (rs.next()) {
                history.add(rs.getString("search_term"));
            }

            conn.commit(); // Commit transaction if successful
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching search history: " + e.getMessage(), e);
        } finally {
            // Ensure PreparedStatement and ResultSet are closed
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
            } catch (SQLException closeEx) {
                throw new RuntimeException("Error closing resources: " + closeEx.getMessage(), closeEx);
            }
        }

        return history;
    }
}
