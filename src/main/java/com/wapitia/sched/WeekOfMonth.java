
package com.wapitia.sched;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Deprecated
public enum WeekOfMonth {

    FIRST(0), SECOND(1), THIRD(2), FOURTH(3), LAST(5);

    int ord;

    WeekOfMonth(int ord) {
        this.ord = ord;
    }

    public int ord() {

        return ord;
    }

    private static final Map<Integer, WeekOfMonth> indexed = new HashMap<>();
    static {
        indexed.put(0, FIRST);
        indexed.put(1, SECOND);
        indexed.put(2, THIRD);
        indexed.put(3, FOURTH);
        indexed.put(5, LAST);
    }

    public static Optional<WeekOfMonth> ofOrdinal(int ord) {

        return Optional.ofNullable(indexed.get(ord));
    }

}
