package websocket.messages;

/**
 * Represents a Message the server can send through a WebSocket
 * <p>
 * Note: You can add to this class, but you should not alter the existing
 * methods.
 */
public class ErrorMessage extends ServerMessage {
    final String errorMessage;

    public ErrorMessage(ServerMessageType type, String message) {
        super(type);
        this.errorMessage = message;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

}
