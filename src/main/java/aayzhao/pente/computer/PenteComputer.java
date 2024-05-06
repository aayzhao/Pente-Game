package aayzhao.pente.computer;

import aayzhao.pente.game.model.Board;

public interface PenteComputer {
    /**
     * Returns the game score. Positive indicates advantage for White, negative indicates advantage for Black
     * @param halfPly   odd indicates white's turn to move, even indicates black
     * @param board     Board to analyze
     * @return          calculated game score
     */
    int advantage(int halfPly, Board board);

    /**
     * Returns the calculated best move for the given board state and half ply.
     * @param halfPly   half ply
     * @param board     current game state
     * @return          best expected move
     */
    Move bestMove(int halfPly, Board board);
}
