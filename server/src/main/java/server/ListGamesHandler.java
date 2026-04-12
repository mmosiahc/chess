package server;

import com.google.gson.Gson;
import data_transfer.ListGamesRequest;
import exceptions.DataAccessException;
import io.javalin.http.Context;
import model.GameData;
import service.GameService;

import java.util.Collection;
import java.util.Map;

public class ListGamesHandler extends BaseHandler{
    final GameService service;

    public ListGamesHandler(GameService service) {
        this.service = service;
    }

    void listGames(Context ctx) throws DataAccessException {
        String token = getAuthHeaderObject(ctx);
        ListGamesRequest request = new ListGamesRequest(token);
        Collection<GameData> games = service.listGames(request);
//        Collection<ListGamesResult> results = games.stream()
//                .map(g -> new ListGamesResult(g.gameID(), g.whiteUsername(), g.blackUsername(), g.gameName()))
//                .toList();
        ctx.json(new Gson().toJson(Map.of("games", games)));
    }
}
