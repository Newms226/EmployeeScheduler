package time;

import java.time.temporal.Temporal;

public interface TimeBasedInterval<INTERVAL extends TimeBasedInterval<INTERVAL, UNIT>, UNIT extends Temporal & Comparable<UNIT>> 
								extends Interval<INTERVAL, UNIT> {
	
	@Override
	public default boolean isTimeSupported() {
		return true;
	}
	
	public long getMinutes();
	
	public boolean isLinked();
	
	public INTERVAL getNext();
	
	public INTERVAL getPrevious();
	
	public default INTERVAL withNext(INTERVAL interval, INTERVAL next) {
		return linkedTo(null, next);
	}
	
	public default INTERVAL withPrevious(INTERVAL previous, INTERVAL interval) {
		return linkedTo(previous, null);
	}
	
	public INTERVAL linkedTo(INTERVAL previous, INTERVAL next);
}