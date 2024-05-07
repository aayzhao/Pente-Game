package aayzhao.pente.computer.mcts.naive;

import aayzhao.pente.computer.Move;
import aayzhao.pente.computer.MoveImpl;
import aayzhao.pente.computer.PenteComputer;
import aayzhao.pente.computer.RandomizedMoveSet;
import aayzhao.pente.game.model.Board;
import aayzhao.pente.game.model.ModelImpl;
import aayzhao.pente.game.model.PieceType;

import java.util.ArrayList;
import java.util.Random;

/**
 * incorrect implementation of a full rollout MCTS Computer that still performs better than
 * a completely new player to the game.
 */
public class FastMCTSComputer implements PenteComputer {
    private static final Random random = new Random();
    @Override
    public double advantage(int halfPly, Board board, int whiteCaptures, int blackCaptures) {
        return 0;
    }

    @Override
    public String toString() {
        return "MCTSv0.5";
    }

    @Override
    public Move bestMove(int halfPly, Board board, int whiteCaptures, int blackCaptures, Move oppMove) throws InterruptedException {
        ArrayList<RandomGame> movesToSearch = new ArrayList<>();
        PieceType player = halfPly % 2 == 1 ? PieceType.WHITE : PieceType.BLACK;
        for (int i = 0; i < board.getSize(); i++) {
            for (int j = 0; j < board.getSize(); j++) {
                if (board.getIntersection(i, j) == PieceType.EMPTY) {
                    movesToSearch.add(
                            new RandomGame(
                                    halfPly,
                                    board,
                                    whiteCaptures,
                                    blackCaptures,
                                    new MoveImpl(i, j)
                            )
                    );
                }
            }
        }

        Thread[] threads = new Thread[movesToSearch.size()];
        for (int i = 0; i < threads.length; i++) {
            Thread thread = new Thread(movesToSearch.get(i));
            threads[i] = thread;
            thread.start();
        }

        for (Thread thread : threads) {
            thread.join();
        }

        int idx = 0;
        int score = movesToSearch.get(idx).score;
        int multiplier = halfPly % 2 == 1 ? 1 : -1;
        for (int i = 1; i < movesToSearch.size(); i++) {
            // System.out.println(movesToSearch.get(i).score);
            if (movesToSearch.get(i).score * multiplier > movesToSearch.get(idx).score * multiplier) {
                idx = i;
                score = movesToSearch.get(i).score;
                // System.out.println(movesToSearch.get(i).score);
            }
        }

        Move best = movesToSearch.get(idx).getMove();
        System.out.printf("Best Move for %s: %s\nScore: %.2f\n", halfPly % 2 == 1 ? "white" : "black", best.toString(),
                (((double) score) + (10000.0 - ((double) score)) / 2.0)/ 10000.0);
        return movesToSearch.get(idx).getMove();
    }

    @Override
    public Move bestMove(Move prevMove) throws InterruptedException {
        return null;
    }

    public static class RandomGame extends ModelImpl implements Runnable {
        private Board startBoard;
        private int originalHalfPly;
        private int originalWhiteCaptures;
        private int originalBlackCaptures;
        public Integer score;
        private RandomizedMoveSet set;
        private final Move move;

        public RandomGame(int halfPly, Board board, int whiteCaptures, int blackCaptures, Move move) {
            super(board.copy(), whiteCaptures, blackCaptures, halfPly);
            this.move = move;
            super.move(move.getRowCoord(), move.getColumnCoord());
            PieceType won = super.getWinner();
            if (won != null) {
                score = halfPly % 2 == 1 ? 10000 : -10000;
            } else {
                startBoard = super.board.copy();
                originalHalfPly = halfPly + 1;
                originalBlackCaptures = blackCaptures;
                originalWhiteCaptures = whiteCaptures;

                createMoveSet();
            }
        }

        private void createMoveSet() {
            this.set = new RandomizedMoveSet(board.getSize(), random);
            for (int i = 0; i < super.getBoardSize(); i++) {
                for (int j = 0; j < super.getBoardSize(); j++) {
                    if (super.isValidMove(i, j)) set.insert(new MoveImpl(i, j));
                }
            }
        }

        private void reset() {
            super.blackCaptures = originalBlackCaptures;
            super.whiteCaptures = originalWhiteCaptures;
            super.halfPly = originalHalfPly;
            super.board = startBoard.copy();
            this.createMoveSet();
        }

        public Move getMove() {
            return move;
        }

        @Override
        public void run() {
            if (score != null) return;
            int tempScore = 0;
            for (int i = 0; i < 10000; i++) {
                PieceType winner = null;
                while (winner == null && set.getIndex() > 0) {
                    Move nextMove = this.set.getRandom();
                    super.move(nextMove.getRowCoord(), nextMove.getColumnCoord());
                    winner = super.getWinner();
                }
                if (winner == PieceType.WHITE) tempScore++;
                else if (winner == PieceType.BLACK) tempScore--;
                reset();
            }
            this.score = tempScore;
        }
    }
}
