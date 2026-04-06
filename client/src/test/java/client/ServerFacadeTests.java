package client;

import dataaccess.AlreadyTakenException;
import dataaccess.BadRequestException;
import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;
import org.junit.jupiter.api.*;
import server.Server;
import service.LoginRequest;
import service.LoginResult;
import service.RegisterRequest;
import service.RegisterResult;


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
}
