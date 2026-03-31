package dataaccess;

import model.AuthData;

import java.sql.SQLException;
import java.sql.Statement;

public class AuthDatabase extends BaseDatabase implements AuthDAO {

    public AuthDatabase() throws DataAccessException {
        // Create authentications table
        String createAuthTableStatement = """
                CREATE TABLE IF NOT EXISTS authentications (
                id INT NOT NULL AUTO_INCREMENT,
                authtoken VARCHAR(255) NOT NULL,
                username VARCHAR(255) NOT NULL,
                PRIMARY KEY (id)
                )
                """;
        createTable(createAuthTableStatement);
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        AuthData authentication = null;
        try(var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement("SELECT authtoken, username FROM authentications WHERE authtoken = ?")) {
                preparedStatement.setString(1, authToken);
                try (var rs = preparedStatement.executeQuery()) {
                    if(rs.next()) {
                        var auth = rs.getString("authtoken");
                        var username = rs.getString("username");
                        authentication = new AuthData(auth, username);
                    }
                }
            }
        }catch (SQLException ex) {
            throw new DataAccessException("Failed to connect to database", ex);
        }
        return authentication;
    }

    @Override
    public void createAuth(AuthData authData) throws DataAccessException{
        String authToken = authData.authToken();
        String username = authData.username();
        String insertAuthStatement = "INSERT INTO authentications (authtoken, username) VALUES (?, ?)";
        try(var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement(insertAuthStatement, Statement.RETURN_GENERATED_KEYS)) {
                preparedStatement.setString(1, authToken);
                preparedStatement.setString(2, username);

                preparedStatement.executeUpdate();
            }
        }catch (SQLException ex) {
            throw new DataAccessException("Failed to connect to database", ex);
        }
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {
        String deleteAuthStatement = "DELETE FROM authentications WHERE authtoken = ?";
        try(var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement(deleteAuthStatement)) {
                preparedStatement.setString(1, authToken);

                preparedStatement.executeUpdate();
            }
        }catch (SQLException ex) {
            throw new DataAccessException("Failed to connect to database", ex);
        }
    }

    @Override
    public void clear() throws DataAccessException{
        String truncateTableStatement = "TRUNCATE TABLE authentications";
        try(var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement(truncateTableStatement)) {

                preparedStatement.executeUpdate();
            }
        }catch (SQLException ex) {
            throw new DataAccessException("Failed to connect to database", ex);
        }
    }
}
