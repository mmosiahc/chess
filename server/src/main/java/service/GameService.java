package service;

import dataaccess.BadRequestException;
import dataaccess.DataAccessException;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryGameDAO;
import model.GameData;

import java.util.Collection;
import java.util.Map;


public class GameService {
    private final MemoryGameDAO gameMemory;
    private final MemoryAuthDAO authMemory;
    int id = 0;

    public GameService(MemoryGameDAO gameMemory, MemoryAuthDAO authMemory) {
        this.gameMemory = gameMemory;
        this.authMemory = authMemory;
    }

    public int generateID() {
        return id++;
    }

    public Collection<ListGamesResult> listGames(ListGamesRequest listGamesRequest) throws DataAccessException {
        String token = listGamesRequest.authToken();

        if(token == null) {
            throw new BadRequestException();
        }
        authMemory.getAuth(token);
        var games = gameMemory.listGames();
        return games.stream()
                .map(g -> new ListGamesResult(g.gameID(), g.wUsername(), g.bUsername(), g.gameName()))
                .toList();
    }

    public Map<Integer, GameData> getGames() {
        return gameMemory.getGames();
    }
//
//    public LoginResult login(LoginRequest loginRequest) throws DataAccessException {
//        String username = loginRequest.username();
//        String password = loginRequest.password();
//
//        if(username == null || password == null) {
//            throw new BadRequestException();
//        }
//        UserData user;
//        user = userMemory.getUser(username);
//        if(!user.password().equals(password)) throw new UnauthorizedException();
//
//        AuthData auth = new AuthData(generateToken(), username);
//        authMemory.createAuth(auth);
//        return new LoginResult(username, auth.authToken());
//    }
//
//    public void logout(LogoutRequest logoutRequest) throws DataAccessException {
//        String authToken = logoutRequest.authToken();
//
//        if (authToken == null) {
//            throw new BadRequestException();
//        }
//        authMemory.deleteAuth(authToken);
//    }
//
//    public Map<String, UserData> getUsers() {return userMemory.getUsers();}
//    public Map<String, AuthData> getAuthentications() {return authMemory.getAuthentications();}
}
