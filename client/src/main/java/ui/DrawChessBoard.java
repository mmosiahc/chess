package ui;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static ui.EscapeSequences.*;

public class DrawChessBoard {

    private static final int ROWS = 10;
    private static final int BOARD_LENGTH = 8;
    private static final int BLACK_OFFSET = 95;
    private static final int WHITE_OFFSET = 96;
    private static final ArrayList<String> blackPieces = new ArrayList<>(List.of(
            BLACK_ROOK, BLACK_KNIGHT, BLACK_BISHOP, BLACK_KING, BLACK_QUEEN, BLACK_BISHOP, BLACK_KNIGHT, BLACK_ROOK));
    private static final ArrayList<String> whitePieces = new ArrayList<>(List.of(
            WHITE_ROOK, WHITE_KNIGHT, WHITE_BISHOP, WHITE_KING, WHITE_QUEEN, WHITE_BISHOP, WHITE_KNIGHT, WHITE_ROOK));
    private static final ArrayList<String> whitePawns = new ArrayList<>(List.of(
            WHITE_PAWN, WHITE_PAWN, WHITE_PAWN, WHITE_PAWN, WHITE_PAWN, WHITE_PAWN, WHITE_PAWN, WHITE_PAWN));
    private static final ArrayList<String> blackPawns = new ArrayList<>(List.of(
            BLACK_PAWN, BLACK_PAWN, BLACK_PAWN, BLACK_PAWN, BLACK_PAWN, BLACK_PAWN, BLACK_PAWN, BLACK_PAWN));

    static void main() {
        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);

        out.print(ERASE_SCREEN);

        printBoardBlack(out);
        printLine(out);
        printBoardWhite(out);
    }

    private static void printBoardBlack(PrintStream out) {
        for(int i = 0; i < ROWS; i++) {
            if(i == 0 || i == ROWS - 1) {
                printHeaderAndFooterBlack(out);
            } else {
                if(i == 1) {
                    printRankORFile(out, String.valueOf(i));
                    printBoardLineWhiteFirstWithPieces(out, whitePieces);
                    printRankORFile(out, String.valueOf(i));
                    printLine(out);
                } else if (i == 2) {
                    printRankORFile(out, String.valueOf(i));
                    printBoardLineBlackFirstWithPieces(out, whitePawns);
                    printRankORFile(out, String.valueOf(i));
                    printLine(out);
                } else if (i == 7) {
                    printRankORFile(out, String.valueOf(i));
                    printBoardLineWhiteFirstWithPieces(out, blackPawns);
                    printRankORFile(out, String.valueOf(i));
                    printLine(out);
                } else if (i == 8) {
                    printRankORFile(out, String.valueOf(i));
                    printBoardLineBlackFirstWithPieces(out, blackPieces);
                    printRankORFile(out, String.valueOf(i));
                    printLine(out);
                } else {
                    printRankORFile(out, String.valueOf(i));
                    if(isEven(i)) {
                        printBoardLineBlackFirst(out);
                    } else {
                        printBoardLineWhiteFirst(out);
                    }
                    printRankORFile(out, String.valueOf(i));
                    printLine(out);
                }
            }
        }
    }

    private static void printBoardWhite(PrintStream out) {
        for(int i = ROWS - 1; i >= 0; i--) {
            if(i == ROWS - 1 || i == 0) {
                printHeaderAndFooterWhite(out);
            } else {
                printRankORFile(out, String.valueOf(i));
                if(isEven(i)) {
                    printBoardLineBlackFirst(out);
                } else {
                    printBoardLineWhiteFirst(out);
                }
                printRankORFile(out, String.valueOf(i));
                printLine(out);
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
                out.print(EscapeSequences.EMPTY);
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
                out.print(EscapeSequences.EMPTY);
            }
        }
        printLine(out);
    }

    private static void printBoardLineWhiteFirst(PrintStream out) {
        for(int i = 0; i < BOARD_LENGTH; i++) {
            if(isEven(i)) {
                setLightSquareColor(out);
                out.print(EscapeSequences.EMPTY);
            } else {
                setDarkSquareColor(out);
                out.print(EscapeSequences.EMPTY);
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
                printPiece(out, pieces.get(i), color);
            } else {
                setDarkSquareColor(out);
                printPiece(out, pieces.get(i), color);
            }
        }
    }

    private static void printBoardLineBlackFirst(PrintStream out) {
        for(int i = 0; i < BOARD_LENGTH; i++) {
            if(isEven(i)) {
                setDarkSquareColor(out);
                out.print(EscapeSequences.EMPTY);
            } else {
                setLightSquareColor(out);
                out.print(EscapeSequences.EMPTY);
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
                printPiece(out, pieces.get(i), color);
            } else {
                setLightSquareColor(out);
                printPiece(out, pieces.get(i), color);
            }
        }
    }

    private static void printRankORFile(PrintStream out, String rank) {
        setBorderSquareColor(out);
        setRankAndFileColor(out);
        out.print("\u2003" + rank + " ");
    }

    private static void printPiece(PrintStream out, String piece, String color) {
        if(color.equalsIgnoreCase("white")) {
            setWhitePieceColor(out);
        } else {
            setBlackPieceColor(out);
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
}
