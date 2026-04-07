package server;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import io.javalin.http.Context;
import service.CreateGameBody;
import service.CreateGameRequest;
import service.CreateGameResult;
import service.GameService;

public class CreateGameHandler extends BaseHandler{
    final GameService service;

    public CreateGameHandler(GameService service) {
        this.service = service;
    }

    void createGame(Context ctx) throws DataAccessException {
        String token = getAuthHeaderObject(ctx);
        CreateGameBody body = getBodyObject(ctx, CreateGameBody.class);
        CreateGameRequest request = new CreateGameRequest(token, body.gameName());
        CreateGameResult result = service.createGame(request);
        ctx.json(new Gson().toJson(result));

    }
}
