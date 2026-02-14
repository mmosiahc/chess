package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    private TeamColor currentTeam;
    private ChessBoard board;
    public ChessGame() {
        this.currentTeam = TeamColor.WHITE;
        this.board = new ChessBoard();
        this.board.resetBoard();
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return currentTeam;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        currentTeam = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessPiece piece;
        Collection<ChessMove> moves = new ArrayList<>();
        Collection<ChessMove> validMoves = new ArrayList<>();
        if(board.getPiece(startPosition) != null) {
           piece = board.getPiece(startPosition);
           moves = piece.pieceMoves(board, startPosition);
            for (ChessMove m : moves) {
                if(!testMove(m)) validMoves.add(m);
            }
            return validMoves;
        }
        return moves;
    }

    public Collection<ChessMove> validateMoves(Collection<ChessMove> moves) {
        Collection<ChessMove> validMoves = new ArrayList<>();
        for (ChessMove m : moves) {
            if(!testMove(m)) validMoves.add(m);
        }
        return validMoves;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPiece piece = board.getPiece(move.getStartPosition());
        otherHelper(piece, move);
        pawnHelper(piece, move);
        if(piece.getPieceType() == ChessPiece.PieceType.ROOK) moveHelper(move);
        board.addPiece(move.getStartPosition(), null);
        if(move.getPromotionPiece() != null) {
            board.addPiece(move.getEndPosition(), new ChessPiece(piece.getTeamColor(), move.getPromotionPiece()));
        }else{
            board.addPiece(move.getEndPosition(), piece);
        }
        if(currentTeam == TeamColor.WHITE) {
            currentTeam = TeamColor.BLACK;
        }else {
            currentTeam = TeamColor.WHITE;
        }
    }

    public void otherHelper(ChessPiece piece, ChessMove move) throws InvalidMoveException {
        if(board.getPiece(move.getStartPosition()) == null) {
            throw new InvalidMoveException("Invalid Move: No piece at " + move.getStartPosition());
        }
        if(piece.getTeamColor() != currentTeam) {
            throw new InvalidMoveException("Invalid Move: Not " + piece.getTeamColor() + "'s turn.");
        }
        if(board.getPiece(move.getEndPosition()) != null && piece.getTeamColor() == board.getPiece(move.getEndPosition()).getTeamColor()) {
            throw  new InvalidMoveException("Invalid Move: Can't take piece of same team.");
        }
        if(isInCheck(piece.getTeamColor())) {
            ChessPosition kingPosition = board.locateKing(piece.getTeamColor());
            throw new InvalidMoveException("Invalid Move: King in check " + kingPosition);
        }
    }

    public void pawnHelper(ChessPiece piece, ChessMove move) throws InvalidMoveException {
        if(piece.getPieceType() == ChessPiece.PieceType.PAWN && (move.getEndPosition().getRow() - move.getStartPosition().getRow()) > 2) {
            throw new InvalidMoveException("Invalid Move: Pawn " + piece + " moved too far.");
        }
        if((piece.getPieceType() == ChessPiece.PieceType.PAWN) && (move.getStartPosition().getColumn() != move.getEndPosition().getColumn())) {
            if( board.getPiece(move.getEndPosition()) == null ) throw new InvalidMoveException("Invalid Move: No piece at " + move.getEndPosition());
        }
        if(piece.getPieceType() == ChessPiece.PieceType.PAWN) {
            if(move.getStartPosition().getRow() < 7  && piece.getTeamColor() == TeamColor.BLACK && (move.getEndPosition().getRow() - move.getStartPosition().getRow()) > 1) {
                throw new InvalidMoveException("Invalid Move: No double move allowed. Pawn already moved. " + piece + " " + move.getStartPosition());
            }
            if(move.getStartPosition().getRow() > 2 && piece.getTeamColor() == TeamColor.WHITE && (move.getEndPosition().getRow() - move.getStartPosition().getRow()) > 1) {
                throw new InvalidMoveException("Invalid Move: No double move allowed. Pawn already moved. " + piece + " " + move.getStartPosition());
            }
        }
    }

    public void moveHelper(ChessMove move) throws InvalidMoveException {
        int rowDiff = move.getEndPosition().getRow() - move.getStartPosition().getRow();
        int colDiff = move.getEndPosition().getColumn() - move.getStartPosition().getColumn();
        if(rowDiff == 0) {
            if(colDiff > 0) {
                for(int i = 1; i < colDiff; i++) {
                    ChessPosition nextSpace = new ChessPosition(move.getStartPosition().getRow(), move.getStartPosition().getColumn() + i);
                    ChessPiece stopPiece;
                    if(board.getPiece(nextSpace) != null) {
                        stopPiece = board.getPiece(nextSpace);
                        if(move.getEndPosition() != nextSpace) {
                            throw new InvalidMoveException("Invalid Move: Can't move through piece " + stopPiece + " " + nextSpace);
                        }
                    }
                }
            }else {
                for(int i = -1; i > colDiff; i--) {
                    ChessPosition nextSpace = new ChessPosition(move.getStartPosition().getRow(), move.getStartPosition().getColumn() + i);
                    ChessPiece stopPiece;
                    if(board.getPiece(nextSpace) != null) {
                        stopPiece = board.getPiece(nextSpace);
                        if(move.getEndPosition() != nextSpace) {
                            throw new InvalidMoveException("Invalid Move: Can't move through piece " + stopPiece + " " + nextSpace);
                        }
                    }
                }
            }
        } else {
            if(rowDiff > 0) {
                for(int i = 1; i < rowDiff; i++) {
                    ChessPosition nextSpace = new ChessPosition(move.getStartPosition().getRow() + i, move.getStartPosition().getColumn());
                    ChessPiece stopPiece;
                    if(board.getPiece(nextSpace) != null) {
                        stopPiece = board.getPiece(nextSpace);
                        if(move.getEndPosition() != nextSpace) {
                            throw new InvalidMoveException("Invalid Move: Can't move through piece " + stopPiece + " " + nextSpace);
                        }
                    }
                }
            }else {
                for(int i = -1; i > rowDiff; i--) {
                    ChessPosition nextSpace = new ChessPosition(move.getStartPosition().getRow() + i, move.getStartPosition().getColumn());
                    ChessPiece stopPiece;
                    if(board.getPiece(nextSpace) != null) {
                        stopPiece = board.getPiece(nextSpace);
                        if(move.getEndPosition() != nextSpace) {
                            throw new InvalidMoveException("Invalid Move: Can't move through piece " + stopPiece + " " + nextSpace);
                        }
                    }
                }
            }
        }
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        ChessPosition kingPosition = board.locateKing(teamColor);
        Collection<ChessMove> enemyMoves = board.getEnemyMoves(teamColor);
        for(ChessMove m: enemyMoves) {
            if (m.getEndPosition().equals(kingPosition)) return true;
        }
        return false;
    }
    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        ChessPosition kingPosition = board.locateKing(teamColor);
        ChessPiece king = board.getPiece(kingPosition);
        Collection<ChessMove> enemyMoves = board.getEnemyMoves(teamColor);
        Collection<ChessMove> kingMoves = king.pieceMoves(board, kingPosition);
        Collection<ChessPosition> kingEndPositions = new ArrayList<>();
        Collection<ChessPosition> enemyStartPositions = new ArrayList<>();
        for(ChessMove km : kingMoves) {
            kingEndPositions.add(km.getEndPosition());
        }
        for(ChessMove m: enemyMoves) {
            enemyStartPositions.add(m.getStartPosition());
        }
        for(ChessPosition p : kingEndPositions) {
            if(enemyStartPositions.contains(p)) {
                return false;
            }
        }
        return false;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        if(isInCheck(teamColor)) {
            return false;
        }else {
            return (checkTeamMoves(teamColor));
        }
    }

    public boolean checkTeamMoves(TeamColor teamColor) {
        Collection<ChessMove> teamMoves = board.getTeamMoves(teamColor);
        Collection<ChessMove> validTeamMoves = new ArrayList<>();
        validTeamMoves = validateMoves(teamMoves);
        if(validTeamMoves.isEmpty()) {
            return true;
        }
        return false;
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
    }

    private boolean testMove(ChessMove move) {
        boolean badMove = false;
        ChessPiece piece = board.getPiece(move.getStartPosition());
        ChessPiece takenPiece = null;
        board.addPiece(move.getStartPosition(), null);
        if(board.getPiece(move.getEndPosition()) != null) {
            takenPiece = board.getPiece(move.getEndPosition());
        }
        if(move.getPromotionPiece() != null) {
            board.addPiece(move.getEndPosition(), new ChessPiece(piece.getTeamColor(), move.getPromotionPiece()));
        }else {
            board.addPiece(move.getEndPosition(), piece);
        }
        if(isInCheck(piece.getTeamColor())) {
            badMove = true;
        }
        board.addPiece(move.getStartPosition(), piece);
        board.addPiece(move.getEndPosition(), takenPiece);
        return badMove;
    }

    @Override
    public String toString() {
        return board.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ChessGame chessGame)) {
            return false;
        }
        return currentTeam == chessGame.currentTeam && Objects.equals(board, chessGame.board);
    }

    @Override
    public int hashCode() {
        return Objects.hash(currentTeam, board);
    }
}
