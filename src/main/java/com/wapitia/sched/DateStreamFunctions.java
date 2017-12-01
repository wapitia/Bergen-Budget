package com.wapitia.sched;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

/**
*/
public interface DateStreamFunctions {

    public static long DAYS_PER_WEEK = ChronoUnit.WEEKS
        .getDuration().getSeconds()
        / ChronoUnit.DAYS.getDuration().getSeconds();

    static UnaryOperator<LocalDate> addWeekOper   = (LocalDate ldt) -> ldt
        .plusDays(DAYS_PER_WEEK);

    /**
     * Return a weekly date stream, starting on the given date and incrementing
     * each 7 days thereafter.
     */
    public static Stream<LocalDate> weekly(LocalDate startOn) {

        return Stream.iterate(startOn, addWeekOper);
    }

    /**
     * Return a weekly date stream, starting with a particular day of week,
     * starting on or after some reference date.
     */
    public static Stream<LocalDate> weeklyOnOrAfter(DayOfWeek dow,
        LocalDate startOnOrAfter)
    {

        final DayOfWeek startOnOrAfterDow = startOnOrAfter.getDayOfWeek();
        int cycleOffset = (int) ((dow.ordinal() - startOnOrAfterDow.ordinal()
            + DAYS_PER_WEEK) % DAYS_PER_WEEK);
        final LocalDate startOn = startOnOrAfter.plusDays(cycleOffset);
        return weekly(startOn);
    }

    /**
     * Return a weekly date stream, starting with a particular day of week,
     * starting on or after some reference date.
     */
    public static Stream<LocalDate> multiWeeklyOnOrAfter(DayOfWeek dow,
        int nWeeksInCycle, int cycleNo, LocalDate startOnOrAfter)
    {

        // TODO
        final DayOfWeek startOnOrAfterDow = startOnOrAfter.getDayOfWeek();
        int cycleOffset = (int) ((dow.ordinal() - startOnOrAfterDow.ordinal()
            + DAYS_PER_WEEK) % DAYS_PER_WEEK);
        final LocalDate startOn = startOnOrAfter.plusDays(cycleOffset);
        return weekly(startOn);
    }

    /**
     * Return a weekly date stream, starting on the given date and incrementing
     * each 7 days thereafter.
     */
    public static Stream<LocalDate> monthly(LocalDate startOn) {

        // TODO
        return null;
    }

    /**
     * Return a weekly date stream, starting with a particular day of week,
     * starting on or after some reference date.
     */
    public static Stream<LocalDate> semiMonthlyOnOrAfter(int dayOfMonth1,
        int dayOfMonth2, LocalDate startOnOrAfter)
    {

        @SuppressWarnings("unused")
        final DayOfWeek startOnOrAfterDow = startOnOrAfter.getDayOfWeek();
        // int cycleOffset = (int)(((long)(dow.ordinal() -
        // startOnOrAfterDow.ordinal()) + DAYS_PER_WEEK) % DAYS_PER_WEEK);
        // final PaymentDate startOn = startOnOrAfter.plus(cycleOffset);
        // return weekly(startOn);
        // TODO
        return null;
    }

    /**
     * Return a weekly date stream, starting with a particular day of week,
     * starting on or after some reference date.
     */
    public static Stream<LocalDate> monthlyOnOrAfter(int dayOfMonth,
        LocalDate startOnOrAfter)
    {

        @SuppressWarnings("unused")
        final DayOfWeek startOnOrAfterDow = startOnOrAfter.getDayOfWeek();
        // int cycleOffset = (int)(((long)(dow.ordinal() -
        // startOnOrAfterDow.ordinal()) + DAYS_PER_WEEK) % DAYS_PER_WEEK);
        // final PaymentDate startOn = startOnOrAfter.plus(cycleOffset);
        // return weekly(startOn);
        // TODO
        return null;
    }

    /**
     * Return a weekly date stream, starting with a particular day of week,
     * starting on or after some reference date.
     */
    public static Stream<LocalDate> multiMonthlyOnOrAfter(int dayOfMonth,
        int nMonthsInCycle, int monthNo, LocalDate startOnOrAfter)
    {

        // final DayOfWeek startOnOrAfterDow =
        // startOnOrAfter.asLocalDate().getDayOfWeek();
        // int cycleOffset = (int)(((long)(dow.ordinal() -
        // startOnOrAfterDow.ordinal()) + DAYS_PER_WEEK) % DAYS_PER_WEEK);
        // final PaymentDate startOn = startOnOrAfter.plus(cycleOffset);
        // return weekly(startOn);
        // TODO
        return null;
    }

}
