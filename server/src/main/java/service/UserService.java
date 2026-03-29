package service;

import dataaccess.*;
import model.AuthData;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

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
        String username = registerRequest.username();
        String password = registerRequest.password();
        String email = registerRequest.email();

        if(username == null || password == null || email == null) {
            throw new BadRequestException();
        }
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
        UserData user = new UserData(username, hashedPassword, email);
        userMemory.createUser(user);

        AuthData auth = new AuthData(generateToken(), username);
        authMemory.createAuth(auth);
        return new RegisterResult(username, auth.authToken());
    }

    public LoginResult login(LoginRequest loginRequest) throws DataAccessException {
        String username = loginRequest.username();
        String password = loginRequest.password();

        if(username == null || password == null) {
            throw new BadRequestException();
        }
        UserData user;
        user = userMemory.getUser(username);
        verifyUser(password, user.password());
        if(!verifyUser(password, user.password())) {throw new UnauthorizedException();}

        AuthData auth = new AuthData(generateToken(), username);
        authMemory.createAuth(auth);
        return new LoginResult(username, auth.authToken());
    }

    public void logout(LogoutRequest logoutRequest) throws DataAccessException {
        String authToken = logoutRequest.authToken();

        if (authToken == null) {
            throw new BadRequestException();
        }
        authMemory.deleteAuth(authToken);
    }
    private boolean verifyUser(String providedPassword, String hashedPassword) {
        return BCrypt.checkpw(providedPassword, hashedPassword);
    }
    public Map<String, UserData> getUsers() {return userMemory.getUsers();}
    public Map<String, AuthData> getAuthentications() {return authMemory.getAuthentications();}
}
