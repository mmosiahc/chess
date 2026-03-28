package dataaccess;

import model.UserData;

import java.sql.Connection;
import java.sql.SQLException;

public class UserDatabase implements DatabaseUserDAO {

    @Override
    public UserData getUser(Connection conn, String username) throws SQLException {
        UserData user = null;
        try (var preparedStatement = conn.prepareStatement("SELECT username, password, email FROM user WHERE username = ?")) {
            preparedStatement.setString(1, username);
            try (var rs = preparedStatement.executeQuery()) {
                if(rs.next()) {
                    var name = rs.getString("username");
                    var password = rs.getString("password");
                    var email = rs.getString("email");

                    user = new UserData(name, password, email);
                }
            }
        }
        return user;
    }
//
//    @Override
//    public void createUser(UserData userData) throws AlreadyTakenException {
//        if(!users.containsKey(userData.username())) {
//            users.put(userData.username(), userData);
//        }else {
//            throw new AlreadyTakenException();
//        }
//    }
//
//    @Override
//    public void clear() {
//        users.clear();
//    }
//
//    @Override
//    public Map<String, UserData> getUsers() {
//        return users;
//    }
//
//    @Override
//    public String toString() {
//        return users.toString();
//    }
}
