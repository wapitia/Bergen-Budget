package com.wapitia.test.payment.sched;

import com.wapitia.sched.DateStreamFunctions;

import org.junit.Assert;
import org.junit.Test;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Optional;
import java.util.stream.Stream;

public class TestDateStream {

	@Test
	public void testSched1() {

        Stream<LocalDate> str = DateStreamFunctions.weekly(LocalDate.of(2017,  10,  23));
		str.limit(10L).forEach(System.out::println);
	}

	@Test
	public void testSched2() {

        LocalDate pd1 = LocalDate.of(2017,  10,  23);
		exerciseTest2(DayOfWeek.MONDAY, "2017-10-23", pd1);
		exerciseTest2(DayOfWeek.TUESDAY, "2017-10-24", pd1);
		exerciseTest2(DayOfWeek.WEDNESDAY, "2017-10-25", pd1);
		exerciseTest2(DayOfWeek.THURSDAY, "2017-10-26", pd1);
		exerciseTest2(DayOfWeek.FRIDAY, "2017-10-27", pd1);
		exerciseTest2(DayOfWeek.SATURDAY, "2017-10-28", pd1);
		exerciseTest2(DayOfWeek.SUNDAY, "2017-10-29", pd1);

        LocalDate pd2 = LocalDate.of(2017,  10,  29);
		exerciseTest2(DayOfWeek.MONDAY, "2017-10-30", pd2);
		exerciseTest2(DayOfWeek.TUESDAY, "2017-10-31", pd2);
		exerciseTest2(DayOfWeek.WEDNESDAY, "2017-11-01", pd2);
		exerciseTest2(DayOfWeek.THURSDAY, "2017-11-02", pd2);
		exerciseTest2(DayOfWeek.FRIDAY, "2017-11-03", pd2);
		exerciseTest2(DayOfWeek.SATURDAY, "2017-11-04", pd2);
		exerciseTest2(DayOfWeek.SUNDAY, "2017-10-29", pd2);
	}

    private void exerciseTest2(final DayOfWeek testDay, final String expectedRes, final LocalDate pd) {
        Stream<LocalDate> str = DateStreamFunctions.weeklyOnOrAfter(testDay, pd);
        final Optional<LocalDate> firstDate = str.findFirst();
		String s = firstDate.map(Object::toString).orElse("MISSING");
		Assert.assertEquals(expectedRes, s);
	}
}
