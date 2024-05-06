package aayzhao.pente.computer;

import aayzhao.pente.game.model.Board;
import aayzhao.pente.game.model.BoardImpl;
import aayzhao.pente.game.model.ModelImpl;
import aayzhao.pente.game.model.PieceType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class FullRolloutMCTSComputer implements PenteComputer  {
    private static final Random random = new Random();
    @Override
    public double advantage(int halfPly, Board board, int whiteCaptures, int blackCaptures) {
        return 0.0;
    }

    @Override
    public String toString() {
        return "MCTSv1.1";
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
            System.out.println(movesToSearch.get(i).score);
            if (movesToSearch.get(i).score * multiplier > movesToSearch.get(idx).score * multiplier) {
                idx = i;
                score = movesToSearch.get(i).score;
                System.out.println(movesToSearch.get(i).score);
            }
        }

        Move best = movesToSearch.get(idx).getMove();
        System.out.printf("Best Move for %s: %s\nScore: %.2f\n", halfPly % 2 == 1 ? "white" : "black", best.toString(),
                (((double) score) + (12500.0 - ((double) score)) / 2.0)/ 12500.0);
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
            this.set = new RandomizedMoveSet(17, random);
            super.move(move.getRowCoord(), move.getColumnCoord());
            PieceType won = super.getWinner();
            if (won != null) {
                score = won == PieceType.WHITE ? 12500 : -12500;
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
        protected void checkCapture(int r, int c) {
            boolean left = c > 2;
            boolean right = c < this.getBoardSize() - 3;
            boolean down = r > 2;
            boolean up = r < this.getBoardSize() - 3;
            int captures = 0;
            PieceType cur = halfPly % 2 == 1 ? PieceType.WHITE : PieceType.BLACK;
            PieceType opp = PieceType.opponentType(cur);
            if (down) {
                if (board.getIntersection(r - 3, c) == cur
                        && board.getIntersection(r - 2, c) == opp
                        && board.getIntersection(r - 1, c) == opp) {
                    board.removePiece(r - 2, c);
                    board.removePiece(r - 1, c);
                    set.insert(new MoveImpl(r - 2, c));
                    set.insert(new MoveImpl(r - 1, c));
                    captures++;
                }
                if (left) {
                    if (board.getIntersection(r - 3, c - 3) == cur
                            && board.getIntersection(r - 2, c - 2) == opp
                            && board.getIntersection(r - 1, c - 1) == opp) {
                        board.removePiece(r - 2, c - 2);
                        board.removePiece(r - 1, c - 1);
                        set.insert(new MoveImpl(r - 2, c - 2));
                        set.insert(new MoveImpl(r - 1, c - 1));
                        captures++;
                    }

                }
                if (right) {
                    if (board.getIntersection(r - 3, c + 3) == cur
                            && board.getIntersection(r - 2, c + 2) == opp
                            && board.getIntersection(r - 1, c + 1) == opp) {
                        board.removePiece(r - 2, c + 2);
                        board.removePiece(r - 1, c + 1);
                        set.insert(new MoveImpl(r - 2, c + 2));
                        set.insert(new MoveImpl(r - 1, c + 1));
                        captures++;
                    }

                }
            }
            if (up) {
                if (board.getIntersection(r + 3, c) == cur
                        && board.getIntersection(r + 2, c) == opp
                        && board.getIntersection(r + 1, c) == opp) {
                    board.removePiece(r + 2, c);
                    board.removePiece(r + 1, c);
                    set.insert(new MoveImpl(r + 2, c));
                    set.insert(new MoveImpl(r + 1, c));
                    captures++;
                }
                if (left) {
                    if (board.getIntersection(r + 3, c - 3) == cur
                            && board.getIntersection(r + 2, c - 2) == opp
                            && board.getIntersection(r + 1, c - 1) == opp) {
                        board.removePiece(r + 2, c - 2);
                        board.removePiece(r + 1, c - 1);
                        set.insert(new MoveImpl(r + 2, c - 2));
                        set.insert(new MoveImpl(r + 1, c - 1));
                        captures++;
                    }
                }
                if (right) {
                    if (board.getIntersection(r + 3, c + 3) == cur
                            && board.getIntersection(r + 2, c + 2) == opp
                            && board.getIntersection(r + 1, c + 1) == opp) {
                        board.removePiece(r + 2, c + 2);
                        board.removePiece(r + 1, c + 1);
                        set.insert(new MoveImpl(r + 2, c + 2));
                        set.insert(new MoveImpl(r + 1, c + 1));
                        captures++;
                    }
                }
            }
            if (left) {
                if (board.getIntersection(r, c - 3) == cur
                        && board.getIntersection(r, c - 2) == opp
                        && board.getIntersection(r, c - 1) == opp) {
                    board.removePiece(r, c - 2);
                    board.removePiece(r, c - 1);
                    set.insert(new MoveImpl(r, c - 2));
                    set.insert(new MoveImpl(r, c - 1));
                    captures++;
                }
            }
            if (right) {
                if (board.getIntersection(r, c + 3) == cur
                        && board.getIntersection(r, c + 2) == opp
                        && board.getIntersection(r, c + 1) == opp) {
                    board.removePiece(r, c + 2);
                    board.removePiece(r, c + 1);
                    set.insert(new MoveImpl(r, c + 2));
                    set.insert(new MoveImpl(r, c + 1));
                    captures++;
                }
            }
            if (cur == PieceType.WHITE) whiteCaptures += captures;
            else blackCaptures += captures;
        }

        @Override
        public void run() {
            if (score != null) return;
            int tempScore = 0;
            for (int i = 0; i < 12500; i++) { // perform 15000 rollouts for the given move
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

    public static void main(String[] args) throws InterruptedException {
        System.out.println("Testing");
        PenteComputer cpu = new FullRolloutMCTSComputer();
        Board board = new BoardImpl(9);
        board.placePiece(PieceType.WHITE, 0, 0);
        board.placePiece(PieceType.WHITE, 0, 1);
        board.placePiece(PieceType.WHITE, 0, 2);
        board.placePiece(PieceType.WHITE, 0, 3);

        System.out.println(cpu.bestMove(2, board, 0, 0));
    }
}
