package time;

public interface TimeBasedInterval extends Interval, Comparable<TimeBasedInterval> {
	
	public default boolean isTimeSupported() {
		return true;
	}
	
	@Override
	public default boolean isDateSupported() {
		return false;
	}
	
	public long getMinutes();
	
	public boolean contains(TimeBasedInterval interval);
	
	public default boolean isWithin(TimeBasedInterval interval) {
		return interval.contains(this);
	}
	
	public boolean isBefore(TimeBasedInterval temporal);
	
	public boolean isAfter(TimeBasedInterval temporal);
	
	public boolean intersects(TimeBasedInterval interval);
	
	public boolean intersectsThisOnLeft(TimeBasedInterval interval);
	
	public boolean intersectsThisOnRight(TimeBasedInterval interval);
}
