package time;

import java.time.chrono.ChronoLocalDate;
import java.time.temporal.Temporal;

public interface DayBasedInterval <INTERVAL extends DayBasedInterval<INTERVAL, UNIT>, UNIT extends Temporal & Comparable<ChronoLocalDate>> 
								  extends Interval<INTERVAL> { 
	
	@Override
	public default boolean isDateSupported() {
		return true;
	}
	
	public long getDays();
	
	public UNIT getStart();
	
	public UNIT getEnd();
}
