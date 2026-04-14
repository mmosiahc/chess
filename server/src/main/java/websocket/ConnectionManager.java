package websocket;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class ConnectionManager {
    public final HashMap<Integer, Set<Session>> wsConnections = new HashMap<>();

    public void add(Integer gameID, Session session) {
        wsConnections.computeIfAbsent(gameID, k -> new HashSet<>()).add(session);
    }

    public void remove(Integer gameID, Session removeSession) {
        Set<Session> sessions = wsConnections.get(gameID);
        if(sessions != null) {
            sessions.remove(removeSession);
        }
        //Clean up if game has no more sessions
        assert sessions != null;
        if(sessions.isEmpty()) {
            wsConnections.remove(gameID);
        }
    }

    public void sendServerMessage(ServerMessage message, Session session) throws Exception {
        try {
            String jsonMessage = new Gson().toJson(message);
            session.getRemote().sendString(jsonMessage);
        } catch (IOException e) {
            throw new Exception("Failed to send websocket message");
        }
    }

    public void broadcastExclude(Session excludeSession, ServerMessage message) throws Exception {
        try {
            String json = new Gson().toJson(message, ServerMessage.class);
            for(Set<Session> sessions : wsConnections.values()) {
                for(Session s : sessions) {
                    if(s.isOpen()) {
                        if(!s.equals(excludeSession)) {
                            s.getRemote().sendString(json);
                        }
                    }
                }
            }
        } catch (IOException e) {
            throw new Exception("Failed to send websocket notification");
        }
    }

    public void broadcastAll(ServerMessage message) throws Exception {
        try {
            String json = new Gson().toJson(message, ServerMessage.class);
            for(Set<Session> sessions : wsConnections.values()) {
                for(Session s : sessions) {
                    if(s.isOpen()) {
                        s.getRemote().sendString(json);
                    }
                }
            }
        } catch (IOException e) {
            throw new Exception("Failed to send websocket notification");
        }
    }
}
