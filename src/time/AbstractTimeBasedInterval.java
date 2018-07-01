package time;

import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalField;
import java.time.temporal.TemporalUnit;

public abstract class AbstractTimeBasedInterval <INTERVAL extends AbstractTimeBasedInterval<INTERVAL, UNIT>, UNIT extends Temporal & Comparable<UNIT>>
				extends AbstractInterval<INTERVAL, UNIT> implements TimeBasedInterval<INTERVAL, UNIT> {
	private static final long serialVersionUID = 5711826628888513978L;

	public static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mma");
	
	final INTERVAL previous, next;
	
	protected AbstractTimeBasedInterval(Availability_Status statusFlag, UNIT start, UNIT end) {
		this(null, statusFlag, start, end, null);
	}
	
	protected AbstractTimeBasedInterval(INTERVAL previous, Availability_Status statusFlag, UNIT start, UNIT end, INTERVAL next) {
		super(statusFlag, start, end);
		this.previous = previous;
		this.next = next;
	}
	
	protected AbstractTimeBasedInterval(INTERVAL previous, INTERVAL current, INTERVAL next) {
		this(previous, current.getStatusFlag(), current.getStart(), current.getEnd(), next);
	}
	
	@Override
	public boolean contains(INTERVAL interval) {
		log.entering(this.getClass().getName(), "(" + this + ").contains(" + interval + ")");
		boolean toReturn = start.compareTo(interval.getStart()) <= 0
				&& interval.getEnd().compareTo(end) <= 0;
		
		if (toReturn) {
			log.finer("TRUE");
		} else {
			log.finer("FALSE");
		}
		
		return toReturn;
	}
	
	@Override
	public boolean contains(UNIT unit) {
		log.entering(this.getClass().getName(), "(" + this + ").contains(" + unit + ")");
		boolean toReturn = start.compareTo(unit) <= 0
				&& unit.compareTo(end) <= 0;
		
		if (toReturn) {
			log.finer("TRUE");
		} else {
			log.finer("FALSE");
		}
		
		return toReturn;
	}
	
	@Override
	public boolean isWithin(INTERVAL interval) {
		log.entering(this.getClass().getName(), "(" + this + ").isWithin(" + interval + ")");
		boolean toReturn = interval.contains(interval);
		
		if (toReturn) {
			log.finer("TRUE");
		} else {
			log.finer("FALSE");
		}
		
		return toReturn;
	}
	
	@Override
	public boolean isBefore(INTERVAL interval) {
		log.entering(this.getClass().getName(), "(" + this + ").isBefore(" + interval + ")");
		boolean toReturn = end.compareTo(interval.getStart()) <= 0;
		
		if (toReturn) {
			log.finer("TRUE");
		} else {
			log.finer("FALSE");
		}
		
		return toReturn;
	}
	
	@Override
	public boolean isAfter(INTERVAL interval) {
		log.entering(this.getClass().getName(), "(" + this + ").isAfter(" + interval + ")");
		boolean toReturn = interval.getEnd().compareTo(start) <= 0; 
		
		if (toReturn) {
			log.finer("TRUE");
		} else {
			log.finer("FALSE");
		}
		
		return toReturn;
	}
	
	@Override
	public boolean intersectsThisOnLeft(INTERVAL interval) {
		log.entering(this.getClass().getName(), "(" + this + ").intersectsThisOnLeft(" + interval + ")");
		boolean toReturn = interval.getStart().compareTo(start) < 0
				&& start.compareTo(interval.getEnd()) < 0;
		
		if (toReturn) {
			log.finer("TRUE");
		} else {
			log.finer("FALSE");
		}
		
		return toReturn;
	}
	
	@Override
	public boolean intersectsThisOnRight(INTERVAL interval) {
		log.entering(this.getClass().getName(), "(" + this + ").intersectsThisOnRight(" + interval + ")");
		boolean toReturn = start.compareTo(interval.getStart()) < 0
				&& interval.getStart().compareTo(end) < 0;

		if (toReturn) {
			log.finer("TRUE");
		} else {
			log.finer("FALSE");
		}
		
		return toReturn;
	}
	
	@Override
	public int compareTo(INTERVAL interval) {
		log.entering(this.getClass().getName(), "(" + this + ").compareTo(" + interval + ")");
		
		if (isBefore(interval)) { 
			log.finer("Less than");
			return -1;
		}
		if (isAfter(interval)) {
			log.finer("Greater than");
			return 1;
		}
		
		// TODO: Specifications when this.interval contains interval
		log.finer("Equal");
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
