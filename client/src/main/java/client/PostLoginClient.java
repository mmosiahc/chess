package client;

import service.*;

import java.util.Arrays;

public class PostLoginClient implements ChessClient{
    private final ServerFacade facade;
    private final State state = State.LOGGED_IN;

    public PostLoginClient(ServerFacade facade) {
        this.facade = facade;
    }

    public String eval(String input) {

        try {
            String[] tokens = input.toLowerCase().split(" ");
            String cmd = (tokens.length > 0) ? tokens[0] : "help";
            String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "create" -> create(params);
                case "list" -> list(params);
                case "join" -> join(params);
                case "observe" -> observe(params);
                case "logout" -> logout(params);
                case "quit" -> "quit";
                default -> help();
            };
        } catch (Exception ex) {
            return ex.getMessage();
        }
    }

    public String create(String... params) throws Exception {
        assertLoggedIn();
        if(params.length != 1) {
            return "Expected <game_name>\n";
        }
        try {
            CreateGameResult result = facade.createGame(params[0]);
            return "Success! Game created.";
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    public String list(String... params) {
        if(params.length != 2) {
            return "Expected <username> <password>\n";
        }
        try {
            LoginRequest request = new LoginRequest(params[0], params[1]);
            LoginResult result = facade.login(request);
            return String.format("You signed in as %s\nYour authtoken is: %s\n", result.username(), result.authToken());
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    public String join(String... params) {
        if(params.length != 2) {
            return "Expected <username> <password>\n";
        }
        try {
            LoginRequest request = new LoginRequest(params[0], params[1]);
            LoginResult result = facade.login(request);
            return String.format("You signed in as %s\nYour authtoken is: %s\n", result.username(), result.authToken());
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    public String observe(String... params) {
        if(params.length != 2) {
            return "Expected <username> <password>\n";
        }
        try {
            LoginRequest request = new LoginRequest(params[0], params[1]);
            LoginResult result = facade.login(request);
            return String.format("You signed in as %s\nYour authtoken is: %s\n", result.username(), result.authToken());
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    public String logout(String... params) {
        if(params.length != 2) {
            return "Expected <username> <password>\n";
        }
        try {
            LoginRequest request = new LoginRequest(params[0], params[1]);
            LoginResult result = facade.login(request);
            return String.format("You signed in as %s\nYour authtoken is: %s\n", result.username(), result.authToken());
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    public String help() {
        return """
                
                create <GAME_NAME> - to create a game
                list - to list all games
                join <GAME_ID> <WHITE|BLACK> - to join game as color
                observe <GAME_ID> - to watch game
                logout - when finished
                quit - to exit chess application
                help - to show options
                """;
    }

    private void assertLoggedIn() throws Exception {
        if(state == State.LOGGED_OUT) {
            throw new Exception("You must sign in");
        }
    }
}
