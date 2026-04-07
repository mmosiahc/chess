package server;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import io.javalin.http.Context;
import service.GameService;
import service.ListGamesRequest;
import service.ListGamesResult;

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
        Collection<ListGamesResult> results = service.listGames(request);
        ctx.json(new Gson().toJson(Map.of("games", results)));
    }
}
