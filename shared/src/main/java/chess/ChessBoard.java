package chess;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {
    ChessPiece[][] spaces = new ChessPiece[8][8];
    public static final int MAX_SIZE = 8;
    public static final int MIN_SIZE = 1;

    public ChessBoard() {
        
    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {

        spaces[position.getRow()-1][position.getColumn()-1] = piece;
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {

        return spaces[position.getRow()-1][position.getColumn()-1];
    }



    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        spaces = new ChessPiece[8][8];
        spaces[0][0] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK);
        spaces[0][1] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT);
        spaces[0][2] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP);
        spaces[0][3] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.QUEEN);
        spaces[0][4] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KING);
        spaces[0][5] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP);
        spaces[0][6] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT);
        spaces[0][7] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK);

        spaces[7][0] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK);
        spaces[7][1] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KNIGHT);
        spaces[7][2] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.BISHOP);
        spaces[7][4] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KING);
        spaces[7][3] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.QUEEN);
        spaces[7][5] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.BISHOP);
        spaces[7][6] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KNIGHT);
        spaces[7][7] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK);


        for (int i = 0; i < spaces.length; i++) {
            for (int j = 0; j < spaces[i].length; j++) {
                if (i == 1) {
                    spaces[i][j] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
                }
                if (i == 6) {
                    spaces[i][j] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN);
                }
            }
        }

//        System.out.println(this);
    }

    public ChessPosition locateKing(ChessGame.TeamColor team) {
        ChessPosition kingPosition = null;
        for(int i = 0; i < spaces.length; i++) {
            for(int j = 0; j < spaces[i].length; j++) {
                if(spaces[i][j] != null) {
                    if(spaces[i][j].getPieceType() == ChessPiece.PieceType.KING && spaces[i][j].getTeamColor() == team) {
                        kingPosition = new ChessPosition(i + 1, j +1);
                    }
                }

            }
        }
        return kingPosition;
    }

    public Collection<ChessMove> getAllMoves(ChessGame.TeamColor team) {
        Collection<ChessMove> teamMoves = new ArrayList<>();
        for(int i = 0; i < spaces.length; i++) {
            for(int j = 0; j < spaces[i].length; j++) {
                if(spaces[i][j] != null) {
                    if(spaces[i][j].getTeamColor() != team) {
                        teamMoves.addAll(spaces[i][j].pieceMoves(this, new ChessPosition(i + 1, j + 1)));
                    }
                }
            }
        }
        return teamMoves;
    }



    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();
        String divider = "+---+---+---+---+---+---+---+---+\n";

        for (int i = 7; i >= 0; i--) {
            sb.append(divider);
            sb.append("|");
            for (int j = 0; j < spaces[i].length; j++) {
                if(spaces[i][j] == null) {
                    sb.append(" . ");
                } else {
                    sb.append(" " + spaces[i][j] + " ");
                }

                if (j < spaces[i].length - 1) {
                    sb.append("|");
                }
            }
            sb.append("|");
            if (i > 0) {
                sb.append('\n');
            }else{
                sb.append('\n');
                sb.append(divider);
            }
        }
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ChessBoard that)) {
            return false;
        }
        return Objects.deepEquals(spaces, that.spaces);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(spaces);
    }
}
