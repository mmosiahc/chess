package client;

import service.CreateGameResult;
import service.ListGamesResult;
import service.LoginRequest;
import service.LoginResult;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class PostLoginClient implements ChessClient{
    private final ServerFacade facade;
    private State state = State.LOGGED_IN;
    private HashMap<Integer, Integer> gamesList = new HashMap<>();
    private int gamesListIndex = 1;

    public PostLoginClient(ServerFacade facade) throws Exception {
        this.facade = facade;
        initializeGamesList();
    }

    private void initializeGamesList() throws Exception {
        Map<String, Collection<ListGamesResult>> games = facade.listGames();
        Collection<ListGamesResult> results = games.get("games");
        for(ListGamesResult result : results) {
            int id = result.gameID();
            gamesList.put(id, generateIndex());
        }
    }

    public String eval(String input) {

        try {
            String[] tokens = input.toLowerCase().split(" ");
            String cmd = (tokens.length > 0) ? tokens[0] : "help";
            String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "create" -> create(params);
                case "list" -> list();
                case "join" -> join(params);
                case "observe" -> observe(params);
                case "logout" -> logout(params);
                case "quit" -> "quit";
                default -> help(cmd);
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
            gamesList.put(result.gameID(), generateIndex());
            return "Success! " + params[0] + " created.\n";
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    public String list() throws Exception {
        assertLoggedIn();
        StringBuilder sb = new StringBuilder();
        String header = String.format("%-3s | %-15s | %-10s | %-10s |%n", "ID", "Name", "White", "Black");
        sb.append(header);
        String divider = "----+-----------------+------------+------------+\n";
        sb.append(divider);
        try {
            Map<String, Collection<ListGamesResult>> games = facade.listGames();
            Collection<ListGamesResult> results = games.get("games");
            for(ListGamesResult result : results) {
                int id = result.gameID();
                String listIndex = gamesList.get(id) + ". ";
                String white = result.whiteUsername();
                if(white == null) {white = "empty";}
                String black = result.blackUsername();
                if(black == null) {black = "empty";}
                String name = result.gameName();
                String line = String.format("%-2s | %-15s | %-10s | %-10s |%n",
                        listIndex, name, white, black);
                sb.append(line);
                sb.append(divider);
            }
            return sb.toString();
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


    public String help(String failedCommand) {
        if(failedCommand.equals("help")) {
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
        return """
                
                create <GAME_NAME> - to create a game
                list - to list all games
                join <GAME_ID> <WHITE|BLACK> - to join game as color
                observe <GAME_ID> - to watch game
                logout - when finished
                quit - to exit chess application
                help - to show options
                
                """ + "Expected <command> got \"" + failedCommand + "\"\n";
    }

    private void assertLoggedIn() throws Exception {
        if(state == State.LOGGED_OUT) {
            throw new Exception("You must sign in");
        }
    }

    private int generateIndex() {
        return gamesListIndex++;
    }
}
