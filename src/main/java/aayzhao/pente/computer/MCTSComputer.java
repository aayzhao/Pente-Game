package aayzhao.pente.computer;

import aayzhao.pente.game.model.Board;
import aayzhao.pente.game.model.ModelImpl;
import aayzhao.pente.game.model.PieceType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class MCTSComputer implements PenteComputer  {
    private static final Random random = new Random();
    @Override
    public int advantage(int halfPly, Board board, int whiteCaptures, int blackCaptures) {
        return 0;
    }


    @Override
    public Move bestMove(int halfPly, Board board, int whiteCaptures, int blackCaptures) throws InterruptedException {
        ArrayList<RandomGame> movesToSearch = new ArrayList<>();
        PieceType player = halfPly % 2 == 1 ? PieceType.WHITE : PieceType.BLACK;
        for (int i = 0; i < board.getSize(); i++) {
            for (int j = 0; j < board.getSize(); j++) {
                if (board.getIntersection(i, j) == PieceType.EMPTY) {
                    movesToSearch.add(
                            new RandomGame(
                                    halfPly + 1,
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
        int multiplier = halfPly % 2 == 1 ? 1 : -1;
        for (int i = 1; i < movesToSearch.size(); i++) {
            if (movesToSearch.get(i).score * multiplier > movesToSearch.get(idx).score) {
                idx = i;
            }
        }

        return movesToSearch.get(idx).getMove();
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
                originalHalfPly = halfPly;
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
                while (winner == null && set.index > 0) {
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

    private static class RandomizedMoveSet {
        int index;
        Move[] vals;
        Map<Move,Integer> map;
        Random rand;

        public RandomizedMoveSet(int size, Random random) {
            index = 0;
            vals = new Move[size * size + 1];
            map = new HashMap<>();
            rand = random;
        }

        public boolean insert(Move val) {
            if (map.containsKey(val)) return false;
            map.put(val, index);
            vals[index++] = val;
            return true;
        }

        public boolean remove(Move val) {
            if (!map.containsKey(val)) return false;
            int temp_index = map.remove(val);
            Move temp_val = vals[index - 1];
            index--;

            vals[temp_index] = temp_val;
            map.replace(temp_val, index, temp_index);
            return true;
        }

        public Move getRandom() {
            Move target = vals[rand.nextInt(index)];
            remove(target);
            return target;
        }
    }
}
