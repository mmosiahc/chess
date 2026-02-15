package chess;

import java.util.ArrayList;
import java.util.Collection;

public class PawnMovesCalculator extends BaseMovesCalculator implements PieceMovesCalculator{
    private static final int W_START_ROW = 2;
    private static final int B_START_ROW = 7;
    private static final int W_DIRECTION = 1;
    private static final int B_DIRECTION = -1;
    private static final int RIGHT = 1;
    private static final int LEFT = -1;
    private static final int DOUBLE = 2;
    private static final int PROMOTE_B_TO_W = 1;
    private static final int PROMOTE_W_TO_B = 8;

    private void addPawnMove(ChessPosition sPosition, ChessPosition ePosition, int promote, Collection<ChessMove> moves) {
        if(ePosition.getRow() == promote) {
            moves.add(new ChessMove(sPosition, ePosition, ChessPiece.PieceType.QUEEN));
            moves.add(new ChessMove(sPosition, ePosition, ChessPiece.PieceType.BISHOP));
            moves.add(new ChessMove(sPosition, ePosition, ChessPiece.PieceType.KNIGHT));
            moves.add(new ChessMove(sPosition, ePosition, ChessPiece.PieceType.ROOK));
        }else {moves.add(new ChessMove(sPosition, ePosition, null));}
    }

    @Override
    public Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition position, ChessPiece piece){
        final Collection<ChessMove> moves = new ArrayList<>();
        ChessGame.TeamColor color = piece.getTeamColor();
        int direction = (piece.getTeamColor() == ChessGame.TeamColor.WHITE) ? W_DIRECTION : B_DIRECTION;
        int promoRow = (piece.getTeamColor() == ChessGame.TeamColor.WHITE) ? PROMOTE_W_TO_B : PROMOTE_B_TO_W;
        int startRow = (piece.getTeamColor() == ChessGame.TeamColor.WHITE) ? W_START_ROW : B_START_ROW;
        int row = position.getRow();
        int col = position.getColumn();

        //Moving forward
        ChessPosition oneSpace = new ChessPosition(row + direction, col);
        if(inBounds(oneSpace)) {
            if(board.getPiece(oneSpace) == null) {
                addPawnMove(position, oneSpace, promoRow, moves);
                //Double move
                if(position.getRow() == startRow) {
                    ChessPosition twoSpaces = new ChessPosition(row + (DOUBLE * direction), col);
                    if(board.getPiece(twoSpaces) == null) {addPawnMove(position, twoSpaces, promoRow, moves);}
                }
            }
        }

        //Captures
        for(int colOffset: new int[] {RIGHT, LEFT}) {
            ChessPosition target = new ChessPosition(row + direction, col + colOffset);
            if(inBounds(target)) {
                ChessPiece targetPiece = board.getPiece(target);
                if(targetPiece != null && targetPiece.getTeamColor() != color) {
                    addPawnMove(position, target, promoRow, moves);
                }
            }
        }
        return moves;
    }
}
