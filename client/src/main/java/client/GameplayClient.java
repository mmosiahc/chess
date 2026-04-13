package client;

import model.GameData;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class GameplayClient implements ChessClient{
    private final ServerFacade facade;
    private final Repl repl;
    static GameData gameData;
//    static DrawChessBoard boardPrinter;
    private final HashMap<Integer, Integer> gamesList = new HashMap<>();
    private int gamesListIndex = 1;

    public GameplayClient(ServerFacade facade, Repl repl, GameData game) {
        this.facade = facade;
        this.repl = repl;
        GameplayClient.gameData = game;
//        boardPrinter = new DrawChessBoard(game.game());
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
                    case "-r" -> redraw();
                    case "-hi" -> highlight();
                    case "-m" -> move(params);
                    case "-l" -> leave();
                    case "-rs" -> logout();
                    default -> help(cmd);
                };
            }else {
                return switch (cmd) {
                    case "redraw" -> redraw();
                    case "highlight" -> highlight();
                    case "move" -> move(params);
                    case "leave" -> leave();
                    case "resign" -> logout();
                    default -> help(cmd);
                };
            }
        } catch (Exception ex) {
            return ex.getMessage() + "\n";
        }
    }


    public String redraw(String... params) {
        return GameplayClient.gameData.game().toString();
    }

    public String highlight() {
        return "Valid moves\n";
    }

    public String move(String... params) {
        return "Made move\n";
    }

    public String leave() {
        try {
            repl.setState(new PostLoginClient(facade, repl));
            facade.sendLeaveCommand(Repl.username, gameData.gameID());
            return String.format("You left \"" + gameData.gameName() + "\"\n");
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

        sb.append(String.format("%-35s | %s%n", "redraw (-r)", "Redraws the chess board"));
        sb.append(String.format("%-35s | %s%n", "highlight (-hi)", "Show legal moves for a piece"));
        sb.append(String.format("%-35s | %s%n", "move (-m)", "Make a move"));
        sb.append(String.format("%-35s | %s%n", "leave (-l)", "Exit the game"));
        sb.append(String.format("%-35s | %s%n", "resign (-rs)", "Forfeit the game"));
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

    private String getGameName(int id) {
        String gameName = null;
        try{
            Map<String, Collection<GameData>> games = facade.listGames();
            Collection<GameData> results = games.get("games");
            for(GameData result : results) {
                if(result.gameID() == id) {
                    gameName = result.gameName();
                }
            }
            return gameName;
        } catch (Exception e) {
            return e.getMessage() + "\n";
        }
    }
}
