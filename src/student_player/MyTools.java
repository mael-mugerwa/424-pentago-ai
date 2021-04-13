package student_player;

import pentago_twist.PentagoBoardState;
import pentago_twist.PentagoBoardState.Piece;
import pentago_twist.PentagoCoord;
import java.util.function.UnaryOperator;

public class MyTools {
    private final static int win = Integer.MAX_VALUE;
    private final static int fourWinnable = 100000;
    private final static int fourBlocked = 1000;
    private final static int threeWinnable = 10000;
    private final static int threeBlocked = 100;
    private final static int twoWinnable = 5;
    private final static int twoBlocked = 1;
    private final static int blocked = 0;
    private final static int centerQuadrant = 20;

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
            for (int j = yStart; j < yEnd;) {
                int ret = checkConsecutivePieces(player, new PentagoCoord(i, j), direction);
                if (ret == win) {
                    j += 5;
                } else if (ret == fourBlocked || ret == fourWinnable) {
                    j += 4;
                } else if (ret == threeBlocked || ret == threeWinnable) {
                    j += 3;
                } else if (ret == twoBlocked || ret == twoWinnable) {
                    j += 2;
                } else {
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
            return win;
        } else if (consecutivePiecesCounter == 4) {
            if (notBlocked) {
                return fourWinnable;
            } else {
                return fourBlocked;
            }
        } else if (consecutivePiecesCounter == 3) {
            if (notBlocked) {
                return threeWinnable;
            } else {
                return threeBlocked;
            }
        } else if (consecutivePiecesCounter == 2) {
            if (notBlocked) {
                return twoWinnable;
            } else {
                return twoBlocked;
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

    private static int checkCenterQuandrant(int player) {
        Piece currColour = player == 0 ? Piece.WHITE : Piece.BLACK;
        int score = 0;
        if (currColour == board[1][1])
            score += centerQuadrant;

        if (currColour == board[1][4])
            score += centerQuadrant;

        if (currColour == board[4][1])
            score += centerQuadrant;

        if (currColour == board[4][4])
            score += centerQuadrant;

        return score;
    }

    public static int evaluate(PentagoBoardState boardState) {
        // public static int evaluate(Piece[][] boardState) {
        // long startTime = System.currentTimeMillis();

        int turnPlayer = boardState.getTurnPlayer();
        board = boardState.getBoard();
        // int turnPlayer = 0;

        // test all rows, colums,
        int score = 0;
        score += checkVerticalWin(turnPlayer);
        score += checkHorizontalWin(turnPlayer);
        score += checkDiagRightWin(turnPlayer);
        score += checkDiagLeftWin(turnPlayer);
        score += checkCenterQuandrant(turnPlayer);

        int opponent = 1 - turnPlayer;
        score -= checkVerticalWin(opponent);
        score -= checkHorizontalWin(opponent);
        score -= checkDiagRightWin(opponent);
        score -= checkDiagLeftWin(opponent);
        score -= checkCenterQuandrant(opponent);

        // long endTime = System.currentTimeMillis();
        // System.out.println("evaluation took " + (endTime-startTime));
        // System.out.println("evaluation score " + score);
        return score;
    }

    public static void main(String[] args) {
        Piece[][] boarding = { { Piece.WHITE, Piece.EMPTY, Piece.WHITE, Piece.WHITE, Piece.BLACK, Piece.EMPTY },
                { Piece.BLACK, Piece.WHITE, Piece.BLACK, Piece.WHITE, Piece.BLACK, Piece.EMPTY },
                { Piece.WHITE, Piece.EMPTY, Piece.WHITE, Piece.WHITE, Piece.EMPTY, Piece.BLACK },
                { Piece.EMPTY, Piece.BLACK, Piece.WHITE, Piece.BLACK, Piece.EMPTY, Piece.EMPTY },
                { Piece.BLACK, Piece.EMPTY, Piece.EMPTY, Piece.EMPTY, Piece.EMPTY, Piece.BLACK },
                { Piece.EMPTY, Piece.EMPTY, Piece.EMPTY, Piece.BLACK, Piece.EMPTY, Piece.EMPTY }, };
        // board = boarding;
        // evaluate(board);
    }
}