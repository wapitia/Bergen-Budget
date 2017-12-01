
package com.wapitia.sched.parse.node;

import java.time.DayOfWeek;
import java.util.EnumSet;
import java.util.stream.Collectors;

public class DaysOfWeekNode implements SchedNode {

    final EnumSet<DayOfWeek> dowSet;

    public DaysOfWeekNode(DayOfWeekNode dow) {
        this.dowSet = EnumSet.<DayOfWeek> of(dow.getDayOfWeek());
    }

    public DaysOfWeekNode(DayOfWeekNode dow, DaysOfWeekNode dowNode) {
        this.dowSet = EnumSet.<DayOfWeek> copyOf(dowNode.getDayOfWeekSet());
        dowSet.add(dow.getDayOfWeek());
    }

    public EnumSet<DayOfWeek> getDayOfWeekSet() {

        return dowSet;
    }

    @Override
    public String toString() {

        final String result;
        if (dowSet.size() == 1) {
            result = "dow=" + dowSet.stream().findFirst().get().toString();
        } else {
            result = dowSet.stream().map(Object::toString)
                .collect(Collectors.joining(",", "dows=[", "]"));
        }
        return result;
    }
}
