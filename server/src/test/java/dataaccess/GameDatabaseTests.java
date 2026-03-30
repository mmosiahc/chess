package dataaccess;

import chess.ChessGame;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

public class GameDatabaseTests {

    @Test
    @DisplayName("Get Game - Successful")
    void getExistingGame() throws DataAccessException {
        GameDatabase games = new GameDatabase();
        GameData gameData1 = new GameData(4, null, null, "wvb", new ChessGame());
        GameData gameData2 = new GameData(1, "white", "black", "speed", new ChessGame());
        int game1Id = games.createGame(gameData1);
        int game2Id = games.createGame(gameData2);
        gameData2 = games.getGame(game1Id);
        gameData1 = games.getGame(game2Id);
        assertEquals("wvb", gameData2.gameName());
        assertEquals("speed", gameData1.gameName());
    }

    @Test
    @DisplayName("Get Game - ID Doesn't Exist")
    void getNonExistentGame() throws DataAccessException {
        GameDatabase games = new GameDatabase();
        GameData gameData;
        gameData = games.getGame(424);
        assertNull(gameData);
    }

    @Test
    @DisplayName("Create Game - Successful")
    void createNewGame() throws DataAccessException {
        GameDatabase games = new GameDatabase();
        GameData game = new GameData(0, "whiteKnight", "blackKnight", "speed", new ChessGame());
        int beforeInsert = countRowsInTable("games");
        games.createGame(game);
        int afterInsert = countRowsInTable("games");
        assertEquals(afterInsert, beforeInsert + 1);
    }

    @Test
    @DisplayName("Create Game - Missing Name")
    void createGameNullGameName() throws DataAccessException {
        GameDatabase games = new GameDatabase();
        GameData game = new GameData(0, "whiteKnight", "blackKnight", null, new ChessGame());
        assertThrows(DataAccessException.class, () -> games.createGame(game));
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
