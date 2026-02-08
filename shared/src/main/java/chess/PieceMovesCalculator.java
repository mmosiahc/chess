package chess;

import java.util.Collection;

public interface PieceMovesCalculator {

    Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition position, ChessPiece piece);
    boolean checkPosition(ChessBoard board, ChessPosition position);
    boolean inBounds(ChessPosition position);
    boolean isBlack(ChessBoard board, ChessPosition position);
    boolean isWhite(ChessBoard board, ChessPosition position);
    ChessPosition getPosition(ChessPosition position, int row, int col);
}
