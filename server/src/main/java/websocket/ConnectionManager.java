package websocket;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.LoadGameMessage;
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

    public void sendLoadGame(LoadGameMessage message, Session session) throws Exception {
        try {
            String jsonLoadGame = new Gson().toJson(message);
            session.getRemote().sendString(jsonLoadGame);
        } catch (IOException e) {
            throw new Exception("Failed to send load game message");
        }
    }

    public void broadcast(Session excludeSession, NotificationMessage message) throws Exception {
        try {
            String msg = message.getMessage();
            for(Session c : wsConnections.values()) {
                if(c.isOpen()) {
                    if(!c.equals(excludeSession)) {
                        c.getRemote().sendString(msg);
                    }
                }
            }
        } catch (IOException e) {
            throw new Exception("Failed to send websocket notification");
        }

    }
}
