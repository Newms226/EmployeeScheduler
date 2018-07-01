package time;

public enum Availability_Status {
	AVAILABLE            (true),
	STRICTLY_AVAILABLE   (true),
	OUTSIDE_AVAILABILITY (false),
	SCHEDULED            (false);
	
	public final boolean SCHEDULEABLE;
	
	private Availability_Status(boolean schedulable) {
		this.SCHEDULEABLE = schedulable;
	}
}