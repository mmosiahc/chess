package dataaccess;

import model.GameData;

import java.util.Collection;
import java.util.Map;

public interface GameDAO {

    Collection<GameData> listGames() throws UnauthorizedException;
    void createGame(GameData gameData);
    GameData getGame(int gameID) throws BadRequestException;
    void updateGame(GameData gameData);
    void clear();
    String toString();
    Map<Integer, GameData> getGames();
}
