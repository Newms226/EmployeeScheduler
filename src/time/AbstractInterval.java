package time;

import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.List;

public abstract class AbstractInterval <T extends LocalTime_M> implements Interval {
	public final T start, end;
	protected Interval_SF statusFlag;
	
	public AbstractInterval(Interval_SF statusFlag, T start, T end) {
		RangeException.assertValidRange(start, end);
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
		statusFlag = sf;
	}

	@Override
	public long getDuration(TemporalUnit unit) {
		return start.until(end, unit);
	}

	@Override
	public long getMinutes() {
		return start.until(end, ChronoUnit.MINUTES);
	}

	@Override
	public long getDays() {
		return start.until(end, ChronoUnit.DAYS);
	}

	@Override
	public boolean contains(Interval interval) {
		return start.toSecondOfDay() <= interval.startToSecondOfDay()
				&& interval.endToSecondOfDay() <= end.toSecondOfDay();
	}

	@Override
	public boolean isWithin(Interval interval) {
		return interval.startToSecondOfDay() <= start.toSecondOfDay()
				&& end.toSecondOfDay() <= interval.endToSecondOfDay();
	}
	
	@Override
	public boolean isAfter(Interval interval) {
		return interval.endToSecondOfDay() <= start.toSecondOfDay();
	}
	
	@Override
	public boolean isBefore(Interval interval) {
		return end.toSecondOfDay() <= interval.startToSecondOfDay();
	}

	@Override
	public boolean intersects(Interval interval) {
		return intersectsThisOnLeft(interval) || intersectsThisOnRight(interval);
	}

	@Override
	public boolean intersectsThisOnLeft(Interval interval) {
		return interval.startToSecondOfDay() < start.toSecondOfDay()
				&& start.toSecondOfDay() < interval.endToSecondOfDay();
	}

	@Override
	public boolean intersectsThisOnRight(Interval interval) {
		return start.toSecondOfDay() < interval.startToSecondOfDay()
				&& interval.startToSecondOfDay() < end.toSecondOfDay();
	}

	@Override
	public int compareTo(Interval interval) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public boolean equals(Object o) {
		if (o == this) return true;
		if (o == null) return false;
		
		if (!this.getClass().equals(o.getClass())) return false;
		
		AbstractInterval<T> that = null;
		try {
			that = (AbstractInterval<T>) o;
		} catch (ClassCastException e) {
			return false;
		}
		
		
		if (start.compareTo(that.start) != 0) return false;
		if (end.compareTo(that.end) != 0) return false;
		if (statusFlag.compareTo(that.statusFlag) != 0) return false;
		
		return true;
	}
	
	public String toString() {
		return start + " - " + end;
	}

}
