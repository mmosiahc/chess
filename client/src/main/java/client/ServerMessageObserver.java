package client;

import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;

public interface ServerMessageObserver {
    void notifyClientLoadMessage(LoadGameMessage message);
    void notifyClientNotification(NotificationMessage message);
    void notifyClientError(ErrorMessage message);
}
