package time;

public interface TimeInterval extends Interval {
	
	public default boolean isTimeSupported() {
		return true;
	}
}
