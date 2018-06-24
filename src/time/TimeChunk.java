package time;

import java.time.Instant;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;

public class TimeChunk {
	public final LocalTime start, end;
	private SF statusFlag;
	
	public static enum SF {
		OUTSIDE_AVAILABILITY,
		AVIOD,
		SCHEDULED,
		TIMEOFF
	}
	
	public TimeChunk(LocalTime start, LocalTime end) {
		if (start.compareTo(end) < 0) {
			throw new IllegalArgumentException("Cannot create a negative TimeChunk.");
		}
		this.start = start;
		this.end = end;
	}
	
	public long getDuration(TemporalUnit unit) {
		return start.until(end, unit);
	}
	
	public long getMinutes() {
		return start.until(end, ChronoUnit.MINUTES);
	}
	
	public boolean intersects(TimeChunk chunk) {
		/*TODO
		 * 6 cases:
		 * 
		 * 1. chunk < this
		 * 2. chunk > that
		 * 3. chunk completely inside this
		 * 4. this completely inside chunk
		 * 5. chunk enters on left
		 * 6. chunk enters on right
		 */
		if (start.compareTo(chunk.end) > 0) { // Case 1: this.start > chunk.end
			return false;
		} else if (end.compareTo(chunk.start) < 0) { // Case 2: this.end < chunk.end
			return false;
		} else {
			return true;
		}
	}
	
	public boolean isBefore(TimeChunk chunk) {
		return isBefore(chunk.end);
	}
	
	public boolean isBefore(LocalTime instant) {
		// TODO
		return false;
	}
	
	public boolean isAfter(TimeChunk chunk) {
		// TODO
		return false;
	}
	
	public boolean isAfter(LocalTime instant) {
		// TODO
		return false;
	}
	
	public boolean intersectsOnLeft(TimeChunk chunk) {
		// TODO
		return false;
	}
	
	public boolean intersectsOnRight(TimeChunk chunk) {
		// TODO
		return false;
	}
	
	public boolean contains(TimeChunk chunk) {
		// TODO
		return false;
	}
	
	public boolean within(TimeChunk chunk) {
		if (start.compareTo(chunk.start) < 0
				&& chunk.end.compareTo(end) < 0) return true;
		// else
		return false;
	}
	
	public void setSatusFlag(SF setTo) {
		statusFlag = setTo;
	}
	
	public SF getStatusFlag() {
		return statusFlag;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o == this) return true;
		if (o == null) return false;
		
		if (!this.getClass().equals(o.getClass())) return false;
		
		TimeChunk that = (TimeChunk) o;
		
		if (start.compareTo(that.start) != 0) return false;
		if (end.compareTo(that.end) != 0) return false;
		if (statusFlag.compareTo(that.statusFlag) != 0) return false;
		
		return true;
	}
}
