package client;

import websocket.messages.ServerMessage;

public interface ServerMessageObserver {
    void notifyClient(ServerMessage message);
}
