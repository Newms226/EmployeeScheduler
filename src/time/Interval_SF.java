package time;

public enum Time_Chunk_SF {
	AVAILABLE            (true),
	OUTSIDE_AVAILABILITY (false),
	AVIOD                (true),
	SCHEDULED            (false),
	TIMEOFF              (false);
	
	public final boolean SCHEDULEABLE;
	
	private Time_Chunk_SF(boolean schedulable) {
		this.SCHEDULEABLE = schedulable;
	}
}