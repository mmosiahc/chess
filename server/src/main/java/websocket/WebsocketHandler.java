package websocket;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import io.javalin.websocket.*;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import org.jetbrains.annotations.NotNull;
import service.GameService;
import websocket.commands.*;
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
    public void handleClose(@NotNull WsCloseContext ctx) throws Exception {
        System.out.println("Websocket closed");
    }

    @Override
    public void handleConnect(@NotNull WsConnectContext ctx) throws Exception {
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
        connections.broadcast(session, notification);
        //Get game data from database
        GameData gameData = gameService.getGame(command.getGameID());
        //Prepare load game message to client
        LoadGameMessage loadGame = new LoadGameMessage(ServerMessage.ServerMessageType.LOAD_GAME, gameData);
        //Send load game message
        connections.sendLoadGame(loadGame, session);
    }

    private void makeMove(MoveCommand command, Session session) {

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
        //Remove player from game
        GameData g = gameService.getGame(command.getGameID());
        //Check for team color
        boolean playingWhite = g.whiteUsername().equals(command.getUsername());
        if(playingWhite) {
            g = new GameData(g.gameID(), null, g.blackUsername(), g.gameName(), g.game());
        } else {
            g = new GameData(g.gameID(), g.whiteUsername(), null, g.gameName(), g.game());
        }
        //Update game data
        gameService.updateGame(g);
        //Broadcast websocket notification
        String msg = String.format("\n%s left the game", command.getUsername());
        NotificationMessage notification = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, msg);
        connections.broadcast(session, notification);
        //Remove websocket connection
        connections.remove(g.gameID(), session);
    }

    private void resign(ResignCommand command) {

    }
}
