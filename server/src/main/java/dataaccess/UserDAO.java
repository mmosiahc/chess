package dataaccess;

import model.UserData;

import java.util.Map;

public interface UserDAO {

    UserData getUser(String username) throws DataAccessException;
    void createUser(UserData userData) throws DataAccessException;
    void clear() throws DataAccessException;
    String toString();
    Map<String, UserData> getUsers();
}
