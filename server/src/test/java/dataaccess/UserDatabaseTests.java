package dataaccess;

import model.UserData;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.*;

public class UserDatabaseTests extends BaseDatabaseTest {

    @Test
    @DisplayName("Get User - Successful")
    void getExistingUser() throws DataAccessException {
        UserData user;
        String insertUserStatement = "INSERT INTO users (username, password, email) VALUES (?, ?, ?)";
        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement(insertUserStatement, Statement.RETURN_GENERATED_KEYS)) {
                preparedStatement.setString(1, "Mike");
                preparedStatement.setString(2, "password");
                preparedStatement.setString(3, "mike@chess.com");

                preparedStatement.executeUpdate();

                UserDatabase users = new UserDatabase();
                user = users.getUser( "Mike");
            }
        }catch (SQLException e) {
            throw new DataAccessException("failed to connect");
        }
        assertEquals("Mike", user.username());
    }

    @Test
    @DisplayName("Get User - username doesn't exist")
    void getNonExistentUser() throws DataAccessException {
        UserData user;
        String insertUserStatement = "INSERT INTO users (username, password, email) VALUES (?, ?, ?)";
        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement(insertUserStatement, Statement.RETURN_GENERATED_KEYS)) {
                preparedStatement.setString(1, "Mike");
                preparedStatement.setString(2, "password");
                preparedStatement.setString(3, "mike@chess.com");

                preparedStatement.executeUpdate();

                UserDatabase users = new UserDatabase();
                user = users.getUser("username");
            }
        }catch (SQLException e) {
            throw new DataAccessException("failed to connect");
        }
        assertNull(user);
    }

    @Test
    @DisplayName("Create User - Successful")
    void createNewUser() throws DataAccessException {
        UserDatabase users = new UserDatabase();
        UserData user = new UserData("newUser", "password", "newUser@chess.com");
        users.createUser(user);
        UserData retrievedUser = users.getUser("newUser");
        assertEquals("newUser", retrievedUser.username());
    }

    @Test
    @DisplayName("Create User - Username taken")
    void createDuplicateUser() {
        UserDatabase users = new UserDatabase();
        UserData user = new UserData("newUser", "password", "newUser@chess.com");
        assertThrows(DataAccessException.class, () -> users.createUser(user));
    }

    @Test
    @DisplayName("Clear - Successful")
    void clearAllUserData() throws DataAccessException {
        UserDatabase users = new UserDatabase();
        UserData user = new UserData("testUser", "testPassword", "testEmail");
        users.createUser(user);
        users.clear();
        int numberOfUsers = countRowsInTable("users");
        assertEquals(0, numberOfUsers);
    }
}
