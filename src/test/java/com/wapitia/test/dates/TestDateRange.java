package com.wapitia.test.dates;

import com.wapitia.dates.DateRangePak;
import com.wapitia.dates.DateRangePak.DateRange;
import com.wapitia.dates.DateRangePak.DateRangeList;
import com.wapitia.dates.RangePak.ARangeList;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.util.Optional;

public class TestDateRange {

	LocalDate date_1019;
	LocalDate date_10_20;
	LocalDate date_1021;
	LocalDate date_10_22;
	LocalDate date_10_23;
	LocalDate date_10_25;
	LocalDate date_1031;
	LocalDate date_1102;

	DateRange range_1020;
	DateRange range_1019_1021;
	DateRange range_1021_1031;
	DateRange range_1021_1031DUP;
	DateRange range_1025_1102;
	DateRange range_thru_1021;
	DateRange range_thru_1021DUP;
	DateRange range_from_1019;
	DateRange range_from_1021;
	DateRange range_from_1022;
	DateRange range_from_1023;
    DateRange range_1022_1031;

	@Before
	public void initDates() {
		this.date_1019 = LocalDate.of(2017, 10, 19);
		this.date_10_20 = LocalDate.of(2017, 10, 20);
		this.date_1021 = LocalDate.of(2017, 10, 21);
		this.date_10_22 = LocalDate.of(2017, 10, 22);
		this.date_10_23 = LocalDate.of(2017, 10, 23);
		this.date_10_25 = LocalDate.of(2017, 10, 25);
		this.date_1031 = LocalDate.of(2017, 10, 31);
		this.date_1102 = LocalDate.of(2017, 11, 02);

		this.range_1020 = DateRange.ofSingle(date_10_20);
		this.range_1019_1021 = DateRange.of(date_1019, date_1021);
        this.range_1022_1031 = DateRange.of(date_10_22, date_1031);
		this.range_1021_1031 = DateRange.of(date_1021, date_1031);
		this.range_1021_1031DUP = DateRange.of(date_1021, date_1031);
		this.range_1025_1102 = DateRange.of(date_10_25, date_1102);
		this.range_thru_1021 = DateRange.ofOpenStart(date_1021);
		this.range_thru_1021DUP = DateRange.ofOpenStart(date_1021);
		this.range_from_1019 = DateRange.ofOpenFinish(date_1019);
		this.range_from_1021 = DateRange.ofOpenFinish(date_1021);
		this.range_from_1022 = DateRange.ofOpenFinish(date_10_22);
		this.range_from_1023 = DateRange.ofOpenFinish(date_10_23);
	}


	@Test
	public void testCreation() {

		validateDateRangeState(range_1020, Optional.<LocalDate>of(date_10_20), Optional.<LocalDate>of(date_10_20), 1L);
		validateDateRangeState(range_1019_1021, Optional.<LocalDate>of(date_1019), Optional.<LocalDate>of(date_1021), 3L);
		validateDateRangeState(range_1021_1031, Optional.<LocalDate>of(date_1021), Optional.<LocalDate>of(date_1031), 11L);
		validateDateRangeState(range_thru_1021, Optional.<LocalDate>empty(), Optional.<LocalDate>of(date_1021), Long.MAX_VALUE);
		validateDateRangeState(range_from_1019, Optional.<LocalDate>of(date_1019), Optional.<LocalDate>empty(), Long.MAX_VALUE);
        validateDateRangeState(DateRangePak.ALL, Optional.<LocalDate>empty(), Optional.<LocalDate>empty(), Long.MAX_VALUE);

		Assert.assertFalse(dateRangeEquals(range_1021_1031, Optional.<LocalDate>of(date_1021), Optional.<LocalDate>empty()));

		Assert.assertEquals("(DateRange 2017-10-20..2017-10-20)", range_1020.toString());
		Assert.assertEquals("(DateRange 2017-10-19..2017-10-21)", range_1019_1021.toString());
		Assert.assertEquals("(DateRange 2017-10-21..2017-10-31)", range_1021_1031.toString());
		Assert.assertEquals("(DateRange 2017-10-25..2017-11-02)", range_1025_1102.toString());
		Assert.assertEquals("(DateRange <<..2017-10-21)", range_thru_1021.toString());
		Assert.assertEquals("(DateRange 2017-10-19..>>)", range_from_1019.toString());
		Assert.assertEquals("(ALL <<..>>)", DateRangePak.ALL.toString());

		Assert.assertTrue(range_thru_1021.isEqual(range_thru_1021DUP));
		Assert.assertTrue(range_1021_1031.isEqual(range_1021_1031DUP));
		Assert.assertFalse(range_1021_1031.isEqual(range_1020));
		Assert.assertFalse(range_1021_1031.isEqual(range_1025_1102));
		Assert.assertFalse(range_1021_1031.isEqual(range_thru_1021));
		Assert.assertFalse(range_1021_1031.isEqual(range_1019_1021));

		Assert.assertTrue(range_1020.intersects(range_1019_1021));
		Assert.assertTrue(range_1019_1021.intersects(range_1020));
		Assert.assertFalse(range_1020.intersects(range_1021_1031));
		Assert.assertFalse(range_1021_1031.intersects(range_1020));
		Assert.assertFalse(range_1019_1021.intersects(range_1021_1031));
		Assert.assertFalse(range_1021_1031.intersects(range_1019_1021));

		Assert.assertTrue(range_1020.adjoins(range_1019_1021));
		Assert.assertTrue(range_1019_1021.adjoins(range_1020));
		Assert.assertTrue(range_1020.adjoins(range_1021_1031));
		Assert.assertTrue(range_1021_1031.adjoins(range_1020));
		Assert.assertFalse(range_1019_1021.adjoins(range_1021_1031));
		Assert.assertFalse(range_1021_1031.adjoins(range_1019_1021));

	}

	@Test
	public void testListCreation() {
	    final DateRangeList dr1 = DateRangeList.of(range_from_1019, range_thru_1021);
        Assert.assertTrue(dr1 == DateRangePak.ALLLIST);
	}


    @Test
    public void testMath01() {
        Assert.assertTrue(range_from_1019.isBoundedStart());
        Assert.assertFalse(range_thru_1021.isBoundedStart());
        Assert.assertFalse(DateRangePak.ALL.isBoundedStart());
    }

	@Test
	public void testUnion() {
        Assert.assertTrue(range_thru_1021.union(range_from_1019) == DateRangePak.ALLLIST);
        Assert.assertTrue(range_from_1019.union(range_thru_1021) == DateRangePak.ALLLIST);

        DateRangeList expList01 = DateRangeList.of(DateRange.of(date_1021, date_1102));
        Assert.assertEquals(range_1021_1031.union(range_1025_1102), expList01);
        Assert.assertEquals(range_1025_1102.union(range_1021_1031), expList01);

        DateRangeList expList02 = DateRangeList.of(range_thru_1021, range_1025_1102);
        Assert.assertEquals(range_thru_1021.union(range_1025_1102), expList02);
        Assert.assertEquals(range_1025_1102.union(range_thru_1021), expList02);

        DateRangeList expList03 = DateRangeList.of(range_1019_1021, range_1025_1102);
        Assert.assertEquals(range_1025_1102.union(range_1019_1021), expList03);
        Assert.assertEquals(range_1019_1021.union(range_1025_1102), expList03);

        // test adjoining unions
        DateRangeList expList04 = DateRangeList.of(DateRange.of(date_1019, date_1031));
        Assert.assertEquals(range_1022_1031.union(range_1019_1021), expList04);
        Assert.assertEquals(range_1019_1021.union(range_1022_1031), expList04);

        DateRangeList expList05 = DateRangeList.of(DateRange.ofOpenStart(date_1031));
        Assert.assertEquals(range_1022_1031.union(range_thru_1021), expList05);
        Assert.assertEquals(range_thru_1021.union(range_1022_1031), expList05);

	}


	@Test
	public void testX() {

//        final ARangeList<? super LocalDate> unTwo = range_1025_1102.union(range_thru_1021);

        ARangeList<? super LocalDate> expList02 = DateRangeList.of(range_thru_1021, range_1025_1102);

        final ARangeList<? super LocalDate> unOne = range_thru_1021.union(range_1025_1102);
        Assert.assertEquals(unOne, expList02);

//        Assert.assertEquals(unTwo, expList02);
	}

	private void validateDateRangeState(DateRange dr, Optional<LocalDate> start, Optional<LocalDate> finish,
			long nDays)
	{
		Assert.assertTrue(dateRangeEquals(dr, start, finish));
		Assert.assertEquals(nDays, dr.size());

	}

	boolean dateRangeEquals(DateRange dr, Optional<LocalDate> startDate, Optional<LocalDate> finishDate) {
		final boolean result = ((startDate.isPresent() && dr.isBoundedStart() && dr.start().equals(startDate.get()))
				                || (!startDate.isPresent() && !dr.isBoundedStart()))
				            && ((finishDate.isPresent() && dr.isBoundedFinish() && dr.finish().equals(finishDate.get()))
				                || (!finishDate.isPresent() && !dr.isBoundedFinish()));
		return result;

	}
}
