package dataaccess;

import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.*;

public class UserDatabaseTests {

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

    @Test
    @DisplayName("Get Authentication - Successful")
    void getExistingAuth() throws DataAccessException {
        AuthDatabase authentications = new AuthDatabase();
        AuthData authData1 = new AuthData("authtoken1", "testUser1");
        AuthData authData2 = new AuthData("authtoken2", "testUser2");
        authentications.createAuth(authData1);
        authentications.createAuth(authData2);
        authData2 = authentications.getAuth("authtoken1");
        authData1 = authentications.getAuth("authtoken2");
        assertEquals("testUser1", authData2.username());
        assertEquals("testUser2", authData1.username());
    }

    @Test
    @DisplayName("Get Authentication - Bad Authtoken")
    void getAuthWrongToken() throws DataAccessException {
        AuthData authData;
        AuthDatabase authentications = new AuthDatabase();
        authData = authentications.getAuth("authtoken");
        assertNull(authData);
    }

    @Test
    @DisplayName("Create Authentication - Successful")
    void createNewAuth() throws DataAccessException {
        AuthDatabase authentications = new AuthDatabase();
        AuthData authData1 = new AuthData("testToken", "testUser24");
        authentications.createAuth(authData1);
        authData1 = authentications.getAuth("testToken");
        assertEquals("testUser24", authData1.username());
    }

    @Test
    @DisplayName("Create Authentication - Null token")
    void createAuthNullToken() throws DataAccessException {
        AuthDatabase authentications = new AuthDatabase();
        AuthData authData = new AuthData(null, "testUser24");
        assertThrows(DataAccessException.class, () -> authentications.createAuth(authData));
    }

    @Test
    @DisplayName("Delete Authentication - Successful")
    void deleteAuth() throws DataAccessException {
        AuthDatabase authentications = new AuthDatabase();
        AuthData authData = new AuthData("testToken", "user");
        authentications.createAuth(authData);
        int beforeDelete = countRowsInTable("authentications");
        authentications.deleteAuth("testToken");
        int afterDelete = countRowsInTable("authentications");
        assertEquals(afterDelete, beforeDelete - 1);
    }

    @Test
    @DisplayName("Clear Auth Table - Successful")
    void clearAllAuthData() throws DataAccessException {
        AuthDatabase authentications = new AuthDatabase();
        AuthData authData = new AuthData("testToken", "testUser");
        authentications.createAuth(authData);
        authentications.clear();
        int numberOfUsers = countRowsInTable("authentications");
        assertEquals(0, numberOfUsers);
    }

    private int countRowsInTable(String database) throws DataAccessException {
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
