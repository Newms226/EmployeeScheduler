package time;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Period;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class TimeInterval implements Interval {
	public static final Comparator<TimeInterval> NATURAL_ORDER = (a, b) -> a.compareTo(b);
	
	public final LocalDateTime start, end;
	protected Interval_SF statusFlag;
	
	public TimeInterval(Interval_SF statusFlag, TemporalAccessor start, TemporalAccessor end) {
		this.start = LocalDateTime.from(start);
		this.end = LocalDateTime.from(end);
		RangeException.assertValidRange(this.start, this.end);
		this.statusFlag = statusFlag;
	}
	
	@Override
	public Interval_SF getStatusFlag() {
		return statusFlag;
	}

	@Override
	public void setStatusFlag(Interval_SF sf) {
		statusFlag = sf;
	}

	@Override
	public long getDurationAsUnit(TemporalUnit unit) {
		return start.until(end, unit);
	}
	
	@Override
	public Duration getDuration() {
		return Duration.between(start, end);
	}
	
	@Override
	public boolean spansOvernight() {
		return start.get(ChronoField.DAY_OF_WEEK) != end.get(ChronoField.DAY_OF_WEEK)
				&& start.until(end, ChronoUnit.DAYS) < 2;
	}
	
	public DayOfWeek getDayOfWeek() {
		return start.getDayOfWeek();
	}
	
	public TimeInterval[] splitByDay() {
		ArrayList<TimeInterval> list = null;
		
		LocalDate startDate = start.toLocalDate(),
				  endDate = end.toLocalDate();
		
		if (startDate.compareTo(endDate) == 0) { // Same day
			return new TimeInterval[] {this}; 
		} else if (startDate.compareTo(endDate.minusDays(1)) == 0) { // Overnight
			return new TimeInterval[] {
					new TimeInterval(statusFlag, start, LocalDateTime.of(startDate, LocalTime.MAX)),
					new TimeInterval(statusFlag, LocalDateTime.of(endDate, LocalTime.MIDNIGHT), end)
			};
		}
		
		// else:
		list = new ArrayList<>();
		list.add(new TimeInterval(statusFlag, start, LocalDateTime.of(startDate, LocalTime.MAX)));
		
		LocalDate workingDate = startDate.plusDays(1);
		while (workingDate.compareTo(endDate) == 0) {
			list.add(new TimeInterval(statusFlag, 
					LocalDateTime.of(workingDate, LocalTime.MIN),
					LocalDateTime.of(workingDate, LocalTime.MAX)));
			workingDate = startDate.plusDays(1);
		} 
		list.add(new TimeInterval(statusFlag, LocalDateTime.of(endDate, LocalTime.MIDNIGHT), end));
		
		return list.toArray(new TimeInterval[] {});
	}

	@Override
	public long getMinutes() {
		return start.until(end, ChronoUnit.MINUTES);
	}

	@Override
	public long getDays() {
		return start.until(end, ChronoUnit.DAYS);
	}

	@Override
	public boolean contains(Interval interval) {
		return start.compareTo(interval.startToLocalDateTime()) <= 0
				&& interval.endToLocalDateTime().compareTo(end) <= 0;
	}

	@Override
	public boolean isWithin(Interval interval) {
		return interval.contains(this);
	}
	
	@Override
	public boolean isAfter(Interval interval) {
		return interval.endToLocalDateTime().compareTo(start) <= 0;
	}
	
	@Override
	public boolean isBefore(Interval interval) {
		return end.compareTo(interval.startToLocalDateTime()) <= 0;
	}

	@Override
	public boolean intersects(Interval interval) {
		return intersectsThisOnLeft(interval) || intersectsThisOnRight(interval);
	}

	@Override
	public boolean intersectsThisOnLeft(Interval interval) {
		return interval.startToLocalDateTime().compareTo(start) < 0
				&& start.compareTo(interval.endToLocalDateTime()) < 0;
	}

	@Override
	public boolean intersectsThisOnRight(Interval interval) {
		return start.compareTo(interval.startToLocalDateTime()) < 0
				&& interval.startToLocalDateTime().compareTo(end) < 0;
	}

	@Override
	public int compareTo(Interval interval) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o == this) return true;
		if (o == null) return false;
		
		if (!this.getClass().equals(o.getClass())) return false;
		
		TimeInterval that = (TimeInterval) o; 
		
		if (start.compareTo(that.start) != 0) return false;
		if (end.compareTo(that.end) != 0) return false;
		if (statusFlag.compareTo(that.statusFlag) != 0) return false;
		
		return true;
	}
	
	public String toString() {
		return start + " - " + end;
	}

	@Override
	public boolean isBefore(TemporalAccessor temporal) {
		return end.compareTo(LocalDateTime.from(temporal)) <= 0;
	}

	@Override
	public boolean isAfter(TemporalAccessor temporal) {
		return LocalDateTime.from(temporal).compareTo(start) <= 0;
	}

	@Override
	public Period getPeriod() {
		throw new UnsupportedOperationException("getPeriod is not a valid option. "
				+ "TimeInterval is time-based, not date based. ");
	}
	
	@Override
	public LocalDateTime startToLocalDateTime() {
		return start;
	}

	@Override
	public LocalDateTime endToLocalDateTime() {
		return end;
	}
	
	private static void testOvernight() {
		TimeInterval interval = new TimeInterval(Interval_SF.AVAILABLE,
				LocalDateTime.of(2018, 2, 2, 20, 0),
				LocalDateTime.of(2018, 2, 9, 2, 0));
		System.out.println("EXPECT FALSE: " + interval.spansOvernight());
		
		TimeInterval interval2 = new TimeInterval(Interval_SF.AVAILABLE,
				LocalDateTime.of(2018, 2, 2, 20, 0),
				LocalDateTime.of(2018, 2, 3, 2, 0));
		System.out.println("EXTPECT TRUE: " + interval2.spansOvernight());
	}
	
	private static void testGetDurationAsUnit() {
		TimeInterval interval = new TimeInterval(Interval_SF.AVAILABLE,
				LocalDateTime.of(2018, 2, 2, 20, 0),
				LocalDateTime.of(2018, 2, 4, 2, 0));
		System.out.println("~6: " + interval.getDurationAsUnit(ChronoUnit.DAYS));
	}
	
	private static void testLocalTimeUnit() {
		LocalDate one = LocalDateTime.of(2018, 2, 2, 20, 0).toLocalDate();
		LocalDate two = LocalDateTime.of(2018, 2, 4, 2, 0).toLocalDate();
		System.out.println(one + "\n" + two + "\n3: " + one.until(two, ChronoUnit.DAYS));
	}
	
	public static void main(String[] args ) {
		testLocalTimeUnit();
	}

	

}
