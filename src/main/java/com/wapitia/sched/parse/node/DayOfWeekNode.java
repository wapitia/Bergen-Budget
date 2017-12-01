
package com.wapitia.sched.parse.node;

import java.time.DayOfWeek;

public class DayOfWeekNode implements SchedNode {

    final DayOfWeek dow;

    public DayOfWeekNode(DayOfWeek dow) {
        this.dow = dow;
    }

    public DayOfWeek getDayOfWeek() {

        return dow;
    }

}
