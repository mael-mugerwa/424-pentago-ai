package student_player;

import pentago_twist.PentagoBoardState;
import pentago_twist.PentagoBoardState.Piece;
import pentago_twist.PentagoCoord;
import java.util.function.UnaryOperator;

public class MyTools {
    private final static int win = Integer.MAX_VALUE;
    private final static int fourWinnable = 10000;
    private final static int fourBlocked = 100;
    private final static int threeWinnable = 1000;
    private final static int threeBlocked = 10;

    private static Piece[][] board;
    private static final UnaryOperator<PentagoCoord> getNextHorizontal = c -> new PentagoCoord(c.getX(), c.getY() + 1);
    private static final UnaryOperator<PentagoCoord> getNextVertical = c -> new PentagoCoord(c.getX() + 1, c.getY());
    private static final UnaryOperator<PentagoCoord> getNextDiagRight = c -> new PentagoCoord(c.getX() + 1,
            c.getY() + 1);
    private static final UnaryOperator<PentagoCoord> getNextDiagLeft = c -> new PentagoCoord(c.getX() + 1,
            c.getY() - 1);

    private static int checkConsecutivePiecesRange(int player, int xStart, int xEnd, int yStart, int yEnd,
            UnaryOperator<PentagoCoord> direction) {
        int score = 0;
        for (int i = xStart; i < xEnd; i++) {
            for (int j = yStart; j < yEnd; ) {
                // score += checkConsecutivePieces(player, new PentagoCoord(i, j), direction);
                int ret = checkConsecutivePieces(player, new PentagoCoord(i, j), direction);
                if (ret == win) {
                    j += 5;
                }
                else if (ret == fourBlocked || ret == fourWinnable) {
                    j += 4;
                }
                else if (ret == threeBlocked || ret == threeWinnable) {
                    j += 3;
                }
                else{
                    j++;
                }
                score += ret;
            }
        }
        return score;
    }

    private static int checkConsecutivePieces(int player, PentagoCoord start, UnaryOperator<PentagoCoord> direction) {
        int consecutivePiecesCounter = 0;
        int winnableCounter = 0;
        Piece currColour = player == 0 ? Piece.WHITE : Piece.BLACK;
        PentagoCoord current = start;
        while (true) {
            try {
                if (currColour == board[current.getX()][current.getY()]) {
                    consecutivePiecesCounter++;
                    winnableCounter++;
                    current = direction.apply(current);
                }
                // else, test to see if pieces are empty
                else if (Piece.EMPTY == board[current.getX()][current.getY()]) {
                    winnableCounter++; // used to test if current row/col/diagonal is blocked or not
                    current = direction.apply(current);
                }
                // we have an opponent piece
                else {
                    break;
                }
            } catch (IllegalArgumentException e) { // We have run off the board
                break;
            }
        }

        boolean notBlocked = winnableCounter >= 5;
        if (consecutivePiecesCounter >= 5) {
            // System.out.println("5 in a row");
            return win;
        } else if (consecutivePiecesCounter == 4) {
            if (notBlocked) {
                // System.out.println("4 in a row winnable");
                return fourWinnable;
            } else {
                // System.out.println("4 in a row blocked");
                return fourBlocked;
            }
        } else if (consecutivePiecesCounter == 3) {
            if (notBlocked) {
                // System.out.println("3 in a row winnable");
                return threeWinnable;
            } else {
                // System.out.println("3 in a row blocked");
                return threeBlocked;
            }
        }
        return 0;
    }

    private static int checkVerticalWin(int player) {
        return checkConsecutivePiecesRange(player, 0, 2, 0, PentagoBoardState.BOARD_SIZE, getNextVertical);
    }

    private static int checkHorizontalWin(int player) {
        return checkConsecutivePiecesRange(player, 0, PentagoBoardState.BOARD_SIZE, 0, 2, getNextHorizontal);
    }

    private static int checkDiagRightWin(int player) {
        return checkConsecutivePiecesRange(player, 0, 2, 0, 2, getNextDiagRight);
    }

    private static int checkDiagLeftWin(int player) {
        return checkConsecutivePiecesRange(player, 0, 2, PentagoBoardState.BOARD_SIZE - 2, PentagoBoardState.BOARD_SIZE,
                getNextDiagLeft);
    }

    public static int evaluate(PentagoBoardState boardState) {
        int turnPlayer = boardState.getTurnPlayer();
        board = boardState.getBoard();

        // test all rows, colums,
        int score = 0;
        score += checkVerticalWin(turnPlayer);
        score += checkHorizontalWin(turnPlayer);
        score += checkDiagRightWin(turnPlayer);
        score += checkDiagLeftWin(turnPlayer);
        // System.out.println("evaluation player " + turnPlayer + " : " + score);
        return score;
    }

    public static void main(String[] args) {
        Piece[][] boarding = { 
                { Piece.WHITE, Piece.WHITE, Piece.WHITE, Piece.WHITE, Piece.BLACK, Piece.EMPTY },
                { Piece.WHITE, Piece.WHITE, Piece.WHITE, Piece.WHITE, Piece.EMPTY, Piece.EMPTY },
                { Piece.EMPTY, Piece.EMPTY, Piece.WHITE, Piece.WHITE, Piece.EMPTY, Piece.EMPTY },
                { Piece.EMPTY, Piece.EMPTY, Piece.EMPTY, Piece.BLACK, Piece.EMPTY, Piece.EMPTY },
                { Piece.EMPTY, Piece.EMPTY, Piece.EMPTY, Piece.EMPTY, Piece.EMPTY, Piece.EMPTY },
                { Piece.EMPTY, Piece.EMPTY, Piece.EMPTY, Piece.EMPTY, Piece.EMPTY, Piece.EMPTY }, };
        board = boarding;
        // evaluate(board);
    }
}