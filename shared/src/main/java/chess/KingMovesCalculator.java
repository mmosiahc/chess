package chess;

import java.util.ArrayList;
import java.util.Collection;

public class KingMovesCalculator extends BaseMovesCalculator implements PieceMovesCalculator{
    @Override
    public Collection<ChessMove> calculateMoves (ChessBoard board, ChessPosition position, ChessPiece piece) {
         final int[][] kingMoves = {
                {1,0}, {-1,0}, {0,1}, {0,-1}, {1,1}, {1,-1}, {-1,1}, {-1,-1}
        };
        Collection<ChessMove> moves = new ArrayList<>();
        applyKingAndKnightMoves(board, position, kingMoves, moves);
        return moves;
    }


}
