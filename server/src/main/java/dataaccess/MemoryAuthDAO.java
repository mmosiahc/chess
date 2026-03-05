package dataaccess;

import model.AuthData;

import java.util.HashMap;
import java.util.Map;

public class MemoryAuthDAO implements AuthDAO {
    Map<String, AuthData> authentications = new HashMap<>();
    @Override
    public AuthData getAuth(String authToken) throws UnauthorizedException {
        if(authentications.containsKey(authToken)) {
            return authentications.get(authToken);
        } else {
            throw new UnauthorizedException("Error: unauthorized");
        }
    }

    @Override
    public void createAuth(AuthData authData) {
        authentications.put(authData.authToken(), authData);
    }

    @Override
    public void deleteAuth(String authToken) throws BadRequestException {
        if(authentications.containsKey(authToken)) {
            authentications.remove(authToken);
        } else {
            throw new BadRequestException("Error: bad request");
        }
    }

    @Override
    public void clear() {
        authentications.clear();
    }
}
