package client;

import chess.*;
import model.GameData;
import ui.DrawChessBoard;

import java.util.Arrays;
import java.util.Collection;
import java.util.Scanner;

public class GameplayClient implements ChessClient{
    private final ServerFacade facade;
    private final Repl repl;
    private final String username;
    private final boolean isObserver;
    private ChessGame.TeamColor teamColor;
    private  boolean perspective = true;
    private GameData gameData;
    static DrawChessBoard boardPrinter;

    public GameplayClient(ServerFacade facade, Repl repl, GameData game, String username, boolean isObserver) {
        this.facade = facade;
        this.repl = repl;
        this.gameData = game;
        this.username = username;
        this.isObserver = isObserver;
        boardPrinter = new DrawChessBoard(game.game());
        setTeamColor();
        setPerspective();
    }

    public String eval(String input) {
        try {
            String[] tokens = input.toLowerCase().split(" ");
            String cmd = (tokens.length > 0) ? tokens[0] : "help";
            String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);
            if(cmd.startsWith("-")) {
                return switch (cmd) {
                    case "-r" -> redraw();
                    case "-hi" -> highlight(params);
                    case "-m" -> move(params);
                    case "-l" -> leave();
                    case "-rs" -> resign(params);
                    case "-h" -> help();
                    default -> failed(cmd);
                };
            }else {
                return switch (cmd) {
                    case "redraw" -> redraw();
                    case "highlight" -> highlight(params);
                    case "move" -> move(params);
                    case "leave" -> leave();
                    case "resign" -> resign(params);
                    case "help" -> help();
                    default -> failed(cmd);
                };
            }
        } catch (Exception ex) {
            return ex.getMessage() + "\n";
        }
    }


    String redraw() {
        boardPrinter = new DrawChessBoard(gameData.game());
        boardPrinter.drawBoardFromGame(perspective);
        return "";
    }

    private void setPerspective() {
        if(!isObserver && !username.equals(gameData.whiteUsername())) {
            perspective = false;
        }
    }

    public String highlight(String... params) {
        if (params.length < 1) {
            return "Expected <COORDINATE> (e.g., a2)\n";
        }
        String input = params[0].toLowerCase();
        if (isBadCoordinate(input)) {
            return String.format("'%s' is not a valid coordinate (e.g., \"a2\")\n", input);
        }
        ChessPosition startPos = getChessPosition(input);
        ChessGame game = gameData.game();
        ChessPiece piece = game.getBoard().getPiece(startPos);
        if (piece == null) {
            return "There is no piece at " + input + ".\n";
        }
        Collection<ChessMove> validMoves = game.validMoves(startPos);
        if (validMoves.isEmpty()) {
            return "The " + piece.getPieceType() + " at " + input + " has no legal moves.\n";
        }
        boardPrinter = new DrawChessBoard(game);
        boardPrinter.drawBoardWithHighlights(perspective, startPos, validMoves);
        return "";
    }

    /**
     * Allows the user to input what move they want to make.
     * The board is updated to reflect the result of the move,
     * and the board automatically updates on all clients involved in the game.
     *
     * @param params input from client
     */
    public String move(String... params) {
        //Check for observer
        if(isObserver) {return "Just observing. Remember?";}
        //Check if game is over
        if(gameData.game().isGameOver()) {return "The game is over. You cannot make any more moves.";}
        //Check whose turn it is
        ChessGame.TeamColor teamTurn = gameData.game().getTeamTurn();
        if(!teamTurn.equals(teamColor)) {return String.format("It is %s turn", teamTurn);
        }
        //Validate number of parameters
        if(params.length != 2) {
            return "Expected <start> <end>\n";
        }
        //Get parameters
        String start = params[0].toLowerCase();
        String end = params[1].toLowerCase();
        //Check if same positon
        if(start.equals(end)) {return String.format("%s and %s are the same position", start, end);}
        //Validate start coordinate syntax
        if(isBadCoordinate(start)) {return String.format("%s is not a valid start position (e.g., \"a2\")", start);
        }
        //Validate end coordinate syntax
        if(isBadCoordinate(end)) {return String.format("%s is not a valid end position (e.g., \"a2\")", end);
        }
        //Get start position
        ChessPosition startPosition = getChessPosition(start);
        //Get end position
        ChessPosition endPosition = getChessPosition(end);
        //Construct chess move
        ChessMove move = new ChessMove(startPosition, endPosition, null);
        ChessGame game = gameData.game();
        ChessBoard board = game.getBoard();
        ChessPiece startPiece = board.getPiece(move.getStartPosition());
        //Check for pawn promotion
        ChessPiece.PieceType promotionType = null;

        // Detect if we need a promotion piece
        if (isPromotion(startPiece, move.getEndPosition())) {
            System.out.print("Pawn Promotion! Choose piece (Q, R, B, N): ");
            Scanner scanner = new Scanner(System.in);
            String choice = scanner.nextLine().trim().toUpperCase();

            promotionType = switch (choice) {
                case "R" -> ChessPiece.PieceType.ROOK;
                case "B" -> ChessPiece.PieceType.BISHOP;
                case "N" -> ChessPiece.PieceType.KNIGHT;
                default -> ChessPiece.PieceType.QUEEN; // Default to Queen
            };
            move = new ChessMove(startPosition, endPosition, promotionType);
        }
        //Validate move
        String validateMsg = chessMoveValidation(params, move);
        if(!validateMsg.isEmpty()) {return validateMsg;}
        facade.makeMove(gameData.gameID(), move);
        return "";
    }


    public String leave() {
        try {
            repl.setState(new PostLoginClient(facade, repl, username));
            facade.sendLeaveCommand(gameData.gameID());
            return String.format("You left \"" + gameData.gameName() + "\"\n");
        } catch (Exception e) {
            return e.getMessage() + "\n";
        }
    }

    public String resign(String... params) {
        if (isObserver) {return "Your an observer";}
        //Check if game is over
        if(gameData.game().isGameOver()) {return "The game is over.\n";}
        String response = "";
        if(params.length == 0) {
            return "Are you sure you want to resign? [y/n]\n";
        }
        this.resignConfirmed();
        return response;
    }

    public void resignConfirmed() {
        facade.sendResignCommand(gameData.gameID());
    }


    public String help() {
        return String.format("%-35s | %s%n", "Command", "Description") +
                "-".repeat(55) + "\n" +
                String.format("%-35s | %s%n", "redraw (-r)", "Redraws the chess board") +
                String.format("%-35s | %s%n", "highlight (-hi)", "Show legal moves for a piece") +
                String.format("%-35s | %s%n", "move <FROM> <TO> (-m)", "Make a move") +
                String.format("%-35s | %s%n", "leave (-l)", "Exit the game") +
                String.format("%-35s | %s%n", "resign (-rs)", "Forfeit the game") +
                String.format("%-35s | %s%n", "help (-h)", "Show these options again");
    }

    public String failed(String failedCommand) {
        return String.format("Expected <Command> got \"%s\" | (-h) for help\n", failedCommand);
    }

    public void updateGameState(ChessGame game) {
        GameData g = this.gameData;
        this.gameData = new GameData(g.gameID(), g.whiteUsername(), g.blackUsername(), g.gameName(), game);
        setTeamColor();
        setPerspective();
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

    private String chessMoveValidation(String[] params, ChessMove move) {
        String validationMsg = ""; //Passes validation
        //Get parameters
        String start = params[0];
        String end = params[1];
        //Client side validation
        ChessGame game = gameData.game();
        ChessBoard board = game.getBoard();
        ChessPiece startPiece = board.getPiece(move.getStartPosition());
        ChessPiece endPiece = board.getPiece(move.getEndPosition());
        //Check for no piece
        if(startPiece == null) {return String.format("No piece at %s\n", start);}
        //Check for wrong team
        ChessGame.TeamColor pieceColor = startPiece.getTeamColor();
        if(!teamColor.equals(pieceColor)) {
            return String.format("Wrong team. Piece at %s is %s\n", start, pieceColor);
        }
        //Capture own team
        if(endPiece != null) {
            if(endPiece.getTeamColor().equals(teamColor)) {return String.format("Piece at %s is your team's piece\n", end);}
        }
        return validationMsg;
    }

    private boolean isPromotion(ChessPiece piece, ChessPosition end) {
        if (piece.getPieceType() != ChessPiece.PieceType.PAWN) return false;
        int row = end.getRow();
        return (piece.getTeamColor() == ChessGame.TeamColor.WHITE && row == 8) ||
                (piece.getTeamColor() == ChessGame.TeamColor.BLACK && row == 1);
    }

    private void setTeamColor() {
        if(!isObserver) {
            if(gameData.whiteUsername() != null) {
                if(username.equals(gameData.whiteUsername())) {
                    teamColor = ChessGame.TeamColor.WHITE;
                }
            }
            if(gameData.blackUsername() != null) {
                if(username.equals(gameData.blackUsername())) {
                    teamColor = ChessGame.TeamColor.BLACK;
                }
            }
        }
    }
}
