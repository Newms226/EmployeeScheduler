package driver;
import MyTime.Time;

public class TimeRange {
	public final Time START_TIME,
	                  END_TIME;
	
	public TimeRange(Time start, Time end) {
		START_TIME = start;
		END_TIME = end;
	}
	
	public TimeRange(short hourStart, short minStart, short hourEnd, short minEnd) {
		this(new Time(hourStart, minStart), new Time(hourEnd, minEnd));
	}
	
	public TimeRange(int hourStart, int minStart, int hourEnd, int minEnd) {
		this(new Time(hourStart, minStart), new Time(hourEnd, minEnd));
	}
	
	// TODO: This doesnt take minutes into account - > resolve by maintaining a double of time?
	public boolean insideRange(TimeRange that) {
		if (this.START_TIME.HOUR <= that.START_TIME.HOUR) {
			if (this.END_TIME.HOUR >= that.END_TIME.HOUR) {
				return true;
			}
		}
		return false;
	}
	
	public String toString() {
		return START_TIME.toString() + " - " + END_TIME.toString();
	}
}
