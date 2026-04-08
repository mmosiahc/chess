package server;

import com.google.gson.Gson;
import exceptions.DataAccessException;
import io.javalin.http.Context;
import model.LoginRequest;
import model.LoginResult;
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
