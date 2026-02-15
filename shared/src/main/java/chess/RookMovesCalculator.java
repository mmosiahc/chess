package chess;

import java.util.ArrayList;
import java.util.Collection;

public class RookMovesCalculator extends BaseMovesCalculator implements PieceMovesCalculator{
    @Override
    public Collection<ChessMove> calculateMoves (ChessBoard board, ChessPosition position, ChessPiece piece) {
        final int[][] directions = { {1,0}, {-1,0}, {0,1}, {0,-1} };
        final Collection<ChessMove> moves = new ArrayList<>();
        addSlidingMoves(board, position, directions, moves);
        return moves;
    }
}
