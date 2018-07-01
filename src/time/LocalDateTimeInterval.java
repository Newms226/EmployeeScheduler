package time;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalUnit;
import java.util.Comparator;
import java.util.List;

public class LocalDateTimeInterval extends AbstractInterval<LocalDateTimeInterval, LocalDateTime> {
	private static final long serialVersionUID = -2674489704779647576L;
	
	public static Comparator<LocalDateTimeInterval> NATURAL_ORDER = (a, b) -> a.compareTo(b);

	protected LocalDateTimeInterval(Availability_Status statusFlag, LocalDateTime start, LocalDateTime end) {
		super(statusFlag, start, end);
		RangeException.assertValidRange(start, end);
	}

	@Override
	public boolean spansMultipleDays() {
		return start.get(ChronoField.DAY_OF_YEAR) == end.get(ChronoField.DAY_OF_YEAR);
	}

	@Override
	public boolean isTimeSupported() {
		return true;
	}

	@Override
	public boolean isDateSupported() {
		return true;
	}

	@Override
	public boolean isNegative() {
		return false;
	}

	@Override
	public boolean isZero() {
		return false;
	}

	@Override
	public boolean contains(LocalDateTimeInterval interval) {
		return start.compareTo(interval.getStart()) <= 0
				&& end.compareTo(interval.getEnd()) <= 0;
	}
	
	@Override
	public boolean isWithin(LocalDateTimeInterval interval) {
		return interval.contains(interval);
	}
	
	@Override
	public boolean isBefore(LocalDateTimeInterval interval) {
		return end.compareTo(interval.getStart()) <= 0;
	}
	
	@Override
	public boolean isAfter(LocalDateTimeInterval interval) {
		return interval.getEnd().compareTo(start) <= 0; 
	}
	
	@Override
	public boolean intersectsThisOnLeft(LocalDateTimeInterval interval) {
		return interval.getStart().compareTo(start) < 0
				&& start.compareTo(interval.getEnd()) < 0;
	}
	
	@Override
	public boolean intersectsThisOnRight(LocalDateTimeInterval interval) {
		return start.compareTo(interval.getStart()) < 0
				&& interval.getStart().compareTo(end) < 0;
	}
	
	@Override
	public int compareTo(LocalDateTimeInterval interval) {
		if (isBefore(interval)) return -1;
		if (isAfter(interval)) return 1;
		// TODO: Specifications when this.interval contains interval
		return 0;
	}

	@Override
	public DateTimeFormatter getFormatter() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<TemporalUnit> getUnits() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Temporal addTo(Temporal temporal) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Temporal subtractFrom(Temporal temporal) {
		// TODO Auto-generated method stub
		return null;
	}
}
