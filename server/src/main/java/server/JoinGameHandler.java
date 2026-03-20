package server;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import io.javalin.http.Context;
import service.GameService;
import service.JoinGameBody;
import service.JoinGameRequest;

import java.util.Map;

public class JoinGameHandler extends BaseHandler{
    final GameService service;

    public JoinGameHandler(GameService service) {
        this.service = service;
    }

    void joinGame(Context ctx) throws DataAccessException, RuntimeException {
        String token = getAuthHeaderObject(ctx, String.class);
        JoinGameBody body = getBodyObject(ctx, JoinGameBody.class);
        JoinGameRequest request = new JoinGameRequest(token, body.playerRequestColor(), body.gameID());
        service.joinGame(request);
        ctx.json(new Gson().toJson(Map.of()));
    }
}
