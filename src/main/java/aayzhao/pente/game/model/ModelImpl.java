package aayzhao.pente.game.model;

import java.util.ArrayList;
import java.util.List;

public class ModelImpl implements Model {
    protected int whiteCaptures; // black stones captured by the white player
    protected int blackCaptures; // white stones captured by the black player
    protected Board board;
    protected int halfPly; // current full turn in progress
    protected final List<ModelObserver> observers;

    public ModelImpl() {
        this(19);
    }

    public ModelImpl(int size) {
        this.board = new BoardImpl(size);
        whiteCaptures = 0;
        blackCaptures = 0;
        this.halfPly = 1;
        this.observers = new ArrayList<>();
    }

    public ModelImpl(Board board, int whiteCaptures, int blackCaptures, int halfPly) {
        this.observers = new ArrayList<>();
        this.whiteCaptures = whiteCaptures;
        this.blackCaptures = blackCaptures;
        this.halfPly =  halfPly;
        this.board = board.copy(); // get a deep copy of the input board
    }

    @Override
    public int getWhitePlayerCaptures() {
        return this.whiteCaptures;
    }

    @Override
    public int getBlackPlayerCaptures() {
        return this.blackCaptures;
    }

    @Override
    public PieceType getCurrentPlayerTurn(int halfPly) {
        if (halfPly % 2 == 1) return PieceType.WHITE;
        else return PieceType.BLACK;
    }

    @Override
    public int getHalfPly() {
        return this.halfPly;
    }

    @Override
    public PieceType getIntersection(int r, int c) {
        return this.board.getIntersection(r, c);
    }

    @Override
    public int getBoardSize() {
        return this.board.getSize();
    }

    @Override
    public int getPly() {
        return (halfPly + 1) / 2;
    }

    @Override
    public boolean move(int r, int c) {
        if (isValidMove(r, c)) {
            if (halfPly % 2 == 1) this.board.placePiece(PieceType.WHITE, r, c);
            else this.board.placePiece(PieceType.BLACK, r, c);
            checkCapture(r, c);
            halfPly++;
            update();
            return true;
        } else {
            return false;
        }
    }

    private void checkCapture(int r, int c) {
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
                captures++;
            }
            if (left) {
                if (board.getIntersection(r - 3, c - 3) == cur
                        && board.getIntersection(r - 2, c - 2) == opp
                        && board.getIntersection(r - 1, c - 1) == opp) {
                    board.removePiece(r - 2, c - 2);
                    board.removePiece(r - 1, c - 1);
                    captures++;
                }

            }
            if (right) {
                if (board.getIntersection(r - 3, c + 3) == cur
                        && board.getIntersection(r - 2, c + 2) == opp
                        && board.getIntersection(r - 1, c + 1) == opp) {
                    board.removePiece(r - 2, c + 2);
                    board.removePiece(r - 1, c + 1);
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
                captures++;
            }
            if (left) {
                if (board.getIntersection(r + 3, c - 3) == cur
                        && board.getIntersection(r + 2, c - 2) == opp
                        && board.getIntersection(r + 1, c - 1) == opp) {
                    board.removePiece(r + 2, c - 2);
                    board.removePiece(r + 1, c - 1);
                    captures++;
                }
            }
            if (right) {
                if (board.getIntersection(r + 3, c + 3) == cur
                        && board.getIntersection(r + 2, c + 2) == opp
                        && board.getIntersection(r + 1, c + 1) == opp) {
                    board.removePiece(r + 2, c + 2);
                    board.removePiece(r + 1, c + 1);
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
                captures++;
            }
        }
        if (right) {
            if (board.getIntersection(r, c + 3) == cur
                    && board.getIntersection(r, c + 2) == opp
                    && board.getIntersection(r, c + 1) == opp) {
                board.removePiece(r, c + 2);
                board.removePiece(r, c + 1);
                captures++;
            }
        }
        if (cur == PieceType.WHITE) whiteCaptures += captures;
        else blackCaptures += captures;
    }

    @Override
    public boolean isValidMove(int r, int c) {
        try {
            return this.board.getIntersection(r, c) == PieceType.EMPTY;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    @Override
    public void clearBoard() {
        this.board.clearBoard();
        halfPly = 1;
        whiteCaptures = 0;
        blackCaptures = 0;
        update();
    }

    @Override
    public Board getBoard() {
        return this.board.copy();
    }

    @Override
    public PieceType getWinner() {
        PieceType winner = null;

        if (whiteCaptures >= 5 || blackCaptures >= 5) {
            winner = whiteCaptures > blackCaptures ? PieceType.WHITE : PieceType.BLACK;
        }

        for (int i = 0; i < this.getBoardSize(); i++) {
            for (int j = 0; j < this.getBoardSize(); j++) {
                if (getIntersection(i, j) == PieceType.EMPTY) continue;
                if (checkFive(i, j)) {
                    winner = getIntersection(i, j);
                    break;
                }
            }
            if (winner != null) break;
        }

        return winner;
    }

    @Override
    public boolean isWon() {
        return getWinner() != null;
    }

    private boolean checkFive(int r, int c) {
        boolean left = c > 3;
        boolean right = c < this.getBoardSize() - 4;
        boolean up = r < this.getBoardSize() - 4;
        PieceType cur = this.getIntersection(r, c);
        if (cur == PieceType.EMPTY) throw new IllegalArgumentException("Cannot check empty square");

        if (left) {
            int count = 4;
            for (int i = -4; i < 0; i++) {
                if (cur == getIntersection(r, c + i)) count--;
            }
            if (count == 0) return true;

            if (up) {
                count = 4;
                for (int i = -4; i < 0; i++) {
                    if (cur == getIntersection(r - i, c + i)) count--;
                }
                if (count == 0) return true;
            }
        }
        if (right) {
            int count = 4;
            for (int i = -4; i < 0; i++) {
                if (cur == getIntersection(r, c - i)) count--;
            }
            if (count == 0) return true;

            if (up) {
                count = 4;
                for (int i = -4; i < 0; i++) {
                    if (cur == getIntersection(r - i, c - i)) count--;
                }
                if (count == 0) return true;
            }
        }
        if (up) {
            int count = 4;
            for (int i = -4; i < 0; i++) {
                if (cur == getIntersection(r - i, c)) count--;
            }
            return count == 0;
        }
        return false;
    }

    @Override
    public void subscribe(ModelObserver modelObserver) {
        this.observers.add(modelObserver);
    }

    @Override
    public void unsubscribe(ModelObserver modelObserver) {
        this.observers.remove(modelObserver);
    }

    @Override
    public void update() {
        for (ModelObserver ob : observers) {
            ob.update(this);
        }
    }
}
