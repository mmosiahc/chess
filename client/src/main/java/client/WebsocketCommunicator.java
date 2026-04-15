package client;

import com.google.gson.Gson;
import jakarta.websocket.*;
import websocket.commands.ConnectCommand;
import websocket.commands.LeaveCommand;
import websocket.commands.MoveCommand;
import websocket.commands.ResignCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
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
                        switch (message.getServerMessageType()) {
                            case LOAD_GAME -> {
                                LoadGameMessage loadGame = new Gson().fromJson(s, LoadGameMessage.class);
                                observer.notifyClientLoadMessage(loadGame);
                            }
                            case NOTIFICATION -> {
                                NotificationMessage notification = new Gson().fromJson(s, NotificationMessage.class);
                                observer.notifyClientNotification(notification);
                            }
                            case ERROR -> {
                                ErrorMessage error = new Gson().fromJson(s, ErrorMessage.class);
                                observer.notifyClientError(error);
                            }
                        }
                    } catch (Exception e) {
                        observer.notifyClientError(new ErrorMessage(ServerMessage.ServerMessageType.ERROR, e.getMessage()));
                    }
                }
            });
        } catch (URISyntaxException | DeploymentException | IOException e) {
            observer.notifyClientError(new ErrorMessage(ServerMessage.ServerMessageType.ERROR, e.getMessage()));

        }
    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {

    }

    public void playerJoins(ConnectCommand connect) {
        try {
            this.session.getBasicRemote().sendText(new Gson().toJson(connect));
        } catch (IOException e) {
            System.out.print(e.getMessage() + "\n");
            throw new RuntimeException(e);
        }
    }

    /**
     * Sends connect command for observer
     *
     * @param connect info to send
     */
    public void observerJoins(ConnectCommand connect) {
        try {
            this.session.getBasicRemote().sendText(new Gson().toJson(connect));
        } catch (IOException e) {
            System.out.print(e.getMessage() + "\n");
            throw new RuntimeException(e);
        }
    }

    /**
     * Sends leave command for a user to
     * leave the game.
     *
     * @param leave info to send to server
     */
    public void playerLeaves(LeaveCommand leave) {
        try {
            this.session.getBasicRemote().sendText(new Gson().toJson(leave));
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Sends resign command for a user to
     * forfeit the game.
     *
     * @param resign info to send to server
     */
    public void playerResigns(ResignCommand resign) {
        try {
            this.session.getBasicRemote().sendText(new Gson().toJson(resign));
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Sends make move command for player to
     * make a move in the game.
     *
     * @param move information to send to server
     */
    public void makeMove(MoveCommand move) {
        try {
            this.session.getBasicRemote().sendText(new Gson().toJson(move));
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
