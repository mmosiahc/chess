package chess;

import java.util.Collection;

public interface PieceMovesCalculator {

    public Collection<ChessMove> calculateMoves (ChessBoard board, ChessPosition position, ChessPiece piece);
    public boolean checkPosition(ChessBoard board, ChessPosition position);
    public boolean inBounds(ChessPosition position);
}
