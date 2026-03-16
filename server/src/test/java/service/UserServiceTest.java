package service;

import dataaccess.DataAccessException;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryUserDAO;
import dataaccess.UnauthorizedException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {
    private UserService service;

    @BeforeEach
    void setup() {
        MemoryUserDAO userDAO = new MemoryUserDAO();
        MemoryAuthDAO authDAO = new MemoryAuthDAO();

        service = new UserService(userDAO, authDAO);
    }

    @Test
    @DisplayName("Registration Successful")
    void registerNewUser() throws DataAccessException {
        RegisterRequest request = new RegisterRequest("User", "password", "User@chess.com");
        RegisterResult result = service.register(request);
        assertNotNull(result.authToken());
        assertEquals("Michael", result.username());
    }

    @Test
    @DisplayName("Registration - User Already Exists")
    void registerFail() throws DataAccessException {
        RegisterRequest request = new RegisterRequest("User", "password", "User@chess.com");
        service.register(request);
        assertThrows(DataAccessException.class, () -> service.register(request));
    }

    @Test
    @DisplayName("Login Successful")
    void loginUser() throws DataAccessException {
        RegisterRequest registerRequest = new RegisterRequest("User", "password", "User@chess.com");
        service.register(registerRequest);
        LoginRequest loginRequest = new LoginRequest("User", "password");
        LoginResult loginResult = service.login(loginRequest);
        assertNotNull(loginResult.authToken());
        assertEquals("User", loginResult.username());
    }

    @Test
    @DisplayName("Login - Missing Information")
    void loginFailNoPassword() throws DataAccessException {
        RegisterRequest registerRequest = new RegisterRequest("User", "password", "User@chess.com");
        service.register(registerRequest);
        LoginRequest loginRequest = new LoginRequest("User", null);
        assertThrows(DataAccessException.class, () -> service.login(loginRequest));
    }

    @Test
    @DisplayName("Login - Bad Password")
    void loginFailBadPassword() throws DataAccessException {
        RegisterRequest registerRequest = new RegisterRequest("User", "password", "User@chess.com");
        service.register(registerRequest);
        LoginRequest loginRequest = new LoginRequest("User", "badPassword");
        assertThrows(DataAccessException.class, () -> service.login(loginRequest));
    }

    @Test
    @DisplayName("Logout Successful")
    void logoutUser() throws DataAccessException {
        RegisterRequest registerRequest = new RegisterRequest("User", "password", "User@chess.com");
        service.register(registerRequest);
        LoginRequest loginRequest = new LoginRequest("User", "password");
        LoginResult loginResult = service.login(loginRequest);
        LogoutRequest logoutRequest = new LogoutRequest(loginResult.authToken());
        assertEquals(2, service.getAuthentications().size());
        service.logout(logoutRequest);
        assertEquals(1, service.getAuthentications().size());
    }

    @Test
    @DisplayName("Logout - Bad Authtoken")
    void logoutFailBadAuthtoken() throws DataAccessException {
        RegisterRequest registerRequest = new RegisterRequest("User", "password", "User@chess.com");
        service.register(registerRequest);
        LogoutRequest logoutRequest = new LogoutRequest("badAuthtoken");
        assertEquals(1, service.getAuthentications().size());
        assertThrows(UnauthorizedException.class, () -> service.logout(logoutRequest));
    }
}