package aayzhao.pente.game.model;

public interface Board {
    /**
     *
     * @param r
     * @param c
     * @return
     */
    PieceType getIntersection(int r, int c);

    /**
     *
     * @param player
     * @param r
     * @param c
     * @return
     */
    boolean placePiece(PieceType player, int r, int c);

    /**
     *
     * @param r
     * @param c
     */
    void removePiece(int r, int c);

    /**
     *
     * @return
     */
    int getSize();

    /**
     *
     */
    void clearBoard();

    /**
     * Returns a deep copy of this board
     * @return      deep copy of this board
     */
    Board copy();
}
