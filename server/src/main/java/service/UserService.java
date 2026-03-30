package service;

import dataaccess.*;
import model.AuthData;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

import java.util.Map;
import java.util.UUID;


public class UserService {
    private final UserDatabase users;
    private final AuthDatabase authentications;

    public UserService(UserDatabase users, AuthDatabase authentications) {
        this.users = users;
        this.authentications = authentications;
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
        users.createUser(user);

        AuthData auth = new AuthData(generateToken(), username);
        authentications.createAuth(auth);
        return new RegisterResult(username, auth.authToken());
    }

    public LoginResult login(LoginRequest loginRequest) throws DataAccessException {
        String username = loginRequest.username();
        String password = loginRequest.password();

        if(username == null || password == null) {
            throw new BadRequestException();
        }
        UserData user;
        user = users.getUser(username);
        if(user == null) throw new UnauthorizedException();
        verifyUser(password, user.password());
        if(!verifyUser(password, user.password())) throw new UnauthorizedException();

        AuthData auth = new AuthData(generateToken(), username);
        authentications.createAuth(auth);
        return new LoginResult(username, auth.authToken());
    }

    public void logout(LogoutRequest logoutRequest) throws DataAccessException {
        String authToken = logoutRequest.authToken();

        if (authToken == null) {
            throw new BadRequestException();
        }
        AuthData authData = authentications.getAuth(authToken);
        if(authData == null) throw new UnauthorizedException();

        authentications.deleteAuth(authToken);
    }

    private boolean verifyUser(String providedPassword, String hashedPassword) {
        return BCrypt.checkpw(providedPassword, hashedPassword);
    }

    public Map<String, UserData> getUsers() {return null;}
    public Map<String, AuthData> getAuthentications() {return null;}
}
