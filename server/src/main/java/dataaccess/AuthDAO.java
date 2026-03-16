package dataaccess;

import model.AuthData;

import java.util.Map;

public interface AuthDAO {

    AuthData getAuth(String authToken) throws UnauthorizedException;
    void createAuth(AuthData authData);
    void deleteAuth(String authToken) throws UnauthorizedException;
    void clear();
    String toString();
    Map<String, AuthData> getAuthentications();
}
