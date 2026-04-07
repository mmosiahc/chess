package client;

import chess.ChessGame;
import service.*;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class PostLoginClient implements ChessClient{
    private final ServerFacade facade;
    private final Repl repl;
    private HashMap<Integer, Integer> gamesList = new HashMap<>();
    private int gamesListIndex = 1;

    public PostLoginClient(ServerFacade facade, Repl repl) {
        this.facade = facade;
        this.repl = repl;
        initializeGamesList();
    }

    private void initializeGamesList() {
        try {
            Map<String, Collection<ListGamesResult>> games = facade.listGames();
            Collection<ListGamesResult> results = games.get("games");
            for(ListGamesResult result : results) {
                int id = result.gameID();
                gamesList.put(id, generateIndex());
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }

    public String eval(String input) {
        try {
            String[] tokens = input.toLowerCase().split(" ");
            String cmd = (tokens.length > 0) ? tokens[0] : "help";
            String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);
            if(cmd.startsWith("-")) {
                return switch (cmd) {
                    case "-c" -> create(params);
                    case "-l" -> list();
                    case "-j" -> join(params);
                    case "-o" -> observe(params);
                    case "-lo" -> logout();
                    case "-q" -> "quit";
                    default -> help(cmd);
                };
            }else {
                return switch (cmd) {
                    case "create" -> create(params);
                    case "list" -> list();
                    case "join" -> join(params);
                    case "observe" -> observe(params);
                    case "logout" -> logout();
                    case "quit" -> "quit";
                    default -> help(cmd);
                };
            }
        } catch (Exception ex) {
            return ex.getMessage();
        }
    }

    public String create(String... params) {
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

    public String list() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%-3s | %-15s |%-11s | %s%n", "ID", "Name", "White", "Black"));
        sb.append("-".repeat(48)).append("\n");
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
                String line = String.format("%-2s | %-15s | %-10s | %-10s%n",
                        listIndex, name, white, black);
                sb.append(line);
            }
            return sb.toString();
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    public String join(String... params) {
        if(params.length != 2) {
            return "Expected <game_id> <white|black>\n";
        }
        int id;
        ChessGame.TeamColor teamColor;
        try {
            id = Integer.parseInt(params[0]);
        } catch (NumberFormatException e) {
            return String.format("%s wasn't a valid number.\n", params[0]);
        }
        try {
            teamColor = ChessGame.TeamColor.valueOf(params[1].trim().toUpperCase());
        } catch (IllegalArgumentException | NullPointerException e) {
            return String.format("Invalid team color selected \"" + params[1] + ".\"\n");
        }
        String gameName = "";
        try{
            Map<String, Collection<ListGamesResult>> games = facade.listGames();
            Collection<ListGamesResult> results = games.get("games");
            for(ListGamesResult result : results) {
                if(result.gameID() == id) {
                    gameName = result.gameName();
                }
            }
        } catch (Exception e) {
            return e.getMessage();
        }

        try {
            JoinGameBody request = new JoinGameBody(teamColor, id);
            facade.joinGame(request);
            return String.format("You joined \"" + gameName + "\" as %s\n", teamColor);
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    public String observe(String... params) {
        if(params.length != 1) {
            return "Expected <game_id>\n";
        }
        int id;
        try {
            id = Integer.parseInt(params[0]);
        } catch (NumberFormatException e) {
            return String.format("%s wasn't a valid number.\n", params[0]);
        }
        String gameName = "";
        try{
            Map<String, Collection<ListGamesResult>> games = facade.listGames();
            Collection<ListGamesResult> results = games.get("games");
            for(ListGamesResult result : results) {
                if(result.gameID() == id) {
                    gameName = result.gameName();
                }
            }
        } catch (Exception e) {
            return e.getMessage();
        }
        try {
            return String.format("You joined \"" + gameName + "\" as an %s\n", "observer.");
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    public String logout() {
        try {
            facade.logout();
            repl.setState(new PreLoginClient(facade, repl));
            return "Signed out.\n";
        } catch (Exception e) {
            return e.getMessage();
        }
    }


    public String help(String failedCommand) {
        StringBuilder sb = new StringBuilder();

        sb.append(String.format("%-35s | %s%n", "Command", "Description"));
        sb.append("-".repeat(55)).append("\n");

        sb.append(String.format("%-35s | %s%n", "create <GAME_NAME> (-c)", "Create a new game"));
        sb.append(String.format("%-35s | %s%n", "list (-l)", "List all current games"));
        sb.append(String.format("%-35s | %s%n", "join <GAME_ID> [WHITE|BLACK] (-j)", "Join a game as a player"));
        sb.append(String.format("%-35s | %s%n", "observe <GAME_ID> (-o)", "Watch a game as a spectator"));
        sb.append(String.format("%-35s | %s%n", "logout (-lo)", "Log out of your account"));
        sb.append(String.format("%-35s | %s%n", "quit (-q)", "Exit the application"));
        sb.append(String.format("%-35s | %s%n", "help (-h)", "Show these options again"));

        if(failedCommand.equalsIgnoreCase("help") || failedCommand.equalsIgnoreCase("-h")) {
            return sb.toString();
        }
        sb.append("\nExpected <Command> got \"" + failedCommand + "\"\n");

        return sb.toString();
    }

    private int generateIndex() {
        return gamesListIndex++;
    }
}
