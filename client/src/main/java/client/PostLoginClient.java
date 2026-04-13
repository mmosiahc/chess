package client;

import chess.ChessGame;
import data_transfer.CreateGameResult;
import data_transfer.JoinGameBody;
import model.GameData;
import ui.DrawChessBoard;
import ui.EscapeSequences;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class PostLoginClient implements ChessClient{
    private final ServerFacade facade;
    private final Repl repl;
    private final HashMap<Integer, Integer> gamesList = new HashMap<>();
    private int gamesListIndex = 1;

    public PostLoginClient(ServerFacade facade, Repl repl) {
        this.facade = facade;
        this.repl = repl;
        initializeGamesList();
    }

    private void initializeGamesList() {
        try {
            Map<String, Collection<GameData>> games = facade.listGames();
            Collection<GameData> results = games.get("games");
            for(GameData result : results) {
                int id = result.gameID();
                gamesList.put(id, generateIndex());
            }
        } catch (Exception e) {
            System.out.println(e.getMessage()  + "\n");
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
            return ex.getMessage() + "\n";
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
            return e.getMessage() + "\n";
        }
    }

    public String list() {
        System.out.print(EscapeSequences.ERASE_SCREEN);
        System.out.flush();

        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%-3s | %-15s | %-11s | %s%n", "ID", "Name", "White", "Black"));
        sb.append("-".repeat(48)).append("\n");
        try {
            Map<String, Collection<GameData>> games = facade.listGames();
            Collection<GameData> results = games.get("games");
            for(GameData result : results) {
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
            if(gamesList.isEmpty()) return gamesList + " There are no current games being played.\n";
            return sb.toString();
        } catch (Exception e) {
            return e.getMessage() + "\n";
        }
    }

    public String join(String... params) throws Exception {
        if(params.length != 2) {
            return "Expected <game_id> <white|black>\n";
        }
        int id;
        ChessGame.TeamColor teamColor;
        boolean isWhite = false;
        try {
            id = Integer.parseInt(params[0]);
        } catch (NumberFormatException e) {
            return String.format("\"%s\" wasn't a valid number.\n", params[0]);
        }
        try {
            teamColor = ChessGame.TeamColor.valueOf(params[1].trim().toUpperCase());
        } catch (IllegalArgumentException | NullPointerException e) {
            return String.format("Invalid team color selected \"" + params[1] + ".\"\n");
        }
        if(teamColor == ChessGame.TeamColor.WHITE) {isWhite = true;}
        GameData game = getGame(id);
        try {
            JoinGameBody request = new JoinGameBody(teamColor, id);
            //Call join endpoint & open websocket connection
            facade.joinGame(request, Repl.username);
            repl.setState(new GameplayClient(facade, repl, game));
//            DrawChessBoard.drawNewBoard(isWhite);
            return (String.format("You joined \"" + game.gameName() + "\" as %s\n", teamColor));
        } catch (Exception e) {
            return e.getMessage() + "\n";
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
            return String.format("\"%s\" wasn't a valid number.\n", params[0]);
        }
        try {
            GameData game = getGame(id);
            if(game == null) {
                return String.format("Invalid game id \"%s\"\n", id);
            }
            repl.setState(new GameplayClient(facade, repl, game));
            DrawChessBoard.drawNewBoard(true);
            return String.format("You joined \"" + game.gameName() + "\" as an %s\n", "observer");
        } catch (Exception e) {
            return e.getMessage() + "\n";
        }
    }

    public String logout() {
        try {
            facade.logout();
            repl.setState(new PreLoginClient(facade, repl));
            return "Signed out.\n";
        } catch (Exception e) {
            return e.getMessage() + "\n";
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
        sb.append("\nExpected <Command> got \"").append(failedCommand).append("\"\n");

        return sb.toString();
    }

    private int generateIndex() {
        return gamesListIndex++;
    }

    private GameData getGame(int id) throws Exception {
        GameData game = null;
        try{
            Map<String, Collection<GameData>> games = facade.listGames();
            Collection<GameData> results = games.get("games");
            for(GameData result : results) {
                if(result.gameID() == id) {
                    game = result;
                }
            }
            return game;
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }
}
