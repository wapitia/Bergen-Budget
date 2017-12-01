
package com.wapitia.sched;

import java.time.DayOfWeek;

public class CycDow {

    private final WeekOfMonth woc;
    private final DayOfWeek   dow;

    public CycDow(WeekOfMonth woc, DayOfWeek dow) {
        this.woc = woc;
        this.dow = dow;
    }

    public WeekOfMonth getWoc() {

        return woc;
    }

    public DayOfWeek getDow() {

        return dow;
    }

}
