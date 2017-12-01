
package com.wapitia.sched.parse.node;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class DaysOfMonthNode implements SchedNode {

    private List<Integer> days;

    public DaysOfMonthNode(DayNode dayNode) {
        this.days = new ArrayList<>(1);
        days.add(dayNode.day());
    }

    public DaysOfMonthNode(DayNode dayNode, DaysOfMonthNode tailDaysNode) {
        this.days = new ArrayList<>(tailDaysNode.days.size() + 1);
        days.add(dayNode.day());
        days.addAll(tailDaysNode.days);
    }

    public List<Integer> daysList() {

        return days;
    }

    public IntStream daysOfMonthStream() {

        final IntStream result = days.stream().mapToInt(Integer::intValue);
        return result;
    }

    @Override
    public String toString() {

        final String result;
        if (days.size() == 1) {
            result = String.format("day=%d", days.get(0));
        } else {
            result = days.stream().map(Object::toString)
                .collect(Collectors.joining(",", "days=[", "]"));
        }
        return result;
    }
}
