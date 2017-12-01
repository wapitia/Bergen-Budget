
package com.wapitia.sched.parse.node;

public class DayNode implements SchedNode {

    private Integer day;

    public DayNode(Integer day) {
        this.day = day;
    }

    public Integer day() {

        return day;
    }

    @Override
    public String toString() {

        final String result = day.toString();
        return result;
    }

}
