package time;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalAmount;
import java.time.temporal.TemporalUnit;
import java.util.List;

interface Interval extends Comparable<Interval>, Temporal, TemporalAmount {
	
	public Interval_SF getStatusFlag();
	
	public void setStatusFlag(Interval_SF sf);
	
	public Duration getDuration();
	
	public Period getPeriod();
	
	public LocalDateTime startToLocalDateTime();
	
	public LocalDateTime endToLocalDateTime();
	
	public boolean spansMultipleDays();
	
	public long getDays();
	
	public long getMinutes();
	
	public boolean contains(TemporalAccessor interval);
	
	public boolean isWithin(TemporalAccessor interval);
	
	public boolean isBefore(TemporalAccessor temporal);
	
	public boolean isAfter(TemporalAccessor temporal);
	
	public boolean intersects(Interval interval);
	
	public boolean intersectsThisOnLeft(Interval interval);
	
	public boolean intersectsThisOnRight(Interval interval);
	
	public boolean equals(Object o);
	
	public boolean isTimeSupported();
	
	public boolean isDateSupported();
	
	public int compareTo(Interval interval);
	
	public String toString();
	
}
