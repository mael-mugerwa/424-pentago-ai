package student_player;

import boardgame.Board;
import pentago_twist.PentagoBoardState;
import pentago_twist.PentagoMove;
import java.util.List;

public class MyTools {
    public final static int winVal = Integer.MAX_VALUE;
    public final static int lostVal = Integer.MIN_VALUE;
    public final static int fourInRow = 1000;
    public final static int threeInRowSameBlock = 200;
    public final static int threeInRow = 100;
    public final static int pieceInCenter = 5;

    final class MyResult {
        private final PentagoMove bestMove;
        private final int alphaOrBeta;

        public MyResult(PentagoMove bestMove, int alphaOrBeta) {
            this.bestMove = bestMove;
            this.alphaOrBeta = alphaOrBeta;
        }

        public PentagoMove getBestMove() {
            return bestMove;
        }

        public int getAlphaOrBeta() {
            return alphaOrBeta;
        }
    }

    public static double getSomething() {
        return Math.random();
    }

    public static MyResult minimax(int myPlayer, PentagoBoardState boardState, int depth, boolean maximizingPlayer, int alpha, int beta) {
        if (boardState.gameOver() || depth == 0) {
            // evaluate(boardState);
        }

        List<PentagoMove> moves = boardState.getAllLegalMoves();
        PentagoMove bestMove;
        PentagoBoardState boardClone;

        int curEval;

        if (maximizingPlayer) {
            int maxEval = Integer.MIN_VALUE;
            for (PentagoMove move : moves) {
                boardClone = (PentagoBoardState) boardState.clone();
                boardClone.processMove(move);
                curEval = minimax(myPlayer, boardClone, depth - 1, false, alpha, beta);

                if (curEval > maxEval){
                    maxEval = curEval;
                    bestMove = move;
                }
                alpha = Math.max(alpha, curEval);
                if (beta<=alpha)
                break;
            }
            return new myResult(bestMove, maxEval);
        } else {
            int minEval = Integer.MAX_VALUE;
            for (PentagoMove move : moves) {
                boardClone = (PentagoBoardState) boardState.clone();
                boardClone.processMove(move);
                curEval = minimax(myPlayer, boardClone, depth - 1, true, alpha, beta);

                if (curEval < minEval){
                    minEval = curEval;
                    bestMove = move;
                }
                beta = Math.min(beta, curEval);
                if (beta<=alpha)
                break;
            }
            return new myResult(bestMove, minEval);
    }
}