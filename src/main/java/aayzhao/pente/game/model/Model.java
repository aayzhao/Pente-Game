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
     * The current half ply of the game. An empty board for a new game has
     * a half ply of 1, indicating White's turn to move.
     * @return  the current half ply
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
