package service;

import data_transfer.*;
import exceptions.AlreadyTakenException;
import exceptions.BadRequestException;
import exceptions.DataAccessException;
import exceptions.UnauthorizedException;
import chess.ChessGame;
import dataaccess.*;
import model.*;

import java.util.Collection;
import java.util.Map;


public class GameService {
    private final GameDatabase games;
    private final AuthDatabase authentications;
    int id = 100;

    public GameService(GameDatabase games, AuthDatabase authentications) {
        this.games = games;
        this.authentications = authentications;
    }

    public int generateID() {
        return id++;
    }

    public Collection<ListGamesResult> listGames(ListGamesRequest listGamesRequest) throws DataAccessException {
        String token = listGamesRequest.authToken();
        if (token == null) {
            throw new BadRequestException();
        }
        AuthData authData = authentications.getAuth(token);
        if(authData == null) {throw new UnauthorizedException();}
        var gamesList = games.listGames();
        return gamesList.stream()
                .map(g -> new ListGamesResult(g.gameID(), g.whiteUsername(), g.blackUsername(), g.gameName()))
                .toList();
    }

    public CreateGameResult createGame(CreateGameRequest createGameRequest) throws DataAccessException {
        String token = createGameRequest.authToken();
        String name = createGameRequest.gameName();

        if (name == null || token == null) {
            throw new BadRequestException();
        }
        AuthData authData = authentications.getAuth(token);
        if(authData == null) {throw new UnauthorizedException();}

        GameData game = new GameData(0, null, null, name, new ChessGame());
        int gameID = games.createGame(game);
        return new CreateGameResult(gameID);
    }


    public void joinGame(JoinGameRequest joinGameRequest) throws DataAccessException {
        String token = joinGameRequest.authToken();
        ChessGame.TeamColor color = joinGameRequest.playerColor();
        int id = joinGameRequest.gameID();

        //Check for missing information
        if (token == null || color == null || id <= 0) {
            throw new BadRequestException();
        }

        //Validate authToken and grab username from associated AuthData
        AuthData authData = authentications.getAuth(token);
        if(authData == null) {throw new UnauthorizedException();}
        String username = authData.username();

        //Get requested GameData by id and determine status of teams
        GameData gameData = games.getGame(id);
        if(gameData == null) {throw new BadRequestException();}

        //Check if requested team is taken and assign username to appropriate team if not
        if(isTeamTaken(color, gameData)) {throw new AlreadyTakenException();}
        switch (color) {
            case WHITE -> gameData = new GameData(id, username, gameData.blackUsername(), gameData.gameName(), gameData.game());
            case BLACK -> gameData = new GameData(id, gameData.whiteUsername(), username, gameData.gameName(), gameData.game());
        }
        games.updateGame(gameData);
    }

    public boolean isTeamTaken (ChessGame.TeamColor color, GameData gameData) {
        String wUser = gameData.whiteUsername();
        String bUser = gameData.blackUsername();
        return color.equals(ChessGame.TeamColor.WHITE) && wUser != null || color.equals(ChessGame.TeamColor.BLACK) && bUser != null;
    }
    public Map<Integer, GameData> getGames() {
        return null;
    }
}
