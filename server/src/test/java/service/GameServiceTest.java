package service;

import chess.ChessGame;
import dataaccess.*;
import model.AuthData;
import model.GameData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

class GameServiceTest {
    private final MemoryGameDAO gameDAO = new MemoryGameDAO();
    private final MemoryAuthDAO authDAO = new MemoryAuthDAO();
    private final GameService service = new GameService(gameDAO, authDAO);

    @Test
    @DisplayName("List Games Successful")
    void listGames() throws DataAccessException {
        GameData game1 = new GameData(service.generateID(), "white", "black", "normal", new ChessGame());
        GameData game2 = new GameData(service.generateID(), "will", "blake", "speed", new ChessGame());
        GameData game3 = new GameData(service.generateID(), "wes", "bob", "test", new ChessGame());
        service.getGames().put(0, game1);
        service.getGames().put(1, game2);
        service.getGames().put(2, game3);
        AuthData auth = new AuthData("authToken", "username");
        authDAO.createAuth(auth);
        ListGamesRequest listGamesRequest = new ListGamesRequest("authToken");
        Collection<ListGamesResult> results = service.listGames(listGamesRequest);
        assertNotNull(results);
        assertEquals(3, results.size());
//        System.out.println(results);
    }

    @Test
    @DisplayName("List Games - Unauthorized")
    void listGamesBadAuth() {
        GameData game1 = new GameData(service.generateID(), "white", "black", "normal", new ChessGame());
        GameData game2 = new GameData(service.generateID(), "will", "blake", "speed", new ChessGame());
        GameData game3 = new GameData(service.generateID(), "wes", "bob", "test", new ChessGame());
        service.getGames().put(0, game1);
        service.getGames().put(1, game2);
        service.getGames().put(2, game3);
        ListGamesRequest request = new ListGamesRequest("authToken");
        assertThrows(UnauthorizedException.class, () -> service.listGames(request));
    }

    @Test
    @DisplayName("Create Game Successful")
    void createNewGame() throws DataAccessException {
        AuthData auth = new AuthData("authToken", "username");
        authDAO.createAuth(auth);
        CreateGameRequest request = new CreateGameRequest("authToken", "testGame");
        CreateGameResult result = service.createGame(request);
        assertNotNull(result);
        assertEquals(1, service.getGames().size());
        assertEquals(0, result.gameID());
    }

    @Test
    @DisplayName("Create Game - Bad Request")
    void createGameBadRequest() {
        AuthData auth = new AuthData("authToken", "username");
        authDAO.createAuth(auth);
        CreateGameRequest request = new CreateGameRequest("authToken", null);
        assertThrows(BadRequestException.class, () -> service.createGame(request));
    }

    @Test
    @DisplayName("Create Game - Unauthorized")
    void createGameBadAuthtoken() {
        AuthData auth = new AuthData("authToken", "username");
        authDAO.createAuth(auth);
        CreateGameRequest request = new CreateGameRequest("badAuthtoken", "test");
        assertThrows(UnauthorizedException.class, () -> service.createGame(request));
    }

    @AfterEach
    void tearDown() {
        gameDAO.clear();
        authDAO.clear();
    }
}