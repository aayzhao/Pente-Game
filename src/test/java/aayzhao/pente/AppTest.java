package aayzhao.pente;

import aayzhao.pente.computer.mcts.tree.Proportion;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AppTest {
    @Test
    public void sanityTrue() { // should answer true
        assertTrue(true);
    }

    @Test
    public void proportionDecimalTest() {
        Proportion prop = new Proportion(1, 10);
        assertEquals(0.1, prop.decimal());
    }

    @Test
    public void proportionCompTest1() {
        Proportion prop1 = new Proportion(10, 20);
        Proportion prop2 = new Proportion(1, 2);
        assertEquals(0.5, prop1.decimal());
        assertTrue(prop2.compareTo(prop1) < 0);
    }

    @Test
    public void proportionCompTest2() {
        Proportion prop1 = new Proportion(10, 20);
        Proportion prop2 = new Proportion(1, 25);
        assertTrue(prop2.compareTo(prop1) < 0);

        Proportion prop3 = new Proportion(45, 87);
        assertTrue(prop3.compareTo(prop1) > 0);
    }

    @Test
    public void proportionCompTest3() {
        Proportion target = new Proportion(15, 81);
        Proportion prop2 = new Proportion(-8, 81);
        Proportion prop3 = new Proportion(6, 81);
        Proportion prop4 = new Proportion(15, 81);
        Proportion prop5 = new Proportion(16, 81);
        assertTrue(target.compareTo(prop2) > 0);
        assertTrue(target.compareTo(prop3) > 0);
        assertEquals(0, target.compareTo(prop4));
        assertTrue(target.compareTo(prop5) < 0);
    }
}
