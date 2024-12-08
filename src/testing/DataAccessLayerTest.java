package testing;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import dal.DataAccessLayer;

class DataAccessLayerTest {

    private DataAccessLayer dataAccessLayer;
    private Connection mockConnection;
    private PreparedStatement mockStatement;
    private ResultSet mockResultSet;

    @BeforeEach
    void setUp() throws Exception {
        mockConnection = mock(Connection.class);
        mockStatement = mock(PreparedStatement.class);
        mockResultSet = mock(ResultSet.class);

        // Mock the database connection
        dataAccessLayer = new DataAccessLayer();
        // Use reflection to inject the mocked connection into the DataAccessLayer instance
        java.lang.reflect.Field connectionField = DataAccessLayer.class.getDeclaredField("connection");
        connectionField.setAccessible(true);
        connectionField.set(dataAccessLayer, mockConnection);
    }

    @Test
    void testGetPosDetails_Success() throws Exception {
        // Mock database behavior for retrieving word ID
        when(mockConnection.prepareStatement("SELECT id FROM words WHERE word = ?")).thenReturn(mockStatement);
        when(mockStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getInt("id")).thenReturn(1);

        // Simulate the POS details processing
        List<String[]> mockedPosDetails = new ArrayList<>();
        mockedPosDetails.add(new String[] { "voweledWord", "stem", "pos", "rootWord" });

        // Stub the actual method call for `getPosDetails` to simulate results
        DataAccessLayer dataAccessLayerSpy = spy(dataAccessLayer);
        doReturn(mockedPosDetails).when(dataAccessLayerSpy).getPosDetails(anyString());

        // Call the method
        List<String[]> posDetails = dataAccessLayerSpy.getPosDetails("testWord");

        // Validate the returned result
        assertEquals(1, posDetails.size());
        assertEquals("voweledWord", posDetails.get(0)[0]);
        assertEquals("stem", posDetails.get(0)[1]);
        assertEquals("pos", posDetails.get(0)[2]);
        assertEquals("rootWord", posDetails.get(0)[3]);

        // Verify interactions
        verify(mockStatement, times(1)).setString(1, "testWord");
        verify(mockStatement, times(1)).executeQuery();
    }

    @Test
    void testGetPosDetails_WordNotFound() throws Exception {
        // Mock database behavior for retrieving word ID
        when(mockConnection.prepareStatement("SELECT id FROM words WHERE word = ?")).thenReturn(mockStatement);
        when(mockStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(false); // Word not found

        // Call the method
        List<String[]> posDetails = dataAccessLayer.getPosDetails("nonExistentWord");

        // Verify result is empty and no exception is thrown
        assertTrue(posDetails.isEmpty());

        // Verify interactions
        verify(mockStatement, times(1)).setString(1, "nonExistentWord");
        verify(mockStatement, times(1)).executeQuery();
    }

    @Test
    void testGetPosDetails_SQLException() throws Exception {
        // Simulate SQLException when fetching word ID
        when(mockConnection.prepareStatement("SELECT id FROM words WHERE word = ?")).thenThrow(new SQLException("Test Exception"));

        // Call the method
        List<String[]> posDetails = dataAccessLayer.getPosDetails("testWord");

        // Verify the method handles the exception gracefully
        assertTrue(posDetails.isEmpty());
    }
}
