package time;

public enum Interval_SF {
	AVAILABLE            (true),
	STRICTLY_AVAILABLE   (true),
	OUTSIDE_AVAILABILITY (false),
	SCHEDULED            (false),
	TIMEOFF              (false);
	
	public final boolean SCHEDULEABLE;
	
	private Interval_SF(boolean schedulable) {
		this.SCHEDULEABLE = schedulable;
	}
}