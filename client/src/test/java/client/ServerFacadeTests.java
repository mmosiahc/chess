package client;

import chess.ChessGame;
import data_transfer.*;
import dataaccess.DatabaseManager;
import exceptions.AlreadyTakenException;
import exceptions.BadRequestException;
import exceptions.DataAccessException;
import exceptions.UnauthorizedException;
import model.GameData;
import org.junit.jupiter.api.*;
import server.Server;

import java.util.Collection;
import java.util.Map;


public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade serverFacade;
    private static ServerMessageObserver observer;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        var url = "http://localhost:" + port;
        serverFacade = new ServerFacade(url, observer);
    }

    @BeforeEach
    public void wipeDatabase() throws DataAccessException {
        DatabaseManager.clearDatabase();
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }


    @Test
    @DisplayName("Register - Success")
    public void registerNewUser() throws Exception {
        RegisterRequest request = new RegisterRequest("testName", "testPassword", "testEmail");
        RegisterResult result = serverFacade.register(request);
        Assertions.assertNotNull(result);
        Assertions.assertEquals("testName", result.username());
        Assertions.assertNotNull(result.authToken());
    }

    @Test
    @DisplayName("Register - Missing Email")
    public void registerNoEmail() {
        RegisterRequest request = new RegisterRequest("testName", "testPassword", null);
        Assertions.assertThrows(BadRequestException.class, () -> serverFacade.register(request));
    }


    @Test
    @DisplayName("Register - Register Existing User")
    public void registerUserAlreadyExists() throws Exception {
        RegisterRequest request = new RegisterRequest("testName", "testPassword", "testEmail");
        serverFacade.register(request);
        Assertions.assertThrows(AlreadyTakenException.class, () -> serverFacade.register(request));
    }

    @Test
    @DisplayName("Login - Success")
    public void loginUser() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest("testName", "testPassword", "testEmail");
        serverFacade.register(registerRequest);
        LoginRequest loginRequest = new LoginRequest("testName", "testPassword");
        LoginResult loginResult = serverFacade.login(loginRequest);
        Assertions.assertNotNull(loginResult);
        Assertions.assertEquals("testName", loginResult.username());
        Assertions.assertNotNull(loginResult.authToken());
    }

    @Test
    @DisplayName("Login - No Password")
    public void loginUserMissingPassword() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest("testName", "testPassword", "testEmail");
        serverFacade.register(registerRequest);
        LoginRequest loginRequest = new LoginRequest("testName", null);
        Assertions.assertThrows(BadRequestException.class, () -> serverFacade.login(loginRequest));
    }

    @Test
    @DisplayName("Login - Bad Password")
    public void loginUserBadPassword() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest("testName", "testPassword", "testEmail");
        serverFacade.register(registerRequest);
        LoginRequest loginRequest = new LoginRequest("testName", "badPassword");
        Assertions.assertThrows(UnauthorizedException.class, () -> serverFacade.login(loginRequest));
    }

    @Test
    @DisplayName("Login - Unregistered")
    public void loginUserNoCredentials() {
        LoginRequest loginRequest = new LoginRequest("badName", "testPassword");
        Assertions.assertThrows(UnauthorizedException.class, () -> serverFacade.login(loginRequest));
    }

    @Test
    @DisplayName("Logout - Success")
    public void logoutUser() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest("testName", "testPassword", "testEmail");
        serverFacade.register(registerRequest);
        serverFacade.logout();
        Assertions.assertThrows(UnauthorizedException.class, () -> serverFacade.createGame("testGame"));
    }

    @Test
    @DisplayName("Logout - No token")
    public void logoutUserUnauthorized() {
        Assertions.assertThrows(UnauthorizedException.class, () -> serverFacade.logout());
    }

    @Test
    @DisplayName("Create Game - Success")
    public void createNewGame() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest("testName", "testPassword", "testEmail");
        serverFacade.register(registerRequest);
        CreateGameResult createGameResult = serverFacade.createGame("testGame");
        Assertions.assertNotNull(createGameResult);
        Assertions.assertEquals(1, createGameResult.gameID());
    }

    @Test
    @DisplayName("Create Game - Missing Game Name")
    public void createGameNoName() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest("testName", "testPassword", "testEmail");
        serverFacade.register(registerRequest);
        Assertions.assertThrows(BadRequestException.class, () -> serverFacade.createGame(null));
    }

    @Test
    @DisplayName("List Games - Success")
    public void listAllGames() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest("testName", "testPassword", "testEmail");
        serverFacade.register(registerRequest);
        serverFacade.createGame("testGame");
        Map<String, Collection<GameData>> games = serverFacade.listGames();
        Assertions.assertNotNull(games);
        Collection<GameData> gamesList = games.get("games");
        GameData game = gamesList.stream()
                .filter(g -> g.gameName().equals("testGame"))
                        .findFirst()
                                .orElse(null);
        Assertions.assertEquals("testGame", game.gameName());
    }

    @Test
    @DisplayName("List Games - Unauthorized")
    public void listAllGamesBadToken() {
        serverFacade.setToken("badToken");
        Assertions.assertThrows(UnauthorizedException.class, () -> serverFacade.listGames());
    }

    @Test
    @DisplayName("Join Game - Success")
    public void joinGameAsWhite() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest("testName", "testPassword", "testEmail");
        serverFacade.register(registerRequest);
        serverFacade.createGame("testGame");
        JoinGameBody joinGameBody = new JoinGameBody(ChessGame.TeamColor.WHITE, 1);
        serverFacade.joinGame(joinGameBody);
        Map<String, Collection<GameData>> games = serverFacade.listGames();
        Collection<GameData> gamesList = games.get("games");
        GameData game = gamesList.stream()
                .filter(g -> g.gameName().equals("testGame"))
                .findFirst()
                .orElse(null);
        Assertions.assertEquals("testName", game.whiteUsername());
    }

    @Test
    @DisplayName("Join Game - Occupied")
    public void joinGameWhiteTaken() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest("testName", "testPassword", "testEmail");
        serverFacade.register(registerRequest);
        serverFacade.createGame("testGame");
        JoinGameBody joinGameBody = new JoinGameBody(ChessGame.TeamColor.WHITE, 1);
        serverFacade.joinGame(joinGameBody);
        Assertions.assertThrows(AlreadyTakenException.class, () -> serverFacade.joinGame(joinGameBody));
    }

    @Test
    @DisplayName("Join Game - No Team")
    public void joinGameMissingInfo() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest("testName", "testPassword", "testEmail");
        serverFacade.register(registerRequest);
        serverFacade.createGame("testGame");
        Map<String, Collection<GameData>> games = serverFacade.listGames();
        JoinGameBody joinGameBody = new JoinGameBody(null, 1);
        Assertions.assertThrows(BadRequestException.class, () -> serverFacade.joinGame(joinGameBody));
    }

    @Test
    @DisplayName("Clear - Success")
    public void clear() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest("testName", "testPassword", "testEmail");
        serverFacade.register(registerRequest);
        serverFacade.createGame("testGame");
        Map<String, Collection<GameData>> games = serverFacade.listGames();
        Assertions.assertEquals(1, games.get("games").size());
        serverFacade.clear();
        Assertions.assertThrows(UnauthorizedException.class, () -> serverFacade.listGames());
    }
}
