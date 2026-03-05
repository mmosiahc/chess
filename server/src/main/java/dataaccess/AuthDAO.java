package dataaccess;

import model.AuthData;

public interface AuthDAO {

    AuthData getAuth(String authToken) throws UnauthorizedException;
    void createAuth(AuthData authData);
    void deleteAuth(String authToken) throws BadRequestException;
    void clear();
}
