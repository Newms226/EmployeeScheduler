package time;

import java.time.chrono.ChronoLocalDate;
import java.time.temporal.ChronoUnit;

abstract class AbstractDateBasedInterval <INTERVAL extends DayBasedInterval<INTERVAL, UNIT>, UNIT extends ChronoLocalDate>
			extends AbstractInterval<INTERVAL, UNIT> implements DayBasedInterval<INTERVAL, UNIT> {
	private static final long serialVersionUID = 8714586373771736374L;

	protected AbstractDateBasedInterval(Interval_SF statusFlag, UNIT start, UNIT end) {
		super(statusFlag, start, end);
		
		RangeException.assertValidRange(start, end);
	}

	@Override
	public long getDays() {
		return super.get(ChronoUnit.DAYS);
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
	public boolean isNegative() {
		return start.compareTo(end) > 0;
	}

	@Override
	public boolean isZero() {
		return start.compareTo(end) == 0;
	}

}
