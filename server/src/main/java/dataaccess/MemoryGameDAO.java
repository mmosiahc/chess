package dataaccess;

import Exceptions.BadRequestException;
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
    public int createGame(GameData gameData) {
        games.put(gameData.gameID(), gameData);
        return gameData.gameID();
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
}
