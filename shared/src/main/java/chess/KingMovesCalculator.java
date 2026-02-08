package chess;

import java.util.ArrayList;
import java.util.Collection;

public class KingMovesCalculator implements PieceMovesCalculator{
    private static final int[][] KING_MOVES = {
            {1,0}, {-1,0}, {0,1}, {0,-1}, {1,1}, {1,-1}, {-1,1}, {-1,-1}
    };

    public Collection<ChessMove> calculateMoves (ChessBoard board, ChessPosition position, ChessPiece piece) {
        Collection<ChessMove> moves = new ArrayList<>();
        applyKingMoves(board, position, moves);
        return moves;
    }
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

    private void validateAndAddPosition(ChessBoard board, ChessPosition sPosition, ChessPosition ePosition, Collection<ChessMove> moves) {
        if(inBounds(ePosition)) {
            if (checkPosition(board, ePosition)) {
                moves.add(new ChessMove(sPosition, ePosition, null));
            } else if (isWhite(board, sPosition) && isBlack(board, ePosition)) {
                moves.add(new ChessMove(sPosition, ePosition, null));
            } else if (isBlack(board, sPosition) && isWhite(board, ePosition)) {
                moves.add(new ChessMove(sPosition, ePosition, null));
            }
        }
    }

    private void applyKingMoves(ChessBoard board, ChessPosition position, Collection<ChessMove> moves) {
        for(int[] move: KING_MOVES) {
            ChessPosition nextSpace = getPosition(position, move[0], move[1]);
            validateAndAddPosition(board, position, nextSpace, moves);
        }
    }
}
