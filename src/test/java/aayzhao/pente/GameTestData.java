package aayzhao.pente;

import aayzhao.pente.game.model.Board;
import aayzhao.pente.game.model.BoardImpl;

public class GameTestData {
    public final static Board fourWhiteBackDiagonal = new BoardImpl(new int[][] {
            { 0, 0, 0, 0, 0, 0, 0, 0, 0,},
            { 0, 0, 1, 0, 0, 0, 0, 0, 0,},
            { 0, 0, 0, 0, 0, 0, 0, 0, 0,}, // 2,3 will make 5 in a row
            { 0, 0, 0, 0, 1, 0, 0, 0, 0,},
            { 0, 0, 0, 0, 0, 1, 0, 0, 0,},
            { 0, 0, 0, 0, 0, 0, 1, 0, 0,},
            { 0, 0, 0, 0, 0, 0, 0, 0, 0,},
            { 0, 0, 0, 0, 0, 0, 0, 0, 0,},
            { 0, 0, 0, 0, 0, 0, 0, 0, 0,},
    });

    public final static Board fourWhiteForwardDiagonal = new BoardImpl(new int[][] {
            { 0, 0, 0, 0, 0, 0, 0, 0, 0,},
            { 0, 0, 1, 0, 0, 0, 0, 0, 0,},
            { 0, 0, 0, 0, 0, 0, 0, 0, 0,},
            { 0, 0, 0, 0, 1, 0, 0, 0, 0,}, // 3,8 will make 5 in a row
            { 0, 0, 0, 0, 0, 0, 0, 1, 0,},
            { 0, 0, 0, 0, 0, 0, 1, 0, 0,},
            { 0, 0, 0, 0, 0, 1, 0, 0, 0,},
            { 0, 0, 0, 0, 1, 0, 0, 0, 0,},
            { 0, 0, 0, 0, 0, 0, 0, 0, 0,},
    });

    public final static Board fourBlackHorizontal = new BoardImpl(new int[][] {
            { 0, 0, 0, 0, 0, 0, 0, 0, 0,},
            { 0, 0, 1, 0, 0, 0, 0, 0, 0,},
            { 0, 0, 0, 0, 0, 0, 0, 0, 0,},
            { 0, 0, 0, 0, 2, 2, 2, 2, 0,}, // 3, 3 or 3, 8 will make 5
            { 0, 0, 0, 0, 0, 0, 0, 1, 0,},
            { 0, 0, 0, 0, 0, 0, 1, 0, 0,},
            { 0, 0, 0, 0, 0, 1, 0, 0, 0,},
            { 0, 0, 0, 0, 1, 0, 0, 0, 0,},
            { 0, 0, 0, 0, 0, 0, 0, 0, 0,},
    });

    public final static Board fourBlackVertical = new BoardImpl(new int[][] {
            { 0, 0, 0, 0, 0, 0, 0, 0, 0,},
            { 0, 0, 1, 0, 0, 0, 0, 0, 0,},
            { 0, 0, 0, 0, 0, 0, 0, 0, 0,},
            { 0, 0, 0, 0, 2, 2, 1, 2, 0,},
            { 0, 0, 0, 0, 2, 0, 0, 1, 0,},
            { 0, 0, 0, 0, 0, 0, 1, 0, 0,}, // 5, 4 will make 5
            { 0, 0, 0, 0, 2, 1, 0, 0, 0,},
            { 0, 0, 0, 0, 2, 0, 0, 0, 0,},
            { 0, 0, 0, 0, 0, 0, 0, 0, 0,},
    });
}
