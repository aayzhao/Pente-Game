package aayzhao.pente.computer.mcts.naive;

import aayzhao.pente.computer.Move;
import aayzhao.pente.computer.MoveImpl;
import aayzhao.pente.computer.PenteComputer;
import aayzhao.pente.computer.mcts.RandomGame;
import aayzhao.pente.game.model.Board;
import aayzhao.pente.game.model.BoardImpl;
import aayzhao.pente.game.model.PieceType;

import java.util.ArrayList;
import java.util.Random;

/**
 * Full rollout/naive MCTS computer
 */
public class FullRolloutMCTSComputer implements PenteComputer {
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
                                    new MoveImpl(i, j),
                                    12500
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

    @Override
    public Move bestMove(Move prevMove) throws InterruptedException {
        return null;
    }

    public static void main(String[] args) throws InterruptedException {
        System.out.println("Testing");
        PenteComputer cpu = new FullRolloutMCTSComputer();
        Board board = new BoardImpl(9);
        board.placePiece(PieceType.WHITE, 0, 0);
        board.placePiece(PieceType.WHITE, 0, 1);
        board.placePiece(PieceType.WHITE, 0, 2);
        board.placePiece(PieceType.WHITE, 0, 3);

        System.out.println(cpu.bestMove(2, board, 0, 0, null));
    }
}
