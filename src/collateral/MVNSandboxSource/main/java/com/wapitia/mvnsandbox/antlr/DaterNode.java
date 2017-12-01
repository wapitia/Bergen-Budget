package com.wapitia.mvnsandbox.antlr;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class DaterNode {

	public static class BlankNode extends DaterNode {
		
	}

	public static class ErrorNode extends DaterNode {

		final List<DaterParseException> daterParseExceptions;
		
		public ErrorNode(List<DaterParseException> daterParseExceptions) {
			this.daterParseExceptions = daterParseExceptions;
		}
		
		public List<DaterParseException> getParseExceptions() {
			return daterParseExceptions;
		}
		
		@Override
		public String toString() {
			return daterParseExceptions.stream()
					.map(Object::toString)
					.collect(Collectors.joining(","));
		}
		
	}
	
	public static class HardDateNode extends DaterNode {
		
		private final LocalDate localDate;

		public HardDateNode(YearNode ynode, MonthNode mnode, DayOfMonthNode dnode) {
			this(LocalDate.of(ynode.getValue(), mnode.getValue(), dnode.getValue()));
		}
		
		protected HardDateNode(LocalDate date) {
			this.localDate = date;
		}

		public LocalDate getDate() {
			return localDate;
		}

		public HardDateNode plusDays(int daysToAdd) {
			LocalDate plusDate = localDate.plusDays(daysToAdd);
			return new HardDateNode(plusDate);
		}

	}

	public static class DateRangeNode extends DaterNode {
		
		private final LocalDate startDate;
		private final boolean closedStart;
		private final LocalDate finishDate;
		private final boolean closedFinish;
		
		public static DateRangeNode makeClosedRange(HardDateNode startDate, HardDateNode finishDate) {
			return new DateRangeNode(startDate, true, finishDate, true);
		}

		public static DateRangeNode makeOpenEnd(HardDateNode startDate) {
			return new DateRangeNode(startDate, true, null, false);
		}
		
		public static DateRangeNode makeOpenStart(HardDateNode finishDate) {
			return new DateRangeNode(null, false, finishDate, true);
		}
		
		public DateRangeNode(HardDateNode startDate, boolean closedStart, HardDateNode finishDate, boolean closedFinish) {
			this.startDate = Optional.<HardDateNode> ofNullable(startDate).map(HardDateNode::getDate).orElse(null);
			this.closedStart = closedStart;
			this.finishDate = Optional.<HardDateNode> ofNullable(finishDate).map(HardDateNode::getDate).orElse(null);
			this.closedFinish = closedFinish;
		}

		public LocalDate getFromDate() {
			return startDate;
		}

		public LocalDate getToDate() {
			return finishDate;
		}
		
		public boolean isClosedStart() {
			return closedStart;
		}

		public boolean isClosedFinish() {
			return closedFinish;
		}

		public String toString() {
			final String result;
			if (closedStart) {
				if (closedFinish) {
					result = String.format("Closed date range from %s through %s", startDate, finishDate);
				}
				else {
					result = String.format("Half-open date range starting from %s", startDate); 
				}
			}
			else {
				if (closedFinish) {
					result = String.format("Half-open date range finishing on %s", finishDate); 
				}
				else {
					result = String.format("Open-ended date range"); 
				}
			}
			return result;
		}
	}
	
	public static class ValueNode<V> extends DaterNode {
		private final V value; 

		public ValueNode(V value) {
			this.value = value;
		}

		public V getValue() {
			return value;
		}
	}
	
	public static class YearNode extends ValueNode<Integer> {

		public YearNode(Integer yearNumber) {
			super(yearNumber);
		}
	}

	public static class MonthNode extends ValueNode<Integer> {

		public MonthNode(Integer monthNumber) {
			super(monthNumber);
		}
	}
	
	public static class DayOfMonthNode extends ValueNode<Integer> {

		public DayOfMonthNode(Integer dayOfMonth) {
			super(dayOfMonth);
		}
	}
}
