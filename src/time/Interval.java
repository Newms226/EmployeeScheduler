package time;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalAmount;
import java.time.temporal.TemporalField;
import java.time.temporal.TemporalUnit;
import java.util.List;
import java.util.logging.Logger;

import driver.Driver;

interface Interval extends TemporalAmount {
	
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
	
	public boolean isLinked();
	
	public Interval getLinked();
	
	public void setLinked(Interval linkTo);
	
	public String toString();
	
	public boolean isSupported(TemporalUnit unit);
	
	public boolean isSupported(TemporalField field);
	
	public List<TemporalField> getFields();
	
	public boolean isNegative();
	
	public boolean isZero();
	
}
