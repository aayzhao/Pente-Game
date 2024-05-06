package aayzhao.pente.game.model;

public class BoardImpl implements  Board{
    private final int size;
    private int[][] board;
    public BoardImpl(int size) {
        this.size = size;
        board = new int[size][size];
    }

    /**
     * Default constructor. Initializes board size to a standard Go board with 19x19 intersections
     */
    public BoardImpl() {
        this(19);
    }

    /**
     * Throws index out of bounds exception if the given coordinates are invalid for the board size.
     * @param r row
     * @param c column
     */
    private void enforceBounds(int r, int c) {
        if (r < 0 || c < 0 || r >= size || c >= size) throw new IndexOutOfBoundsException("Not valid coordinates for given board size");
    }

    @Override
    public PieceType getIntersection(int r, int c) {
        enforceBounds(r, c);
        return PieceType.intToPiece(board[r][c]);
    }

    @Override
    public boolean placePiece(PieceType player, int r, int c) {
        enforceBounds(r, c);
        if (getIntersection(r, c) != PieceType.EMPTY) return false; // not a valid piece placement
        board[r][c] = PieceType.pieceToInt(player);
        return true;
    }

    @Override
    public void removePiece(int r, int c) {
        enforceBounds(r, c);
        if (getIntersection(r, c) == PieceType.EMPTY) throw new IllegalArgumentException("Cannot remove empty piece");
        board[r][c] = PieceType.pieceToInt(PieceType.EMPTY);
    }

    @Override
    public int getSize() {
        return this.size;
    }

    @Override
    public void clearBoard() {
        board = new int[size][size];
    }

    @Override
    public Board copy() {
        Board temp = new BoardImpl(this.getSize());
        for (int i = 0; i < this.size; i++) {
            for (int j = 0; j < this.size; j++) {
                if (this.getIntersection(i, j) != PieceType.EMPTY)
                    temp.placePiece(this.getIntersection(i, j), i, j);
            }
        }
        return temp;
    }
}
