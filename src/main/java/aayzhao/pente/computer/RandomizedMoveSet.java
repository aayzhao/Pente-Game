package aayzhao.pente.computer;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class RandomizedMoveSet {
    int index;
    Move[] vals;
    Map<Move,Integer> map;
    Random rand;

    public RandomizedMoveSet(int size, Random random) {
        index = 0;
        vals = new Move[size * size + 1];
        map = new HashMap<>();
        rand = random;
    }

    public int getIndex() {
        return this.index;
    }

    public boolean insert(Move val) {
        if (map.containsKey(val)) return false;
        map.put(val, index);
        vals[index++] = val;
        return true;
    }

    public boolean remove(Move val) {
        if (!map.containsKey(val)) return false;
        int temp_index = map.remove(val);
        Move temp_val = vals[index - 1];
        index--;

        vals[temp_index] = temp_val;
        map.replace(temp_val, index, temp_index);
        return true;
    }

    public Move getRandom() {
        Move target = vals[rand.nextInt(index)];
        remove(target);
        return target;
    }
}
