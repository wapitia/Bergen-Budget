package com.wapitia.dates;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Chronologically ordered list of DateRanges
 */
public class DateRangeList {
	
	public static DateRangeList ALL = new DateRangeList(Collections.<DateRange> singletonList(DateRange.ALL)) {
		
	};
	
	public static DateRangeList NONE = new DateRangeList(Collections.<DateRange> emptyList()) {
		
		@Override
		public DateRange getFirstRange() {
			throw new IndexOutOfBoundsException();
		}
		
		@Override
		public DateRange getLastRange() {
			throw new IndexOutOfBoundsException();
		}
		
	};


	public static DateRangeList of(DateRange ... drs) {
		return new DateRangeList(Arrays.<DateRange> asList(drs));
	}
	
	private final List<DateRange> ranges;
	
	public List<DateRange> getDateRanges() {
		return ranges;
	}

	public boolean isBoundedStart() {
		// TODO
		return true;
	}

	public boolean isBoundedEnd() {
		// TODO
		return true;
	}
	
	public DateRangeList or(DateRangeList dr) {
		// TODO
		return null;
	}
	
	
	public DateRangeList intersect(DateRangeList dr) {
		// TODO
		return null;
	}
	
	
	public DateRangeList exclude(DateRangeList dr) {
		// TODO
		return null;
	}
	
	
	public DateRangeList intersect(DateRange dr) {
		// TODO
		return null;
	}
	
	public DateRangeList exclude(DateRange dr) {
		// TODO
		return null;
	}
	
	public DateRangeList not() {
		// TODO
		return null;
	}
	
	
	public long days() {
		// TODO
		return Long.MAX_VALUE;
	}
	
	public boolean isBounded() {
		// TODO
		return true;
	}
	
	
	public boolean contains(DateRangeList drs) {
		// TODO
		return true;
	}
	
	public boolean isContainedBy(DateRangeList drs) {
		// TODO
		return true;
	}
	
	public boolean contains(DateRange dr) {
		// TODO
		return true;
	}
	
	public boolean isContainedBy(DateRange dr) {
		// TODO
		return true;
	}
	
	
	public boolean startsBefore(DateRangeList dr) {
		// TODO
		return true;
	}
	
	public boolean isUnbounded() {
		// TODO
		return true;
	}
	
	public boolean endsAfter(DateRangeList dr) {
		// TODO
		return true;
	}
	
	public boolean startsBefore(DateRange dr) {
		// TODO
		return true;
	}
	
	public boolean endsAfter(DateRange dr) {
		// TODO
		return true;
	}
	
	public boolean isEqual(DateRangeList dr) {
		
		// TODO
		return true;
	}
	
	/**
	 * @throws NoSuchElementException if start not bounded
	 */
	public LocalDate startDate() {
		// TODO
		return null;
	}
	
	/**
	 * @throws NoSuchElementException if end not bounded
	 */
	public LocalDate endDate() {
		// TODO
		return null;
	}
	
	public boolean equals(Object o) {
		final boolean result = (o == this) || ( (o instanceof DateRangeList) && ((DateRangeList)o).isEqual(this) );
		return result;
	}
	
	public DateRange getFirstRange() {
		return ranges.get(0);
	}
	
	public DateRange getLastRange() {
		return ranges.get(ranges.size()-1);
	}
	
	protected DateRangeList(final List<DateRange> ranges) {
		this.ranges = ranges;
	}
	
}
