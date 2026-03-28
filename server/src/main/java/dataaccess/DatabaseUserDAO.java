package dataaccess;

import model.UserData;

import java.sql.Connection;
import java.sql.SQLException;

public interface DatabaseUserDAO {
    UserData getUser(Connection conn, String username) throws SQLException;
//    void createUser(UserData userData) throws AlreadyTakenException;
//    void clear();
//    String toString();
//    Map<String, UserData> getUsers();
}
