package time;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class LocalTimeInterval extends AbstractInterval<LocalTime_M> {
	
	public static LocalTimeInterval getAlwaysAvailable() {
		return new LocalTimeInterval(Interval_SF.AVAILABLE, 
				new LocalTime_M(LocalTime.MIN), 
				new LocalTime_M(LocalTime.MAX));
	}
	
	public LocalTimeInterval(Interval_SF statusFlag, LocalTime_M start, LocalTime_M end) {
		super(statusFlag, start, end);
		// TODO Auto-generated constructor stub
	}

	@Override
	public List<? extends Interval> schedule(Interval interval) {
		if (!contains(interval)) {
			throw new IllegalArgumentException(this + " does not contain " + interval);
		}
		
		List<LocalTimeInterval> toReturn = new ArrayList<LocalTimeInterval>();
		toReturn.add(new LocalTimeInterval(Interval_SF.AVIOD, start.minusMinutes(), start));
		toReturn.add(new LocalTimeInterval(Interval_SF.AVIOD, end, end.plusMinutes()));
		toReturn.add(new LocalTimeInterval(Interval_SF.SCHEDULED, start, end));
		
		return toReturn;
	}

}
