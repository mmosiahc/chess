package dataaccess;

import model.GameData;

import java.util.Collection;
import java.util.Map;

public interface GameDAO {

    Collection<GameData> listGames() throws DataAccessException;
    int createGame(GameData gameData) throws DataAccessException;
    GameData getGame(int gameID) throws DataAccessException;
    void updateGame(GameData gameData) throws DataAccessException;
    void clear();
    String toString();
    Map<Integer, GameData> getGames();
}
