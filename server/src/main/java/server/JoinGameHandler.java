package server;

import com.google.gson.Gson;
import Exceptions.DataAccessException;
import io.javalin.http.Context;
import service.GameService;
import model.JoinGameBody;
import model.JoinGameRequest;

import java.util.Map;

public class JoinGameHandler extends BaseHandler{
    final GameService service;

    public JoinGameHandler(GameService service) {
        this.service = service;
    }

    void joinGame(Context ctx) throws DataAccessException, RuntimeException {
        String token = getAuthHeaderObject(ctx);
        JoinGameBody body = getBodyObject(ctx, JoinGameBody.class);
        JoinGameRequest request = new JoinGameRequest(token, body.playerColor(), body.gameID());
        service.joinGame(request);
        ctx.json(new Gson().toJson(Map.of()));
    }
}
