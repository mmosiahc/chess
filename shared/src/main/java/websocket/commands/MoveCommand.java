package websocket.commands;

import chess.ChessMove;

import java.util.Objects;

/**
 * Represents a command a user can send the server over a websocket
 * <p>
 * Note: You can add to this class, but you should not alter the existing
 * methods.
 */
public class MoveCommand extends UserGameCommand {
    private final ChessMove move;
    private final String username;

    public MoveCommand(CommandType type, String token, Integer gameID, ChessMove move, String username) {
        super(type, token, gameID);
        this.move = move;
        this.username = username;
    }

    public ChessMove getMove() {
        return move;
    }

    public String getUsername() {return username;}

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof MoveCommand that)) {
            return false;
        }
        return getCommandType() == that.getCommandType() &&
                Objects.equals(getAuthToken(), that.getAuthToken()) &&
                Objects.equals(getGameID(), that.getGameID());
    }

}
