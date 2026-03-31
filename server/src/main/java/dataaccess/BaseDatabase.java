package dataaccess;

import java.sql.SQLException;

public abstract class BaseDatabase {

    public void createTable(String sqlStatement) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement(sqlStatement)) {
                preparedStatement.executeUpdate();
            }
        } catch (SQLException ex) {
            throw new DataAccessException("failed to create table", ex);
        }
    }

}
