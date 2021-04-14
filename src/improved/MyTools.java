package improved;

import pentago_twist.PentagoBoardState;
import pentago_twist.PentagoBoardState.Piece;

import java.util.ArrayList;
import java.util.Arrays;

public class MyTools {

    // evaluation for each position
    private final static int win = 1000000;
    private final static int fourWinnable = 10000;
    private final static int fourBlocked = 100;
    private final static int threeWinnable = 1000;
    private final static int threeBlocked = 50;
    private final static int twoWinnable = 0;
    private final static int twoBlocked = 0;
    private final static int centerQuadrant = 50;

    // variables for the piece that corresponds to myPlayer and his opponent
    // if myPlayer is white, myPlayerPiece = 1 for example
    private static int myPlayerPiece = 0;
    private static int opponentPlayerPiece = 0;

    // variable to indicate at what point to stop evaluation
    // (if I only want to evaluate wins, I set stopAt = 5; for 4 in a row, set to 4)
    private static int stopAt = 0;

    // turn int[][] board into a string
    public static String getBoardString(PentagoBoardState boardState) {
        String ret = "";
        int[][] board = getBoard(boardState);
        for (int[] i : board) {
            ret += Arrays.toString(i);
        }

        return ret;
    }

    // return an int[][] corresponding to the board of the board state with (white =
    // 1, black = 2, empty = 0 )
    public static int[][] getBoard(PentagoBoardState boardState) {
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

    // check whether myPlayer's pieces are in the center of each quadrant
    private static int checkCenterQuandrant(int[][] board) {
        int score = 0;
        if (myPlayerPiece == board[1][1])
            score += centerQuadrant;

        if (myPlayerPiece == board[1][4])
            score += centerQuadrant;

        if (myPlayerPiece == board[4][1])
            score += centerQuadrant;

        if (myPlayerPiece == board[4][4])
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
    // myPlayer's pieces
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

    // evaluation function, returns total eval of the board based on myPlayer
    public static int evaluate(PentagoBoardState boardState) {
        // get piece number for myPlayer
        myPlayerPiece = StudentPlayer.myPlayer == 0 ? 1 : 2;
        opponentPlayerPiece = myPlayerPiece == 1 ? 2 : 1;

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

        // see if i win
        boolean iWin = (score >= win);

        // switch player pieces to test opponent's position
        int tmp = myPlayerPiece;
        myPlayerPiece = opponentPlayerPiece;
        opponentPlayerPiece = tmp;

        // stop opponent eval at 5 as to only remove situations where opponent wins
        stopAt = 2;
        // subtract opponent's score from myPlayer's turn to prevent win
        int opponentScore = 0;
        opponentScore += evaluateRow(diags);
        opponentScore += evaluateRow(rows);
        opponentScore += evaluateRow(cols);
        score -= opponentScore;

        // see if opponent won
        boolean opponentWin = (opponentScore >= win);

        if (iWin && opponentWin) {// game drawn avoid if possible
            return 0;
        }

        return score;
    }

    // public static void main(String[] args) {
    //     // test evaluation function
    //     // int[][] int_board = { { 2, 1, 1, 1, 0, 0 }, { 2, 2, 1, 1, 0, 0 }, { 2, 0, 1,
    //     // 1, 0, 0 }, { 2, 0, 1, 0, 1, 0 },
    //     // { 2, 1, 0, 0, 2, 0 }, { 2, 0, 0, 0, 0, 0 }, };

    //     // System.out.println(evaluate(int_board));
    // }
}