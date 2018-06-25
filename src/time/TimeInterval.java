package time;

import java.time.Duration;
import java.time.Instant;
import java.time.Period;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalUnit;
import java.util.List;

public class TimeInterval implements Interval {
	public final Instant start, end;
	protected Interval_SF statusFlag;
	
	public TimeInterval(Interval_SF statusFlag, TemporalAccessor start, TemporalAccessor end) {
		this.start = Instant.from(start);
		this.end = Instant.from(end);
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
	public boolean spansDays() {
		return start.get(ChronoField.DAY_OF_WEEK) == end.get(ChronoField.DAY_OF_WEEK)
				&& start.until(end, ChronoUnit.DAYS) < 1.5;
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
		return start.toEpochMilli() <= interval.startToEpochMilli()
				&& interval.endToEpochMilli() <= end.toEpochMilli();
	}

	@Override
	public boolean isWithin(Interval interval) {
		return interval.contains(this);
	}
	
	@Override
	public boolean isAfter(Interval interval) {
		return interval.endToEpochMilli() <= start.toEpochMilli();
	}
	
	@Override
	public boolean isBefore(Interval interval) {
		return end.toEpochMilli() <= interval.startToEpochMilli();
	}

	@Override
	public boolean intersects(Interval interval) {
		return intersectsThisOnLeft(interval) || intersectsThisOnRight(interval);
	}

	@Override
	public boolean intersectsThisOnLeft(Interval interval) {
		return interval.startToEpochMilli() < start.toEpochMilli()
				&& start.toEpochMilli() < interval.endToEpochMilli();
	}

	@Override
	public boolean intersectsThisOnRight(Interval interval) {
		return start.toEpochMilli() < interval.startToEpochMilli()
				&& interval.startToEpochMilli() < end.toEpochMilli();
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
	public long startToEpochMilli() {
		return start.toEpochMilli();
	}

	@Override
	public long endToEpochMilli() {
		return end.toEpochMilli();
	}

	@Override
	public boolean isBefore(TemporalAccessor temporal) {
		return end.toEpochMilli() <= Instant.from(temporal).toEpochMilli();
	}

	@Override
	public boolean isAfter(TemporalAccessor temporal) {
		return Instant.from(temporal).toEpochMilli() <= start.toEpochMilli();
	}

	@Override
	public Period getPeriod() {
		throw new UnsupportedOperationException("getPeriod is not a valid option. "
				+ "TimeInterval is time-based, not date based. ");
	}

}
