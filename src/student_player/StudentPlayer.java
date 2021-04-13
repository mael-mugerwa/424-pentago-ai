package student_player;

import java.util.ArrayList;

import boardgame.Move;

import pentago_twist.PentagoPlayer;
import pentago_twist.PentagoBoardState;
import pentago_twist.PentagoMove;
import boardgame.Server;

/** A player file submitted by a student. */
public class StudentPlayer extends PentagoPlayer {
    public static boolean searchCutoff = false;
    public static final int winCutoff = 500000;

    public static int myPlayer;

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

        int maxScore = Integer.MIN_VALUE;
        PentagoMove bestMove = null;

        ArrayList<PentagoMove> moves = boardState.getAllLegalMoves();

        for (PentagoMove move : moves) {
            // Copy the current game state
            PentagoBoardState newBoard = (PentagoBoardState) boardState.clone();
            newBoard.processMove(move);

            // Compute how long to spend looking at each move
            long searchTimeLimit = ((Server.DEFAULT_TIMEOUT - 1000) / (moves.size()));

            int score = iterativeDeepening(newBoard, searchTimeLimit);

            // If the search finds a winning move
            if (score >= winCutoff) {
                return move;
            }

            if (score > maxScore) {
                maxScore = score;
                bestMove = move;
            }
        }

        // Return your move to be processed by the server.
        return bestMove;
    }

    public static int iterativeDeepening(PentagoBoardState boardState, long timeLimit) {
        long startTime = System.currentTimeMillis();
        long endTime = startTime + timeLimit;
        int depth = 1;
        int score = 0;
        searchCutoff = false;

        while (true) {
            long currentTime = System.currentTimeMillis();
            if (currentTime >= endTime) {
                break;
            }

            int searchResult = minimax(boardState, depth, Integer.MIN_VALUE, Integer.MAX_VALUE, currentTime,
                    endTime - currentTime);

            // If the search finds a winning move, stop searching
            if (searchResult >= winCutoff) {
                return searchResult;
            }

            if (!searchCutoff) {
                score = searchResult;
            }

            depth++;
        }

        return score;
    }

    // search() will perform minimax search with alpha-beta pruning on a game state,
    // and will cut off if the given time
    // limit has elapsed since the beginning of the search
    public static int minimax(PentagoBoardState boardState, int depth, int alpha, int beta, long startTime,
            long timeLimit) {
        ArrayList<PentagoMove> moves = boardState.getAllLegalMoves();
        boolean maximize = boardState.getTurnPlayer() == StudentPlayer.myPlayer;

        int savedScore = (maximize) ? Integer.MIN_VALUE : Integer.MAX_VALUE;
        int score = MyTools.evaluate(boardState);
        long currentTime = System.currentTimeMillis();
        long elapsedTime = (currentTime - startTime);

        if (elapsedTime >= timeLimit) {
            searchCutoff = true;
        }

        // If this is a terminal node or a win for either player, abort the search
        if (searchCutoff || (depth == 0) || (moves.size() == 0) || (score >= winCutoff) || (score <= -winCutoff)) {
            return score;
        }

        if (maximize) {
            for (PentagoMove move : moves) {
                PentagoBoardState boardClone = (PentagoBoardState) boardState.clone();
                boardClone.processMove(move);

                alpha = Math.max(alpha, minimax(boardClone, depth - 1, alpha, beta, startTime, timeLimit));

                if (beta <= alpha) {
                    break;
                }
            }

            return alpha;
        } else {
            for (PentagoMove move : moves) {
                PentagoBoardState boardClone = (PentagoBoardState) boardState.clone();
                boardClone.processMove(move);

                beta = Math.min(beta, minimax(boardClone, depth - 1, alpha, beta, startTime, timeLimit));

                if (beta <= alpha) {
                    break;
                }
            }
            return beta;
        }
    }
}