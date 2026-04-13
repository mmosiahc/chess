package websocket;

import jakarta.websocket.Session;
import websocket.messages.NotificationMessage;

import java.io.IOException;
import java.util.HashMap;

public class ConnectionManager {
    public final HashMap<Integer, Session> wsConnections = new HashMap<>();

    public void add(Integer gameID, Session session) {
        wsConnections.put(gameID, session);
    }

    public void remove(Integer gameID) {
        wsConnections.remove(gameID);
    }

    public void broadcast(Session excludeSession, NotificationMessage message) throws Exception {
        try {
            String msg = message.getMessage();
            for(Session c : wsConnections.values()) {
                if(c.isOpen()) {
                    if(!c.equals(excludeSession)) {
                        c.getBasicRemote().sendText(msg);
                    }
                }
            }
        } catch (IOException e) {
            throw new Exception("Failed to send websocket notification");
        }

    }
}
