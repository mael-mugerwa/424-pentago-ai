package improved;

import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Comparator;

import boardgame.Move;
import boardgame.Server;
import pentago_twist.PentagoPlayer;
import pentago_twist.PentagoBoardState;
import pentago_twist.PentagoMove;

/** A player file submitted by a student. */
public class StudentPlayer extends PentagoPlayer {
    public static int myPlayer;
    private static PentagoMove randomMove;
    private static long startTime, endTime;
    private static boolean cutoff;
    private static HashMap<String, TTEntry> hashMap;

    /**
     * You must modify this constructor to return your student number. This is
     * important, because this is what the code that runs the competition uses to
     * associate you with your agent. The constructor should do nothing else.
     */
    public StudentPlayer() {
        super("testing");
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

        // set myPlayer, start time and end time
        myPlayer = boardState.getTurnPlayer();
        startTime = System.currentTimeMillis();
        endTime = (boardState.getTurnNumber() == 0) ? System.currentTimeMillis() + Server.FIRST_MOVE_TIMEOUT - 60
                : System.currentTimeMillis() + Server.DEFAULT_TIMEOUT - 20;

        // boolean flag to indicate that minimax was cutoff to avoid timeout
        cutoff = false;

        // hashMap transposition table
        if (boardState.getTurnNumber() == 0)// initialize hash map
            hashMap = new HashMap<String, TTEntry>();
        else { // clean hashMap by removing useless moves
            int a = hashMap.size();
            // if an entry has a lower turn # than the current one, it's useless remove it
            hashMap.entrySet().removeIf(entry -> entry.getValue().getTurnNumber() < boardState.getTurnNumber());
            int b = hashMap.size();
            System.out.println("Removed " + (a - b) + " items from map");
        }

        // initialize best result with random values
        randomMove = (PentagoMove) boardState.getRandomMove();
        TTEntry bestResult = new TTEntry(randomMove, 0, 0, 0);

        // start at depth 1
        int depth = boardState.getTurnNumber() == 0 ? 4 : 3;
        // incremental depth minimax
        while (true) {
            // run minimax at current depth
            TTEntry res = minimax(boardState, depth, true, Integer.MIN_VALUE, Integer.MAX_VALUE);
            // if score is better change result
            if (res.getScore() > bestResult.getScore())
                bestResult = res;

            if (cutoff) {
                System.out.println("Cutoff search at depth " + depth);
                System.out.println("Hash map size " + hashMap.size());

                // force 1st move to be a center quadrant move if possible
                if (boardState.getTurnNumber() == 0) {
                    int[][] board = MyTools.getBoard(boardState);
                    System.out.println("TESTING Found 1st Move in " + (System.currentTimeMillis() - startTime));
                    if (0 == board[1][1]) {
                        return new PentagoMove(1, 1, 0, 0, boardState.getTurnPlayer());
                    } else if (0 == board[1][4]) {
                        return new PentagoMove(1, 4, 1, 0, boardState.getTurnPlayer());
                    } else if (0 == board[4][1]) {
                        return new PentagoMove(4, 1, 2, 0, boardState.getTurnPlayer());
                    } else if (0 == board[4][4]) {
                        return new PentagoMove(4, 4, 3, 0, boardState.getTurnPlayer());
                    }
                }
                break;
            }
            depth++; // increment depth
        }

        System.out.println("TESTING Found Best Move in " + (System.currentTimeMillis() - startTime));
        return bestResult.getBestMove();
    }
        

    // class of an entry in my Transposition Table
    final class TTEntry {
        private PentagoMove bestMove;
        private int score;
        private int depth;
        private int turnNumber;

        public TTEntry(PentagoMove bestMove, int score, int depth, int turnNumber) {
            this.bestMove = bestMove;
            this.score = score;
            this.depth = depth;
            this.turnNumber = turnNumber;
        }

        public PentagoMove getBestMove() {
            return this.bestMove;
        }

        public int getScore() {
            return this.score;
        }

        public int getDepth() {
            return this.depth;
        }

        public int getTurnNumber() {
            return this.turnNumber;
        }
    }

    private TTEntry minimax(PentagoBoardState boardState, int depth, boolean maximizingPlayer, int alpha, int beta) {
        // search for boardState in Transposition Table instead of running minimax
        TTEntry tte = hashMap.get(MyTools.getBoardString(boardState));
        if (tte != null && tte.depth >= depth) {
            return tte;
        }

        // minimax algorithm
        if (boardState.gameOver() || depth == 0) {
            return new TTEntry(null, MyTools.evaluate(boardState), depth, boardState.getTurnNumber());
        }

        List<PentagoMove> moves = boardState.getAllLegalMoves();

        PentagoMove bestMove = moves.get(0);

        if (maximizingPlayer) {
            int maxEval = Integer.MIN_VALUE;
            for (PentagoMove move : moves) {
                if (timeExceeded()) { // if while running minimax, time is exceeded break and return best move so far
                    cutoff = true;
                    break;
                }

                PentagoBoardState boardClone = (PentagoBoardState) boardState.clone();
                boardClone.processMove(move);
                TTEntry curEval = minimax(boardClone, depth - 1, false, alpha, beta);

                if (curEval.getScore() > maxEval) {
                    maxEval = curEval.getScore();
                    bestMove = move;
                }
                alpha = Math.max(alpha, curEval.getScore());
                if (beta <= alpha)
                    break;
            }
            TTEntry res = new TTEntry(bestMove, maxEval, depth, boardState.getTurnNumber());
            hashMap.put(MyTools.getBoardString(boardState), res);
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
                TTEntry curEval = minimax(boardClone, depth - 1, true, alpha, beta);

                if (curEval.getScore() < minEval) {
                    minEval = curEval.getScore();
                    bestMove = move;
                }
                beta = Math.min(beta, curEval.getScore());
                if (beta <= alpha)
                    break;
            }
            TTEntry res = new TTEntry(bestMove, minEval, depth, boardState.getTurnNumber());
            hashMap.put(MyTools.getBoardString(boardState), res);
            return res;
        }
    }

    // returns true if the timeout was exceeded
    public static boolean timeExceeded() {
        return (System.currentTimeMillis() > endTime);
    }

    public boolean testMove(PentagoBoardState boardState, PentagoMove move) {
        PentagoBoardState boardClone = (PentagoBoardState) boardState.clone();
        boardClone.processMove(move);

        // test see if opponent wins
        MyTools.stopAt = 5;

        int[][] board = MyTools.getBoard(boardClone);

        ArrayList<String> diags = MyTools.getDiagonals(board);
        ArrayList<String> rows = MyTools.getRows(board);
        ArrayList<String> cols = MyTools.getColumns(board);

        int opponentScore = 0;
        opponentScore += MyTools.evaluateRow(diags);
        opponentScore += MyTools.evaluateRow(rows);
        opponentScore += MyTools.evaluateRow(cols);

        if (opponentScore >= MyTools.win) {
            return true;
        }
        return false;
    }

    public boolean testMove2(PentagoBoardState boardState, PentagoMove move) {
        PentagoBoardState boardClone = (PentagoBoardState) boardState.clone();
        boardClone.processMove(move);

        int score = MyTools.evaluate(boardClone);

        if (score <= 0) {
            return true;
        }
        return false;
    }

    final class MoveComparator implements Comparator<PentagoMove> {

        private PentagoBoardState boardState;

        public MoveComparator(PentagoBoardState boardState) {
            this.boardState = boardState;
        }

        @Override
        public int compare(PentagoMove moveA, PentagoMove moveB) {
            PentagoBoardState boardCloneA = (PentagoBoardState) this.boardState.clone();
            boardCloneA.processMove(moveA);
            PentagoBoardState boardCloneB = (PentagoBoardState) this.boardState.clone();
            boardCloneB.processMove(moveB);

            int a = MyTools.evaluate(boardCloneA);
            int b = MyTools.evaluate(boardCloneB);
            return -(a - b);
        }
    }

    public void orderMoves(PentagoBoardState boardState, PentagoMove moveA, PentagoMove moveB) {
        PentagoBoardState boardCloneA = (PentagoBoardState) boardState.clone();
        boardCloneA.processMove(moveA);
        PentagoBoardState boardCloneB = (PentagoBoardState) boardState.clone();
        boardCloneB.processMove(moveB);
    }

    public void testOrdering(PentagoBoardState boardState, List<PentagoMove> moves) {
        boolean fail = false;
        for (int i = 0; i < moves.size() - 1; i++) {
            PentagoBoardState boardCloneA = (PentagoBoardState) boardState.clone();
            boardCloneA.processMove(moves.get(i));
            PentagoBoardState boardCloneB = (PentagoBoardState) boardState.clone();
            boardCloneB.processMove(moves.get(i + 1));
            int a = MyTools.evaluate(boardCloneA);
            int b = MyTools.evaluate(boardCloneB);
            if (a < b) {
                fail = true;
            }
        }
        if (fail) {
            System.out.println("MOVE ORDERING NO WORK");
        } else {
            System.out.println("YEY");
        }
    }

}