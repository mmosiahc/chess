package websocket;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import io.javalin.websocket.*;
import jakarta.websocket.Session;
import model.GameData;
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
        Session session = (Session) ctx.session;
        try {
            UserGameCommand message = new Gson().fromJson(ctx.message(), UserGameCommand.class);
            switch (message.getCommandType()) {
                case CONNECT -> joinPlayer((ConnectCommand) message, session);
                case MAKE_MOVE -> makeMove((MoveCommand) message);
                case LEAVE -> leave((LeaveCommand) message);
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
    private void makeMove(MoveCommand command) {

    }
    private void leave(LeaveCommand command) {

    }
    private void resign(ResignCommand command) {

    }
}
