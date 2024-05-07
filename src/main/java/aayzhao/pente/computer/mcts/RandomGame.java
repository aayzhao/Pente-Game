package aayzhao.pente.computer.mcts;

import aayzhao.pente.computer.Move;
import aayzhao.pente.computer.MoveImpl;
import aayzhao.pente.computer.RandomizedMoveSet;
import aayzhao.pente.game.model.Board;
import aayzhao.pente.game.model.ModelImpl;
import aayzhao.pente.game.model.PieceType;

import java.util.Random;

public class RandomGame extends ModelImpl implements Runnable {
    public static Random random = new Random();
    protected int rollouts;
    protected Board startBoard;
    protected int originalHalfPly;
    protected int originalWhiteCaptures;
    protected int originalBlackCaptures;
    public Integer score;
    protected RandomizedMoveSet set;
    protected final Move move;
    protected boolean solved = false;

    public RandomGame(int halfPly, Board board, int whiteCaptures, int blackCaptures, Move move, int rollouts) {
        super(board.copy(), whiteCaptures, blackCaptures, halfPly);
        this.move = move;
        this.rollouts = rollouts;
        this.set = new RandomizedMoveSet(17, random);
        super.move(move.getRowCoord(), move.getColumnCoord());
        PieceType won = super.getWinner();
        if (won != null) {
            score = won == PieceType.WHITE ? rollouts : -rollouts;
            solved = true;
        } else {
            startBoard = super.board.copy();
            originalHalfPly = halfPly + 1;
            originalBlackCaptures = blackCaptures;
            originalWhiteCaptures = whiteCaptures;
            createMoveSet();
        }
    }

    protected void createMoveSet() {
        this.set = new RandomizedMoveSet(board.getSize(), random);
        for (int i = 0; i < super.getBoardSize(); i++) {
            for (int j = 0; j < super.getBoardSize(); j++) {
                if (super.isValidMove(i, j)) set.insert(new MoveImpl(i, j));
            }
        }
    }

    protected void reset() {
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

    public int getRollouts() {
        return this.rollouts;
    }

    public void setRollouts(int rollouts) {
        this.rollouts = rollouts;
        this.score = null;
    }

    @Override
    public void run() {
        if (score != null) return;
        if (solved) return;
        int tempScore = 0;
        for (int i = 0; i < rollouts; i++) { // perform specified rollouts for the given move
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