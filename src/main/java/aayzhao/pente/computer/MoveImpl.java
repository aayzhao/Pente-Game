package aayzhao.pente.computer;

public class MoveImpl implements Move {
    int[] pair;
    public MoveImpl(int r, int c) {
        pair = new int[2];
        pair[0] = r;
        pair[1] = c;
    }

    @Override
    public int getRowCoord() {
        return pair[0];
    }

    @Override
    public int getColumnCoord() {
        return pair[1];
    }

    @Override
    public String getAlphaNumericCoord() {
        StringBuilder sb = new StringBuilder();
        sb.append((char) ('a' + pair[0]));
        sb.append((char) ('1' + pair[1]));
        return sb.toString();
    }

    @Override
    public String toString() {
        return getAlphaNumericCoord();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) return false;
        if (o.getClass() != MoveImpl.class) return false;
        return (((MoveImpl) o).getRowCoord() == this.getRowCoord() && ((MoveImpl) o).getColumnCoord() == this.getColumnCoord());
    }

    @Override
    public int compareTo(Move o) {
        return o.toString().compareTo(this.toString());
    }
}
