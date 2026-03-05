package dataaccess;

import model.UserData;

import java.util.HashMap;
import java.util.Map;

public class MemoryUserDAO implements UserDAO {
    Map<String, UserData> users = new HashMap<>();
    @Override
    public UserData getUser(String username) throws BadRequestException {
        try{
            return users.get(username);
        }catch (Exception e) {
            throw new BadRequestException("Error: bad request", e);
        }
//        if(users.containsKey(username)) {
//            return users.get(username);
//        } else {
//            throw new BadRequestException("Error: bad request");
//        }

    }

    @Override
    public void createUser(UserData userData) throws AlreadyTakenException {
        if(!users.containsKey(userData.username())) {
            users.put(userData.username(), userData);
        }else {
            throw new AlreadyTakenException("Error: already taken.");
        }
    }

    @Override
    public void clear() {
        users.clear();
    }
}
