package time;

import java.time.Duration;
import java.time.Period;
import java.time.temporal.Temporal;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

import driver.Driver;
import emp.ClassNotEqualException;

public abstract class AbstractInterval implements Interval {
	
	Temporal start, end;
	Interval_SF statusFlag;
	Interval linked;

	
	public AbstractInterval(Interval_SF statusFlag, Temporal start, Temporal end) {
		this(statusFlag, start, end, null);
	}
	
	public AbstractInterval(Interval_SF statusFlag, Temporal start, Temporal end, Interval linked) {
		ClassNotEqualException.assertEqual(start.getClass(), end.getClass());
		this.start = start;
		this.end = end;
		this.statusFlag = statusFlag;
		this.linked = linked;
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
	public boolean isLinked() {
		return linked != null;
	}
	
	@Override
	public Interval getLinked() {
		return linked;
	}
	
	@Override
	public void setLinked(Interval linkTo) {
		try {
			Objects.requireNonNull(linkTo);
		} catch (NullPointerException e) {
			log.log(Level.SEVERE, e.getMessage(), e);
		}
		
		ClassNotEqualException.assertEqual(linkTo.getClass(), getClass());
		
		this.linked = linkTo;
	} 
	
}
