package time;

import java.time.chrono.ChronoLocalDate;
import java.time.temporal.Temporal;

public interface DayBasedInterval <INTERVAL extends AbstractDateBasedInterval<INTERVAL, UNIT>, UNIT extends ChronoLocalDate> 
								  extends Interval<INTERVAL> { 
	
	@Override
	public default boolean isDateSupported() {
		return true;
	}
	
	public long getDays();
	
	public boolean contains(UNIT unit);
}
