package chess;

import java.util.Collection;
import java.util.Objects;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    private final ChessGame.TeamColor pieceColor;
    private final PieceType type;

    public ChessPiece(ChessGame.TeamColor pieceColor, PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return type;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        PieceMovesCalculator calc = switch (type) {
            case PAWN -> new PawnMovesCalculator();
            case KING -> null;
            case QUEEN -> new QueenMovesCalculator();
            case BISHOP -> new BishopMovesCalculator();
            case KNIGHT -> new KnightMovesCalculator();
            case ROOK -> new RookMovesCalculator();
        };
        return calc.calculateMoves(board, myPosition, this);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (pieceColor == ChessGame.TeamColor.WHITE && type == PieceType.KING) {
            sb.append("KG");
        } else if (pieceColor == ChessGame.TeamColor.WHITE && type == PieceType.QUEEN) {
            sb.append("Q");
        } else if (pieceColor == ChessGame.TeamColor.WHITE && type == PieceType.BISHOP) {
            sb.append("B");
        } else if (pieceColor == ChessGame.TeamColor.WHITE && type == PieceType.KNIGHT) {
            sb.append("KN");
        } else if (pieceColor == ChessGame.TeamColor.WHITE && type == PieceType.ROOK) {
            sb.append("R");
        } else if (pieceColor == ChessGame.TeamColor.WHITE && type == PieceType.PAWN) {
            sb.append("P");
        }

        if (pieceColor == ChessGame.TeamColor.BLACK && type == PieceType.KING) {
            sb.append("kg");
        } else if (pieceColor == ChessGame.TeamColor.BLACK && type == PieceType.QUEEN) {
            sb.append("q");
        } else if (pieceColor == ChessGame.TeamColor.BLACK && type == PieceType.BISHOP) {
            sb.append("b");
        } else if (pieceColor == ChessGame.TeamColor.BLACK && type == PieceType.KNIGHT) {
            sb.append("kn");
        } else if (pieceColor == ChessGame.TeamColor.BLACK && type == PieceType.ROOK) {
            sb.append("r");
        } else if (pieceColor == ChessGame.TeamColor.BLACK && type == PieceType.PAWN) {
            sb.append("p");
        }

        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ChessPiece that)) {
            return false;
        }
        return pieceColor == that.pieceColor && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pieceColor, type);
    }
}
