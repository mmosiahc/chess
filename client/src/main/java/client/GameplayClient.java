package client;

import chess.*;
import model.GameData;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class GameplayClient implements ChessClient{
    private final ServerFacade facade;
    private final Repl repl;
    private final boolean isWhite = Repl.username.equals(gameData.whiteUsername());
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
                    case "-rs" -> resign();
                    default -> help(cmd);
                };
            }else {
                return switch (cmd) {
                    case "redraw" -> redraw();
                    case "highlight" -> highlight();
                    case "move" -> move(params);
                    case "leave" -> leave();
                    case "resign" -> resign();
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

    /**
     * Allows the user to input what move they want to make.
     * The board is updated to reflect the result of the move,
     * and the board automatically updates on all clients involved in the game.
     *
     * @param params input from client
     */
    public String move(String... params) {
        //Validate number of parameters
        if(params.length != 2) {
            return "Expected <start> <end>\n";
        }
        //Get parameters
        String start = params[0];
        String end = params[1];
        //Validate start coordinate
        if(isBadCoordinate(start)) {
            return "\"" + start + "\" is not a valid start position  (e.g., \"a2\")";
        }
        //Validate end coordinate
        if(isBadCoordinate(end)) {
            return "\"" + end + "\" is not a valid end position  (e.g., \"a2\")";
        }
        //Get start position
        ChessPosition startPosition = getChessPosition(start);
        //Get end position
        ChessPosition endPosition = getChessPosition(end);
        //Construct chess move
        ChessMove move = new ChessMove(startPosition, endPosition, null);
        //Client side validation
        ChessGame game = gameData.game();
        ChessBoard board = game.getBoard();
        ChessPiece piece = board.getPiece(startPosition);
        if(piece == null) {return "No piece at \"" + start + "\"";}
        ChessGame.TeamColor pieceColor = piece.getTeamColor();
        if(isWhite && pieceColor == ChessGame.TeamColor.BLACK) {
            return String.format("Wrong team. Piece at %s is %s", start, pieceColor);
        } else if (!isWhite && pieceColor == ChessGame.TeamColor.WHITE) {
            return String.format("Wrong team. Piece at %s is %s", start, pieceColor);
        }
//        game.is
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

    public String resign() {
        return "You resigned\n";
    }


    public String help(String failedCommand) {
        StringBuilder sb = new StringBuilder();

        sb.append(String.format("%-35s | %s%n", "Command", "Description"));
        sb.append("-".repeat(55)).append("\n");

        sb.append(String.format("%-35s | %s%n", "redraw (-r)", "Redraws the chess board"));
        sb.append(String.format("%-35s | %s%n", "highlight (-hi)", "Show legal moves for a piece"));
        sb.append(String.format("%-35s | %s%n", "move <FROM> <TO> (-m)", "Make a move"));
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

    private boolean isBadCoordinate(String s) {
        return !s.matches("[a-h][1-8]");
    }

    private ChessPosition getChessPosition(String coordinate) {
        //Construct chess position
        int col = coordinate.charAt(0) - 'a' + 1;
        int row = coordinate.charAt(1) - '0';
        return new ChessPosition(row, col);
    }
}
