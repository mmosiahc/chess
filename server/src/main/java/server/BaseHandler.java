package server;

import com.google.gson.Gson;
import exceptions.BadRequestException;
import exceptions.UnauthorizedException;
import io.javalin.http.Context;

public class BaseHandler {

    static <T> T getBodyObject(Context context, Class<T> tClass) throws BadRequestException {
        var bodyObject = new Gson().fromJson(context.body(), tClass);

        if (bodyObject == null) {
            throw new BadRequestException();
        }

        return bodyObject;
    }

    static String getAuthHeaderObject(Context context) throws UnauthorizedException {
        String authToken = context.header("authorization");
        //Check authtoken
        if (authToken == null || authToken.isEmpty()) {
            throw new UnauthorizedException();
        }

        return authToken;
    }
}
