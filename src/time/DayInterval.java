package time;

public interface DayInterval extends Interval {
	
	public default boolean isDateSupported() {
		return true;
	}
}
