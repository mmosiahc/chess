package server;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryUserDAO;
import io.javalin.http.Context;
import service.RegisterRequest;
import service.RegisterResult;
import service.UserService;

public class RegisterHandler extends BaseHandler{
    final MemoryUserDAO userDAO = new MemoryUserDAO();
    final MemoryAuthDAO authDAO = new MemoryAuthDAO();
    final UserService service = new UserService(userDAO, authDAO);

    void register(Context ctx) throws DataAccessException, RuntimeException {
        RegisterRequest request = getBodyObject(ctx, RegisterRequest.class);
        RegisterResult result = service.register(request);
        String json = new Gson().toJson(result);
        ctx.json(json);
    }
}
