package service;

import chess.ChessGame;
import dataaccess.*;
import model.AuthData;
import model.GameData;

import java.util.Collection;
import java.util.Map;


public class GameService {
    private final MemoryGameDAO gameMemory;
    private final MemoryAuthDAO authMemory;
    int id = 100;

    public GameService(MemoryGameDAO gameMemory, MemoryAuthDAO authMemory) {
        this.gameMemory = gameMemory;
        this.authMemory = authMemory;
    }

    public int generateID() {
        return id++;
    }

    public Collection<ListGamesResult> listGames(ListGamesRequest listGamesRequest) throws DataAccessException {
        String token = listGamesRequest.authToken();
        if (token == null) {
            throw new BadRequestException();
        }
        authMemory.getAuth(token);
        var games = gameMemory.listGames();
        return games.stream()
                .map(g -> new ListGamesResult(g.gameID(), g.whiteUsername(), g.blackUsername(), g.gameName()))
                .toList();
    }

    public CreateGameResult createGame(CreateGameRequest createGameRequest) throws DataAccessException {
        String token = createGameRequest.authToken();
        String name = createGameRequest.gameName();

        if (name == null || token == null) {
            throw new BadRequestException();
        }
        authMemory.getAuth(token);
        GameData game = new GameData(generateID(), null, null, name, new ChessGame());
        gameMemory.createGame(game);
        return new CreateGameResult(game.gameID());
    }


    public void joinGame(JoinGameRequest joinGameRequest) throws DataAccessException {
        String token = joinGameRequest.authToken();
        ChessGame.TeamColor color = joinGameRequest.playerColor();
        int id = joinGameRequest.gameID();

        //Check for missing information
        if (token == null || color == null || id < 100) {
            throw new BadRequestException();
        }

        //Validate authToken and grab username from associated AuthData
        AuthData auth = authMemory.getAuth(token);
        String username = auth.username();

        //Get requested GameData by id and determine status of teams
        GameData gameData = gameMemory.getGame(id);

        //Check if requested team is taken and assign username to appropriate team if not
        if(isTeamTaken(color, gameData)) {throw new AlreadyTakenException();}
        switch (color) {
            case WHITE -> gameData = new GameData(id, username, gameData.blackUsername(), gameData.gameName(), gameData.game());
            case BLACK -> gameData = new GameData(id, gameData.whiteUsername(), username, gameData.gameName(), gameData.game());
        }
        gameMemory.updateGame(gameData);
    }

    public boolean isTeamTaken (ChessGame.TeamColor color, GameData gameData) {
        String wUser = gameData.whiteUsername();
        String bUser = gameData.blackUsername();
        return color.equals(ChessGame.TeamColor.WHITE) && wUser != null || color.equals(ChessGame.TeamColor.BLACK) && bUser != null;
    }
    public Map<Integer, GameData> getGames() {
        return gameMemory.getGames();
    }

    public Map<String, AuthData> getAuthentications() {
        return authMemory.getAuthentications();
    }
}
