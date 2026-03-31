package service;

import chess.ChessGame;
import dataaccess.*;
import model.GameData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ClearServiceTest extends BaseDatabaseTest {
    private ClearService clearService;
    private UserService userService;
    private final AuthDatabase authentications = new AuthDatabase();
    private final UserDatabase users = new UserDatabase();
    private final GameDatabase games = new GameDatabase();

    ClearServiceTest() throws DataAccessException {
    }

    @BeforeEach
    void setup() {
        userService = new UserService(users, authentications);
        clearService = new ClearService(users, authentications, games);
    }

    @Test
    @DisplayName("Clear Successful")
    void clearAllData() throws DataAccessException {
        RegisterRequest request = new RegisterRequest("Michael", "password", "Michael@chess.com");
        userService.register(request);
        ChessGame testGame = new ChessGame();
        GameData gameData = new GameData(1, "jack", "bill", "test", testGame);
        games.createGame(gameData);
        clearService.clear();
        int numberOfUsers = countRowsInTable("users");
        int numberOfAuthentications = countRowsInTable("authentications");
        int numberOfGames = countRowsInTable("games");
        assertEquals(0, numberOfGames);
        assertEquals(0, numberOfUsers);
        assertEquals(0, numberOfAuthentications);
    }
}