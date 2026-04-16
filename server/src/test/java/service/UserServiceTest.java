package service;

import datatransfer.*;
import exceptions.AlreadyTakenException;
import exceptions.BadRequestException;
import exceptions.DataAccessException;
import exceptions.UnauthorizedException;
import dataaccess.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest extends BaseDatabaseTest {
    private UserService userService;
    private ClearService clearService;

    @BeforeEach
    void setup() throws DataAccessException {
        AuthDatabase authentications = new AuthDatabase();
        UserDatabase users = new UserDatabase();
        GameDatabase games = new GameDatabase();

        userService = new UserService(users, authentications);
        clearService = new ClearService(users, authentications, games);
    }

    @AfterEach
    void tearDown() throws DataAccessException {
        clearService.clear();
    }

    @Test
    @DisplayName("Registration Successful")
    void registerNewUser() throws DataAccessException {
        RegisterRequest request = new RegisterRequest("User", "password", "User@chess.com");
        RegisterResult result = userService.register(request);
        assertNotNull(result.authToken());
        assertEquals("User", result.username());
    }

    @Test
    @DisplayName("Registration - User Already Exists")
    void registerUserAlreadyExists() throws DataAccessException {
        RegisterRequest request = new RegisterRequest("User", "password", "User@chess.com");
        userService.register(request);
        assertThrows(AlreadyTakenException.class, () -> userService.register(request));
    }

    @Test
    @DisplayName("Registration - Bad Request")
    void registerBadRequest() throws DataAccessException {
        RegisterRequest request = new RegisterRequest("User", "password", null);
        assertThrows(BadRequestException.class, () -> userService.register(request));
    }

    @Test
    @DisplayName("Login Successful")
    void loginUser() throws DataAccessException {
        RegisterRequest registerRequest = new RegisterRequest("User", "password", "User@chess.com");
        userService.register(registerRequest);
        LoginRequest loginRequest = new LoginRequest("User", "password");
        LoginResult loginResult = userService.login(loginRequest);
        assertNotNull(loginResult.authToken());
        assertEquals("User", loginResult.username());
    }

    @Test
    @DisplayName("Login - Missing Information")
    void loginMissingPassword() throws DataAccessException {
        RegisterRequest registerRequest = new RegisterRequest("User", "password", "User@chess.com");
        userService.register(registerRequest);
        LoginRequest loginRequest = new LoginRequest("User", null);
        assertThrows(BadRequestException.class, () -> userService.login(loginRequest));
    }

    @Test
    @DisplayName("Login - Bad Password")
    void loginBadPassword() throws DataAccessException {
        RegisterRequest registerRequest = new RegisterRequest("User", "password", "User@chess.com");
        userService.register(registerRequest);
        LoginRequest loginRequest = new LoginRequest("User", "badPassword");
        assertThrows(UnauthorizedException.class, () -> userService.login(loginRequest));
    }

    @Test
    @DisplayName("Logout Successful")
    void logoutUser() throws DataAccessException {
        RegisterRequest registerRequest = new RegisterRequest("User", "password", "User@chess.com");
        userService.register(registerRequest);
        LoginRequest loginRequest = new LoginRequest("User", "password");
        LoginResult loginResult = userService.login(loginRequest);
        LogoutRequest logoutRequest = new LogoutRequest(loginResult.authToken());
        int beforeLogout = countRowsInTable("authentications");
        assertEquals(2, beforeLogout);
        userService.logout(logoutRequest);
        int afterLogout = countRowsInTable("authentications");
        assertEquals(1, afterLogout);
    }

    @Test
    @DisplayName("Logout - Bad Authtoken")
    void logoutBadAuthtoken() throws DataAccessException {
        RegisterRequest registerRequest = new RegisterRequest("User", "password", "User@chess.com");
        userService.register(registerRequest);
        LogoutRequest logoutRequest = new LogoutRequest("badAuthtoken");
        int numberOfAuths = countRowsInTable("authentications");
        assertEquals(1, numberOfAuths);
        assertThrows(UnauthorizedException.class, () -> userService.logout(logoutRequest));
    }
}