package time;

import java.time.LocalDateTime;

public interface DayBasedInterval extends Interval {
	
	@Override
	public default boolean isDateSupported() {
		return true;
	}
	
	public LocalDateTime startToLocalDateTime();
	
	public LocalDateTime endToLocalDateTime();
	
	public long getDays();
	
	public boolean contains(DayBasedInterval interval);
	
	public boolean isWithin(DayBasedInterval interval);
	
	public boolean isBefore(DayBasedInterval temporal);
	
	public boolean isAfter(DayBasedInterval temporal);
	
	public boolean intersects(DayBasedInterval interval);
	
	public boolean intersectsThisOnLeft(DayBasedInterval interval);
	
	public boolean intersectsThisOnRight(DayBasedInterval interval);
}
