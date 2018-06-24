package time;

import java.time.*;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;

public class TimeChunk {
	public final LocalDateTime start, end;
	private Time_Chunk_SF statusFlag;
	
	public static TimeChunk getAlwaysAvailableDuring(LocalDate date) {
		return new TimeChunk(LocalDateTime.of(date, LocalTime.MIN), 
				             LocalDateTime.of(date, LocalTime.MAX));
	}
	
	public TimeChunk(LocalDateTime start, LocalDateTime end) {
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
	
	public void setSatusFlag(Time_Chunk_SF setTo) {
		statusFlag = setTo;
	}
	
	public Time_Chunk_SF getStatusFlag() {
		return statusFlag;
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
		// TODO
		return false;
	}
	
	public boolean isAfter(TimeChunk chunk) {
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
	
	TimeChunk[] markSection(TimeChunk chunk) {
		// TODO
		return null;
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
