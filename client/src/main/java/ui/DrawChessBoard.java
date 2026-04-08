package ui;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import static ui.EscapeSequences.*;

public class DrawChessBoard {

    private static final String EMPTY = "   ";
    private static final int ROWS = 10;
    private static final int BOARD_LENGTH = 8;
    private static final int BLACK_OFFSET = 95;
    private static final int WHITE_OFFSET = 96;

    static void main() {
        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);

        out.print(ERASE_SCREEN);

        printBoardBlack(out);
        printBoardWhite(out);
    }

    private static void printBoardBlack(PrintStream out) {
        for(int i = 0; i < ROWS; i++) {
            if(i == 0 || i == ROWS - 1) {
                printHeaderAndFooterBlack(out);
            } else {
                printRankORFile(out, String.valueOf(i));
                if(isEven(i)) {
                    printBoardLineBlackFirst(out);
                } else {
                    printBoardLineWhiteFirst(out);
                }
                printRankORFile(out, String.valueOf(i));
                out.print(RESET_BG_COLOR);
                out.println();
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
                out.print(RESET_BG_COLOR);
                out.println();
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
        out.print(RESET_BG_COLOR);
        out.println();
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
        out.print(RESET_BG_COLOR);
        out.println();
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

    private static void printRankORFile(PrintStream out, String rank) {
        setBorderSquareColor(out);
        setRankAndFileColor(out);
        out.print(" " + rank + " ");
    }

    private static boolean isEven (int iteration) {
        return iteration % 2 == 0;
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
