package time;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalUnit;
import java.util.List;

public interface Interval extends Comparable<Interval> {
	
	public Interval_SF getStatusFlag();
	
	public void setStatusFlag(Interval_SF sf);
	
	public int startToSecondOfDay();
	
	public int endToSecondOfDay();
	
	public long getDuration(TemporalUnit unit);
	
	public abstract long getMinutes();
	
	public abstract long getDays();
	
	public boolean contains(Interval interval);
	
	public abstract boolean isWithin(Interval interval);
	
	public boolean isBefore(Interval interval);
	
	public abstract boolean isBefore(TemporalAccessor tempral);
	
	public boolean isAfter(Interval interval);
	
	public abstract boolean isAfter(TemporalAccessor tempral);
	
	public boolean intersects(Interval interval);
	
	public abstract boolean intersectsThisOnLeft(Interval interval);
	
	public abstract boolean intersectsThisOnRight(Interval interval);
	
	public boolean equals(Object o);
	
	public int compareTo(Interval interval);
	
	public String toString();
	
}
