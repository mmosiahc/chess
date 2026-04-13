package client;

import com.google.gson.Gson;
import jakarta.websocket.*;
import websocket.commands.ConnectCommand;
import websocket.commands.LeaveCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class WebsocketCommunicator extends Endpoint {
    private final ServerMessageObserver messageObserver;
    Session session;

    public WebsocketCommunicator(String url, ServerMessageObserver observer) {
            this.messageObserver = observer;
        try {
            url = url.replace("http", "ws");
            URI socketURI = new URI(url + "/ws");


            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketURI);

            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String s) {
                    try {
                        ServerMessage message = new Gson().fromJson(s, ServerMessage.class);
                        observer.notifyClient(message, s);
                    } catch (Exception e) {
                        observer.notifyClient(new ErrorMessage(ServerMessage.ServerMessageType.ERROR, e.getMessage()), null);
                    }
                }
            });
        } catch (URISyntaxException | DeploymentException | IOException e) {
            observer.notifyClient(new ErrorMessage(ServerMessage.ServerMessageType.ERROR, e.getMessage()), null);
        }
    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {

    }

    public void playerJoins(ConnectCommand connect) {
        try {
            this.session.getBasicRemote().sendText(new Gson().toJson(connect, ConnectCommand.class));
        } catch (IOException e) {
            System.out.print(e.getMessage() + "\n");
            throw new RuntimeException(e);
        }
    }

    public void playerLeaves(LeaveCommand leave) {
        try {
            this.session.getBasicRemote().sendText(new Gson().toJson(leave, LeaveCommand.class));
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }



}
