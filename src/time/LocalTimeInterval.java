package time;

import java.time.LocalTime;
import java.time.temporal.TemporalAccessor;
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
	}
	
	public LocalTimeInterval(Interval_SF statusFlag, LocalTimeInterval toClone) {
		super(statusFlag, toClone.start, toClone.end);
	}

	@Override
	public int startToSecondOfDay() {
		return start.toSecondOfDay();
	}

	@Override
	public int endToSecondOfDay() {
		return end.toSecondOfDay();
	}

	@Override
	public boolean isBefore(TemporalAccessor temporal) {
		return end.localTime.isBefore(LocalTime.from(temporal));
	}

	@Override
	public boolean isAfter(TemporalAccessor temporal) {
		return start.localTime.isAfter(LocalTime.from(temporal));
	}

}
