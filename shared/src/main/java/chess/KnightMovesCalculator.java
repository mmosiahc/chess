package chess;

import java.util.ArrayList;
import java.util.Collection;

public class KnightMovesCalculator extends BaseMovesCalculator implements PieceMovesCalculator{
    private static final int[][] KNIGHT_MOVES = {
            {2,1}, {2,-1}, {1,2}, {1,-2}, {-2,1}, {-2,-1}, {-1,2}, {-1,-2}
    };

    @Override
    public Collection<ChessMove> calculateMoves (ChessBoard board, ChessPosition position, ChessPiece piece) {
        Collection<ChessMove> moves = new ArrayList<>();
        applyKingAndKnightMoves(board, position, KNIGHT_MOVES, moves);
        return moves;
    }
}
