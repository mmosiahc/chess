package chess;

import java.util.ArrayList;
import java.util.Collection;

public class KnightMovesCalculator extends BaseMovesCalculator implements PieceMovesCalculator{
    @Override
    public Collection<ChessMove> calculateMoves (ChessBoard board, ChessPosition position, ChessPiece piece) {
        final int[][] knightMoves = {
                {2,1}, {2,-1}, {1,2}, {1,-2}, {-2,1}, {-2,-1}, {-1,2}, {-1,-2}
        };
        Collection<ChessMove> moves = new ArrayList<>();
        applyKingAndKnightMoves(board, position, knightMoves, moves);
        return moves;
    }
}
