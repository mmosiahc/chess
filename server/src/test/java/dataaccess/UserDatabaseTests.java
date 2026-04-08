package dataaccess;

import exceptions.AlreadyTakenException;
import exceptions.DataAccessException;
import model.UserData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class UserDatabaseTests extends BaseDatabaseTest {
    private final UserDatabase users = new UserDatabase();

    public UserDatabaseTests() throws DataAccessException {
    }

    @AfterEach
    void tearDown() throws DataAccessException {
        users.clear();
    }

    @Test
    @DisplayName("Get User - Successful")
    void getExistingUser() throws DataAccessException {
        UserData user = new UserData("Mike", "password", "mike@chess.com");
        users.createUser(user);
        user = users.getUser("Mike");
        assertEquals("Mike", user.username());
        assertEquals("mike@chess.com", user.email());
    }

    @Test
    @DisplayName("Get User - username doesn't exist")
    void getNonExistentUser() throws DataAccessException {
        UserData user = new UserData("newMike", "password", "mike@chess.com");
        users.createUser(user);
        user = users.getUser("unknownUser");
        assertNull(user);
    }

    @Test
    @DisplayName("Create User - Successful")
    void createNewUser() throws DataAccessException {
        UserData user = new UserData("newUser", "password", "newUser@chess.com");
        users.createUser(user);
        UserData retrievedUser = users.getUser("newUser");
        assertEquals("newUser", retrievedUser.username());
    }

    @Test
    @DisplayName("Create User - Username taken")
    void createDuplicateUser() throws DataAccessException{
        UserDatabase users = new UserDatabase();
        UserData user = new UserData("newUser", "password", "newUser@chess.com");
        users.createUser(user);
        assertThrows(AlreadyTakenException.class, () -> users.createUser(user));
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
