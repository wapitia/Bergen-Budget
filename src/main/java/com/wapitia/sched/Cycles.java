package com.wapitia.sched;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * Weeks start on Monday.
 *
 * @author Corey Morgan
 *
 */
public class Cycles {

    static final int         ALL_CYCLES    = -1;
    public static final long DAYS_PER_WEEK = ChronoUnit.WEEKS.getDuration()
        .getSeconds() / ChronoUnit.DAYS.getDuration().getSeconds();

    public static WeeklyBuilder weeklyCycle() {

        return new WeeklyBuilder().weeksInCycle(1);
    }

    public static WeeklyBuilder biweeklyCycle() {

        return new WeeklyBuilder().weeksInCycle(2);
    }

    public static class WeeklyBuilder {

        int       nWeeks                 = 1;
        LocalDate cycleWeek0ContainsDate = null;
        LocalDate startOnOrAfter         = null;
        List<DC>  days                   = new ArrayList<>();

        public WeeklyBuilder weeksInCycle(int nWeeks) {

            this.nWeeks = nWeeks;
            return this;
        }

        public WeeklyBuilder cycleWeekZeroHasDate(LocalDate date) {

            this.cycleWeek0ContainsDate = date;
            return this;
        }

        public WeeklyBuilder startOnOrAfter(LocalDate date) {

            this.startOnOrAfter = date;
            return this;
        }

        public WeeklyBuilder addDay(DayOfWeek dow) {

            days.add(new DC(dow.ordinal(), ALL_CYCLES));
            return this;
        }

        public WeeklyBuilder addDay(DayOfWeek dow, int weekOfCycle) {

            days.add(new DC(dow.ordinal(), weekOfCycle));
            return this;
        }

        List<Integer> getDayOffsets(LocalDate startOfCycle) {

            List<Integer> accum = new ArrayList<>();
            for (int cw = 0; cw < nWeeks; ++cw) {
                for (DC dc : days) {
                    if (dc.weekOfCycle == ALL_CYCLES || dc.weekOfCycle == cw) {
                        int dord = dc.dowOrd;
                        int doff = (int) ((cw * DAYS_PER_WEEK) + dord);
                        accum.add(doff);
                    }
                }
            }

            return accum;
        }

        LocalDate rollTheCycle(LocalDate from) {

            LocalDate result = from.plusDays(nWeeks * DAYS_PER_WEEK);
            return result;
        }

        public Cycle toCycle() {

            LocalDate dateHavingWeek0 = cycleWeek0ContainsDate;
            if (dateHavingWeek0 == null) {
                dateHavingWeek0 = startOnOrAfter;
            }
            if (dateHavingWeek0 == null)
                throw new RuntimeException("No starting date");

            // find the Monday of this week. Monday is ordinal 0
            int dord = dateHavingWeek0.getDayOfWeek().ordinal();
            LocalDate mondayWeek0 = dateHavingWeek0.plusDays(0L - dord);
            Cycle result = new Cycle(mondayWeek0, startOnOrAfter,
                this::getDayOffsets, this::rollTheCycle);
            return result;
        }

        public Stream<LocalDate> stream() {

            return toCycle().stream();
        }

    }

    static class DC {

        int dowOrd;
        int weekOfCycle;

        public DC(int dowOrd, int weekOfCycle) {
            this.dowOrd = dowOrd;
            this.weekOfCycle = weekOfCycle;
        }

    }

    public static MonthlyBuilder monthlyCycle() {

        return new MonthlyBuilder();
    }

    public static class MonthlyBuilder {

        static final int ALL_CYCLES            = -1;

        int            nMonths                 = 1;
        LocalDate      cycleMonth0ContainsDate = null;
        LocalDate      startOnOrAfter          = null;
        List<DC>       days                    = new ArrayList<>();

        public MonthlyBuilder monthsInCycle(int nWeeks) {

            this.nMonths = nWeeks;
            return this;
        }

        public MonthlyBuilder startOnOrAfter(LocalDate date) {

            this.startOnOrAfter = date;
            return this;
        }

        public MonthlyBuilder cycleMonthZeroHasDate(LocalDate date) {

            this.cycleMonth0ContainsDate = date;
            return this;
        }

        public MonthlyBuilder addDayOfMonth(int dayOfMonth) {

            days.add(new DC(dayOfMonth - 1, ALL_CYCLES));
            return this;
        }

        List<Integer> getDayOffsets(LocalDate startOfCycle) {

            List<Integer> accum = new ArrayList<>();
            for (int cw = 0; cw < nMonths; ++cw) {
                for (DC dc : days) {
                    if (dc.weekOfCycle == ALL_CYCLES || dc.weekOfCycle == cw) {
                        int dord = dc.dowOrd;
                        int doff = (dord);
                        accum.add(doff);
                    }
                }
            }

            return accum;
        }

        LocalDate rollTheCycle(LocalDate from) {

            int y = from.getYear();
            int m = from.getMonthValue();
            int d = from.getDayOfMonth();
            m += nMonths;
            while (m > 12) {
                y++;
                m -= 12;
            }
            LocalDate result = LocalDate.of(y, m, d);
            return result;
        }

        public Cycle toCycle() {

            LocalDate dateHavingMonth0 = cycleMonth0ContainsDate;
            if (dateHavingMonth0 == null) {
                dateHavingMonth0 = startOnOrAfter;
            }
            if (dateHavingMonth0 == null)
                throw new RuntimeException("No starting date");

            // find the Monday of this week. Monday is ordinal 0
            final LocalDate d0 = dateHavingMonth0;
            LocalDate day0 = LocalDate.of(d0.getYear(), d0.getMonthValue(),
                1);
            Cycle result = new Cycle(day0, startOnOrAfter, this::getDayOffsets,
                this::rollTheCycle);
            return result;
        }

        public Stream<LocalDate> stream() {

            return toCycle().stream();
        }
    }

}
