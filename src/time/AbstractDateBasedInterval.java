package time;

import java.time.chrono.ChronoLocalDate;
import java.time.temporal.ChronoUnit;

abstract class AbstractDateBasedInterval <INTERVAL extends AbstractDateBasedInterval<INTERVAL, UNIT>, UNIT extends ChronoLocalDate>
			extends AbstractInterval<INTERVAL, UNIT> implements DateBasedInterval<INTERVAL, UNIT> {
	private static final long serialVersionUID = 8714586373771736374L;

	protected AbstractDateBasedInterval(Availability_Status statusFlag, UNIT start, UNIT end) {
		super(statusFlag, start, end);
	}

	@Override
	public long getDays() {
		return super.get(ChronoUnit.DAYS);
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

	public static void main(String[] args) {
		System.out.println(true && false);
	}
	
}
