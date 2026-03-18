package server;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import io.javalin.http.Context;
import service.CreateGameRequest;
import service.CreateGameResult;
import service.GameService;

public class CreateGameHandler extends BaseHandler{
    final GameService service;

    public CreateGameHandler(GameService service) {
        this.service = service;
    }

    void createGame(Context ctx) throws DataAccessException, RuntimeException {
        String token = getAuthHeaderObject(ctx, String.class);
        String name = getBodyObject(ctx, String.class);
        System.out.println(name);
        CreateGameRequest request = new CreateGameRequest(token, name);
        CreateGameResult result = service.createGame(request);
        ctx.json(new Gson().toJson(result));


    }
}
