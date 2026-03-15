package service;

import dataaccess.*;
import model.AuthData;
import model.UserData;

import java.util.Map;
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

    public LoginResult login(LoginRequest loginRequest) throws DataAccessException {
        if(loginRequest.username() == null || loginRequest.password() == null) {
            throw new BadRequestException();
        }
        UserData user;
        user = userMemory.getUser(loginRequest.username());
        if(!user.password().equals(loginRequest.password())) throw new UnauthorizedException();

        AuthData auth = new AuthData(generateToken(), loginRequest.username());
        authMemory.createAuth(auth);
        return new LoginResult(loginRequest.username(), auth.authToken());
    }

    public Map<String, UserData> getUsers() {
        return userMemory.getUsers();
    }
}
