package chess;

import java.util.ArrayList;
import java.util.Collection;

public class KingMovesCalculator implements PieceMovesCalculator{
    private static final int NO_MOVE = 0;
    private static final int UP = 1;
    private static final int DOWN = -1;
    private static final int RIGHT = 1;
    private static final int LEFT = -1;

    public Collection<ChessMove> calculateMoves (ChessBoard board, ChessPosition position, ChessPiece piece) {
        Collection<ChessMove> moves = new ArrayList<>();
        moveUp(board, position, moves);
        moveRight(board, position, moves);
        moveDown(board, position, moves);
        moveLeft(board, position, moves);
        moveUpRight(board, position, moves);
        moveDownRight(board, position, moves);
        moveUpLeft(board, position, moves);
        moveDownLeft(board, position, moves);
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

    public ChessPosition getPosition(ChessPosition position, int row, int col) {
        return new ChessPosition(position.getRow() + row, position.getColumn() + col);
    }

    private void moveUp(ChessBoard board, ChessPosition position, Collection<ChessMove> moves) {
        ChessPosition nextSpace = getPosition(position, UP, NO_MOVE);
        if(inBounds(nextSpace)) {
            if(checkPosition(board, nextSpace)) {
                moves.add(new ChessMove(position, nextSpace, null));
            } else if (isWhite(board, position) && isBlack(board,nextSpace)) {
                moves.add(new ChessMove(position, nextSpace, null));
            } else if (isBlack(board, position) && isWhite(board, nextSpace)) {
                moves.add(new ChessMove(position, nextSpace, null));
            }
        }
    }

    private void moveRight(ChessBoard board, ChessPosition position, Collection<ChessMove> moves) {
        ChessPosition nextSpace = getPosition(position, NO_MOVE, RIGHT);
        if(inBounds(nextSpace)) {
            if(checkPosition(board, nextSpace)) {
                moves.add(new ChessMove(position, nextSpace, null));
            } else if (isWhite(board, position) && isBlack(board,nextSpace)) {
                moves.add(new ChessMove(position, nextSpace, null));
            } else if (isBlack(board, position) && isWhite(board, nextSpace)) {
                moves.add(new ChessMove(position, nextSpace, null));
            }
        }
    }

    private void moveDown(ChessBoard board, ChessPosition position, Collection<ChessMove> moves) {
        ChessPosition nextSpace = getPosition(position, DOWN, NO_MOVE);
        if(inBounds(nextSpace)) {
            if(checkPosition(board, nextSpace)) {
                moves.add(new ChessMove(position, nextSpace, null));
            } else if (isWhite(board, position) && isBlack(board,nextSpace)) {
                moves.add(new ChessMove(position, nextSpace, null));
            } else if (isBlack(board, position) && isWhite(board, nextSpace)) {
                moves.add(new ChessMove(position, nextSpace, null));
            }
        }
    }

    private void moveLeft(ChessBoard board, ChessPosition position, Collection<ChessMove> moves) {
        ChessPosition nextSpace = getPosition(position, NO_MOVE, LEFT);
        if(inBounds(nextSpace)) {
            if(checkPosition(board, nextSpace)) {
                moves.add(new ChessMove(position, nextSpace, null));
            } else if (isWhite(board, position) && isBlack(board,nextSpace)) {
                moves.add(new ChessMove(position, nextSpace, null));
            } else if (isBlack(board, position) && isWhite(board, nextSpace)) {
                moves.add(new ChessMove(position, nextSpace, null));
            }
        }
    }

    private void moveUpRight(ChessBoard board, ChessPosition position, Collection<ChessMove> moves) {
        ChessPosition nextSpace = getPosition(position, UP, RIGHT);
        if(inBounds(nextSpace)) {
            if(checkPosition(board, nextSpace)) {
                moves.add(new ChessMove(position, nextSpace, null));
            } else if (isWhite(board, position) && isBlack(board,nextSpace)) {
                moves.add(new ChessMove(position, nextSpace, null));
            } else if (isBlack(board, position) && isWhite(board, nextSpace)) {
                moves.add(new ChessMove(position, nextSpace, null));
            }
        }
    }

    private void moveDownRight(ChessBoard board, ChessPosition position, Collection<ChessMove> moves) {
        ChessPosition nextSpace = getPosition(position, DOWN, RIGHT);
        if(inBounds(nextSpace)) {
            if(checkPosition(board, nextSpace)) {
                moves.add(new ChessMove(position, nextSpace, null));
            } else if (isWhite(board, position) && isBlack(board,nextSpace)) {
                moves.add(new ChessMove(position, nextSpace, null));
            } else if (isBlack(board, position) && isWhite(board, nextSpace)) {
                moves.add(new ChessMove(position, nextSpace, null));
            }
        }
    }

    private void moveUpLeft(ChessBoard board, ChessPosition position, Collection<ChessMove> moves) {
        ChessPosition nextSpace = getPosition(position, UP, LEFT);
        if(inBounds(nextSpace)) {
            if(checkPosition(board, nextSpace)) {
                moves.add(new ChessMove(position, nextSpace, null));
            } else if (isWhite(board, position) && isBlack(board,nextSpace)) {
                moves.add(new ChessMove(position, nextSpace, null));
            } else if (isBlack(board, position) && isWhite(board, nextSpace)) {
                moves.add(new ChessMove(position, nextSpace, null));
            }
        }
    }

    private void moveDownLeft(ChessBoard board, ChessPosition position, Collection<ChessMove> moves) {
        ChessPosition nextSpace = getPosition(position, DOWN, LEFT);
        if(inBounds(nextSpace)) {
            if(checkPosition(board, nextSpace)) {
                moves.add(new ChessMove(position, nextSpace, null));
            } else if (isWhite(board, position) && isBlack(board,nextSpace)) {
                moves.add(new ChessMove(position, nextSpace, null));
            } else if (isBlack(board, position) && isWhite(board, nextSpace)) {
                moves.add(new ChessMove(position, nextSpace, null));
            }
        }
    }
}
