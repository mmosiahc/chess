package dataaccess;

import exceptions.DataAccessException;

import java.sql.SQLException;

public class BaseDatabaseTest {
    public int countRowsInTable(String database) throws DataAccessException {
        int numberOfRows = -1;
        String countAuthQuery = "SELECT COUNT(*) FROM " + database;
        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement(countAuthQuery)) {
                var rs = preparedStatement.executeQuery();
                if(rs.next()) {
                    numberOfRows = rs.getInt(1);
                }
                return numberOfRows;
            }
        } catch (SQLException e) {
            throw new DataAccessException("failed to connect to database", e);
        }
    }
}
