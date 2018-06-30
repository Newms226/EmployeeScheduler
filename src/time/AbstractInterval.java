package time;

import java.time.Duration;
import java.time.Period;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalField;
import java.time.temporal.TemporalUnit;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

import driver.Driver;
import emp.ClassNotEqualException;

public abstract class AbstractInterval <INTERVAL extends Interval<INTERVAL>, UNIT extends Temporal & Comparable<?>>
									   implements Interval<INTERVAL> {
	private static final long serialVersionUID = 8664569071286330116L;
	
	UNIT start, end;
	Interval_SF statusFlag;
	
	protected AbstractInterval(Interval_SF statusFlag, UNIT start, UNIT end) {
		this.start = start;
		this.end = end;
		this.statusFlag = statusFlag;
	}

	@Override
	public Interval_SF getStatusFlag() {
		return statusFlag;
	}

	@Override
	public void setStatusFlag(Interval_SF sf) {
		this.statusFlag = sf;
	}

	@Override
	public Duration getDuration() {
		return Duration.between(start, end);
	}

	@Override
	public Period getPeriod() {
		return Period.from(this);
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
}
