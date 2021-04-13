package student_player;

import java.util.HashMap;
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
    public static HashMap<PentagoBoardState, Result> hashMap;
    // useless just to count hash hit
    public static int c;

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
        endTime = (boardState.getTurnNumber() == 0) ? System.currentTimeMillis() + Server.FIRST_MOVE_TIMEOUT - 500
                : System.currentTimeMillis() + Server.DEFAULT_TIMEOUT - 500;
        cutoff = false;
        hashMap = new HashMap<PentagoBoardState, Result>();

        PentagoMove randomMove = (PentagoMove) boardState.getRandomMove();
        // start with random result
        Result bestResult = new Result(randomMove, 0);
        
        // incremental depth minimax
        int depth = 1;
        while (depth <= 5) {
            c = 0;
            Result res = minimax(boardState, depth, true, Integer.MIN_VALUE, Integer.MAX_VALUE);
            if (res.getScore() > bestResult.getScore())
                bestResult = res;

            System.out.println("Hash was used " + c + " times for depth " + depth);
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
        Result hashResult = hashMap.get(boardState);
        // the deeper depths are calculated faster then
        // searchin the hash map Transpositions Table
        // TODO
        if (hashResult != null) {
            c++;
            return hashResult;
        }

        if (boardState.gameOver() || depth == 0) {
            return new Result (null, MyTools.evaluate(boardState));
        }

        List<PentagoMove> moves = boardState.getAllLegalMoves();
        PentagoMove bestMove = moves.get(0);

        if (maximizingPlayer) {
            int maxEval = Integer.MIN_VALUE;
            for (PentagoMove move : moves) {
                if (timeExceeded()) {
                    cutoff = true;
                    break;
                }

                PentagoBoardState boardClone = (PentagoBoardState) boardState.clone();
                boardClone.processMove(move);
                Result curEval = minimax(boardClone, depth - 1, false, alpha, beta);

                if (curEval.getScore() > maxEval) {
                    maxEval = curEval.getScore();
                    bestMove = move;
                }
                alpha = Math.max(alpha, curEval.getScore());
                if (beta <= alpha)
                    break;
            }
            Result res = new Result(bestMove, maxEval);
            hashMap.put(boardState, res);
            return res;
        } else {
            int minEval = Integer.MAX_VALUE;
            for (PentagoMove move : moves) {
                if (timeExceeded()) {
                    cutoff = true;
                    break;
                }

                PentagoBoardState boardClone = (PentagoBoardState) boardState.clone();
                boardClone.processMove(move);
                Result curEval = minimax(boardClone, depth - 1, true, alpha, beta);

                if (curEval.getScore() < minEval) {
                    minEval = curEval.getScore();
                    bestMove = move;
                }
                beta = Math.min(beta, curEval.getScore());
                if (beta <= alpha)
                    break;
            }
            Result res = new Result(bestMove, minEval);
            hashMap.put(boardState, res);
            return res;
        }
    }

    public static boolean timeExceeded() {
        return (System.currentTimeMillis() > endTime);
    }
}