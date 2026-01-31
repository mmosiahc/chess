package chess;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

public class PawnMovesCalculator implements PieceMovesCalculator{
    private static final int W_START_ROW = 2;
    private static final int B_START_ROW = 7;
    private static final int W_DIRECTION = 1;
    private static final int B_DIRECTION = -1;
    private static final int RIGHT = 1;
    private static final int LEFT = -1;
    private static final int W_INITIAL = 2;
    private static final int B_INITIAL = -2;
    private static final int PROMOTE_B_TO_W = 1;
    private static final int PROMOTE_W_TO_B = 8;
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

    private boolean promotionW(ChessPosition position) {
        return position.getRow() == PROMOTE_W_TO_B;
    }

    private boolean promotionB(ChessPosition position) {
        return position.getRow() == PROMOTE_B_TO_W;
    }

    private void moveForwardW(ChessBoard board, ChessPosition position, Collection<ChessMove> moves) {
        ChessPosition nextSpaceW = getPosition(position, W_DIRECTION, NO_MOVE);
        if(inBounds(nextSpaceW)){
            if (checkPosition(board, nextSpaceW) && promotionW(nextSpaceW) ) {
                moves.add(new ChessMove(position, nextSpaceW, ChessPiece.PieceType.KNIGHT));
                moves.add(new ChessMove(position, nextSpaceW, ChessPiece.PieceType.ROOK));
                moves.add(new ChessMove(position, nextSpaceW, ChessPiece.PieceType.QUEEN));
                moves.add(new ChessMove(position, nextSpaceW, ChessPiece.PieceType.BISHOP));
            } else if (checkPosition(board, nextSpaceW)) {
                moves.add(new ChessMove(position, nextSpaceW, null));
            }
        }
    }

    private void moveForwardB(ChessBoard board, ChessPosition position, Collection<ChessMove> moves) {
        ChessPosition nextSpaceB = getPosition(position, B_DIRECTION, NO_MOVE);
        if(inBounds(nextSpaceB)) {
            if (checkPosition(board, nextSpaceB) && promotionB(nextSpaceB) ) {
                moves.add(new ChessMove(position, nextSpaceB, ChessPiece.PieceType.KNIGHT));
                moves.add(new ChessMove(position, nextSpaceB, ChessPiece.PieceType.ROOK));
                moves.add(new ChessMove(position, nextSpaceB, ChessPiece.PieceType.QUEEN));
                moves.add(new ChessMove(position, nextSpaceB, ChessPiece.PieceType.BISHOP));
            } else if (checkPosition(board, nextSpaceB)) {
                moves.add(new ChessMove(position, nextSpaceB, null));
            }
        }
    }

    private void moveInitialW(ChessBoard board, ChessPosition position, Collection<ChessMove> moves) {
        ChessPosition initialW = getPosition(position, W_INITIAL, NO_MOVE);
        ChessPosition nextSpaceW = getPosition(position, W_DIRECTION, NO_MOVE);
        if(position.getRow() == W_START_ROW) {
            if(checkPosition(board, initialW) && checkPosition(board, nextSpaceW)) {
                moves.add(new ChessMove(position, initialW, null));
            }
        }
    }

    private void moveInitialB(ChessBoard board, ChessPosition position, Collection<ChessMove> moves) {
        ChessPosition initialB = getPosition(position, B_INITIAL, NO_MOVE);
        ChessPosition nextSpaceB = getPosition(position, B_DIRECTION, NO_MOVE);;
        if(position.getRow() == B_START_ROW) {
            if(checkPosition(board, initialB) && checkPosition(board, nextSpaceB)) {
                moves.add(new ChessMove(position, initialB, null));
            }
        }
    }

    private void captureWhite(ChessBoard board, ChessPosition position, Collection<ChessMove> moves) {
        ChessPosition captureRight = getPosition(position, W_DIRECTION, RIGHT);
        ChessPosition captureLeft = getPosition(position, W_DIRECTION, LEFT);
        if(inBounds(captureRight)) {
            if (!checkPosition(board, captureRight) && isBlack(board, captureRight) && promotionW(captureRight) ) {
                moves.add(new ChessMove(position, captureRight, ChessPiece.PieceType.KNIGHT));
                moves.add(new ChessMove(position, captureRight, ChessPiece.PieceType.ROOK));
                moves.add(new ChessMove(position, captureRight, ChessPiece.PieceType.QUEEN));
                moves.add(new ChessMove(position, captureRight, ChessPiece.PieceType.BISHOP));
            } else if (!checkPosition(board, captureRight) && isBlack(board, captureRight)) {
                moves.add(new ChessMove(position, captureRight, null));
            }
        }

        if (inBounds(captureLeft)) {
            if (!checkPosition(board, captureLeft) && isBlack(board, captureLeft) && promotionW(captureLeft) ) {
                moves.add(new ChessMove(position, captureLeft, ChessPiece.PieceType.KNIGHT));
                moves.add(new ChessMove(position, captureLeft, ChessPiece.PieceType.ROOK));
                moves.add(new ChessMove(position, captureLeft, ChessPiece.PieceType.QUEEN));
                moves.add(new ChessMove(position, captureLeft, ChessPiece.PieceType.BISHOP));
            } else if (!checkPosition(board, captureLeft) && isBlack(board, captureLeft)) {
                moves.add(new ChessMove(position, captureLeft, null));
            }
        }
    }

    private void captureBlack(ChessBoard board, ChessPosition position, Collection<ChessMove> moves) {
        ChessPosition captureRight = getPosition(position, B_DIRECTION, RIGHT);
        ChessPosition captureLeft = getPosition(position, B_DIRECTION, LEFT);
        if(inBounds(captureRight)) {
            if (!checkPosition(board, captureRight) && isWhite(board, captureRight) && promotionB(captureRight) ) {
                moves.add(new ChessMove(position, captureRight, ChessPiece.PieceType.KNIGHT));
                moves.add(new ChessMove(position, captureRight, ChessPiece.PieceType.ROOK));
                moves.add(new ChessMove(position, captureRight, ChessPiece.PieceType.QUEEN));
                moves.add(new ChessMove(position, captureRight, ChessPiece.PieceType.BISHOP));
            } else if (!checkPosition(board, captureRight) && isWhite(board, captureRight)) {
                moves.add(new ChessMove(position, captureRight, null));
            }
        }

        if (inBounds(captureLeft)) {
            if (!checkPosition(board, captureLeft) && isWhite(board, captureLeft) && promotionB(captureLeft) ) {
                moves.add(new ChessMove(position, captureLeft, ChessPiece.PieceType.KNIGHT));
                moves.add(new ChessMove(position, captureLeft, ChessPiece.PieceType.ROOK));
                moves.add(new ChessMove(position, captureLeft, ChessPiece.PieceType.QUEEN));
                moves.add(new ChessMove(position, captureLeft, ChessPiece.PieceType.BISHOP));
            } else if (!checkPosition(board, captureLeft) && isWhite(board, captureLeft)) {
                moves.add(new ChessMove(position, captureLeft, null));
            }
        }
    }

    public Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition position, ChessPiece piece){
        final Collection<ChessMove> moves = new ArrayList<>();
        if(piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
            moveForwardW(board, position, moves);
            moveInitialW(board, position, moves);
            captureWhite(board, position, moves);
        }
        if(piece.getTeamColor() == ChessGame.TeamColor.BLACK) {
            moveForwardB(board, position, moves);
            moveInitialB(board, position, moves);
            captureBlack(board, position, moves);
        }
//        System.out.println(Arrays.toString(moves.toArray()));
        return moves;
    }
}
