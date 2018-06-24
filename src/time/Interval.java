package time;

import java.time.temporal.TemporalUnit;

public interface Interval extends Comparable<Interval> {
	
	public Interval_SF getStatusFlag();
	
	public void setStatusFlag(Interval_SF sf);
	
	public long getDuration(TemporalUnit unit);
	
	public abstract long getMinutes();
	
	public abstract long getDays();
	
	public boolean contains(Interval interval);
	
	public boolean within(Interval interval);
	
	public boolean intersects(Interval interval);
	
	public boolean intersectsOnLeft(Interval interval);
	
	public boolean intersectsOnRight(Interval interval);
	
	public boolean equals(Object o);
	
	public int compareTo(Interval interval);
	
	public String toString();
}