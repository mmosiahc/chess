package dataaccess;

import model.UserData;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;

public class UserDatabase implements UserDAO {

    public UserDatabase() {}

    @Override
    public UserData getUser(String username) throws DataAccessException {
        UserData user = null;
        try(var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement("SELECT username, password, email FROM users WHERE username = ?")) {
                preparedStatement.setString(1, username);
                try (var rs = preparedStatement.executeQuery()) {
                    if(rs.next()) {
                        var name = rs.getString("username");
                        var password = rs.getString("password");
                        var email = rs.getString("email");

                        user = new UserData(name, password, email);
                    }
                }
            }
        }catch (SQLException ex) {
            throw new DataAccessException("Failed to connect to database", ex);
        }
        return user;
    }

    @Override
    public void createUser(UserData userData) throws DataAccessException {
        String username = userData.username();
        UserData user = getUser(username);
        if(user != null)  throw new AlreadyTakenException();
        String password = userData.password();
        String email = userData.email();
        String insertUserStatement = "INSERT INTO users (username, password, email) VALUES (?, ?, ?)";
        try(var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement(insertUserStatement, Statement.RETURN_GENERATED_KEYS)) {
                preparedStatement.setString(1, username);
                preparedStatement.setString(2, password);
                preparedStatement.setString(3, email);

                preparedStatement.executeUpdate();
            }
        }catch (SQLException ex) {
            throw new DataAccessException("Failed to connect to database", ex);
        }
    }

    @Override
    public void clear() {

    }

    @Override
    public Map<String, UserData> getUsers() {
        return null;
    }

    @Override
    public String toString() {
        return null;
    }
}
