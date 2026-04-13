package client;

import websocket.messages.ServerMessage;

public interface ServerMessageObserver {
    void notifyClient(ServerMessage message, String json);
    void printMessage(ServerMessage message, String json);
}
