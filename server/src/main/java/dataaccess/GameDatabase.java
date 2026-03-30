package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.GameData;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

public class GameDatabase implements GameDAO {
    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        GameData game = null;
        try(var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement("SELECT id, white_username, black_username, game_name, game FROM games WHERE id = ?")) {
                preparedStatement.setInt(1, gameID);
                try (var rs = preparedStatement.executeQuery()) {
                    if(rs.next()) {
                        var id = rs.getInt("id");
                        var whiteName = rs.getString("white_username");
                        var blackName = rs.getString("black_username");
                        var gameName = rs.getString("game_name");
                        var chess = rs.getString("game");
                        ChessGame chessGame = new Gson().fromJson(chess, ChessGame.class);
                        game = new GameData(id, whiteName, blackName, gameName, chessGame);
                    }
                }
            }
        }catch (SQLException ex) {
            throw new DataAccessException("Failed to connect to database", ex);
        }
        return game;
    }

    @Override
    public int createGame(GameData gameData) throws DataAccessException {
        String gameName = gameData.gameName();
        String whiteUsername = gameData.whiteUsername();
        String blackUsername = gameData.blackUsername();
        ChessGame game = gameData.game();
        String json = new Gson().toJson(game);
        String insertGameStatement = "INSERT INTO games (white_username, black_username, game_name, game) VALUES (?, ?, ?, ?)";
        try(var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement(insertGameStatement, Statement.RETURN_GENERATED_KEYS)) {
                preparedStatement.setString(1, whiteUsername);
                preparedStatement.setString(2, blackUsername);
                preparedStatement.setString(3, gameName);
                preparedStatement.setString(4, json);

                preparedStatement.executeUpdate();
                var rs = preparedStatement.getGeneratedKeys();
                var id = 0;
                if(rs.next()) {
                    id = rs.getInt(1);
                }
                return id;
            }
        }catch (SQLException ex) {
            throw new DataAccessException("Failed to connect to database", ex);
        }
    }

    @Override
    public void updateGame(GameData gameData) throws DataAccessException {
        int gameID = gameData.gameID();
        String whiteUsername = gameData.whiteUsername();
        String blackUsername = gameData.blackUsername();
        String gameName = gameData.gameName();
        ChessGame game = gameData.game();
        String json = new Gson().toJson(game);
        String updateGameStatement = "UPDATE games SET white_username = ?, black_username = ?, game_name = ?, game = ? WHERE id = ?";
        try(var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement(updateGameStatement)) {
                preparedStatement.setString(1, whiteUsername);
                preparedStatement.setString(2, blackUsername);
                preparedStatement.setString(3, gameName);
                preparedStatement.setString(4, json);
                preparedStatement.setInt(5, gameID);

                preparedStatement.executeUpdate();
            }
        }catch (SQLException ex) {
            throw new DataAccessException("Failed to connect to database", ex);
        }
    }

    @Override
    public Collection<GameData> listGames() throws DataAccessException {
        Collection<GameData> gamesList = new ArrayList<>();
        try(var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement("SELECT * FROM games")) {
                try (var rs = preparedStatement.executeQuery()) {
                    while(rs.next()) {
                        var id = rs.getInt("id");
                        var whiteName = rs.getString("white_username");
                        var blackName = rs.getString("black_username");
                        var gameName = rs.getString("game_name");
                        var chess = rs.getString("game");
                        ChessGame chessGame = new Gson().fromJson(chess, ChessGame.class);
                        GameData game = new GameData(id, whiteName, blackName, gameName, chessGame);
                        gamesList.add(game);
                    }
                }
            }
        }catch (SQLException ex) {
            throw new DataAccessException("Failed to connect to database", ex);
        }
        return gamesList;
    }

    @Override
    public void clear() throws DataAccessException {
        String truncateTableStatement = "TRUNCATE TABLE games";
        try(var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement(truncateTableStatement)) {

                preparedStatement.executeUpdate();
            }
        }catch (SQLException ex) {
            throw new DataAccessException("Failed to connect to database", ex);
        }
    }

    @Override
    public Map<Integer, GameData> getGames() {
        return null;
    }

    @Override
    public String toString() {
        return null;
    }
}
