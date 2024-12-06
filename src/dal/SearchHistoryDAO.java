package dal;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SearchHistoryDAO implements ISearchHistoryDAO {
    @Override
    public List<String> getSearchHistory() {
        List<String> history = new ArrayList<>();
        String query = "SELECT search_term FROM search_history ORDER BY search_date DESC";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                history.add(rs.getString("search_term"));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching search history: " + e.getMessage(), e);
        }

        return history;
    }
}
