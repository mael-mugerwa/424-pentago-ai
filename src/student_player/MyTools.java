package student_player;

import pentago_twist.PentagoBoardState;
import pentago_twist.PentagoBoardState.Piece;
import pentago_twist.PentagoCoord;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.UnaryOperator;

public class MyTools {
    private final static int win = Integer.MAX_VALUE;

    private final static int fourWinnable = 100000;
    private final static int fourBlocked = 1000;
    private final static int threeWinnable = 10000;
    private final static int threeBlocked = 100;
    private final static int twoWinnable = 5;
    private final static int twoBlocked = 1;
    private final static int centerQuadrant = 20;

    private static int[][] board;

    private static int checkCenterQuandrant(int player) {
        int currColour = player == 0 ? 1 : 2;
        int score = 0;
        if (currColour == board[1][1])
            score += centerQuadrant;

        if (currColour == board[1][4])
            score += centerQuadrant;

        if (currColour == board[4][1])
            score += centerQuadrant;

        if (currColour == board[4][4])
            score += centerQuadrant;

        System.out.println("num of quadrants " + (score / centerQuadrant));
        return score;
    }

    // public static int evaluate(PentagoBoardState boardState) {
    public static int evaluate(Piece[][] boardState) {
        // long startTime = System.currentTimeMillis();

        // int turnPlayer = boardState.getTurnPlayer();
        // board = boardState.getBoard();
        int turnPlayer = 0;

        // test all rows, colums,
        int score = 0;
        System.out.println("VERTICAL");
        score += checkVerticalWin(turnPlayer, 2);
        System.out.println("HORIZONTAL");
        score += checkHorizontalWin(turnPlayer, 2);
        System.out.println("R DIAGONAL");
        score += checkDiagRightWin(turnPlayer, 2);
        System.out.println("L DIAGONAL");
        score += checkDiagLeftWin(turnPlayer, 2);
        System.out.println("QUADRANTS");
        score += checkCenterQuandrant(turnPlayer);

        int opponent = 1 - turnPlayer;
        System.out.println("OPPONENT");
        score -= checkVerticalWin(opponent, 5);
        score -= checkHorizontalWin(opponent, 5);
        score -= checkDiagRightWin(opponent, 5);
        score -= checkDiagLeftWin(opponent, 5);

        // long endTime = System.currentTimeMillis();
        // System.out.println("evaluation took " + (endTime-startTime));
        System.out.println("evaluation score " + score);
        return score;
    }

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

    private static int evaluateRow(ArrayList<String> rows) {
        int score = 0;
        for (String row : rows) {
            String[] splitRow = row.split("2");

            for (String consecutiveBlock : splitRow) {
                boolean winnable = false;
                if (consecutiveBlock.length() >= 5) {
                    winnable = true;
                }

                for (String consecutivePieces : consecutiveBlock.split("0")) {
                    score += getEval(winnable, consecutivePieces.length());
                }
            }
        }
        return score;
    }

    private static int getEval(boolean winnable, int consecutivePieces) {
        if (winnable) {
            if (consecutivePieces >= 5) {
                System.out.println("Five Winnable");
                return win;
            } else if (consecutivePieces == 4) {
                System.out.println("Four Winnable");
                return fourWinnable;
            } else if (consecutivePieces == 3) {
                System.out.println("Three Winnable");
                return threeWinnable;
            } else if (consecutivePieces == 2) {
                System.out.println("Two Winnable");
                return twoWinnable;
            } else {
                return 0;
            }
        } else {
            if (consecutivePieces >= 5) {
                System.out.println("Five Blocked");
                return win;
            } else if (consecutivePieces == 4) {
                System.out.println("Four Blocked");
                return fourBlocked;
            } else if (consecutivePieces == 3) {
                System.out.println("Three Blocked");
                return threeBlocked;
            } else if (consecutivePieces == 2) {
                System.out.println("Two Blocked");
                return twoBlocked;
            } else {
                return 0;
            }
        }
    }

    private static int evaluateNEW(int[][] int_board) {
        ArrayList<String> diags = getDiagonals(int_board);
        ArrayList<String> rows = getRows(int_board);
        ArrayList<String> cols = getColumns(int_board);
        int score = 0;
        System.out.println("DIAGONAL");
        score += evaluateRow(diags);
        System.out.println("ROWS");
        score += evaluateRow(rows);
        System.out.println("COLS");
        score += evaluateRow(cols);
        return score;
    }

    public static void main(String[] args) {
        Piece[][] boarding = { { Piece.WHITE, Piece.EMPTY, Piece.WHITE, Piece.WHITE, Piece.BLACK, Piece.EMPTY },
                { Piece.BLACK, Piece.WHITE, Piece.BLACK, Piece.WHITE, Piece.BLACK, Piece.EMPTY },
                { Piece.WHITE, Piece.EMPTY, Piece.WHITE, Piece.WHITE, Piece.EMPTY, Piece.BLACK },
                { Piece.EMPTY, Piece.BLACK, Piece.WHITE, Piece.BLACK, Piece.EMPTY, Piece.EMPTY },
                { Piece.BLACK, Piece.EMPTY, Piece.EMPTY, Piece.EMPTY, Piece.WHITE, Piece.BLACK },
                { Piece.EMPTY, Piece.EMPTY, Piece.EMPTY, Piece.BLACK, Piece.EMPTY, Piece.EMPTY }, };

        int[][] int_board = { 
            { 2, 1, 1, 1, 0, 0 }, 
            { 0, 2, 1, 1, 0, 0 }, 
            { 2, 0, 1, 1, 0, 0 }, 
            { 0, 0, 1, 0, 0, 0 },
            { 0, 1, 0, 0, 2, 0 }, 
            { 0, 0, 0, 0, 0, 0 }, };

        evaluateNEW(int_board);
    }
}