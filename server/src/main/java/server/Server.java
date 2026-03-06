package server;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import io.javalin.Javalin;
import io.javalin.http.Context;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class Server {

    private final Javalin javalin;

    public Server() {
        RegisterHandler registerHandler = new RegisterHandler();
        javalin = Javalin.create(config -> config.staticFiles.add("web"))
                .post("/user", registerHandler::register)
                .exception(DataAccessException.class, this::dataAccessExceptionHandler)
                .exception(Exception.class, this::exceptionHandler)
                .error(404, this::notFound);
    }

    private void dataAccessExceptionHandler(DataAccessException e, @NotNull Context context) {
        context.status(e.getStatusCode());
        context.json(new Gson().toJson(Map.of("message", "Error: " + e.getMessage())));
    }

    private void exceptionHandler(Exception e, Context context) {
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
