package ui;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static ui.EscapeSequences.*;

public class DrawChessBoard {

    private final ChessGame game;
    private final ChessBoard board;
    private static final int ROWS = 10;
    private static final int BOARD_LENGTH = 8;
    private static final int BLACK_OFFSET = 95;
    private static final int WHITE_OFFSET = 96;
    private static final ArrayList<String> BLACK_PIECES_PRINT_WHITE = new ArrayList<>(List.of(
            BLACK_ROOK, BLACK_KNIGHT, BLACK_BISHOP, BLACK_QUEEN, BLACK_KING, BLACK_BISHOP, BLACK_KNIGHT, BLACK_ROOK));
    private static final ArrayList<String> BLACK_PIECES_PRINT_BLACK = new ArrayList<>(List.of(
            BLACK_ROOK, BLACK_KNIGHT, BLACK_BISHOP, BLACK_KING, BLACK_QUEEN, BLACK_BISHOP, BLACK_KNIGHT, BLACK_ROOK));
    private static final ArrayList<String> WHITE_PIECES_PRINT_WHITE = new ArrayList<>(List.of(
            WHITE_ROOK, WHITE_KNIGHT, WHITE_BISHOP, WHITE_QUEEN, WHITE_KING, WHITE_BISHOP, WHITE_KNIGHT, WHITE_ROOK));
    private static final ArrayList<String> WHITE_PIECES_PRINT_BLACK = new ArrayList<>(List.of(
            WHITE_ROOK, WHITE_KNIGHT, WHITE_BISHOP, WHITE_KING,WHITE_QUEEN, WHITE_QUEEN, WHITE_BISHOP, WHITE_KNIGHT, WHITE_ROOK));
    private static final ArrayList<String> WHITE_PAWNS = new ArrayList<>(List.of(
            WHITE_PAWN, WHITE_PAWN, WHITE_PAWN, WHITE_PAWN, WHITE_PAWN, WHITE_PAWN, WHITE_PAWN, WHITE_PAWN));
    private static final ArrayList<String> BLACK_PAWNS = new ArrayList<>(List.of(
            BLACK_PAWN, BLACK_PAWN, BLACK_PAWN, BLACK_PAWN, BLACK_PAWN, BLACK_PAWN, BLACK_PAWN, BLACK_PAWN));


    public DrawChessBoard(ChessGame game) {
        this.game = game;
        this.board = game.getBoard();
    }

    public String toString() {
        return game.toString();
    }

    public static void main(String[] args) {
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
        mainLoop(out, isWhite);
    }

    public static void drawNewBoard(boolean isWhite) {
        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);
        out.print(ERASE_SCREEN);
        if(isWhite) {
            printBoardWhite(out);
            printLine(out);
        } else {
            printBoardBlack(out);
            printLine(out);
        }
        out.print(RESET_BG_COLOR);
        out.print(RESET_TEXT_COLOR);
    }

    private static void printBoardBlack(PrintStream out) {
        for(int i = 0; i < ROWS; i++) {
            if(i == 0 || i == ROWS - 1) {
                printHeaderAndFooterBlack(out);
            } else {
                if(i == 1) {
                    printRowOnBoardWithPieces(out, i, "white", WHITE_PIECES_PRINT_BLACK);
                } else if (i == 2) {
                    printRowOnBoardWithPieces(out, i, "black", WHITE_PAWNS);
                } else if (i == 7) {
                    printRowOnBoardWithPieces(out, i, "white", BLACK_PAWNS);
                } else if (i == 8) {
                    printRowOnBoardWithPieces(out, i, "black", BLACK_PIECES_PRINT_BLACK);
                } else {
                    printRowOnBoardNoPiecesBlack(out, i);
                }
            }
        }
    }

    private static void printBoardBlackFromGame(PrintStream out, ChessGame game) {
        for(int i = 0; i < ROWS; i++) {
            if(i == 0 || i == ROWS - 1) {
                printHeaderAndFooterBlack(out);
            } else {
                if(i == 1) {
                    printRowOnBoardWithPieces(out, i, "white", WHITE_PIECES_PRINT_BLACK);
                } else if (i == 2) {
                    printRowOnBoardWithPieces(out, i, "black", WHITE_PAWNS);
                } else if (i == 7) {
                    printRowOnBoardWithPieces(out, i, "white", BLACK_PAWNS);
                } else if (i == 8) {
                    printRowOnBoardWithPieces(out, i, "black", BLACK_PIECES_PRINT_BLACK);
                } else {
                    printRowOnBoardNoPiecesBlack(out, i);
                }
            }
        }
    }

    private static void printBoardWhite(PrintStream out) {
        for(int i = ROWS - 1; i >= 0; i--) {
            if(i == ROWS - 1 || i == 0) {
                printHeaderAndFooterWhite(out);
            } else {
                if(i == 8) {
                    printRowOnBoardWithPieces(out, i, "white", BLACK_PIECES_PRINT_WHITE);
                } else if (i == 7) {
                    printRowOnBoardWithPieces(out, i, "black", BLACK_PAWNS);
                } else if (i == 2) {
                    printRowOnBoardWithPieces(out, i, "white", WHITE_PAWNS);
                } else if (i == 1) {
                    printRowOnBoardWithPieces(out, i, "black", WHITE_PIECES_PRINT_WHITE);
                } else {
                    printRowOnBoardNoPiecesWhite(out, i);
                }
            }
        }
    }

    private static void printBoardWhiteFromGame(PrintStream out, ChessGame game) {
        for(int i = ROWS - 1; i >= 0; i--) {
            if(i == ROWS - 1 || i == 0) {
                printHeaderAndFooterWhite(out);
            } else {
                if(i == 8) {
                    printRowOnBoardWithPieces(out, i, "white", BLACK_PIECES_PRINT_WHITE);
                } else if (i == 7) {
                    printRowOnBoardWithPieces(out, i, "black", BLACK_PAWNS);
                } else if (i == 2) {
                    printRowOnBoardWithPieces(out, i, "white", WHITE_PAWNS);
                } else if (i == 1) {
                    printRowOnBoardWithPieces(out, i, "black", WHITE_PIECES_PRINT_WHITE);
                } else {
                    printRowOnBoardNoPiecesWhite(out, i);
                }
            }
        }
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

    private static void printBoardLineWhiteFirst(PrintStream out) {
        for(int i = 0; i < BOARD_LENGTH; i++) {
            if(isEven(i)) {
                setLightSquareColor(out);
                out.print(EMPTY);
            } else {
                setDarkSquareColor(out);
                out.print(EMPTY);
            }
        }
    }

    private static void printBoardLineWhiteFirstWithPieces(PrintStream out, ArrayList<String> pieces) {
        String color;
        if(pieces.contains(WHITE_ROOK) || pieces.contains(WHITE_PAWN)) {
            color = "white";
        } else {
            color = "black";
        }
        for(int i = 0; i < BOARD_LENGTH; i++) {
            if(isEven(i)) {
                setLightSquareColor(out);
                printPiece(out, pieces.get(i), ChessGame.TeamColor.valueOf(color));
            } else {
                setDarkSquareColor(out);
                printPiece(out, pieces.get(i), ChessGame.TeamColor.valueOf(color));
            }
        }
    }

    private static void printBoardLineBlackFirst(PrintStream out) {
        for(int i = 0; i < BOARD_LENGTH; i++) {
            if(isEven(i)) {
                setDarkSquareColor(out);
                out.print(EMPTY);
            } else {
                setLightSquareColor(out);
                out.print(EMPTY);
            }
        }
    }

    private static void printBoardLineBlackFirstWithPieces(PrintStream out, ArrayList<String> pieces) {
        String color;
        if(pieces.contains(WHITE_ROOK) || pieces.contains(WHITE_PAWN)) {
            color = "white";
        } else {
            color = "black";
        }
        for(int i = 0; i < BOARD_LENGTH; i++) {
            if(isEven(i)) {
                setDarkSquareColor(out);
                printPiece(out, pieces.get(i), ChessGame.TeamColor.valueOf(color));
            } else {
                setLightSquareColor(out);
                printPiece(out, pieces.get(i), ChessGame.TeamColor.valueOf(color));
            }
        }
    }

    private static void printRankORFile(PrintStream out, String rank) {
        setBorderSquareColor(out);
        setRankAndFileColor(out);
        out.print("\u2003" + rank + " ");
    }

    private static void printPiece(PrintStream out, String piece, ChessGame.TeamColor color) {
        if(color.equals(ChessGame.TeamColor.WHITE)) {
            setWhitePieceColor(out);
        } else {
            setBlackPieceColor(out);
        }
        out.print(SET_TEXT_BOLD);
        out.print(piece);
    }

    private static void printRowOnBoardWithPieces(PrintStream out, int rowNumber, String firstSpaceColor, ArrayList<String> pieces) {
        printRankORFile(out, String.valueOf(rowNumber));
        if(firstSpaceColor.equals("white")) {
            printBoardLineWhiteFirstWithPieces(out, pieces);
        } else {
            printBoardLineBlackFirstWithPieces(out, pieces);
        }
        printRankORFile(out, String.valueOf(rowNumber));
        printLine(out);
    }
    private static void printRowOnBoardNoPiecesWhite(PrintStream out, int rowNumber) {
        printRankORFile(out, String.valueOf(rowNumber));
        if(isEven(rowNumber)) {
            printBoardLineWhiteFirst(out);
        } else {
            printBoardLineBlackFirst(out);
        }
        printRankORFile(out, String.valueOf(rowNumber));
        printLine(out);
    }

    private static void printRowOnBoardNoPiecesBlack(PrintStream out, int rowNumber) {
        printRankORFile(out, String.valueOf(rowNumber));
        if(isEven(rowNumber)) {
            printBoardLineBlackFirst(out);
        } else {
            printBoardLineWhiteFirst(out);
        }
        printRankORFile(out, String.valueOf(rowNumber));
        printLine(out);
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

    private void mainLoop(PrintStream out, boolean isWhiteView) {
        //Change row/column to fit perspective
        int startRow = isWhiteView ? 8 : 1;
        int endRow = isWhiteView ? 1 : 8;
        int direction = isWhiteView ? -1 : 1;

        for(int r = startRow; r != endRow; r += direction) {
            for(int c = 1; c <= 8; c++) {
                int actualCol = isWhiteView ? c : (9 - c);
                if(Objects.isNull(board.getPiece(new ChessPosition(r, actualCol)))) {
                    printNull(out, calcCoordinateSum(r, actualCol));
                } else {
                    ChessPiece piece = board.getPiece(new ChessPosition(r, actualCol));
                    ChessGame.TeamColor color = piece.getTeamColor();
                    printPiece(out, getPieceString(piece, color), color);
                }

            }
        }
    }

    private String getPieceString(ChessPiece piece, ChessGame.TeamColor color) {
        boolean isWhite = color == ChessGame.TeamColor.WHITE;

        return switch (piece.getPieceType()) {
            case PAWN -> isWhite ? WHITE_PAWN : BLACK_PAWN;
            case KING -> isWhite ? WHITE_KING : BLACK_KING;
            case QUEEN -> isWhite ? WHITE_QUEEN : BLACK_QUEEN;
            case BISHOP -> isWhite ? WHITE_BISHOP : BLACK_BISHOP;
            case KNIGHT -> isWhite ? WHITE_KNIGHT : BLACK_KNIGHT;
            case ROOK -> isWhite ? WHITE_ROOK : BLACK_ROOK;
        };
    }

    private void printNull(PrintStream out, int coordinateSum) {
        if(isEven(coordinateSum)) {
            setDarkSquareColor(out);
            out.print(EMPTY);
        } else {
            setLightSquareColor(out);
            out.print(EMPTY);
        }

    }

    private int calcCoordinateSum(int row, int col) {
        return row + col;
    }
}
