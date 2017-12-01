
package com.wapitia.sched.parse.node;

import com.wapitia.sched.WeekOfMonth;

import java.util.EnumSet;
import java.util.stream.Collectors;

public class WeeksOfMonthNode implements SchedNode {

    final EnumSet<WeekOfMonth> womSet;

    public WeeksOfMonthNode(WeekOfMonthNode wom) {
        this.womSet = EnumSet.<WeekOfMonth> of(wom.getWeekOfMonth());
    }

    public WeeksOfMonthNode(WeekOfMonthNode wom, WeeksOfMonthNode woms) {
        this.womSet = EnumSet.<WeekOfMonth> copyOf(woms.getWeekOfMonthSet());
        womSet.add(wom.getWeekOfMonth());
    }

    public EnumSet<WeekOfMonth> getWeekOfMonthSet() {

        return womSet;
    }

    @Override
    public String toString() {

        final String result;
        if (womSet.size() == 1) {
            result = "wom=" + womSet.stream().findFirst().get().toString();
        } else {
            result = womSet.stream().map(Object::toString)
                .collect(Collectors.joining(",", "woms=[", "]"));
        }
        return result;
    }
}
