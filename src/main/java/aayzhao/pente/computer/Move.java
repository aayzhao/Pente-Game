package aayzhao.pente.computer;

public interface Move extends Comparable<Move> {
    int getRowCoord();
    int getColumnCoord();
    String getAlphaNumericCoord();
}
