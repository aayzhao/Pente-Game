package aayzhao.pente.computer.mcts;

import aayzhao.pente.computer.Move;
import aayzhao.pente.game.model.Board;

import java.util.concurrent.PriorityBlockingQueue;

public class RandomQueueGame extends RandomGame {
    PriorityBlockingQueue<RandomGame> queue;
    public RandomQueueGame(
            int halfPly,
            Board board,
            int whiteCaptures,
            int blackCaptures,
            Move move,
            int rollouts,
            PriorityBlockingQueue<RandomGame> queue) {
        super(halfPly, board, whiteCaptures, blackCaptures, move, rollouts);
        this.queue = queue;
    }

    @Override
    public void run() {
        super.run();
        if (this.score != null) queue.add(this);
    }
}
