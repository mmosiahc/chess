package service;

import chess.ChessGame;
import dataaccess.DataAccessException;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryGameDAO;
import dataaccess.MemoryUserDAO;
import model.GameData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ClearServiceTest {
    private ClearService clearService;
    private UserService userService;
    private final MemoryAuthDAO authDAO = new MemoryAuthDAO();
    private final MemoryGameDAO gameDAO = new MemoryGameDAO();
    private final MemoryUserDAO userDAO = new MemoryUserDAO();

    @BeforeEach
    void setup() {
        userService = new UserService(userDAO, authDAO);
        clearService = new ClearService(userDAO, authDAO, gameDAO);

    }

    @Test
    @DisplayName("Clear Successful")
    void clearAllData() throws DataAccessException {
        RegisterRequest request = new RegisterRequest("Michael", "password", "Michael@chess.com");
        userService.register(request);
        ChessGame testGame = new ChessGame();
        GameData gameData = new GameData(1, "jack", "bill", "test", testGame);
        gameDAO.createGame(gameData);
        clearService.clear();
        assertEquals(0, userDAO.getUsers().size());
        assertEquals(0, authDAO.getAuthentications().size());
        assertEquals(0, gameDAO.getGames().size());
    }
}