package websocket.messages;

import java.util.Objects;

/**
 * Represents a Message the server can send through a WebSocket
 * <p>
 * Note: You can add to this class, but you should not alter the existing
 * methods.
 */
public class NotificationMessage extends ServerMessage {
    final String notification;

    public NotificationMessage(ServerMessageType type, String message) {
        super(type);
        this.notification = message;
    }

    public String getMessage() {
        return notification;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof NotificationMessage that)) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        return Objects.equals(notification, that.notification);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), notification);
    }
}
