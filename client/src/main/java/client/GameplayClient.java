package client;

import chess.ChessGame;
import data_transfer.JoinGameBody;
import model.GameData;
import ui.EscapeSequences;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class GameplayClient implements ChessClient{
    private final ServerFacade facade;
    private final Repl repl;
    private final ChessGame game;
    private final HashMap<Integer, Integer> gamesList = new HashMap<>();
    private int gamesListIndex = 1;

    public GameplayClient(ServerFacade facade, Repl repl, ChessGame game) {
        this.facade = facade;
        this.repl = repl;
        this.game = game;
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
                    case "-m" -> join(params);
                    case "-l" -> observe(params);
                    case "-rs" -> logout();
                    default -> help(cmd);
                };
            }else {
                return switch (cmd) {
                    case "redraw" -> redraw();
                    case "highlight" -> highlight();
                    case "move" -> join(params);
                    case "leave" -> observe(params);
                    case "resign" -> logout();
                    default -> help(cmd);
                };
            }
        } catch (Exception ex) {
            return ex.getMessage() + "\n";
        }
    }


    public String redraw(String... params) {
        try {
            return "Redrew board.\n";
        } catch (Exception e) {
            return e.getMessage() + "\n";
        }
    }

    public String highlight() {
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

    public String join(String... params) {
        if(params.length != 2) {
            return "Expected <game_id> <white|black>\n";
        }
        int id;
        ChessGame.TeamColor teamColor;
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
        String gameName = getGameName(id);
        try {
            JoinGameBody request = new JoinGameBody(teamColor, id);
            facade.joinGame(request);
            return (String.format("You joined \"" + gameName + "\" as %s\n", teamColor));
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
            String gameName = getGameName(id);
            if(gameName == null) {
                return String.format("Invalid game id \"%s\"\n", id);
            }
            return String.format("You joined \"" + gameName + "\" as an %s\n", "observer");
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
