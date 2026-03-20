package dataaccess;

import model.UserData;

import java.util.Map;

public interface UserDAO {

    UserData getUser(String username) throws UnauthorizedException;
    void createUser(UserData userData) throws AlreadyTakenException;
    void clear();
    String toString();
    Map<String, UserData> getUsers();
}
