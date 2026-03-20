package server;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import dataaccess.UnauthorizedException;
import io.javalin.http.Context;

public class BaseHandler {

    static <T> T getBodyObject(Context context, Class<T> tClass) throws RuntimeException{
        var bodyObject = new Gson().fromJson(context.body(), tClass);

        if (bodyObject == null) {
            throw new RuntimeException("missing required body");
        }

        return bodyObject;
    }

    static String getAuthHeaderObject(Context context) throws RuntimeException, UnauthorizedException {
        String authToken;
        try {
            authToken = new Gson().fromJson(context.header("authorization"), String.class);
        } catch (JsonSyntaxException e) {
            throw new UnauthorizedException();
        }

        if (authToken == null) {
            throw new RuntimeException("missing required header");
        }

        return authToken;
    }
}
