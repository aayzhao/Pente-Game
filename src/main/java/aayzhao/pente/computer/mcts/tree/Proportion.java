package aayzhao.pente.computer.mcts.tree;

public class Proportion implements Comparable<Proportion> {
    int numerator;
    int denominator;

    public Proportion() {
        this(0, 0);
    }

    public Proportion(int numerator, int denominator) {
        this.numerator = numerator;
        this.denominator = denominator;
    }

    public double decimal() {
        if (denominator == 0) return 0.0;
        return ((double) numerator) / ((double) denominator);
    }

    @Override
    public String toString() {
        return numerator + "/" + denominator;
    }

    @Override
    public int compareTo(Proportion o) {
        if (this.decimal() < o.decimal()) return -1;
        else if (this.decimal() > o.decimal()) return 1;
        else return this.denominator - o.denominator;
    }
}
