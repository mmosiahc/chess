package chess;

import java.util.ArrayList;
import java.util.Collection;

public class KingMovesCalculator extends BaseMovesCalculator implements PieceMovesCalculator{
    private static final int[][] KING_MOVES = {
            {1,0}, {-1,0}, {0,1}, {0,-1}, {1,1}, {1,-1}, {-1,1}, {-1,-1}
    };

    @Override
    public Collection<ChessMove> calculateMoves (ChessBoard board, ChessPosition position, ChessPiece piece) {
        Collection<ChessMove> moves = new ArrayList<>();
        applyKingAndKnightMoves(board, position, KING_MOVES, moves);
        return moves;
    }


}
