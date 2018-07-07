package time;

import java.time.Duration;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalField;
import java.time.temporal.TemporalUnit;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

import driver.Driver;
import emp.ClassNotEqualException;

public abstract class AbstractInterval <INTERVAL extends AbstractInterval<INTERVAL, UNIT>, UNIT extends Temporal & Comparable<?>>
									   implements Interval<INTERVAL> {
	private static final long serialVersionUID = 8664569071286330116L;
	

	
	
	
	UNIT start, end;
	Availability_Status statusFlag;
	
	protected AbstractInterval(Availability_Status statusFlag, UNIT start, UNIT end) {
		this.start = start;
		this.end = end;
		this.statusFlag = statusFlag;
	}

	@Override
	public Availability_Status getStatusFlag() {
		return statusFlag;
	}

	@Override
	public void setStatusFlag(Availability_Status sf) {
		this.statusFlag = sf;
	}

	@Override
	public long get(TemporalUnit unit) {
		return start.until(end, unit);
	}

	@Override
	public boolean isSupported(TemporalUnit unit) {
		return start.isSupported(unit);
	}

	@Override
	public boolean isSupported(TemporalField field) {
		return start.isSupported(field);
	}
	
	public UNIT getStart() {
		return start;
	}
	
	public UNIT getEnd() {
		return end;
	}
	
	public String toString() {
		return start + " - " + end;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public INTERVAL addTo(INTERVAL otherInterval) {
		if (intersectsThisOnLeft(otherInterval)) {
			return otherInterval.condense((INTERVAL) this);
		} else if (intersectsThisOnRight(otherInterval)) {
			return this.condense(otherInterval);
		} else if (otherInterval.getStart().equals(getStart()) 
					&& otherInterval.getEnd().equals(getEnd())
					&& getStatusFlag() == otherInterval.getStatusFlag()){
			return otherInterval;
		} 
		
		// else
		throw new UnsupportedOperationException(otherInterval + " cannot be combined with " + this);
	}
	
}
