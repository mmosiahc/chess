package chess;

import java.util.ArrayList;
import java.util.Collection;

public class BishopMovesCalculator implements PieceMovesCalculator{
    public Collection<ChessMove> calculateMoves (ChessBoard board, ChessPosition position, ChessPiece piece) {
        Collection<ChessMove> moves = new ArrayList<>();
        moveToEdgeUpRight(board, position, moves);
        moveToEdgeUpLeft(board, position, moves);
        moveToEdgeDownRight(board, position, moves);
        moveToEdgeDownLeft(board, position, moves);
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

    private void moveToEdgeUpRight(ChessBoard board, ChessPosition position, Collection<ChessMove> moves) {
        int i = ChessBoard.MIN_SIZE;
        boolean blocked = false;
        while (i < ChessBoard.MAX_SIZE) {
            ChessPosition nextSpace = new ChessPosition(position.getRow() + i, position.getColumn() + i);
            if(inBounds(nextSpace)) {
                if(checkPosition(board, nextSpace)) {
                    moves.add(new ChessMove(position, nextSpace, null));
                } else if (isWhite(board, position) && isBlack(board,nextSpace)) {
                    moves.add(new ChessMove(position, nextSpace, null));
                    blocked = true;
                } else if (isBlack(board, position) && isWhite(board, nextSpace)) {
                    moves.add(new ChessMove(position, nextSpace, null));
                    blocked = true;
                }else {blocked = true;}
            } else {break;}
            if(blocked) {break;}
            i++;
        }
    }

    private void moveToEdgeDownRight(ChessBoard board, ChessPosition position, Collection<ChessMove> moves) {
        int i = ChessBoard.MIN_SIZE;
        boolean blocked = false;
        while (i < ChessBoard.MAX_SIZE) {
            ChessPosition nextSpace = new ChessPosition(position.getRow() - i, position.getColumn() + i);
            if(inBounds(nextSpace)) {
                if(checkPosition(board, nextSpace)) {
                    moves.add(new ChessMove(position, nextSpace, null));
                } else if (isWhite(board, position) && isBlack(board,nextSpace)) {
                    moves.add(new ChessMove(position, nextSpace, null));
                    blocked = true;
                } else if (isBlack(board, position) && isWhite(board, nextSpace)) {
                    moves.add(new ChessMove(position, nextSpace, null));
                    blocked = true;
                }else {blocked = true;}
            } else {break;}
            if(blocked) {break;}
            i++;
        }
    }

    private void moveToEdgeUpLeft(ChessBoard board, ChessPosition position, Collection<ChessMove> moves) {
        int i = ChessBoard.MIN_SIZE;
        boolean blocked = false;
        while (i < ChessBoard.MAX_SIZE) {
            ChessPosition nextSpace = new ChessPosition(position.getRow() + i, position.getColumn() - i);
            if(inBounds(nextSpace)) {
                if(checkPosition(board, nextSpace)) {
                    moves.add(new ChessMove(position, nextSpace, null));
                } else if (isWhite(board, position) && isBlack(board,nextSpace)) {
                    moves.add(new ChessMove(position, nextSpace, null));
                    blocked = true;
                } else if (isBlack(board, position) && isWhite(board, nextSpace)) {
                    moves.add(new ChessMove(position, nextSpace, null));
                    blocked = true;
                }else {blocked = true;}
            } else {break;}
            if(blocked) {break;}
            i++;
        }
    }

    private void moveToEdgeDownLeft(ChessBoard board, ChessPosition position, Collection<ChessMove> moves) {
        int i = ChessBoard.MIN_SIZE;
        boolean blocked = false;
        while (i < ChessBoard.MAX_SIZE) {
            ChessPosition nextSpace = new ChessPosition(position.getRow() - i, position.getColumn() - i);
            if(inBounds(nextSpace)) {
                if(checkPosition(board, nextSpace)) {
                    moves.add(new ChessMove(position, nextSpace, null));
                } else if (isWhite(board, position) && isBlack(board,nextSpace)) {
                    moves.add(new ChessMove(position, nextSpace, null));
                    blocked = true;
                } else if (isBlack(board, position) && isWhite(board, nextSpace)) {
                    moves.add(new ChessMove(position, nextSpace, null));
                    blocked = true;
                }else {blocked = true;}
            } else {break;}
            if(blocked) {break;}
            i++;
        }
    }
}
