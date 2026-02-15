package chess;

import java.util.Collection;

public abstract class BaseMovesCalculator {

    protected boolean checkPosition(ChessBoard board, ChessPosition position)  {
        return board.getPiece(position) == null;
    }

    protected boolean inBounds(ChessPosition position) {
        int r = position.getRow();
        int c = position.getColumn();
        return (r >= ChessBoard.MIN_SIZE && r <= ChessBoard.MAX_SIZE) && (c >= ChessBoard.MIN_SIZE && c <= ChessBoard.MAX_SIZE);
    }

    protected boolean isBlack(ChessBoard board, ChessPosition position) {
        return board.getPiece(position).getTeamColor() == ChessGame.TeamColor.BLACK;
    }

    protected boolean isWhite(ChessBoard board, ChessPosition position) {
        return board.getPiece(position).getTeamColor() == ChessGame.TeamColor.WHITE;
    }

    protected ChessPosition getPosition(ChessPosition position, int row, int col) {
        return new ChessPosition(position.getRow() + row, position.getColumn() + col);
    }

    protected boolean isBlocked(ChessBoard board, ChessPosition sPosition, ChessPosition ePosition, boolean blocked, Collection<ChessMove> moves) {
        if(inBounds(ePosition)) {
            if(checkPosition(board, ePosition)) {
                moves.add(new ChessMove(sPosition, ePosition, null));
            } else if (isWhite(board, sPosition) && isBlack(board,ePosition)) {
                moves.add(new ChessMove(sPosition, ePosition, null));
                blocked = true;
            } else if (isBlack(board, sPosition) && isWhite(board, ePosition)) {
                moves.add(new ChessMove(sPosition, ePosition, null));
                blocked = true;
            }else {blocked = true;}
        }
        return blocked;
    }

    protected void moveInDirection(ChessBoard board, ChessPosition position, int rowDir, int colDir, Collection<ChessMove> moves) {
        boolean blocked;
        for(int i = ChessBoard.MIN_SIZE; i < ChessBoard.MAX_SIZE; i++) {
            ChessPosition nextSpace = getPosition(position, i * rowDir, i * colDir);
            blocked = isBlocked(board, position, nextSpace, false, moves);
            if(blocked) {break;}
        }
    }

    protected void addSlidingMoves(ChessBoard board, ChessPosition position, int [][] directions, Collection<ChessMove> moves) {
        for(int[] d : directions) {
            moveInDirection(board, position, d[0], d[1], moves);
        }
    }

    protected void validateAndAddPosition(ChessBoard board, ChessPosition sPosition, ChessPosition ePosition, Collection<ChessMove> moves) {
        if(inBounds(ePosition)) {
            if (checkPosition(board, ePosition)) {
                moves.add(new ChessMove(sPosition, ePosition, null));
            } else if (isWhite(board, sPosition) && isBlack(board, ePosition)) {
                moves.add(new ChessMove(sPosition, ePosition, null));
            } else if (isBlack(board, sPosition) && isWhite(board, ePosition)) {
                moves.add(new ChessMove(sPosition, ePosition, null));
            }
        }
    }

    protected void applyKingAndKnightMoves(ChessBoard board, ChessPosition position, int[][] directions, Collection<ChessMove> moves) {
        for(int[] move: directions) {
            ChessPosition nextSpace = getPosition(position, move[0], move[1]);
            validateAndAddPosition(board, position, nextSpace, moves);
        }
    }
}
