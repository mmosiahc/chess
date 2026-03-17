package server;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import io.javalin.http.Context;
import service.LogoutRequest;
import service.UserService;

import java.util.Map;

public class LogoutHandler extends BaseHandler{
    final UserService service;

    public LogoutHandler(UserService service) {
        this.service = service;
    }

    void logout(Context ctx) throws DataAccessException, RuntimeException {
        LogoutRequest request = getBodyObject(ctx, LogoutRequest.class);
        service.logout(request);
        ctx.json(new Gson().toJson(Map.of()));
    }
}
