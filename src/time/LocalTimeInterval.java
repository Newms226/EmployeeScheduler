package time;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalField;
import java.time.temporal.TemporalUnit;
import java.time.temporal.UnsupportedTemporalTypeException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class LocalTimeInterval extends AbstractInterval implements TimeBasedInterval {
	public static ChronoField PERCISION = ChronoField.SECOND_OF_DAY;
	
	public static final List<TemporalField> exceptedChronoFields = new ArrayList<>(3);
	static {
		exceptedChronoFields.add(ChronoField.HOUR_OF_DAY);
		exceptedChronoFields.add(ChronoField.MINUTE_OF_HOUR);
		exceptedChronoFields.add(ChronoField.SECOND_OF_MINUTE);
	}
	
	public static final List<TemporalUnit> exceptedChronoUnits = new ArrayList<>(3);
	static {
		exceptedChronoUnits.add(ChronoUnit.MINUTES);
		exceptedChronoUnits.add(ChronoUnit.HOURS);
		exceptedChronoUnits.add(ChronoUnit.SECONDS);
	}
	
	// @Overrides super variables!
	final LocalTime start, end;
	
	// TODO: This method must be called WHENEVER start > end. Or constructor will throw exception
	public static LocalTimeInterval[] buildLinkedOvernight(Interval_SF statusFlag, LocalTime start, LocalTime end) {
		if (start.get(PERCISION) < end.get(PERCISION)) {
			return new LocalTimeInterval[] {new LocalTimeInterval(statusFlag, start, end)};
		} else if (start.get(PERCISION) > end.get(PERCISION)) {
			LocalTimeInterval firstDay = null, secondDay = null;
			firstDay = new LocalTimeInterval(statusFlag, start, LocalTime.MAX, secondDay);
			secondDay = new LocalTimeInterval(statusFlag, LocalTime.MIN, end, firstDay);
			return new LocalTimeInterval[] {firstDay, secondDay};
		} else {
			IllegalArgumentException e = new IllegalArgumentException("Cannot create a range of size 0. "
					+ "Start: " + start + " End: " + end);
			log.log(Level.SEVERE, e.getMessage(), e);
			throw e;
		}
	}
	
	public static LocalTimeInterval getAlwaysAvailabile() {
		return new LocalTimeInterval(Interval_SF.AVAILABLE, LocalTime.MIN, LocalTime.MAX);
	}

	public LocalTimeInterval(Interval_SF statusFlag, LocalTime start, LocalTime end) {
		this(statusFlag, start, end, null);
	}
	
	public LocalTimeInterval(Interval_SF statusFlag, LocalTime start, LocalTime end, LocalTimeInterval linkedTo) {
		super(statusFlag, start, end, linkedTo);
		
		RangeException.assertValidRange(start, end);
		this.start = start;
		this.end = end;
	}
	
	public LocalDateTime startToLocalDateTime(LocalDate date) {
		return LocalDateTime.of(date, start);
	}
	
	public LocalDateTime endToLocalDateTime(LocalDate date) {
		return LocalDateTime.of(date, end);
	}

	@Override
	public long getMinutes() {
		return start.until(end, ChronoUnit.MINUTES);
	}
	
	@Override
	public boolean contains(TimeBasedInterval interval) {
		if (interval instanceof LocalTimeInterval) {
			LocalTimeInterval that = (LocalTimeInterval) interval;
			return start.compareTo(that.start) <= 0
					&& end.compareTo(that.end) <= 0;
		}
		throw new UnsupportedTemporalTypeException("Called TimeInterval.contains() on an instance not of TimeInterval");
	}

	@Override
	public boolean isSupported(TemporalUnit unit) {
		return exceptedChronoUnits.contains(unit);
	}
	
	@Override
	public boolean isSupported(TemporalField field) {
		return exceptedChronoFields.contains(field);
	}

	@Override
	public long get(TemporalUnit unit) {
		if (isSupported(unit)) {
			return start.until(end, unit);
		}
		throw new UnsupportedTemporalTypeException(getUnsupportedUnitExceptionMessage("get", unit));
	}

	@Override
	public List<TemporalUnit> getUnits() {
		return exceptedChronoUnits;
	}
	
	@Override
	public List<TemporalField> getFields() {
		return exceptedChronoFields;
	}

	@Override
	public Temporal addTo(Temporal temporal) {
		// TODO
		throw new UnsupportedOperationException(getUnsupportedOperationText("addTo"));
	}

	@Override
	public Temporal subtractFrom(Temporal temporal) {
		// TODO
		throw new UnsupportedOperationException(getUnsupportedOperationText("subtractFrom"));
	}

	@Override
	public boolean isBefore(TimeBasedInterval temporal) {
		// TODO
		throw new UnsupportedOperationException(getUnsupportedOperationText("isBefore"));
	}

	@Override
	public boolean isAfter(TimeBasedInterval temporal) {
		// TODO
		throw new UnsupportedOperationException(getUnsupportedOperationText("isAfter"));
	}

	@Override
	public boolean intersects(TimeBasedInterval interval) {
		return intersectsThisOnLeft(interval) || intersectsThisOnRight(interval);
	}

	@Override
	public boolean intersectsThisOnLeft(TimeBasedInterval interval) {
		// TODO
		throw new UnsupportedOperationException(getUnsupportedOperationText("intersectsThisOnLeft"));
	}

	@Override
	public boolean intersectsThisOnRight(TimeBasedInterval interval) {
		// TODO
		throw new UnsupportedOperationException(getUnsupportedOperationText("intersectsThisOnRight"));
	}

	@Override
	public int compareTo(TimeBasedInterval o) {
		// TODO
		throw new UnsupportedOperationException(getUnsupportedOperationText("compareTo"));
	}
	
	private String getUnsupportedUnitExceptionMessage(String methodName, TemporalUnit unit) {
		return "Cannot access unit " + unit.getClass().getName() + " from " + methodName;
	}
	
	private String getUnsupportedOperationText(String methodName) {
		return methodName + " is not currently enabled in " + getClass().getName();
	}

	@Override
	public boolean isNegative() {
		return end.compareTo(start) <= 0;
	}
	
	@Override
	public boolean isZero() {
		return false; // Defined by constructor through RangeException.assertValidRange
	}

	@Override
	public boolean spansMultipleDays() {
		return false; // Defined by constructor through RangeException.assertValidRange
	}

}
