package aayzhao.pente.game.model;

public enum PieceType {
    EMPTY,
    WHITE,
    BLACK;

    public static int pieceToInt(PieceType piece) {
        switch (piece) {
            case EMPTY -> { return 0; }
            case WHITE -> { return 1; }
            case BLACK -> { return 2; }
            default -> { return -1; }
        }
    }

    public static PieceType intToPiece(int num) {
        switch (num) {
            case 0 -> { return EMPTY; }
            case 1 -> { return WHITE; }
            case 2 -> { return BLACK; }
            default -> { throw new IllegalArgumentException("Number must be 0-2 inclusive"); }
        }
    }

    public static PieceType opponentType(PieceType piece) {
        switch (piece) {
            case WHITE -> { return BLACK; }
            case BLACK -> { return WHITE; }
            default -> { return EMPTY; }
        }
    }
}
