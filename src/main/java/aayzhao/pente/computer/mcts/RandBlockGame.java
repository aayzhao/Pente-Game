package aayzhao.pente.computer.mcts;

import aayzhao.pente.computer.Move;
import aayzhao.pente.computer.RandomizedMoveSet;
import aayzhao.pente.game.model.Board;
import aayzhao.pente.game.model.PieceType;

import java.util.ArrayList;
import java.util.HashMap;

public class RandBlockGame extends RandomGame {
    private final static int[][] directions = {
            {1, 0}, // vertical
            {0, 1}, // horizontal
            {1, 1}, // forward diagonal
            {1, -1} // backward diagonal
    };
    public RandBlockGame(int halfPly, Board board, int whiteCaptures, int blackCaptures, Move move, int rollouts) {
        super(halfPly, board, whiteCaptures, blackCaptures, move, rollouts);
    }

    public boolean checkMakesFive(Move move) {
        if (move.getRowCoord() < 0 || move.getRowCoord() > this.getBoardSize() - 1 || move.getColumnCoord() < 0 || move.getColumnCoord() > this.getBoardSize() - 1)
            throw new IllegalArgumentException("Invalid move to check");
        PieceType piece = this.getCurrentPlayerTurn(this.getHalfPly());

        // check vertical directions
//        int r = move.getRowCoord() - 1;
//        int c = move.getRowCoord();
//        int len = 1;
//        while (r > -1 && this.getIntersection(r, c) == piece) {
//            len++;
//            r--;
//        }
//        r = move.getRowCoord() + 1;
//        while (r < this.getBoardSize() - 1 && this.getIntersection(r, c) == piece) {
//            len++;
//            r++;
//        }

        for (int[] consts : directions) {
            int len = 1;
            int r = move.getRowCoord() + consts[0];
            int c = move.getColumnCoord() + consts[1];
            while (isValid(r, c) && this.getIntersection(r, c) == piece) {
                len++;
                r += consts[0];
                c += consts[1];
            }
            r = move.getRowCoord() - consts[0];
            c = move.getColumnCoord() - consts[1];
            while (isValid(r, c) && this.getIntersection(r, c) == piece) {
                len++;
                r -= consts[0];
                c -= consts[1];
            }
            if (len >= 5) return true;
        }

        return false;
    }

    public boolean checkMakesLen(Move move, int length) {
        if (move.getRowCoord() < 0 || move.getRowCoord() > this.getBoardSize() - 1 || move.getColumnCoord() < 0 || move.getColumnCoord() > this.getBoardSize() - 1)
            throw new IllegalArgumentException("Invalid move to check");
        PieceType piece = this.getCurrentPlayerTurn(this.getHalfPly());

        for (int[] consts : directions) {
            int len = 1;
            int r = move.getRowCoord() + consts[0];
            int c = move.getColumnCoord() + consts[1];
            while (isValid(r, c) && this.getIntersection(r, c) == piece) {
                len++;
                r += consts[0];
                c += consts[1];
            }
            r = move.getRowCoord() - consts[0];
            c = move.getColumnCoord() - consts[1];
            while (isValid(r, c) && this.getIntersection(r, c) == piece) {
                len++;
                r -= consts[0];
                c -= consts[1];
            }
            if (len >= length) return true;
        }

        return false;
    }

    public boolean checkMakesFourUncontested(Move move) {
        if (move.getRowCoord() < 0 || move.getRowCoord() > this.getBoardSize() - 1 || move.getColumnCoord() < 0 || move.getColumnCoord() > this.getBoardSize() - 1)
            throw new IllegalArgumentException("Invalid move to check");
        PieceType piece = this.getCurrentPlayerTurn(this.getHalfPly());

        for (int[] consts : directions) {
            int len = 1;
            int r = move.getRowCoord() + consts[0];
            int c = move.getColumnCoord() + consts[1];
            while (isValid(r, c) && this.getIntersection(r, c) == piece) {
                len++;
                if (len == 4
                        && isValid(r + consts[0], c + consts[1])
                        && this.getIntersection(r + consts[0], c + consts[1]) == PieceType.EMPTY
                        && isValid(r - consts[0] * 4, c - consts[1] * 4)
                        && this.getIntersection(r - consts[0] * 4, c - consts[0] * 4) == PieceType.EMPTY)
                    return true;
                r += consts[0];
                c += consts[1];
            }
            r = move.getRowCoord() - consts[0];
            c = move.getColumnCoord() - consts[1];
            while (isValid(r, c) && this.getIntersection(r, c) == piece) {
                len++;
                if (len == 4
                        && isValid(r - consts[0], c - consts[1])
                        && this.getIntersection(r - consts[0], c - consts[1]) == PieceType.EMPTY
                        && isValid(r + consts[0] * 4, c + consts[1] * 4)
                        && this.getIntersection(r + consts[0] * 4, c + consts[0] * 4) == PieceType.EMPTY)
                    return true;
                r -= consts[0];
                c -= consts[1];
            }
        }
        return false;
    }

    /**
     * Checks if a game state that will result in a
     * forced or automatic win by making an uncontested 4 or simply 5
     * in a row respectively is created if the input move is played
     * @param move  Move to check
     * @return      False if forced win is not possible with the current move
     */
    public boolean checkForceWin(Move move) {
        if (!isValid(move.getRowCoord(), move.getColumnCoord()))
            throw new IllegalArgumentException("Invalid move to check");
        PieceType piece = this.getCurrentPlayerTurn(this.getHalfPly());
        for (int[] consts : directions) {
            int len = 1;
            int r = move.getRowCoord() + consts[0];
            int c = move.getColumnCoord() + consts[1];
            while (isValid(r, c) && this.getIntersection(r, c) == piece) {
                len++;
                if (len == 4
                        && isValid(r + consts[0], c + consts[1])
                        && this.getIntersection(r + consts[0], c + consts[1]) == PieceType.EMPTY
                        && isValid(r - consts[0] * 4, c - consts[1] * 4)
                        && this.getIntersection(r - consts[0] * 4, c - consts[0] * 4) == PieceType.EMPTY)
                    return true;
                if (len == 5) return true;
                r += consts[0];
                c += consts[1];
            }
            r = move.getRowCoord() - consts[0];
            c = move.getColumnCoord() - consts[1];
            while (isValid(r, c) && this.getIntersection(r, c) == piece) {
                len++;
                if (len == 4
                        && isValid(r - consts[0], c - consts[1])
                        && this.getIntersection(r - consts[0], c - consts[1]) == PieceType.EMPTY
                        && isValid(r + consts[0] * 4, c + consts[1] * 4)
                        && this.getIntersection(r + consts[0] * 4, c + consts[0] * 4) == PieceType.EMPTY)
                    return true;
                if (len == 5) return true;
                r -= consts[0];
                c -= consts[1];
            }
        }

        return false;
    }

    private boolean isValid(int r, int c) {
        return !(r < 0 || r > this.getBoardSize() - 1 || c < 0 || c > this.getBoardSize() - 1);
    }

    @Override
    public void run() {
        if (score != null) return;
        if (solved) return;
        int tempScore = 0;
        for (int i = 0; i < rollouts; i++) { // perform specified rollouts for the given move
            PieceType winner = null;
            while (winner == null && set.getIndex() > 0) {
                int idx = 0;
                for (Move move : set.getVals()) {
                    if (checkForceWin(move)) {
                        winner = this.getCurrentPlayerTurn(this.getHalfPly());
                        break;
                    }
                    idx++;
                    if (idx == this.set.getIndex()) break;
                }
                if (winner != null) break;
                else {
                    Move nextMove = this.set.getRandom();
                    super.move(nextMove.getRowCoord(), nextMove.getColumnCoord());
                    winner = super.getWinner();
                }
            }
            if (winner == PieceType.WHITE) tempScore++;
            else if (winner == PieceType.BLACK) tempScore--;
            reset();
        }
        this.score = tempScore;
    }
}
