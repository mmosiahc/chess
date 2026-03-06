package service;

import dataaccess.BadRequestException;
import dataaccess.DataAccessException;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryUserDAO;
import model.AuthData;
import model.UserData;

import java.util.UUID;


public class UserService {
    private final MemoryUserDAO userMemory;
    private final MemoryAuthDAO authMemory;

    public UserService(MemoryUserDAO userMemory, MemoryAuthDAO authMemory) {
        this.userMemory = userMemory;
        this.authMemory = authMemory;
    }

    public static String generateToken() {
        return UUID.randomUUID().toString();
    }

    public RegisterResult register(RegisterRequest registerRequest) throws DataAccessException {
        if(registerRequest.username() == null || registerRequest.password() == null || registerRequest.email() == null) {
            throw new BadRequestException();
        }
        UserData user = new UserData(registerRequest.username(), registerRequest.password(), registerRequest.email());
        userMemory.createUser(user);

        AuthData auth = new AuthData(generateToken(), registerRequest.username());
        authMemory.createAuth(auth);
        return new RegisterResult(registerRequest.username(), auth.authToken());
    }
}
