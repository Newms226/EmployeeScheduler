package time;

import java.time.Period;
import java.time.chrono.ChronoLocalDate;

public interface DateBasedInterval <INTERVAL extends AbstractDateBasedInterval<INTERVAL, UNIT>, UNIT extends ChronoLocalDate> 
								  extends Interval<INTERVAL> { 
	
	@Override
	public default boolean isDateSupported() {
		return true;
	}
	
	public long getDays();
	
	public Period getPeriod();
	
	public boolean contains(UNIT unit);
}
