package dataaccess;

import model.GameData;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class MemoryGameDAO implements GameDAO {
    Map<Integer, GameData> games = new HashMap<>();
    @Override
    public GameData getGame(int gameID) throws BadRequestException {
        if(games.containsKey(gameID)) {
            return games.get(gameID);
        } else {
            throw new BadRequestException();
        }
    }

    @Override
    public void createGame(GameData gameData) {
        games.put(gameData.gameID(), gameData);
    }

    @Override
    public void updateGame(GameData gameData) {
        games.put(gameData.gameID(), gameData);
    }

    @Override
    public Collection<GameData> listGames() {
        return games.values();
    }

    @Override
    public void clear() {
        games.clear();
    }

    @Override
    public Map<Integer, GameData> getGames() {
        return games;
    }

    @Override
    public String toString() {
        return games.toString();
    }
}
