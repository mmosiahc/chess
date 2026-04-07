package client;

import service.RegisterRequest;
import service.RegisterResult;

import java.util.Arrays;

public class PreloginClient implements ChessClient{
    private final ServerFacade facade;

    public PreloginClient(ServerFacade facade) {
        this.facade = facade;
    }

    public String eval(String input) {

        try {
            String[] tokens = input.toLowerCase().split(" ");
            String cmd = (tokens.length > 0) ? tokens[0] : "help";
            String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "register" -> register(params);
//                case "login" -> login(params);
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
            return String.format("You signed in as %s\nYour authtoken is: %s\n", result.username(), result.authToken());
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
