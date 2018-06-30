package time;

import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalField;
import java.time.temporal.TemporalUnit;

public abstract class AbstractTimeBasedInterval <INTERVAL extends TimeBasedInterval<INTERVAL, UNIT>, UNIT extends Temporal & Comparable<UNIT>>
				extends AbstractInterval<INTERVAL, UNIT> implements TimeBasedInterval<INTERVAL, UNIT> {
	private static final long serialVersionUID = 5711826628888513978L;

	public static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mma");
	
	final INTERVAL previous, next;
	
	protected AbstractTimeBasedInterval(Interval_SF statusFlag, UNIT start, UNIT end) {
		this(null, statusFlag, start, end, null);
	}
	
	protected AbstractTimeBasedInterval(INTERVAL previous, Interval_SF statusFlag, UNIT start, UNIT end, INTERVAL next) {
		super(statusFlag, start, end);
		this.previous = previous;
		this.next = next;
	}
	
	protected AbstractTimeBasedInterval(INTERVAL previous, INTERVAL current, INTERVAL next) {
		this(previous, current.getStatusFlag(), current.getStart(), current.getEnd(), next);
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
	
	@Override
	public boolean isLinked() {
		return next != null || previous != null;
	}

	@Override
	public INTERVAL getNext() {
		return next;
	}

	@Override
	public INTERVAL getPrevious() {
		return previous;
	}
	
	@Override
	public long getMinutes() {
		return super.get(ChronoUnit.MINUTES);
	}
	
	@Override
	public DateTimeFormatter getFormatter() {
		return formatter;
	}
	
	@Override
	public String toString() {
		return formatter.format(start) + " - " + formatter.format(end);
	}
}
