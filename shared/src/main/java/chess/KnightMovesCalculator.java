package chess;

import java.util.ArrayList;
import java.util.Collection;

public class KnightMovesCalculator implements PieceMovesCalculator{
    public Collection<ChessMove> calculateMoves (ChessBoard board, ChessPosition position, ChessPiece piece) {
        Collection<ChessMove> moves = new ArrayList<>();
        moveUp2Right1(board, position, moves);
        moveUp2Left1(board, position, moves);
        moveUp1Left2(board, position, moves);
        moveUp1Right2(board, position, moves);
        moveDown2Left1(board, position, moves);
        moveDown2Right1(board, position, moves);
        moveDown1Left2(board, position, moves);
        moveDown1Right2(board, position, moves);
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

    private void moveUp2Right1(ChessBoard board, ChessPosition position, Collection<ChessMove> moves) {
        ChessPosition endPosition = getPosition(position, 2, 1);
        if(inBounds(endPosition)) {
            if(checkPosition(board, endPosition)) {
                moves.add(new ChessMove(position, endPosition, null));
            } else if (isWhite(board, position) && isBlack(board,endPosition)) {
                moves.add(new ChessMove(position, endPosition, null));
            } else if (isBlack(board, position) && isWhite(board, endPosition)) {
                moves.add(new ChessMove(position, endPosition, null));
            }
        }
    }

    private void moveUp2Left1(ChessBoard board, ChessPosition position, Collection<ChessMove> moves) {
        ChessPosition endPosition = getPosition(position, 2, -1);
        if(inBounds(endPosition)) {
            if(checkPosition(board, endPosition)) {
                moves.add(new ChessMove(position, endPosition, null));
            } else if (isWhite(board, position) && isBlack(board,endPosition)) {
                moves.add(new ChessMove(position, endPosition, null));
            } else if (isBlack(board, position) && isWhite(board, endPosition)) {
                moves.add(new ChessMove(position, endPosition, null));
            }
        }
    }

    private void moveUp1Right2(ChessBoard board, ChessPosition position, Collection<ChessMove> moves) {
        ChessPosition endPosition = getPosition(position, 1, 2);
        if(inBounds(endPosition)) {
            if(checkPosition(board, endPosition)) {
                moves.add(new ChessMove(position, endPosition, null));
            } else if (isWhite(board, position) && isBlack(board,endPosition)) {
                moves.add(new ChessMove(position, endPosition, null));
            } else if (isBlack(board, position) && isWhite(board, endPosition)) {
                moves.add(new ChessMove(position, endPosition, null));
            }
        }
    }

    private void moveUp1Left2(ChessBoard board, ChessPosition position, Collection<ChessMove> moves) {
        ChessPosition endPosition = getPosition(position, 1, -2);
        if(inBounds(endPosition)) {
            if(checkPosition(board, endPosition)) {
                moves.add(new ChessMove(position, endPosition, null));
            } else if (isWhite(board, position) && isBlack(board,endPosition)) {
                moves.add(new ChessMove(position, endPosition, null));
            } else if (isBlack(board, position) && isWhite(board, endPosition)) {
                moves.add(new ChessMove(position, endPosition, null));
            }
        }
    }

    private void moveDown2Right1(ChessBoard board, ChessPosition position, Collection<ChessMove> moves) {
        ChessPosition endPosition = getPosition(position, -2, 1);
        if(inBounds(endPosition)) {
            if(checkPosition(board, endPosition)) {
                moves.add(new ChessMove(position, endPosition, null));
            } else if (isWhite(board, position) && isBlack(board,endPosition)) {
                moves.add(new ChessMove(position, endPosition, null));
            } else if (isBlack(board, position) && isWhite(board, endPosition)) {
                moves.add(new ChessMove(position, endPosition, null));
            }
        }
    }

    private void moveDown2Left1(ChessBoard board, ChessPosition position, Collection<ChessMove> moves) {
        ChessPosition endPosition = getPosition(position, -2, -1);
        if(inBounds(endPosition)) {
            if(checkPosition(board, endPosition)) {
                moves.add(new ChessMove(position, endPosition, null));
            } else if (isWhite(board, position) && isBlack(board,endPosition)) {
                moves.add(new ChessMove(position, endPosition, null));
            } else if (isBlack(board, position) && isWhite(board, endPosition)) {
                moves.add(new ChessMove(position, endPosition, null));
            }
        }
    }

    private void moveDown1Right2(ChessBoard board, ChessPosition position, Collection<ChessMove> moves) {
        ChessPosition endPosition = getPosition(position, -1, 2);
        if(inBounds(endPosition)) {
            if(checkPosition(board, endPosition)) {
                moves.add(new ChessMove(position, endPosition, null));
            } else if (isWhite(board, position) && isBlack(board,endPosition)) {
                moves.add(new ChessMove(position, endPosition, null));
            } else if (isBlack(board, position) && isWhite(board, endPosition)) {
                moves.add(new ChessMove(position, endPosition, null));
            }
        }
    }

    private void moveDown1Left2(ChessBoard board, ChessPosition position, Collection<ChessMove> moves) {
        ChessPosition endPosition = getPosition(position, -1, -2);
        if(inBounds(endPosition)) {
            if(checkPosition(board, endPosition)) {
                moves.add(new ChessMove(position, endPosition, null));
            } else if (isWhite(board, position) && isBlack(board,endPosition)) {
                moves.add(new ChessMove(position, endPosition, null));
            } else if (isBlack(board, position) && isWhite(board, endPosition)) {
                moves.add(new ChessMove(position, endPosition, null));
            }
        }
    }
}
