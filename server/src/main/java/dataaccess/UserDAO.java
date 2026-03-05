package dataaccess;

import model.UserData;

public interface UserDAO {

    UserData getUser(String username) throws BadRequestException;
    void createUser(UserData userData) throws AlreadyTakenException;
    void clear();
}
