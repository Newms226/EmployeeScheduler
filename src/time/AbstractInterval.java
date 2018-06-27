package time;

import java.time.Duration;
import java.time.Period;
import java.time.temporal.Temporal;

import emp.ClassNotEqualException;

public abstract class AbstractInterval implements Interval {
	Temporal start, end;
	Interval_SF statusFlag;

	
	public AbstractInterval(Interval_SF statusFlag, Temporal start, Temporal end) {
		ClassNotEqualException.assertEqual(start.getClass(), end.getClass());
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
}
