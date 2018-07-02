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
	
	public static Availability_Status getInverse(Availability_Status statusFlag) {
		if (statusFlag == AVAILABLE || statusFlag == STRICTLY_AVAILABLE) {
			return SCHEDULED;
		}
		if (statusFlag == SCHEDULED || statusFlag == OUTSIDE_AVAILABILITY) {
			return AVAILABLE;
		}
		throw new UnsupportedOperationException("Attempted to get inverse from " + statusFlag + " when none was present");
	}
}