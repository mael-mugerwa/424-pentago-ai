package student_player;

import java.util.List;

import boardgame.Move;
import boardgame.Server;
import pentago_twist.PentagoPlayer;
import pentago_twist.PentagoBoardState;
import pentago_twist.PentagoMove;

/** A player file submitted by a student. */
public class StudentPlayer extends PentagoPlayer {
    public static int myPlayer;
    public static long startTime, endTime;
    public static boolean cutoff;
    public static PentagoMove randomMove;

    /**
     * You must modify this constructor to return your student number. This is
     * important, because this is what the code that runs the competition uses to
     * associate you with your agent. The constructor should do nothing else.
     */
    public StudentPlayer() {
        super("260805446");
    }

    /**
     * This is the primary method that you need to implement. The ``boardState``
     * object contains the current state of the game, which your agent must use to
     * make decisions.
     */
    public Move chooseMove(PentagoBoardState boardState) {
        // You probably will make separate functions in MyTools.
        // For example, maybe you'll need to load some pre-processed best opening
        // strategies...

        myPlayer = boardState.getTurnPlayer();
        startTime = System.currentTimeMillis();
        endTime = (boardState.getTurnNumber() == 0) ? System.currentTimeMillis() + Server.FIRST_MOVE_TIMEOUT - 50
                : System.currentTimeMillis() + Server.DEFAULT_TIMEOUT - 50;
        cutoff = false;
        randomMove = (PentagoMove) boardState.getRandomMove();

        Result bestResult = new Result(randomMove, 0);
        int depth = 1;
        while (true) {
            Result res = minimax(boardState, depth, true, Integer.MIN_VALUE, Integer.MAX_VALUE);
            if (res.getScore() > bestResult.getScore())
                bestResult = res;

            if (cutoff) {
                System.out.println("Cutoff search at depth " + depth);
                break;
            }
            depth++;
        }

        System.out.println("Found Best Move in " + (System.currentTimeMillis() - startTime));
        return bestResult.getBestMove();
    }

    final class Result {
        private final PentagoMove bestMove;
        private final int score;

        public Result(PentagoMove bestMove, int score) {
            this.bestMove = bestMove;
            this.score = score;
        }

        public PentagoMove getBestMove() {
            return bestMove;
        }

        public int getScore() {
            return score;
        }
    }

    private Result minimax(PentagoBoardState boardState, int depth, boolean maximizingPlayer, int alpha, int beta) {
        if (boardState.gameOver() || depth == 0) {
            MyTools.evaluate(boardState);
        }

        List<PentagoMove> moves = boardState.getAllLegalMoves();
        PentagoMove bestMove = randomMove;
        PentagoBoardState boardClone;

        int curEval;

        if (maximizingPlayer) {
            int maxEval = Integer.MIN_VALUE;
            for (PentagoMove move : moves) {
                if (timeExceeded()) {
                    cutoff = true;
                    break;
                }

                boardClone = (PentagoBoardState) boardState.clone();
                boardClone.processMove(move);
                Result res = minimax(boardClone, depth - 1, false, alpha, beta);
                curEval = res.getScore();

                if (curEval > maxEval) {
                    maxEval = curEval;
                    bestMove = move;
                }
                alpha = Math.max(alpha, curEval);
                if (beta <= alpha)
                    break;
            }
            return new Result(bestMove, maxEval);
        } else {
            int minEval = Integer.MAX_VALUE;
            for (PentagoMove move : moves) {
                if (timeExceeded()) {
                    cutoff = true;
                    break;
                }

                boardClone = (PentagoBoardState) boardState.clone();
                boardClone.processMove(move);
                Result res = minimax(boardClone, depth - 1, true, alpha, beta);
                curEval = res.getScore();

                if (curEval < minEval) {
                    minEval = curEval;
                    bestMove = move;
                }
                beta = Math.min(beta, curEval);
                if (beta <= alpha)
                    break;
            }
            return new Result(bestMove, minEval);
        }
    }

    public static boolean timeExceeded() {
        return (System.currentTimeMillis() > endTime);
    }
}