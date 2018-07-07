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
import java.time.temporal.UnsupportedTemporalTypeException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.omg.CORBA.INTERNAL;

import driver.Driver;
import jdk.nashorn.internal.ir.annotations.Immutable;

public interface Interval <INTERVAL extends Interval<INTERVAL>> 
					extends TemporalAmount, Comparable<INTERVAL>, Serializable {
	
	static final Logger log = Driver.timeLog;
	
	public static <I extends Interval<I>> boolean validateStatusFlag(String callingMethod, Availability_Status expectedFlag, I interval) {
		return validateStatusFlag(callingMethod, expectedFlag, interval.getStatusFlag(), log);
	}
	
	public static <I extends Interval<I>> boolean validateStatusFlag(String callingMethod, Availability_Status expectedFlag, I interval, Logger log) {
		return validateStatusFlag(callingMethod, expectedFlag, interval.getStatusFlag(), log);
	}
	
	public static <I extends Interval<I>> boolean validateStatusFlag(String callingMethod, Availability_Status expectedFlag, Availability_Status actualFlag, Logger log) {
		log.entering("INTERFACE: Interval", callingMethod + ".validateStatusFlag(" + expectedFlag + ", " + actualFlag + ", " + log.getName() + ")");
		if (actualFlag == expectedFlag) {
			log.finer("PASS");
			return true;
		} else {
			log.warning("FAIL: validateStatusFlag(" + expectedFlag + ", " + actualFlag + ", " + log.getName() + ")");
			return false;
		}
	}
	
	public Availability_Status getStatusFlag();
	
	public void setStatusFlag(Availability_Status sf);
	
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
	
	public default boolean conflictsWith(INTERVAL interval) {
		return intersects(interval) || contains(interval);
	}
	
	public default boolean intersects(INTERVAL interval) {
		return intersectsThisOnLeft(interval) || intersectsThisOnRight(interval);
	}
	
	public boolean intersectsThisOnLeft(INTERVAL interval);
	
	public boolean intersectsThisOnRight(INTERVAL interval);
	
	public DateTimeFormatter getFormatter();
	
	public INTERVAL addTo(INTERVAL interval);
	
	public INTERVAL[] subtractFrom(INTERVAL interval);
	
	public INTERVAL condense(INTERVAL end);
	
	@SuppressWarnings("unchecked")
	@Override
	public default Temporal addTo(Temporal temporal) {
		log.warning("Called TemporalAmount.addTo NOT Interval.addTo");
		try {
			return (Temporal) 
					addTo((INTERVAL) temporal);
		} catch (ClassCastException | UnsupportedTemporalTypeException e) {
			log.log(Level.SEVERE, "Called TemporalAmount.addTo(temporal) and failed to execute\n\t" + e.getMessage(), e);
			throw e;
		}
	}

	@Override
	public default Temporal subtractFrom(Temporal temporal) {
		UnsupportedOperationException e = new UnsupportedOperationException("Called TemporalAmount.subtractFrom(temporal)");
		log.log(Level.SEVERE, e.getMessage(), e);
		throw e;
	}
	
}
