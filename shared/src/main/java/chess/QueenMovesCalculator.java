package chess;

import java.util.ArrayList;
import java.util.Collection;

public class QueenMovesCalculator extends BaseMovesCalculator implements PieceMovesCalculator{
    private static final int[][] DIRECTIONS = {
            {1,0}, {-1,0}, {0,1}, {0,-1}, {1,1}, {1,-1}, {-1,1}, {-1,-1}
    };

    @Override
    public Collection<ChessMove> calculateMoves (ChessBoard board, ChessPosition position, ChessPiece piece) {
        Collection<ChessMove> moves = new ArrayList<>();
        for(int[] dir: DIRECTIONS) {
            moveInDirection(board, position, dir[0], dir[1], moves);
        }
        return moves;
    }
}
