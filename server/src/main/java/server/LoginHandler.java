package server;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import io.javalin.http.Context;
import service.LoginRequest;
import service.LoginResult;
import service.UserService;

public class LoginHandler extends BaseHandler{
    final UserService service;

    public LoginHandler(UserService service) {
        this.service = service;
    }

    void login(Context ctx) throws DataAccessException {
        LoginRequest request = getBodyObject(ctx, LoginRequest.class);
        LoginResult result = service.login(request);
        String json = new Gson().toJson(result);
        ctx.json(json);
    }
}
