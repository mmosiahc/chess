package server;

import com.google.gson.Gson;
import io.javalin.http.Context;

public class BaseHandler {

    static <T> T getBodyObject(Context context, Class<T> tClass) throws RuntimeException{
        var bodyObject = new Gson().fromJson(context.body(), tClass);

        if (bodyObject == null) {
            throw new RuntimeException("missing required body");
        }

        return bodyObject;
    }

    static <T> T getAuthHeaderObject(Context context, Class<T> tClass) throws RuntimeException{
        var headerObject = new Gson().fromJson(context.header("authorization"), tClass);

        if (headerObject == null) {
            throw new RuntimeException("missing required header");
        }

        return headerObject;
    }
}
