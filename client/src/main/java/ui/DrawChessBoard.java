package ui;

import chess.*;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;

import static ui.EscapeSequences.*;

public class DrawChessBoard {

    private final ChessGame game;
    private final ChessBoard board;
    private static final int ROWS = 10;
    private static final int BLACK_OFFSET = 95;
    private static final int WHITE_OFFSET = 96;


    public DrawChessBoard(ChessGame game) {
        this.game = game;
        this.board = game.getBoard();
    }

    public String toString() {
        return game.toString();
    }

    static void main(String[] args) {
        // 1. Create a real game and board
        ChessGame testGame = new ChessGame();
        ChessBoard testBoard = new ChessBoard();

        // 2. Initialize with standard pieces
        testBoard.resetBoard();
        testGame.setBoard(testBoard);

        // 3. Create the printer instance
        DrawChessBoard ui = new DrawChessBoard(testGame);

        // 4. Test White View
        System.out.println("White Perspective:");
        ui.drawBoardFromGame(true);

        System.out.print(RESET_BG_COLOR);
        System.out.print(RESET_TEXT_COLOR);
        System.out.println();

        // 5. Test Black View
        System.out.println("\nBlack Perspective:");
        ui.drawBoardFromGame(false);
    }

    public void drawBoardFromGame(boolean isWhite) {
        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);
        out.print(ERASE_SCREEN);
        mainLoop(out, isWhite, null, null);
        out.print(RESET_BG_COLOR);
        out.print(RESET_TEXT_COLOR);
    }

    public void drawBoardWithHighlights(boolean isWhiteView, ChessPosition start, Collection<ChessMove> validMoves) {
        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);
        out.print(ERASE_SCREEN);
        Collection<ChessPosition> endPositions = new ArrayList<>();
        for (ChessMove move : validMoves) {
            endPositions.add(move.getEndPosition());
        }
        mainLoop(out, isWhiteView, start, endPositions);
    }

    private void mainLoop(PrintStream out, boolean isWhiteView, ChessPosition selected, Collection<ChessPosition> targets) {
        //Change row/column to fit perspective
        int startRow = isWhiteView ? 8 : 1;
        int endRow = isWhiteView ? 0 : 9;
        int direction = isWhiteView ? -1 : 1;

        if(isWhiteView) {printHeaderAndFooterWhite(out);}
        else {printHeaderAndFooterBlack(out);}

        for (int r = startRow; r != endRow; r += direction) {
            printRankORFile(out, String.valueOf(r));
            for (int c = 1; c <= 8; c++) {
                int actualCol = isWhiteView ? c : (9 - c);
                ChessPosition currentPos = new ChessPosition(r, actualCol);
                ChessPiece piece = board.getPiece(currentPos);
                if(currentPos.equals(selected)) {
                    out.print(SET_BG_COLOR_RED);
                } else if (targets != null && targets.contains(currentPos)) {
                    out.print(SET_BG_COLOR_GREEN);
                }
                else {
                    if(isEven(r + actualCol)) {setDarkSquareColor(out);}
                    else {setLightSquareColor(out);}
                }

                if (piece == null) {
                    out.print(EMPTY);
                } else {
                    ChessGame.TeamColor pieceColor = piece.getTeamColor();
                    printPiece(out, getPieceString(piece), pieceColor);
                }
            }
            printRankORFile(out, String.valueOf(r));
            printLine(out);
        }
        if(isWhiteView) {printHeaderAndFooterWhite(out);}
        else {printHeaderAndFooterBlack(out);}
    }

    private static void printHeaderAndFooterBlack(PrintStream out) {
        setBorderSquareColor(out);
        for(int i = ROWS; i > 0; i--) {
            if(i < ROWS && i > 1) {
                char file = (char) (i + BLACK_OFFSET);
                printRankORFile(out, String.valueOf(file));
            } else {
                out.print(EMPTY);
            }
        }
        printLine(out);
    }

    private static void printHeaderAndFooterWhite(PrintStream out) {
        setBorderSquareColor(out);
        for(int i = 0; i < ROWS; i++) {
            if(i > 0 && i < ROWS - 1) {
                char file = (char) (i + WHITE_OFFSET);
                printRankORFile(out, String.valueOf(file));
            } else {
                out.print(EMPTY);
            }
        }
        printLine(out);
    }

    private static void printRankORFile(PrintStream out, String rank) {
        setBorderSquareColor(out);
        setRankAndFileColor(out);
        out.print("\u2003" + rank + " ");
    }

    private static void printPiece(PrintStream out, String piece, ChessGame.TeamColor color) {
        if(color != null) {
            if(color.equals(ChessGame.TeamColor.WHITE)) {
                setWhitePieceColor(out);
            } else {
                setBlackPieceColor(out);
            }
        }

        out.print(SET_TEXT_BOLD);
        out.print(piece);
    }

    private static boolean isEven (int iteration) {
        return iteration % 2 == 0;
    }

    private static void printLine(PrintStream out) {
        out.print(RESET_BG_COLOR);
        out.println();
    }

    private static void setWhitePieceColor(PrintStream out) {
        out.print(WHITE_PIECE_COLOR);
    }

    private static void setBlackPieceColor(PrintStream out) {
        out.print(BLACK_PIECE_COLOR);
    }

    private static void setRankAndFileColor(PrintStream out) {
        out.print(SET_TEXT_COLOR_WHITE);
    }

    private static void setLightSquareColor(PrintStream out) {
        out.print(LIGHT_SQUARE);
    }

    private static void setDarkSquareColor(PrintStream out) {
        out.print(DARK_SQUARE);
    }

    private static void setBorderSquareColor(PrintStream out) {
        out.print(BORDER_SQUARE);
    }



    private String getPieceString(ChessPiece piece) {

        return switch (piece.getPieceType()) {
            case PAWN -> BLACK_PAWN;
            case KING -> BLACK_KING;
            case QUEEN -> BLACK_QUEEN;
            case BISHOP -> BLACK_BISHOP;
            case KNIGHT -> BLACK_KNIGHT;
            case ROOK -> BLACK_ROOK;
        };
    }
}
