package server;

import com.google.gson.Gson;
import exceptions.DataAccessException;
import io.javalin.http.Context;
import model.RegisterRequest;
import model.RegisterResult;
import service.UserService;

public class RegisterHandler extends BaseHandler{
    final UserService service;

    public RegisterHandler(UserService service) {
        this.service = service;
    }

    void register(Context ctx) throws DataAccessException {
        RegisterRequest request = getBodyObject(ctx, RegisterRequest.class);
        RegisterResult result = service.register(request);
        String json = new Gson().toJson(result);
        ctx.json(json);
    }
}
