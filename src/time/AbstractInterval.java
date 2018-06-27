package time;

import java.time.Duration;
import java.time.Period;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalField;
import java.time.temporal.TemporalUnit;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

import driver.Driver;
import emp.ClassNotEqualException;

public abstract class AbstractInterval <INTERVAL extends Interval<INTERVAL, UNIT>, UNIT extends Temporal & Comparable<UNIT>> 
									   implements Interval<INTERVAL, UNIT> {
	private static final long serialVersionUID = 8664569071286330116L;
	
	UNIT start, end;
	Interval_SF statusFlag;
	
	protected AbstractInterval(Interval_SF statusFlag, UNIT start, UNIT end) {
		this.start = start;
		this.end = end;
		this.statusFlag = statusFlag;
	}

	@Override
	public Interval_SF getStatusFlag() {
		return statusFlag;
	}

	@Override
	public void setStatusFlag(Interval_SF sf) {
		this.statusFlag = sf;
	}

	@Override
	public Duration getDuration() {
		return Duration.between(start, end);
	}

	@Override
	public Period getPeriod() {
		return Period.from(this);
	}
	
	@Override
	public UNIT getStart() {
		return start;
	}
	
	@Override
	public UNIT getEnd() {
		return end;
	}
	
	@Override
	public boolean contains(INTERVAL interval) {
		return start.compareTo(interval.getStart()) <= 0
				&& end.compareTo(interval.getEnd()) <= 0;
	}
	
	@Override
	public boolean isWithin(INTERVAL interval) {
		return interval.contains(interval);
	}
	
	@Override
	public boolean isBefore(INTERVAL interval) {
		return end.compareTo(interval.getStart()) <= 0;
	}
	
	@Override
	public boolean isAfter(INTERVAL interval) {
		return interval.getEnd().compareTo(start) <= 0; 
	}
	
	@Override
	public boolean intersectsThisOnLeft(INTERVAL interval) {
		return interval.getStart().compareTo(start) < 0
				&& start.compareTo(interval.getEnd()) < 0;
	}
	
	@Override
	public boolean intersectsThisOnRight(INTERVAL interval) {
		return start.compareTo(interval.getStart()) < 0
				&& interval.getStart().compareTo(end) < 0;
	}
	
	@Override
	public int compareTo(INTERVAL interval) {
		if (isBefore(interval)) return -1;
		if (isAfter(interval)) return 1;
		// TODO: Specifications when this.interval contains interval
		return 0;
	}

	@Override
	public long get(TemporalUnit unit) {
		return start.until(end, unit);
	}

	@Override
	public boolean isSupported(TemporalUnit unit) {
		return start.isSupported(unit);
	}

	@Override
	public boolean isSupported(TemporalField field) {
		return start.isSupported(field);
	}

	@Override
	public boolean isNegative() {
		return start.compareTo(end) > 0;
	}

	@Override
	public boolean isZero() {
		return start.compareTo(end) == 0;
	}
}
