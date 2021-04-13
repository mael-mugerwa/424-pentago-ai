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

    public static ArrayList<String> getDiagonals(int[][] array) {
        int length = array.length;
        ArrayList<String> ret = new ArrayList<String>(22);

        String tmp = "";
        for (int k = 0; k < length; k++) {
            for (int j = 0; j <= k; j++) {
                int i = k - j;
                tmp += array[i][j];
            }
            ret.add(tmp);
            tmp = "";
        }

        tmp = "";
        for (int k = length - 2; k >= 0; k--) {
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
            for (int j = 0, x = i; x < length; j++, x++) {
                tmp += array[x][j];
            }
            ret.add(tmp);
            tmp = "";
        }

        tmp = "";
        for (int i = 0; i < length; i++) {
            for (int j = 0, y = i; y < length; j++, y++) {
                tmp += array[j][y];
            }
            ret.add(tmp);
            tmp = "";
        }

        return ret;
    }

    public static ArrayList<String> getRows(int[][] array) {
        ArrayList<String> ret = new ArrayList<String>(6);
        String tmp = "";
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 6; j++) {
                tmp += array[i][j];
            }
            ret.add(tmp);
            tmp="";
        }
        return ret;
    }

    public static ArrayList<String> getColumns(int[][] array) {
        ArrayList<String> ret = new ArrayList<String>(6);
        String tmp = "";
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 6; j++) {
                tmp += array[j][i];
            }
            ret.add(tmp);
            tmp="";
        }
        return ret;
    }

    public static void main(String[] args) {
        Piece[][] boarding = { { Piece.WHITE, Piece.EMPTY, Piece.WHITE, Piece.WHITE, Piece.BLACK, Piece.EMPTY },
                { Piece.BLACK, Piece.WHITE, Piece.BLACK, Piece.WHITE, Piece.BLACK, Piece.EMPTY },
                { Piece.WHITE, Piece.EMPTY, Piece.WHITE, Piece.WHITE, Piece.EMPTY, Piece.BLACK },
                { Piece.EMPTY, Piece.BLACK, Piece.WHITE, Piece.BLACK, Piece.EMPTY, Piece.EMPTY },
                { Piece.BLACK, Piece.EMPTY, Piece.EMPTY, Piece.EMPTY, Piece.WHITE, Piece.BLACK },
                { Piece.EMPTY, Piece.EMPTY, Piece.EMPTY, Piece.BLACK, Piece.EMPTY, Piece.EMPTY }, };

        int[][] int_board = { 
            { 1, 0, 1, 1, 2, 0 }, 
            { 2, 1, 2, 1, 2, 0 }, 
            { 1, 0, 1, 1, 0, 2 }, 
            { 0, 2, 1, 2, 0, 0 },
            { 2, 0, 0, 0, 1, 2 }, 
            { 0, 0, 0, 2, 0, 0 }, };
            
        String[] expected = {"20", "111", "0021", "22112", "001120", "00200", "0002", "210", "02", "20", "000", "1202", "20100", "111210", "02102", "1100", "122", "20"};
        System.out.println(Arrays.toString(expected));
        ArrayList<String> diags = getDiagonals(int_board);
        System.out.println(diags.toString());

        // ArrayList<String> rows = getRows(int_board);
        // System.out.println(rows.toString());
        // ArrayList<String> cols = getColumns(int_board);
        // System.out.println(cols.toString());

        // System.out.println("SIZE "+list.size());
        // for (String s : list){
        // System.out.println(s);
        // }

        // String a = "1-110-10";
        // String[] arr = a.split("-1");

        // System.out.println(Arrays.toString(arr));
        // evaluate(board);
    }
}