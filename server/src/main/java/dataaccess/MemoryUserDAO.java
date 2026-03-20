package dataaccess;

import model.UserData;

import java.util.HashMap;
import java.util.Map;

public class MemoryUserDAO implements UserDAO {
    Map<String, UserData> users = new HashMap<>();
    @Override
    public UserData getUser(String username) throws UnauthorizedException {
        if(users.containsKey(username)) {
            return users.get(username);
        } else {
            throw new UnauthorizedException();
        }
    }

    @Override
    public void createUser(UserData userData) throws AlreadyTakenException {
        if(!users.containsKey(userData.username())) {
            users.put(userData.username(), userData);
        }else {
            throw new AlreadyTakenException();
        }
    }

    @Override
    public void clear() {
        users.clear();
    }

    @Override
    public Map<String, UserData> getUsers() {
        return users;
    }

    @Override
    public String toString() {
        return users.toString();
    }
}
