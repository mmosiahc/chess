package websocket.commands;

import chess.ChessGame;

import java.util.Objects;

/**
 * Represents a command a user can send the server over a websocket
 * <p>
 * Note: You can add to this class, but you should not alter the existing
 * methods.
 */
public class LeaveCommand extends UserGameCommand {
    private final String username;
    private final boolean isObserver;
    private final ChessGame.TeamColor teamColor;
    public LeaveCommand(CommandType type, String token, Integer gameID, String username, boolean isObserver, ChessGame.TeamColor teamColor) {
        super(type, token, gameID);
        this.username = username;
        this.isObserver = isObserver;
        this.teamColor = teamColor;
    }

    public String getUsername() {
        return username;
    }

    public boolean getIsObserver() {
        return isObserver;
    }

    public ChessGame.TeamColor getTeamColor() {
        return teamColor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof LeaveCommand that)) {
            return false;
        }
        return getCommandType() == that.getCommandType() &&
                Objects.equals(getAuthToken(), that.getAuthToken()) &&
                Objects.equals(getGameID(), that.getGameID());
    }

}
