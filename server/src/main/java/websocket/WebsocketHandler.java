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

import java.util.Objects;

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
                case RESIGN -> {
                    ResignCommand resign = new Gson().fromJson(ctx.message(), ResignCommand.class);
                    resign(resign, session);
                }
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
        String user;
        GameData gameData;
        try {
            //Validate User authorization
            AuthData authData = gameService.getAuthData(command.getAuthToken());
            if(authData == null) {throw new UnauthorizedException();}
            //Set username
            user = authData.username();
            //Validate game request
            gameData = gameService.getGame(command.getGameID());
            if(gameData == null) {throw new BadRequestException();}
        } catch (DataAccessException e) {
            connections.sendServerMessage(new ErrorMessage(ServerMessage.ServerMessageType.ERROR, e.getMessage()), session);
            throw new Exception(e.getMessage());
        }
        String wUser = gameData.whiteUsername();
        String bUser = gameData.blackUsername();
        String msg;
        //Add session to set of connections
        connections.add(command.getGameID(), session);
        //Check user role
//        boolean isObserver = Objects.isNull(wUser) && Objects.isNull(bUser);
        ChessGame.TeamColor userColor = null;
        if(wUser != null) {
            if(user.equals(wUser)) {
                userColor = ChessGame.TeamColor.WHITE;
            }
        }
        if(bUser != null) {
            if(user.equals(bUser)) {
                userColor = ChessGame.TeamColor.BLACK;
            }
        }
        //Prepare message depending on player or observer
        if(Objects.nonNull(userColor)) {
            msg = String.format("[Player:%s] %s joined the game", user, userColor);
        } else {
            msg = String.format("[Observer:%s] joined the game", user);
        }
        //Make notification
        NotificationMessage notification = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, msg);
        //Broadcast notification to all users in game
        connections.broadcastExclude(session, notification, command.getGameID());
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
        ChessMove move = command.getMove();
        String user;
        GameData g;
        try {
            //Validate User authorization
            AuthData authData = gameService.getAuthData(command.getAuthToken());
            if(authData == null) {throw new UnauthorizedException();}
            //Set username
            user = authData.username();
            //Validate game request
            g = gameService.getGame(command.getGameID());
            if(g == null) {throw new BadRequestException();}
        } catch (DataAccessException e) {
            connections.sendServerMessage(new ErrorMessage(ServerMessage.ServerMessageType.ERROR, e.getMessage()), session);
            throw new Exception(e.getMessage());
        }
        //Check for observer
        boolean isObserver = !user.equals(g.whiteUsername()) && !user.equals(g.blackUsername());
        if(isObserver) {
            connections.sendServerMessage(new ErrorMessage(ServerMessage.ServerMessageType.ERROR, "Invalid Move: user is an observer\n"), session);
            throw new InvalidMoveException("Invalid Move: user is an observer\n");
        }
        //Check if game is over
        ChessGame game = g.game();
        if(game.isGameOver()) {
            connections.sendServerMessage(new ErrorMessage(ServerMessage.ServerMessageType.ERROR, "Invalid Move: game is over\n"), session);
            throw new InvalidMoveException("Invalid Move: Game is over\n");
        }
        //Determine user color
        ChessGame.TeamColor userColor = null;
        if(g.whiteUsername() != null) {
            if(user.equals(g.whiteUsername())) {
                userColor = ChessGame.TeamColor.WHITE;
            }
        }
        if(g.blackUsername() != null) {
            if(user.equals(g.blackUsername())) {
                userColor = ChessGame.TeamColor.BLACK;
            }
        }
        //Validate correct team
        ChessBoard originalBoard = g.game().getBoard();
        ChessPosition moveStartPosition = move.getStartPosition();
        ChessGame.TeamColor teamTurn = g.game().getTeamTurn();
        if(!userColor.equals(teamTurn)) {
            String errorMsg = String.format("Invalid Move: piece at %s is %s\n", moveStartPosition, teamTurn);
            connections.sendServerMessage(new ErrorMessage(ServerMessage.ServerMessageType.ERROR, errorMsg), session);
            throw new InvalidMoveException(errorMsg);
        }
        //Prepare move notification
        String moveMessage = prepareMoveMsg(originalBoard, move, user);
        NotificationMessage moveNotification = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, moveMessage);
        //Try to execute move
        try {
            //Execute move in game
            game.makeMove(move);
        } catch (InvalidMoveException e) {
            connections.sendServerMessage(new ErrorMessage(ServerMessage.ServerMessageType.ERROR, e.getMessage()), session);
            throw new Exception(e.getMessage());
        }

        //Update game data in database
        gameService.updateGame(g);
        //Prepare load game message to client
        LoadGameMessage loadGame = new LoadGameMessage(ServerMessage.ServerMessageType.LOAD_GAME, g);
        //Broadcast load game message
        connections.broadcastAll(loadGame, command.getGameID());
        //Broadcast move notification
        connections.broadcastExclude(session, moveNotification, command.getGameID());
        //Prepare game status notification
        String status = getStatusNotification(g, move, user);
        if(status != null) {
            //Broadcast game status notification
            NotificationMessage gameStatus = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, status);
            connections.broadcastAll(gameStatus, command.getGameID());
        }
    }

    private String getStatusNotification(GameData gameData, ChessMove move, String username) {
        ChessGame.TeamColor opponentColor;
        ChessGame game = gameData.game();
        ChessBoard board = game.getBoard();
        ChessGame.TeamColor userColor = board.getPiece(move.getEndPosition()).getTeamColor();
        if(userColor == ChessGame.TeamColor.BLACK) {
            opponentColor = ChessGame.TeamColor.WHITE;
        } else {
            opponentColor = ChessGame.TeamColor.BLACK;
        }
        if (game.isInCheckmate(opponentColor)) {
            return String.format("\nCheckmate! %s wins.", username);
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

    private String prepareMoveMsg(ChessBoard board, ChessMove move, String user) {
        ChessPiece.PieceType type = board.getPiece(move.getStartPosition()).getPieceType();
        String start = getRankAndFile(move.getStartPosition());
        String end = getRankAndFile(move.getEndPosition());
        return String.format("\n%s moved %s at %s to %s", user, type, start, end);
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
        String user;
        //Validate User authorization
        AuthData authData = gameService.getAuthData(command.getAuthToken());
        if(authData == null) {throw new UnauthorizedException();}
        //Validate game request
        GameData g = gameService.getGame(command.getGameID());
        if(g == null) {throw new BadRequestException();}
        //Set username
        user = authData.username();
        //Check user role
        boolean isObserver = !user.equals(g.whiteUsername()) && !user.equals(g.blackUsername());
        ChessGame.TeamColor userColor = null;
        if(g.whiteUsername() != null) {
            if(user.equals(g.whiteUsername())) {
                userColor = ChessGame.TeamColor.WHITE;
            }
        }
        if(g.blackUsername() != null) {
            if(user.equals(g.blackUsername())) {
                userColor = ChessGame.TeamColor.BLACK;
            }
        }
        //Update game data to reflect leaving
        if(!isObserver) {
            if(userColor == ChessGame.TeamColor.WHITE) {
                g = new GameData(g.gameID(), null, g.blackUsername(), g.gameName(), g.game());
            } else {
                g = new GameData(g.gameID(), g.whiteUsername(), null, g.gameName(), g.game());
            }
            //Update game data
            gameService.updateGame(g);
        }
        //Broadcast websocket notification
        String msg = String.format("\n%s left the game", user);
        NotificationMessage notification = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, msg);
        connections.broadcastExclude(session, notification, command.getGameID());
        //Remove websocket connection
        connections.remove(command.getGameID(), session);
    }

    /**
     * Marks the game as over and updatest the game.
     * Broadcasts notification to all
     * users in the game that the root client resigned.
     *
     * @param command information sent from client
     */
    private void resign(ResignCommand command, Session session) throws Exception {
        String user;
        //Validate User authorization
        AuthData authData = gameService.getAuthData(command.getAuthToken());
        if(authData == null) {throw new UnauthorizedException();}
        //Set username
        user = authData.username();
        //Validate game request
        GameData gameData = gameService.getGame(command.getGameID());
        if(gameData == null) {throw new BadRequestException();}
        //Check for observer
        boolean isObserver = !user.equals(gameData.whiteUsername()) && !user.equals(gameData.blackUsername());
        if(isObserver) {
            connections.sendServerMessage(new ErrorMessage(ServerMessage.ServerMessageType.ERROR, "User is an observer\n"), session);
            throw new InvalidMoveException("User is an observer\n");
        }
        //Check if game is already over
        ChessGame game = gameData.game();
        if(game.isGameOver()) {
            connections.sendServerMessage(new ErrorMessage(ServerMessage.ServerMessageType.ERROR, "Game is over\n"), session);
            throw new InvalidMoveException("Game is over\n");
        }
        //Mark game as over
        game.setGameOver(true);
        //Update game in database
        gameService.updateGame(gameData);
        //Broadcast websocket notification that player resigned
        String msg = String.format("\n%s forfeits the game", authData.username());
        NotificationMessage notification = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, msg);
        connections.broadcastAll(notification, command.getGameID());
    }
}
