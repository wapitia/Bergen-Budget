
package com.wapitia.sched.parse.node;

import com.wapitia.sched.WeekOfMonth;

public class WeekOfMonthNode implements SchedNode {

    final WeekOfMonth weekOfMonth;

    public WeekOfMonthNode(WeekOfMonth weekOfMonth) {
        this.weekOfMonth = weekOfMonth;
    }

    public WeekOfMonth getWeekOfMonth() {

        return weekOfMonth;
    }

}
