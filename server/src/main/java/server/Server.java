package server;

import com.google.gson.Gson;
import dataaccess.AuthDatabase;
import dataaccess.DataAccessException;
import dataaccess.GameDatabase;
import dataaccess.UserDatabase;
import io.javalin.Javalin;
import io.javalin.http.Context;
import service.ClearService;
import service.GameService;
import service.UserService;

import java.util.Map;

public class Server {

    private final Javalin javalin;

    public Server(){
        AuthDatabase authentications = new AuthDatabase();
        UserDatabase users = new UserDatabase();
        GameDatabase games = new GameDatabase();

        UserService userService = new UserService(users, authentications);
        ClearService clearService = new ClearService(users, authentications, games);
        GameService gameService = new GameService(games, authentications);

        RegisterHandler registerHandler = new RegisterHandler(userService);
        ClearHandler clearHandler = new ClearHandler(clearService);
        LoginHandler loginHandler = new LoginHandler(userService);
        LogoutHandler logoutHandler = new LogoutHandler(userService);
        ListGamesHandler listGamesHandler = new ListGamesHandler(gameService);
        CreateGameHandler createGameHandler = new CreateGameHandler(gameService);
        JoinGameHandler joinGameHandler = new JoinGameHandler(gameService);

        javalin = Javalin.create(config -> config.staticFiles.add("web"))
                .post("/user", registerHandler::register)
                .post("/session", loginHandler::login)
                .post("/game", createGameHandler::createGame)
                .put("/game", joinGameHandler::joinGame)
                .get("/game", listGamesHandler::listGames)
                .exception(DataAccessException.class, this::dataAccessExceptionHandler)
                .exception(Exception.class, this::exceptionHandler)
                .error(404, this::notFound)
                .delete("/db", clearHandler::clear)
                .delete("/session", logoutHandler::logout);
    }

    private void dataAccessExceptionHandler(DataAccessException e, Context context) {
//        e.printStackTrace();
        int status = (e.getStatusCode() != 0) ? e.getStatusCode() : 500;
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
