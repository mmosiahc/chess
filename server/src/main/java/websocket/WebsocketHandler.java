package websocket;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import io.javalin.websocket.*;
import org.jetbrains.annotations.NotNull;
import websocket.commands.*;

public class WebsocketHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler {
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
        try {
            UserGameCommand message = new Gson().fromJson(ctx.message(), UserGameCommand.class);
            switch (message.getCommandType()) {
                case CONNECT -> joinPlayer((ConnectCommand) message);
                case MAKE_MOVE -> makeMove((MoveCommand) message);
                case LEAVE -> leave((LeaveCommand) message);
                case RESIGN -> resign((ResignCommand) message);
            }
        } catch (JsonSyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    private void joinPlayer(ConnectCommand command) {

    }
    private void makeMove(MoveCommand command) {

    }
    private void leave(LeaveCommand command) {

    }
    private void resign(ResignCommand command) {

    }
}
