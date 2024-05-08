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

    public final static Board threeUncontestedBlackVert = new BoardImpl(new int[][] {
            { 0, 0, 0, 0, 0, 0, 0, 0, 0,}, // white will play 0,0 for testing purposes
            { 0, 0, 1, 0, 0, 0, 0, 0, 0,},
            { 0, 0, 0, 0, 0, 0, 0, 0, 0,},
            { 0, 0, 0, 0, 2, 1, 1, 2, 0,},
            { 0, 2, 0, 0, 1, 0, 0, 1, 0,},
            { 0, 2, 0, 0, 0, 0, 2, 0, 0,},
            { 0, 2, 0, 0, 2, 1, 0, 0, 0,},
            { 0, 0, 0, 0, 2, 0, 0, 0, 0,}, // 7,1 will make 4 uncontested
            { 0, 0, 0, 0, 0, 0, 0, 0, 0,},
    });

    public final static Board threeWhiteForwardDiagonal = new BoardImpl(new int[][] {
            { 0, 0, 0, 0, 0, 0, 0, 0, 0,}, // black will play 0,0 for testing purposes
            { 0, 0, 1, 0, 0, 0, 0, 0, 0,},
            { 0, 0, 0, 0, 0, 0, 0, 0, 0,},
            { 0, 0, 0, 0, 2, 1, 1, 2, 0,}, // 3,3 will make four in a row, but not uncontested on both sides
            { 0, 2, 1, 0, 1, 0, 0, 1, 0,},
            { 0, 1, 0, 0, 0, 0, 2, 0, 0,},
            { 1, 2, 0, 0, 2, 1, 0, 0, 0,},
            { 0, 0, 0, 0, 2, 0, 0, 0, 0,},
            { 0, 0, 0, 0, 0, 0, 0, 0, 0,},
    });

    public final static Board threeWhiteForwardDiagonalDisc = new BoardImpl(new int[][] {
            { 0, 0, 0, 0, 0, 0, 0, 0, 0,}, // black will play 0,0 for testing purposes
            { 0, 0, 1, 0, 0, 0, 0, 0, 0,},
            { 0, 0, 0, 0, 1, 0, 0, 0, 0,},
            { 0, 0, 0, 0, 2, 1, 1, 2, 0,}, // 3,3 will make four in a row, uncontested
            { 0, 2, 1, 0, 1, 0, 0, 1, 0,},
            { 0, 1, 0, 0, 0, 0, 2, 0, 0,},
            { 0, 2, 0, 0, 2, 1, 0, 0, 0,},
            { 0, 0, 0, 0, 2, 0, 0, 0, 0,},
            { 0, 0, 0, 0, 0, 0, 0, 0, 0,},
    });

    public final static Board threeWhiteForwardDiagonalDiscContested = new BoardImpl(new int[][] {
            { 0, 0, 0, 0, 0, 0, 0, 0, 0,}, // black will play 0,0 for testing purposes
            { 0, 0, 1, 0, 0, 2, 0, 0, 0,},
            { 0, 0, 0, 0, 1, 0, 0, 0, 0,},
            { 0, 0, 0, 0, 2, 1, 1, 2, 0,}, // 3,3 will make four in a row, but not uncontested
            { 0, 2, 1, 0, 1, 0, 0, 1, 0,},
            { 0, 1, 0, 0, 0, 0, 2, 0, 0,},
            { 0, 2, 0, 0, 2, 1, 0, 0, 0,},
            { 0, 0, 0, 0, 2, 0, 0, 0, 0,},
            { 0, 0, 0, 0, 0, 0, 0, 0, 0,},
    });

    public final static Board threeWhite1 = new BoardImpl(new int[][] {
            { 0, 0, 0, 0, 0, 0, 0, 0, 0,},
            { 0, 0, 1, 0, 0, 0, 0, 0, 0,},
            { 0, 0, 0, 0, 0, 0, 0, 0, 0,}, // 2,3 will make 4 in a row uncontested
            { 0, 0, 0, 0, 1, 0, 0, 0, 0,},
            { 0, 0, 0, 0, 0, 1, 0, 0, 0,},
            { 0, 0, 0, 0, 0, 0, 0, 0, 0,},
            { 0, 0, 0, 0, 0, 0, 0, 0, 0,},
            { 0, 2, 1, 1, 1, 0, 0, 0, 0,}, // 7,5 will make 4 in a row contested
            { 0, 0, 0, 0, 0, 0, 0, 0, 0,},
    });

    public final static Board threeContestedWhite = new BoardImpl(new int[][] {
            { 0, 0, 0, 0, 0, 0, 0, 0, 0,},
            { 0, 0, 1, 0, 0, 0, 0, 0, 0,},
            { 0, 0, 0, 0, 0, 0, 0, 0, 0,}, // 2,3 will make 4 in a row contested
            { 0, 0, 0, 0, 1, 0, 0, 2, 0,},
            { 0, 0, 0, 0, 0, 1, 0, 0, 0,},
            { 0, 0, 0, 0, 0, 0, 2, 0, 0,},
            { 0, 0, 0, 0, 0, 0, 0, 0, 0,},
            { 0, 2, 1, 0, 1, 0, 0, 0, 0,},
            { 0, 0, 0, 0, 0, 0, 0, 0, 0,},
    });
}
