package time;

import java.io.Serializable;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalAmount;
import java.time.temporal.TemporalField;
import java.time.temporal.TemporalUnit;
import java.util.List;
import java.util.logging.Logger;

import org.omg.CORBA.INTERNAL;

import driver.Driver;
import jdk.nashorn.internal.ir.annotations.Immutable;

public interface Interval <INTERVAL extends Interval<INTERVAL>> 
					extends TemporalAmount, Comparable<INTERVAL>, Serializable {
	
	static final Logger log = Driver.timeLog;
	
	public Interval_SF getStatusFlag();
	
	public void setStatusFlag(Interval_SF sf);
	
	public Duration getDuration();
	
	public Period getPeriod();
	
	public long get(TemporalUnit unit);
	
	public boolean spansMultipleDays();
	
	public boolean equals(Object o);
	
	public boolean isTimeSupported();
	
	public boolean isDateSupported();
	
	public String toString();
	
	public boolean isSupported(TemporalUnit unit);
	
	public boolean isSupported(TemporalField field);
	
	public boolean isNegative();
	
	public boolean isZero();
	
	public boolean contains(INTERVAL interval);
	
	public boolean isWithin(INTERVAL interval);
	
	public boolean isBefore(INTERVAL temporal);
	
	public boolean isAfter(INTERVAL temporal);
	
	public default boolean intersects(INTERVAL interval) {
		return intersectsThisOnLeft(interval) || intersectsThisOnRight(interval);
	}
	
	public boolean intersectsThisOnLeft(INTERVAL interval);
	
	public boolean intersectsThisOnRight(INTERVAL interval);
	
	public DateTimeFormatter getFormatter();
	
}
