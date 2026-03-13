package server;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryGameDAO;
import dataaccess.MemoryUserDAO;
import io.javalin.Javalin;
import io.javalin.http.Context;
import service.ClearService;
import service.UserService;

import java.util.Map;

public class Server {

    private final Javalin javalin;

    public Server() {
        MemoryAuthDAO authDAO = new MemoryAuthDAO();
        MemoryUserDAO userDAO = new MemoryUserDAO();
        MemoryGameDAO gameDAO = new MemoryGameDAO();

        UserService userService = new UserService(userDAO, authDAO);
        ClearService clearService = new ClearService(userDAO, authDAO, gameDAO);

        RegisterHandler registerHandler = new RegisterHandler(userService);
        ClearHandler clearHandler = new ClearHandler(clearService);

        javalin = Javalin.create(config -> config.staticFiles.add("web"))
                .post("/user", registerHandler::register)
                .exception(DataAccessException.class, this::dataAccessExceptionHandler)
                .exception(Exception.class, this::exceptionHandler)
                .error(404, this::notFound)
                .delete("/db", clearHandler::clear);
    }

    private void dataAccessExceptionHandler(DataAccessException e, Context context) {
        if(e.getStatusCode() == 0) {
            context.status(500);
        }else{
            context.status(e.getStatusCode());
        }
        context.json(new Gson().toJson(Map.of("message", "Error: " + e.getMessage())));
    }

    private void exceptionHandler(Exception e, Context context) {
//        e.printStackTrace();
        var body = new Gson().toJson(Map.of("message", String.format("Error: %s", e.getMessage())));
        context.status(500);
        context.json(body);
    }

    private void notFound(Context context) {
        String msg = String.format("[%s] %s not found", context.method(), context.path());
        exceptionHandler(new Exception(msg), context);
    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}
