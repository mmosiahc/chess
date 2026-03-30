package dataaccess;

import chess.*;
import model.GameData;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

public class GameDatabaseTests extends BaseDatabaseTest{

    @Test
    @DisplayName("Get Game - Successful")
    void getExistingGame() throws DataAccessException {
        GameDatabase games = new GameDatabase();
        GameData gameData1 = new GameData(4, null, null, "wvb", new ChessGame());
        GameData gameData2 = new GameData(1, "white", "black", "speed", new ChessGame());
        int game1Id = games.createGame(gameData1);
        int game2Id = games.createGame(gameData2);
        gameData2 = games.getGame(game1Id);
        gameData1 = games.getGame(game2Id);
        assertEquals("wvb", gameData2.gameName());
        assertEquals("speed", gameData1.gameName());
    }

    @Test
    @DisplayName("Get Game - ID Doesn't Exist")
    void getNonExistentGame() throws DataAccessException {
        GameDatabase games = new GameDatabase();
        GameData gameData;
        gameData = games.getGame(424);
        assertNull(gameData);
    }

    @Test
    @DisplayName("Create Game - Successful")
    void createNewGame() throws DataAccessException {
        GameDatabase games = new GameDatabase();
        GameData game = new GameData(0, "whiteKnight", "blackKnight", "speed", new ChessGame());
        int beforeInsert = countRowsInTable("games");
        games.createGame(game);
        int afterInsert = countRowsInTable("games");
        assertEquals(afterInsert, beforeInsert + 1);
    }

    @Test
    @DisplayName("Create Game - Missing Name")
    void createGameNullGameName() throws DataAccessException {
        GameDatabase games = new GameDatabase();
        GameData game = new GameData(0, "whiteKnight", "blackKnight", null, new ChessGame());
        assertThrows(DataAccessException.class, () -> games.createGame(game));
    }

    @Test
    @DisplayName("Clear - Successful")
    void clearAllGameData() throws DataAccessException {
        GameDatabase games = new GameDatabase();
        GameData gameData = new GameData(0, "testWhite", "testBlack", "testName", new ChessGame());
        games.createGame(gameData);
        games.clear();
        int numberOfGames = countRowsInTable("games");
        assertEquals(0, numberOfGames);
    }

    @Test
    @DisplayName("Update Game - Successful")
    void updateExistingGame() throws DataAccessException {
        GameDatabase games = new GameDatabase();
        GameData gameData = new GameData(0, "white", "black", "testName", new ChessGame());
        int gameID = games.createGame(gameData);
        gameData = new GameData(gameID, "newWhite", "newBlack", "testName", new ChessGame());
        games.updateGame(gameData);
        gameData = games.getGame(gameID);
        assertEquals("newWhite", gameData.whiteUsername());
    }

    @Test
    @DisplayName("Update Game - Make Move")
    void updateExistingGameMovePawn() throws DataAccessException, InvalidMoveException {
        GameDatabase games = new GameDatabase();
        ChessGame newGame = new ChessGame();
        GameData gameData = new GameData(0, "white", "black", "testName", newGame);
        int gameID = games.createGame(gameData);
        gameData = games.getGame(gameID);
        newGame = gameData.game();
        ChessMove move = new ChessMove(new ChessPosition(2,1), new ChessPosition(4,1), null);
        newGame.makeMove(move);
        ChessGame pawnMoved = newGame;
        gameData = new GameData(gameID, "white", "black", "testName", pawnMoved);
        games.updateGame(gameData);
        gameData = games.getGame(gameID);
        ChessPiece whitePawn = gameData.game().getBoard().getPiece(new ChessPosition(4, 1));
        assertEquals(ChessPiece.PieceType.PAWN, whitePawn.getPieceType());
        assertEquals(ChessGame.TeamColor.WHITE, whitePawn.getTeamColor());
    }

    @Test
    @DisplayName("Update Game - Null Game")
    void updateGameNullGame() throws DataAccessException {
        GameDatabase games = new GameDatabase();
        GameData gameData = new GameData(0, "white", "black", "testName", new ChessGame());
        int gameID = games.createGame(gameData);
        gameData = new GameData(gameID, "newWhite", "newBlack", null, null);
        GameData finalGameData = gameData;
        assertThrows(DataAccessException.class, () -> games.updateGame(finalGameData));
    }

    @Test
    @DisplayName("List Games - Successful")
    void listAllGames() throws DataAccessException {
        GameDatabase games = new GameDatabase();
        Collection<GameData> gamesList;
        games.clear();
        for(int i = 0; i < 3; i++) {
            games.createGame(new GameData(i, "w", "b", "gameName", new ChessGame()));
        }
        gamesList = games.listGames();
        assertEquals(3, gamesList.size());
    }

    @Test
    @DisplayName("List Games - Empty Table")
    void listGamesNoData() throws DataAccessException {
        GameDatabase games = new GameDatabase();
        Collection<GameData> gamesList;
        games.clear();
        gamesList = games.listGames();
        assertEquals(0, gamesList.size());
    }
}
