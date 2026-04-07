package client;

import service.LoginRequest;
import service.LoginResult;
import service.RegisterRequest;
import service.RegisterResult;

import java.util.Arrays;

public class PreLoginClient implements ChessClient{
    private final ServerFacade facade;

    public PreLoginClient(ServerFacade facade) {
        this.facade = facade;
    }

    public String eval(String input) {

        try {
            String[] tokens = input.toLowerCase().split(" ");
            String cmd = (tokens.length > 0) ? tokens[0] : "help";
            String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "register" -> register(params);
                case "login" -> login(params);
                case "quit" -> "quit";
                default -> help();
            };
        } catch (Exception ex) {
            return ex.getMessage();
        }
    }

    public String register(String... params) {
        if(params.length != 3) {
            return "Expected <username> <password> <email>\n";
        }
        try {
            RegisterRequest request = new RegisterRequest(params[0], params[1], params[2]);
            RegisterResult result = facade.register(request);
            return String.format("You signed in as %s\n", result.username());
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    public String login(String... params) {
        if(params.length != 2) {
            return "Expected <username> <password>\n";
        }
        try {
            LoginRequest request = new LoginRequest(params[0], params[1]);
            LoginResult result = facade.login(request);
            return String.format("You signed in as %s\n", result.username());
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    public String help() {
        return """
                
                register <USERNAME> <PASSWORD> <EMAIL> - to create account
                login <USERNAME> <PASSWORD> - to play chess
                quit - to exit this chess application
                help - to show options
                """;
    }

}
