package aayzhao.pente.game.model;

public interface Model {
    /**
     *
     * @return
     */
    int getWhitePlayerCaptures();

    /**
     *
     * @return
     */
    int getBlackPlayerCaptures();

    /**
     *
     * @param halfPly
     * @return
     */
    PieceType getCurrentPlayerTurn(int halfPly);

    /**
     *
     * @return
     */
    int getHalfPly();

    /**
     *
     * @param r
     * @param c
     * @return
     */
    PieceType getIntersection(int r, int c);

    /**
     *
     * @return
     */
    int getBoardSize();
    /**
     *
     * @return
     */
    int getPly();

    /**
     *
     * @param r
     * @param c
     * @return
     */
    boolean move(int r, int c);

    boolean isValidMove(int r, int c);

    /**
     *
     */
    void clearBoard();

    /**
     * Returns a copy of the current game board
     * @return copy of board
     */
    Board getBoard();

    /**
     *
     * @return
     */
    PieceType getWinner();

    /**
     *
     * @return
     */
    boolean isWon();

    /**
     *
     * @param modelObserver
     */
    void subscribe(ModelObserver modelObserver);

    /**
     *
     * @param modelObserver
     */
    void unsubscribe(ModelObserver modelObserver);

    /**
     *
     */
    void update();
}
