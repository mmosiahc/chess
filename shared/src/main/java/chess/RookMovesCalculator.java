package chess;

import java.util.ArrayList;
import java.util.Collection;

public class RookMovesCalculator implements PieceMovesCalculator{
    private static final int[][] DIRECTIONS = {
            {1,0}, {-1,0}, {0,1}, {0,-1}
    };

    public boolean checkPosition(ChessBoard board, ChessPosition position) {
        return board.getPiece(position) == null;
    }

    public boolean inBounds(ChessPosition position) {
        int r = position.getRow();
        int c = position.getColumn();
        return (r >= ChessBoard.MIN_SIZE && r <= ChessBoard.MAX_SIZE) && (c >= ChessBoard.MIN_SIZE && c <= ChessBoard.MAX_SIZE);
    }

    public boolean isBlack(ChessBoard board, ChessPosition position) {
        return board.getPiece(position).getTeamColor() == ChessGame.TeamColor.BLACK;
    }

    public boolean isWhite(ChessBoard board, ChessPosition position) {
        return board.getPiece(position).getTeamColor() == ChessGame.TeamColor.WHITE;
    }

    public ChessPosition getPosition(ChessPosition position, int row, int col) {
        return new ChessPosition(position.getRow() + row, position.getColumn() + col);
    }

    private boolean validateAndAddPosition(ChessBoard board, ChessPosition sPosition, ChessPosition ePosition, boolean blocked, Collection<ChessMove> moves) {
        if(inBounds(ePosition)) {
            if(checkPosition(board, ePosition)) {
                moves.add(new ChessMove(sPosition, ePosition, null));
            } else if (isWhite(board, sPosition) && isBlack(board,ePosition)) {
                moves.add(new ChessMove(sPosition, ePosition, null));
                blocked = true;
            } else if (isBlack(board, sPosition) && isWhite(board, ePosition)) {
                moves.add(new ChessMove(sPosition, ePosition, null));
                blocked = true;
            }else {blocked = true;}
        }
        return blocked;
    }

    private void moveInDirection(ChessBoard board, ChessPosition position, int rowDir, int colDir, Collection<ChessMove> moves) {
        boolean blocked;
        for(int i = ChessBoard.MIN_SIZE; i < ChessBoard.MAX_SIZE; i++) {
            ChessPosition nextSpace = getPosition(position, i * rowDir, i * colDir);
            blocked = validateAndAddPosition(board, position, nextSpace, false, moves);
            if(blocked) {break;}
        }
    }

    public Collection<ChessMove> calculateMoves (ChessBoard board, ChessPosition position, ChessPiece piece) {
        final Collection<ChessMove> moves = new ArrayList<>();
        for(int[] dir: DIRECTIONS) {
            moveInDirection(board, position, dir[0], dir[1], moves);
        }
        return moves;
    }
}
