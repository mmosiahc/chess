package websocket.messages;

import chess.ChessGame;

/**
 * Represents a Message the server can send through a WebSocket
 * <p>
 * Note: You can add to this class, but you should not alter the existing
 * methods.
 */
public class LoadGameMessage extends ServerMessage {
    final String message;
    ChessGame game;

    public LoadGameMessage(ServerMessageType type, String message, ChessGame game) {
        super(type);
        this.message = message;
        this.game = game;
    }

    public String getMessage() {
        return message;
    }

    public ChessGame getGame() {
        return game;
    }

}
