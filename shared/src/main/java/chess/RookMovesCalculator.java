package chess;

import java.util.ArrayList;
import java.util.Collection;

public class RookMovesCalculator implements PieceMovesCalculator{
    private static final int NO_MOVE = 0;
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

    public ChessPosition getPosition(ChessPosition position, int row, int col) {
        return new ChessPosition(position.getRow() + row, position.getColumn() + col);
    }

    private void moveToEdgeUp(ChessBoard board, ChessPosition position, Collection<ChessMove> moves) {
        int i = ChessBoard.MIN_SIZE;
        boolean blocked = false;
        while (i < ChessBoard.MAX_SIZE) {
            ChessPosition nextSpace = getPosition(position, i, NO_MOVE);
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

    private void moveToEdgeDown(ChessBoard board, ChessPosition position, Collection<ChessMove> moves) {
        int i = ChessBoard.MIN_SIZE;
        boolean blocked = false;
        while (i < ChessBoard.MAX_SIZE) {
            ChessPosition nextSpace = getPosition(position, -i, NO_MOVE);
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

    private void moveToEdgeRight(ChessBoard board, ChessPosition position, Collection<ChessMove> moves) {
        int i = ChessBoard.MIN_SIZE;
        boolean blocked = false;
        while (i < ChessBoard.MAX_SIZE) {
            ChessPosition nextSpace = getPosition(position, NO_MOVE, i);
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

    private void moveToEdgeLeft(ChessBoard board, ChessPosition position, Collection<ChessMove> moves) {
        int i = ChessBoard.MIN_SIZE;
        boolean blocked = false;
        while (i < ChessBoard.MAX_SIZE) {
            ChessPosition nextSpace = getPosition(position, NO_MOVE, -i);
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

    public Collection<ChessMove> calculateMoves (ChessBoard board, ChessPosition position, ChessPiece piece) {
        final Collection<ChessMove> moves = new ArrayList<>();
        moveToEdgeUp(board, position, moves);
        moveToEdgeDown(board, position, moves);
        moveToEdgeRight(board, position, moves);
        moveToEdgeLeft(board, position, moves);
        return moves;
    }
}
