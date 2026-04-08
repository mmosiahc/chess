package dataaccess;

import Exceptions.DataAccessException;
import model.AuthData;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class AuthDatabaseTests extends BaseDatabaseTest {

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
    @DisplayName("Delete Authentication - Empty Table")
    void deleteAuthNoData() throws DataAccessException {
        AuthDatabase authentications = new AuthDatabase();
        authentications.clear();
        authentications.deleteAuth(null);
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
}
