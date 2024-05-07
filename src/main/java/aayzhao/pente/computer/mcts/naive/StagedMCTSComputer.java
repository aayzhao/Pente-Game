package aayzhao.pente.computer.mcts.naive;

import aayzhao.pente.computer.Move;
import aayzhao.pente.computer.MoveImpl;
import aayzhao.pente.computer.PenteComputer;
import aayzhao.pente.computer.RandomizedMoveSet;
import aayzhao.pente.computer.mcts.RandomGame;
import aayzhao.pente.computer.mcts.RandomQueueGame;
import aayzhao.pente.game.model.Board;
import aayzhao.pente.game.model.PieceType;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.PriorityBlockingQueue;

public class StagedMCTSComputer implements PenteComputer {
    private RandomizedMoveSet set;
    private PriorityBlockingQueue<RandomGame> queue = null;

    @Override
    public String toString() {
        return "MCTSv1.4";
    }

    @Override
    public double advantage(int halfPly, Board board, int whiteCaptures, int blackCaptures) {
        return 0;
    }

    @Override
    public Move bestMove(int halfPly, Board board, int whiteCaptures, int blackCaptures, Move oppMove) throws InterruptedException {
        Comparator<RandomGame> comp;
        if (halfPly % 2 == 0) comp = (a , b) -> a.score - b.score;
        else comp = (a, b) -> b.score - a.score;

        queue = new PriorityBlockingQueue<>(board.getSize() * board.getSize() + 1, comp);
        ArrayList<RandomGame> movesToSearch = new ArrayList<>();
        for (int i = 0; i < board.getSize(); i++) {
            for (int j = 0; j < board.getSize(); j++) {
                if (board.getIntersection(i, j) == PieceType.EMPTY) {
                    movesToSearch.add(
                            new RandomQueueGame(
                                    halfPly,
                                    board,
                                    whiteCaptures,
                                    blackCaptures,
                                    new MoveImpl(i, j),
                                    10,
                                    queue
                            )
                    );
                }
            }
        }

        // Stage 1, prepare to eliminate 50% of the board
        movesToSearch = this.executeRollouts(movesToSearch, halfPly, 0.75);

        // Stage 2, eliminate another 50% of the possible moves
        movesToSearch = this.executeRollouts(movesToSearch, halfPly, 0.75, 200);

        // Stage 3, eliminate a further 50%
        movesToSearch = this.executeRollouts(movesToSearch, halfPly, 0.75, 750);

        // Stage 4, eliminate one last 50%
        movesToSearch = this.executeRollouts(movesToSearch, halfPly, 0.75, 1500);

        // Stage 5, eliminate one last 50%
        movesToSearch = this.executeRollouts(movesToSearch, halfPly, 0.75, 4000);

        // Stage 4, eliminate one last 50%
        movesToSearch = this.executeRollouts(movesToSearch, halfPly, 0.75, 8750);

        // Stage 4, eliminate one last 50%
        movesToSearch = this.executeRollouts(movesToSearch, halfPly, 0.5, 10000);

        int idx = 0;
        int score = movesToSearch.get(idx).score;
        int multiplier = halfPly % 2 == 1 ? 1 : -1;
        for (int i = 0; i < movesToSearch.size(); i++) {
            System.out.println(movesToSearch.get(i).score + "\t" + movesToSearch.get(i).getMove().toString());
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

    @Override
    public Move bestMove(int halfPly, Move prevMove) throws InterruptedException {
        return null;
    }

    private ArrayList<RandomGame> executeRollouts(List<RandomGame> movesToSearch, int halfPly, double threshold) throws InterruptedException {
        queue.clear();
        Thread[] threads = new Thread[movesToSearch.size()];
        for (int i = 0; i < threads.length; i++) {
            Thread thread = new Thread(movesToSearch.get(i));
            threads[i] = thread;
            thread.start();
        }

        for (Thread thread : threads) {
            thread.join();
        }

        int count = (int) Math.max(Math.ceil(movesToSearch.size()  * threshold), 1);
        ArrayList<RandomGame> bestMoves = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            bestMoves.add(queue.poll());
        }
        return bestMoves;
    }

    private ArrayList<RandomGame> executeRollouts(List<RandomGame> movesToSearch, int halfPly, double threshold, int rollouts) throws InterruptedException {
        for (RandomGame move : movesToSearch) {
            move.setRollouts(rollouts);
        }
        return executeRollouts(movesToSearch, halfPly,  threshold);
    }
}
