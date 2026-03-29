package dataaccess;

import model.AuthData;

import java.util.Map;

public interface AuthDAO {

    AuthData getAuth(String authToken) throws DataAccessException;
    void createAuth(AuthData authData) throws DataAccessException;
    void deleteAuth(String authToken) throws DataAccessException;
    void clear();
    String toString();
    Map<String, AuthData> getAuthentications();
}
