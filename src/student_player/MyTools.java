package student_player;

import pentago_twist.PentagoBoardState;
import pentago_twist.PentagoBoardState.Piece;

import java.util.ArrayList;

public class MyTools {

    // evaluation for each position
    private final static int win = Integer.MAX_VALUE;
    private final static int fourWinnable = 10000;
    private final static int fourBlocked = 0;
    private final static int threeWinnable = 1000;
    private final static int threeBlocked = 0;
    private final static int twoWinnable = 1;
    private final static int twoBlocked = 0;
    private final static int centerQuadrant = 5;
    // private final static int fourWinnable = 100000;
    // private final static int fourBlocked = 1000;
    // private final static int threeWinnable = 10000;
    // private final static int threeBlocked = 100;
    // private final static int twoWinnable = 5;
    // private final static int twoBlocked = 1;
    // private final static int centerQuadrant = 20;

    // variables for the piece that corresponds to turnPlayer and his opponent
    // if turnPlayer is white, turnPlayerPiece = 1 for example
    private static int turnPlayerPiece = 0;
    private static int opponentPlayerPiece = 0;

    // variable to indicate at what point to stop evaluation
    // (if I only want to evaluate wins, I set stopAt = 5; for 4 in a row, set to 4)
    private static int stopAt = 0;

    // make an int[][] board based on boardState (white = 1, black = 2, empty = 0)
    private static int[][] getBoard(PentagoBoardState boardState) {
        int[][] board = new int[6][6];
        for (int j = 0; j < 6; j++) {
            for (int k = 0; k < 6; k++) {
                Piece p = boardState.getPieceAt(j, k);
                if (p == Piece.EMPTY) {
                    board[j][k] = 0;
                } else if (p == Piece.WHITE) {
                    board[j][k] = 1;
                } else {
                    board[j][k] = 2;
                }
            }
        }
        return board;
    }

    // check whether turnPlayer's pieces are in the center of each quadrant
    private static int checkCenterQuandrant(int[][] board) {
        int score = 0;
        if (turnPlayerPiece == board[1][1])
            score += centerQuadrant;

        if (turnPlayerPiece == board[1][4])
            score += centerQuadrant;

        if (turnPlayerPiece == board[4][1])
            score += centerQuadrant;

        if (turnPlayerPiece == board[4][4])
            score += centerQuadrant;
        return score;
    }

    // return all diagonals of length >= 2 as strings in an arraylist
    private static ArrayList<String> getDiagonals(int[][] array) {
        int length = array.length;
        ArrayList<String> ret = new ArrayList<String>(22);

        String tmp = "";
        for (int k = 1; k < length; k++) {
            for (int j = 0; j <= k; j++) {
                int i = k - j;
                tmp += array[i][j];
            }
            ret.add(tmp);
            tmp = "";
        }

        tmp = "";
        for (int k = length - 2; k > 0; k--) {
            for (int j = 0; j <= k; j++) {
                int i = k - j;
                tmp += array[length - j - 1][length - i - 1];
            }
            ret.add(tmp);
            tmp = "";
        }

        // right diagonals top half
        tmp = "";
        for (int i = length - 1; i > 0; i--) {
            for (int j = 0, x = i - 1; x < length; j++, x++) {
                tmp += array[x][j];
            }
            if (tmp != "")
                ret.add(tmp);
            tmp = "";
        }

        tmp = "";
        for (int i = 1; i < length - 1; i++) {
            for (int j = 0, y = i; y < length; j++, y++) {
                tmp += array[j][y];
            }
            ret.add(tmp);
            tmp = "";
        }

        return ret;
    }

    // return all rows of the board as strings in an arraylist
    private static ArrayList<String> getRows(int[][] array) {
        ArrayList<String> ret = new ArrayList<String>(6);
        String tmp = "";
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 6; j++) {
                tmp += array[i][j];
            }
            ret.add(tmp);
            tmp = "";
        }
        return ret;
    }

    // return all columns of the board as strings in an arraylist
    private static ArrayList<String> getColumns(int[][] array) {
        ArrayList<String> ret = new ArrayList<String>(6);
        String tmp = "";
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 6; j++) {
                tmp += array[j][i];
            }
            ret.add(tmp);
            tmp = "";
        }
        return ret;
    }

    // evaluates all the rows of the list by splitting them into strings with only
    // turnPlayer's pieces
    // (can also be used to evaluate diagonals and columns )
    private static int evaluateRow(ArrayList<String> rows) {
        int score = 0;
        // for each row
        for (String row : rows) {
            // split the row by the opponent's piece
            String[] splitRow = row.split(String.valueOf(opponentPlayerPiece));

            // for each split block of the row
            for (String consecutiveBlock : splitRow) {
                boolean winnable = false;
                // we can still win on that block if it has enough empty pieces
                if (consecutiveBlock.length() >= 5) {
                    winnable = true;
                }

                // for each split block, split in consecutives pieces of current player by
                // removing empty pieces
                for (String consecutivePieces : consecutiveBlock.split("0")) {
                    // based on number of consecutive pieces, return eval
                    score += getEval(winnable, consecutivePieces.length());
                }
            }
        }
        return score;
    }

    // return the correct evaluation based on if a consecutive block is blocked and
    // how many consecutive pieces there are
    private static int getEval(boolean winnable, int consecutivePieces) {
        if (winnable) {
            if (consecutivePieces >= 5 && stopAt <= 5) {
                return win;
            } else if (consecutivePieces == 4 && stopAt <= 4) {
                return fourWinnable;
            } else if (consecutivePieces == 3 && stopAt <= 3) {
                return threeWinnable;
            } else if (consecutivePieces == 2 && stopAt <= 2) {
                return twoWinnable;
            } else {
                return 0;
            }
        } else {
            if (consecutivePieces >= 5) {
                return win;
            } else if (consecutivePieces == 4) {
                return fourBlocked;
            } else if (consecutivePieces == 3) {
                return threeBlocked;
            } else if (consecutivePieces == 2) {
                return twoBlocked;
            } else {
                return 0;
            }
        }
    }

    // evaluation function, returns total eval of the board based on turnPlayer
    public static int evaluate(PentagoBoardState boardState) {
        int turnPlayer = boardState.getTurnPlayer();

        // get piece number for turnPlayer
        turnPlayerPiece = turnPlayer == 0 ? 1 : 2;
        opponentPlayerPiece = turnPlayerPiece == 1 ? 2 : 1;

        int[][] board = getBoard(boardState);

        ArrayList<String> diags = getDiagonals(board);
        ArrayList<String> rows = getRows(board);
        ArrayList<String> cols = getColumns(board);
        int score = 0;
        stopAt = 2;
        score += evaluateRow(diags);
        score += evaluateRow(rows);
        score += evaluateRow(cols);
        score += checkCenterQuandrant(board);

        // switch player pieces to test opponent's position
        int tmp = turnPlayerPiece;
        turnPlayerPiece = opponentPlayerPiece;
        opponentPlayerPiece = tmp;

        // stop opponent eval at 5 as to only remove situations where opponent wins
        stopAt = 5;
        // subtract opponent's score from turnPlayer's turn
        score -= evaluateRow(diags);
        score -= evaluateRow(rows);
        score -= evaluateRow(cols);
        score -= checkCenterQuandrant(board);

        return score;
    }

    public static void main(String[] args) {
        // test eval
        // int[][] int_board = {
        // { 2, 1, 1, 1, 0, 0 },
        // { 0, 2, 1, 1, 0, 0 },
        // { 2, 0, 1, 1, 0, 0 },
        // { 0, 0, 1, 0, 0, 0 },
        // { 0, 1, 0, 0, 2, 0 },
        // { 0, 0, 0, 0, 0, 0 }, };

        // evaluate(int_board);
    }
}