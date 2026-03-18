package service;

import chess.ChessGame;
import dataaccess.DataAccessException;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryGameDAO;
import model.AuthData;
import model.GameData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class GameServiceTest {
    private MemoryGameDAO gameDAO = new MemoryGameDAO();
    private MemoryAuthDAO authDAO = new MemoryAuthDAO();
    private GameService service = new GameService(gameDAO, authDAO);

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

    @AfterEach
    void tearDown() {
        gameDAO.clear();
        authDAO.clear();
    }
//
//    @Test
//    @DisplayName("Registration - User Already Exists")
//    void registerFail() throws DataAccessException {
//        RegisterRequest request = new RegisterRequest("User", "password", "User@chess.com");
//        service.register(request);
//        assertThrows(DataAccessException.class, () -> service.register(request));
//    }
//
//    @Test
//    @DisplayName("Login Successful")
//    void loginUser() throws DataAccessException {
//        RegisterRequest registerRequest = new RegisterRequest("User", "password", "User@chess.com");
//        service.register(registerRequest);
//        LoginRequest loginRequest = new LoginRequest("User", "password");
//        LoginResult loginResult = service.login(loginRequest);
//        assertNotNull(loginResult.authToken());
//        assertEquals("User", loginResult.username());
//    }
//
//    @Test
//    @DisplayName("Login - Missing Information")
//    void loginFailNoPassword() throws DataAccessException {
//        RegisterRequest registerRequest = new RegisterRequest("User", "password", "User@chess.com");
//        service.register(registerRequest);
//        LoginRequest loginRequest = new LoginRequest("User", null);
//        assertThrows(DataAccessException.class, () -> service.login(loginRequest));
//    }
//
//    @Test
//    @DisplayName("Login - Bad Password")
//    void loginFailBadPassword() throws DataAccessException {
//        RegisterRequest registerRequest = new RegisterRequest("User", "password", "User@chess.com");
//        service.register(registerRequest);
//        LoginRequest loginRequest = new LoginRequest("User", "badPassword");
//        assertThrows(DataAccessException.class, () -> service.login(loginRequest));
//    }
//
//    @Test
//    @DisplayName("Logout Successful")
//    void logoutUser() throws DataAccessException {
//        RegisterRequest registerRequest = new RegisterRequest("User", "password", "User@chess.com");
//        service.register(registerRequest);
//        LoginRequest loginRequest = new LoginRequest("User", "password");
//        LoginResult loginResult = service.login(loginRequest);
//        LogoutRequest logoutRequest = new LogoutRequest(loginResult.authToken());
//        assertEquals(2, service.getAuthentications().size());
//        service.logout(logoutRequest);
//        assertEquals(1, service.getAuthentications().size());
//    }
//
//    @Test
//    @DisplayName("Logout - Bad Authtoken")
//    void logoutFailBadAuthtoken() throws DataAccessException {
//        RegisterRequest registerRequest = new RegisterRequest("User", "password", "User@chess.com");
//        service.register(registerRequest);
//        LogoutRequest logoutRequest = new LogoutRequest("badAuthtoken");
//        assertEquals(1, service.getAuthentications().size());
//        assertThrows(UnauthorizedException.class, () -> service.logout(logoutRequest));
//    }
}