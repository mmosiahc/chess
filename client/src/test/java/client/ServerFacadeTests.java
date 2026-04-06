package client;

import dataaccess.*;
import org.junit.jupiter.api.*;
import server.Server;
import service.*;


public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade serverFacade;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        var url = "http://localhost:" + port;
        serverFacade = new ServerFacade(url);
    }

    @BeforeEach
    public void clear() throws DataAccessException {
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
    public void LoginUser() throws Exception {
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
    public void LoginUserMissingPassword() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest("testName", "testPassword", "testEmail");
        serverFacade.register(registerRequest);
        LoginRequest loginRequest = new LoginRequest("testName", null);
        Assertions.assertThrows(BadRequestException.class, () -> serverFacade.login(loginRequest));
    }

    @Test
    @DisplayName("Login - Bad Password")
    public void LoginUserBadPassword() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest("testName", "testPassword", "testEmail");
        serverFacade.register(registerRequest);
        LoginRequest loginRequest = new LoginRequest("testName", "badPassword");
        Assertions.assertThrows(UnauthorizedException.class, () -> serverFacade.login(loginRequest));
    }

    @Test
    @DisplayName("Login - Bad Username")
    public void LoginUserBadUsername() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest("testName", "testPassword", "testEmail");
        serverFacade.register(registerRequest);
        LoginRequest loginRequest = new LoginRequest("badName", "testPassword");
        Assertions.assertThrows(UnauthorizedException.class, () -> serverFacade.login(loginRequest));
    }

    @Test
    @DisplayName("Logout - Success")
    public void LogoutUser() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest("testName", "testPassword", "testEmail");
        RegisterResult result = serverFacade.register(registerRequest);
        serverFacade.logout(result.authToken());
        CreateGameRequest createGameRequest = new CreateGameRequest(result.authToken(), "testGame");
        Assertions.assertThrows(UnauthorizedException.class, () -> serverFacade.createGame(createGameRequest));
    }

    @Test
    @DisplayName("Logout - Bad token")
    public void LogoutUserUnauthorized() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest("testName", "testPassword", "testEmail");
        RegisterResult result = serverFacade.register(registerRequest);
        Assertions.assertThrows(UnauthorizedException.class, () -> serverFacade.logout("badAuth"));
    }

    @Test
    @DisplayName("Create Game - Success")
    public void createNewGame() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest("testName", "testPassword", "testEmail");
        RegisterResult registerResult = serverFacade.register(registerRequest);
        CreateGameRequest createGameRequest = new CreateGameRequest(registerResult.authToken(), "testGame");
        CreateGameResult createGameResult = serverFacade.createGame(createGameRequest);
        Assertions.assertNotNull(createGameResult);
        Assertions.assertEquals(1, createGameResult.gameID());
    }

    @Test
    @DisplayName("Create Game - Missing Game Name")
    public void createGameNoName() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest("testName", "testPassword", "testEmail");
        RegisterResult registerResult = serverFacade.register(registerRequest);
        CreateGameRequest createGameRequest = new CreateGameRequest(registerResult.authToken(), null);
        Assertions.assertThrows(BadRequestException.class, () -> serverFacade.createGame(createGameRequest));
    }
}
