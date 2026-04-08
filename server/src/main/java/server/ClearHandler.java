package server;

import com.google.gson.Gson;
import Exceptions.DataAccessException;
import io.javalin.http.Context;
import service.ClearService;

import java.util.Map;

public class ClearHandler extends BaseHandler{
    private final ClearService service;

    public ClearHandler(ClearService service) {
        this.service = service;
    }

    void clear(Context context) throws DataAccessException {
        service.clear();
        context.json(new Gson().toJson(Map.of()));
    }
}
