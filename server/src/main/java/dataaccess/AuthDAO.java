package dataaccess;

import exceptions.DataAccessException;
import model.AuthData;

public interface AuthDAO {

    AuthData getAuth(String authToken) throws DataAccessException;
    void createAuth(AuthData authData) throws DataAccessException;
    void deleteAuth(String authToken) throws DataAccessException;
    void clear() throws DataAccessException;
}
