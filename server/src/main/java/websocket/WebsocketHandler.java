package websocket;

import chess.*;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import exceptions.BadRequestException;
import exceptions.DataAccessException;
import exceptions.UnauthorizedException;
import io.javalin.websocket.*;
import model.AuthData;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import org.jetbrains.annotations.NotNull;
import service.GameService;
import websocket.commands.*;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

public class WebsocketHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler {
    private final ConnectionManager connections;
    private final GameService gameService;

    public WebsocketHandler(GameService service) {
        this.gameService = service;
        this.connections = new ConnectionManager();
    }

    @Override
    public void handleClose(@NotNull WsCloseContext ctx) {
        System.out.println("Websocket closed");
    }

    @Override
    public void handleConnect(@NotNull WsConnectContext ctx) {
        System.out.println("Websocket connected");
        ctx.enableAutomaticPings();
    }

    @Override
    public void handleMessage(@NotNull WsMessageContext ctx) throws Exception {
        Session session = ctx.session;
        try {
            UserGameCommand message = new Gson().fromJson(ctx.message(), UserGameCommand.class);
            switch (message.getCommandType()) {
                case CONNECT -> {
                    ConnectCommand connect = new Gson().fromJson(ctx.message(), ConnectCommand.class);
                    joinPlayer(connect, session);
                }
                case MAKE_MOVE -> {
                    MoveCommand makeMove = new Gson().fromJson(ctx.message(), MoveCommand.class);
                    makeMove(makeMove, session);
                }
                case LEAVE -> {
                    LeaveCommand leave = new Gson().fromJson(ctx.message(), LeaveCommand.class);
                    leave(leave, session);
                }
                case RESIGN -> resign((ResignCommand) message);
            }
        } catch (JsonSyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Adds websocket connection for user
     * and broadcasts actions to all users
     * of the game
     *
     * @param command information sent from client
     * @param session websocket handle
     */
    private void joinPlayer(ConnectCommand command, Session session) throws Exception {
        try {
            //Validate User authorization
            AuthData authData = gameService.getAuthData(command.getAuthToken());
            if(authData == null) {throw new UnauthorizedException();}
            //Validate game request
            GameData gameData = gameService.getGame(command.getGameID());
            if(gameData == null) {throw new BadRequestException();}
        } catch (DataAccessException e) {
            connections.sendServerMessage(new ErrorMessage(ServerMessage.ServerMessageType.ERROR, e.getMessage()), session);
            throw new Exception(e.getMessage());
        }
        String msg;
        //Add session to set of connections
        connections.add(command.getGameID(), session);
        //Prepare message depending on player or observer
        if(command.getColor() != null) {
            msg = String.format("\n%s joined the game as %s", command.getUsername(), command.getColor());
        } else {
            msg = String.format("\n%s joined the game as an observer", command.getUsername());
        }
        //Make notification
        NotificationMessage notification = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, msg);
        //Broadcast notification to all users in game
        connections.broadcastExclude(session, notification, command.getGameID());
        //Get game data from database
        GameData gameData = gameService.getGame(command.getGameID());
        //Prepare load game message to client
        LoadGameMessage loadGame = new LoadGameMessage(ServerMessage.ServerMessageType.LOAD_GAME, gameData);
        //Send load game message
        connections.sendServerMessage(loadGame, session);
    }

    /**
     * Validates the chess move. Updates game
     * state and saves it in the database. Sends
     * load game message to all clients in game.
     * Broadcasts move and status of check, checkmate,
     * and stalemate to clients.
     *
     * @param command chess move information sent from client
     * @param session websocket handle
     */
    private void makeMove(MoveCommand command, Session session) throws Exception {
        try {
            //Validate User authorization
            AuthData authData = gameService.getAuthData(command.getAuthToken());
            if(authData == null) {throw new UnauthorizedException();}
            //Validate game request
            GameData gameData = gameService.getGame(command.getGameID());
            if(gameData == null) {throw new BadRequestException();}
        } catch (DataAccessException e) {
            connections.sendServerMessage(new ErrorMessage(ServerMessage.ServerMessageType.ERROR, e.getMessage()), session);
            throw new Exception(e.getMessage());
        }
        //Get game by game id
        GameData g = gameService.getGame(command.getGameID());
        ChessGame game = g.game();
        if(game.isGameOver()) {
            connections.sendServerMessage(new ErrorMessage(ServerMessage.ServerMessageType.ERROR, "Invalid Move: game is over\n"), session);
            throw new InvalidMoveException("Invalid Move: Game is over\n");
        }
        //Validate correct team
        ChessBoard originalBoard = g.game().getBoard();
        ChessPosition moveStartPosition = command.getMove().getStartPosition();
        ChessGame.TeamColor userColor = originalBoard.getPiece(moveStartPosition).getTeamColor();
        ChessGame.TeamColor teamTurn = g.game().getTeamTurn();
        if(userColor.equals(teamTurn)) {
            String errorMsg = String.format("Invalid Move: piece at %s is %s\n", moveStartPosition, teamTurn);
            connections.sendServerMessage(new ErrorMessage(ServerMessage.ServerMessageType.ERROR, errorMsg), session);
            throw new InvalidMoveException(errorMsg);
        }
        //Prepare move notification
        String moveMessage = prepareMoveMsg(originalBoard, command);
        NotificationMessage moveNotification = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, moveMessage);
        try {
            //Make move in game
            game.makeMove(command.getMove());
        } catch (InvalidMoveException e) {
            connections.sendServerMessage(new ErrorMessage(ServerMessage.ServerMessageType.ERROR, e.getMessage()), session);
            throw new Exception(e.getMessage());
        }

        //Save new game data to database
        GameData newG = new GameData(g.gameID(), g.whiteUsername(), g.blackUsername(), g.gameName(), game);
        gameService.updateGame(newG);
        //Prepare load game message to client
        LoadGameMessage loadGame = new LoadGameMessage(ServerMessage.ServerMessageType.LOAD_GAME, newG);
        //Broadcast load game message
        connections.broadcastAll(loadGame, command.getGameID());
        //Broadcast move notification
        connections.broadcastExclude(session, moveNotification, command.getGameID());
        //Prepare game status notification
        String status = getStatusNotification(newG, command);
        if(status != null) {
            //Broadcast game status notification
            NotificationMessage gameStatus = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, status);
            connections.broadcastAll(gameStatus, command.getGameID());
        }
    }

    private String getStatusNotification(GameData gameData, MoveCommand command) {
        ChessGame.TeamColor opponentColor;
        ChessGame game = gameData.game();
        ChessBoard board = game.getBoard();
        ChessGame.TeamColor userColor = board.getPiece(command.getMove().getEndPosition()).getTeamColor();
        if(userColor == ChessGame.TeamColor.BLACK) {
            opponentColor = ChessGame.TeamColor.WHITE;
        } else {
            opponentColor = ChessGame.TeamColor.BLACK;
        }
        if (game.isInCheckmate(opponentColor)) {
            return String.format("\nCheckmate! %s wins.", command.getUsername());
        }
        if (game.isInStalemate(opponentColor)) {
            return "\nStalemate! The game is a draw.";
        }
        if (game.isInCheck(opponentColor)) {
            if(opponentColor == ChessGame.TeamColor.BLACK) {
                return String.format("\n%s is in check!", gameData.blackUsername());
            }
            return String.format("\n%s is in check!", gameData.whiteUsername());
        }
        return null; // No special status
    }

    private String prepareMoveMsg(ChessBoard board, MoveCommand command) {
        ChessPiece.PieceType type = board.getPiece(command.getMove().getStartPosition()).getPieceType();
        String start = getRankAndFile(command.getMove().getStartPosition());
        String end = getRankAndFile(command.getMove().getEndPosition());
        return String.format("\n%s moved %s at %s to %s", command.getUsername(), type, start, end);
    }

    private String getRankAndFile(ChessPosition position) {
        int col = position.getColumn();
        int row = position.getRow();
        char file  = (char) ((char) col - 1 + 'a');
        char rank  = (char) ((char) row + '0');
        return "" + file + rank;
    }

    /**
     * Removes user from the game
     * and updatest the game.
     * Broadcasts notification to all
     * users in the game. Removes websocket
     * connection.
     *
     * @param command information sent from client
     * @param session websocket handle
     */
    private void leave(LeaveCommand command, Session session) throws Exception {
        //Validate User authorization
        AuthData authData = gameService.getAuthData(command.getAuthToken());
        if(authData == null) {throw new UnauthorizedException();}
        //Validate game request
        GameData gameData = gameService.getGame(command.getGameID());
        if(gameData == null) {throw new BadRequestException();}
        //Get game by game id
        GameData g = gameService.getGame(command.getGameID());
        //Check for player or observer
        if(!command.getIsObserver()) {
            if(command.getTeamColor() == ChessGame.TeamColor.WHITE) {
                g = new GameData(g.gameID(), null, g.blackUsername(), g.gameName(), g.game());
            } else {
                g = new GameData(g.gameID(), g.whiteUsername(), null, g.gameName(), g.game());
            }
            //Update game data
            gameService.updateGame(g);
        }
        //Broadcast websocket notification
        String msg = String.format("\n%s left the game", command.getUsername());
        NotificationMessage notification = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, msg);
        connections.broadcastExclude(session, notification, command.getGameID());
        //Remove websocket connection
        connections.remove(g.gameID(), session);
    }

    private void resign(ResignCommand command) {

    }
}
