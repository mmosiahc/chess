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

    private void joinPlayer(ConnectCommand command, Session session) throws Exception {
        connections.add(command.getGameID(), session);
        var msg = String.format("%s joined the game as %s\n", command.getUsername(), command.getColor());
        NotificationMessage notification = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, msg);
        connections.broadcast(session, notification);
        GameData gameData = gameService.getGame(command.getGameID());
        LoadGameMessage loadGame = new LoadGameMessage(ServerMessage.ServerMessageType.LOAD_GAME, gameData);
        connections.sendLoadGame(loadGame, session);
    }

    private void makeMove(MoveCommand command, Session session) {

    }

    private void leave(LeaveCommand command, Session session) throws Exception {
        //Remove player from game
        GameData g = gameService.getGame(command.getGameID());
        boolean playingWhite = g.whiteUsername().equals(command.getUsername());
        if(playingWhite) {
            g = new GameData(g.gameID(), null, g.blackUsername(), g.gameName(), g.game());
        } else {
            g = new GameData(g.gameID(), g.whiteUsername(), null, g.gameName(), g.game());
        }
        gameService.updateGame(g);
        //Send websocket notification
        String msg = String.format("%s left the game", command.getUsername());
        NotificationMessage notification = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, msg);
        connections.broadcast(session, notification);
    }

    private void resign(ResignCommand command) {

    }
}
