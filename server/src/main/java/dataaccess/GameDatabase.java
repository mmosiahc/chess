package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.GameData;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.Map;

public class GameDatabase implements GameDAO {
    @Override
    public GameData getGame(int gameID) throws BadRequestException {
        return null;
    }

    @Override
    public void createGame(GameData gameData) throws DataAccessException {
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
            }
        }catch (SQLException ex) {
            throw new DataAccessException("Failed to connect to database", ex);
        }
    }

    @Override
    public void updateGame(GameData gameData) {

    }

    @Override
    public Collection<GameData> listGames() {
        return null;
    }

    @Override
    public void clear() {

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
