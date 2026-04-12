package client;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import jakarta.websocket.*;
import websocket.messages.ErrorMessage;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class WebsocketCommunicator extends Endpoint {
    private final ServerMessageObserver messageObserver;
    Session session;

    public WebsocketCommunicator(String url, ServerMessageObserver observer) {
        try {
            url = url.replace("http", "ws");
            URI socketURI = new URI(url + "/ws");
            this.messageObserver = observer;

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketURI);

            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String s) {
                    try {
                        ServerMessage message = new Gson().fromJson(s, ServerMessage.class);
                        observer.notifyClient(message);
                    } catch (JsonSyntaxException e) {
                        observer.notifyClient(new ErrorMessage(ServerMessage.ServerMessageType.ERROR, e.getMessage()));
                    }
                }
            });
        } catch (URISyntaxException | DeploymentException | IOException e) {
            throw new RuntimeException(e);
        }
    }




    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {

    }
}
