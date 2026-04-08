package service;

import exceptions.AlreadyTakenException;
import exceptions.BadRequestException;
import exceptions.DataAccessException;
import exceptions.UnauthorizedException;
import chess.ChessGame;
import dataaccess.*;
import model.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

class GameServiceTest extends BaseDatabaseTest {
    private final AuthDatabase authentications = new AuthDatabase();
    private final UserDatabase users = new UserDatabase();
    private final GameDatabase games = new GameDatabase();
    private final GameService service = new GameService(games, authentications);

    GameServiceTest() throws DataAccessException {
    }

    @Test
    @DisplayName("List Games Successful")
    void listGames() throws DataAccessException {
        GameData game1 = new GameData(service.generateID(), "white", "black", "normal", new ChessGame());
        GameData game2 = new GameData(service.generateID(), "will", "Blake", "speed", new ChessGame());
        GameData game3 = new GameData(service.generateID(), "wes", "bob", "test", new ChessGame());
        games.createGame(game1);
        games.createGame(game2);
        games.createGame(game3);
        AuthData auth = new AuthData("authToken", "username");
        authentications.createAuth(auth);
        ListGamesRequest request = new ListGamesRequest("authToken");
        Collection<ListGamesResult> results = service.listGames(request);
        assertNotNull(results);
        assertEquals(3, results.size());
    }

    @Test
    @DisplayName("List Games - Unauthorized")
    void listGamesBadAuth() throws DataAccessException {
        GameData game1 = new GameData(service.generateID(), "white", "black", "normal", new ChessGame());
        GameData game2 = new GameData(service.generateID(), "will", "Blake", "speed", new ChessGame());
        GameData game3 = new GameData(service.generateID(), "wes", "bob", "test", new ChessGame());
        games.createGame(game1);
        games.createGame(game2);
        games.createGame(game3);
        ListGamesRequest request = new ListGamesRequest("badAuthToken");
        assertThrows(UnauthorizedException.class, () -> service.listGames(request));
    }

    @Test
    @DisplayName("Create Game Successful")
    void createNewGame() throws DataAccessException {
        AuthData auth = new AuthData("authToken", "username");
        authentications.createAuth(auth);
        CreateGameRequest request = new CreateGameRequest("authToken", "testGame");
        CreateGameResult result = service.createGame(request);
        assertNotNull(result);
        int numberOfGames = countRowsInTable("games");
        assertEquals(1, numberOfGames);
        assertEquals(1, result.gameID());
    }

    @Test
    @DisplayName("Create Game - Bad Request")
    void createGameMissingName() throws DataAccessException {
        AuthData auth = new AuthData("authToken", "username");
        authentications.createAuth(auth);
        CreateGameRequest request = new CreateGameRequest("authToken", null);
        assertThrows(BadRequestException.class, () -> service.createGame(request));
    }

    @Test
    @DisplayName("Create Game - Unauthorized")
    void createGameBadToken() throws DataAccessException {
        AuthData auth = new AuthData("authToken", "username");
        authentications.createAuth(auth);
        CreateGameRequest request = new CreateGameRequest("badAuthToken", "test");
        assertThrows(UnauthorizedException.class, () -> service.createGame(request));
    }

    @Test
    @DisplayName("Join Game Successful")
    void joinGameSuccess() throws DataAccessException {
        UserData user = new UserData("username", "password", "email");
        users.createUser(user);
        AuthData auth = new AuthData("authToken", "username");
        authentications.createAuth(auth);
        CreateGameRequest createGameRequest = new CreateGameRequest("authToken", "test");
        CreateGameResult result = service.createGame(createGameRequest);
        JoinGameRequest joinGameRequest = new JoinGameRequest("authToken", ChessGame.TeamColor.WHITE, result.gameID());
        service.joinGame(joinGameRequest);
        GameData gameData = games.getGame(result.gameID());
        assertEquals("username", gameData.whiteUsername());
    }

    @Test
    @DisplayName("Join Game - Missing Info")
    void joinGameMissingTeam() throws DataAccessException {
        UserData user = new UserData("username", "password", "email");
        users.createUser(user);
        AuthData auth = new AuthData("authToken", "username");
        authentications.createAuth(auth);
        CreateGameRequest createGameRequest = new CreateGameRequest("authToken", "test");
        CreateGameResult result = service.createGame(createGameRequest);
        JoinGameRequest joinGameRequest = new JoinGameRequest("authToken", null, result.gameID());
        assertThrows(BadRequestException.class, () -> service.joinGame(joinGameRequest));
    }

    @Test
    @DisplayName("Join Game - Wrong ID")
    void joinGameWrongID() throws DataAccessException {
        UserData user = new UserData("username", "password", "email");
        users.createUser(user);
        AuthData auth = new AuthData("authToken", "username");
        authentications.createAuth(auth);
        CreateGameRequest createGameRequest = new CreateGameRequest("authToken", "test");
        service.createGame(createGameRequest);
        JoinGameRequest joinGameRequest = new JoinGameRequest("authToken", ChessGame.TeamColor.WHITE, 101);
        assertThrows(BadRequestException.class, () -> service.joinGame(joinGameRequest));
    }

    @Test
    @DisplayName("Join Game - Bad Authorization")
    void joinGameBadToken() throws DataAccessException {
        UserData user = new UserData("username", "password", "email");
        users.createUser(user);
        AuthData auth = new AuthData("authToken", "username");
        authentications.createAuth(auth);
        CreateGameRequest createGameRequest = new CreateGameRequest("authToken", "test");
        CreateGameResult result = service.createGame(createGameRequest);
        JoinGameRequest joinGameRequest = new JoinGameRequest("badAuthToken", ChessGame.TeamColor.WHITE, result.gameID());
        assertThrows(UnauthorizedException.class, () -> service.joinGame(joinGameRequest));
    }

    @Test
    @DisplayName("Join Game - Team taken")
    void joinGameTeamTaken() throws DataAccessException {
        UserData user = new UserData("username", "password", "email");
        users.createUser(user);
        AuthData auth = new AuthData("authToken", "username");
        authentications.createAuth(auth);
        GameData gameData = new GameData(0, "name", null, "test", new ChessGame());
        int gameID = games.createGame(gameData);
        JoinGameRequest joinGameRequest = new JoinGameRequest("authToken", ChessGame.TeamColor.WHITE, gameID);
        assertThrows(AlreadyTakenException.class, () -> service.joinGame(joinGameRequest));
    }

    @AfterEach
    void tearDown() throws DataAccessException {
        games.clear();
        authentications.clear();
        users.clear();
    }
}