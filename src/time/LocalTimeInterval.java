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
import java.util.Comparator;
import java.util.List;
import java.util.logging.Level;

import restaurant.Restaurant;

public class LocalTimeInterval extends AbstractTimeBasedInterval<LocalTimeInterval, LocalTime> {
	private static final long serialVersionUID = -4438381979147816413L;
	
	public static Comparator<LocalTimeInterval> NATURAL_ORDER = (a, b) -> a.compareTo(b);
	public static ChronoField PERCISION = ChronoField.SECOND_OF_DAY;
	
	public static final List<TemporalField> supportedChronoFields = new ArrayList<>(15);
	static {
		supportedChronoFields.add(ChronoField.NANO_OF_SECOND);
		supportedChronoFields.add(ChronoField.NANO_OF_DAY);
		supportedChronoFields.add(ChronoField.MICRO_OF_SECOND);
		supportedChronoFields.add(ChronoField.MICRO_OF_DAY);
		supportedChronoFields.add(ChronoField.MILLI_OF_SECOND);
		supportedChronoFields.add(ChronoField.MILLI_OF_DAY);
		supportedChronoFields.add(ChronoField.SECOND_OF_MINUTE);
		supportedChronoFields.add(ChronoField.SECOND_OF_DAY);
		supportedChronoFields.add(ChronoField.MINUTE_OF_HOUR);
		supportedChronoFields.add(ChronoField.MINUTE_OF_DAY);
		supportedChronoFields.add(ChronoField.HOUR_OF_AMPM);
		supportedChronoFields.add(ChronoField.CLOCK_HOUR_OF_AMPM);
		supportedChronoFields.add(ChronoField.HOUR_OF_DAY);
		supportedChronoFields.add(ChronoField.CLOCK_HOUR_OF_DAY);
		supportedChronoFields.add(ChronoField.AMPM_OF_DAY);
	}
	
	public static final List<TemporalUnit> supportedChronoUnits = new ArrayList<>(7);
	static {
		supportedChronoUnits.add(ChronoUnit.NANOS);
		supportedChronoUnits.add(ChronoUnit.MICROS);
		supportedChronoUnits.add(ChronoUnit.MILLIS);
		supportedChronoUnits.add(ChronoUnit.SECONDS);
		supportedChronoUnits.add(ChronoUnit.MINUTES);
		supportedChronoUnits.add(ChronoUnit.HOURS);
		supportedChronoUnits.add(ChronoUnit.HALF_DAYS);
	}
	
	// TODO: This method must be called WHENEVER start > end. Or constructor will throw exception
	public static LocalTimeInterval from(Availability_Status statusFlag, LocalTime start, LocalTime end) {
		if (start.get(PERCISION) < end.get(PERCISION)) {
			return new LocalTimeInterval(statusFlag, start, end);
		} else if (start.get(PERCISION) > end.get(PERCISION)) {
			LocalTimeInterval firstDay = null, secondDay = null;
			firstDay = new LocalTimeInterval(null, statusFlag, start, LocalTime.MAX, secondDay);
			secondDay = new LocalTimeInterval(firstDay, statusFlag, LocalTime.MIN, end, null);
			return firstDay;
		} else {
			IllegalArgumentException e = new IllegalArgumentException("Cannot create a range of size 0. "
					+ "Start: " + start + " End: " + end);
			log.log(Level.SEVERE, e.getMessage(), e);
			throw e;
		}
	}
	
	public static LocalTimeInterval getAlwaysAvailabile() {
		return new LocalTimeInterval(Availability_Status.AVAILABLE, LocalTime.MIN, LocalTime.MAX);
	}
	
	public static LocalTimeInterval getAlways(Availability_Status statusFlag) {
		return new LocalTimeInterval(statusFlag, LocalTime.MIN, LocalTime.MAX); 
	}
	
	public static LocalTimeInterval getAlwaysAvailabile(Restaurant restaurant) {
		return new LocalTimeInterval(Availability_Status.AVAILABLE, restaurant.dayStart, restaurant.dayEnd);
	}

	private LocalTimeInterval(Availability_Status statusFlag, LocalTime start, LocalTime end) {
		this(null, statusFlag, start, end, null);
	}
	
	private LocalTimeInterval(LocalTimeInterval previous, Availability_Status statusFlag, LocalTime start, 
			LocalTime end, LocalTimeInterval next) {
		super(previous, statusFlag, start, end, next);
		
		RangeException.assertValidRange(start, end);
		
		this.start = start;
		this.end = end;
	}
	
	private LocalTimeInterval(LocalTimeInterval previous, LocalTimeInterval interval, LocalTimeInterval next) {
		this(previous, interval.statusFlag, interval.start, interval.end, next);
	}
	
	public LocalDateTime startToLocalDateTime(LocalDate date) {
		return LocalDateTime.of(date, start);
	}
	
	public LocalDateTime endToLocalDateTime(LocalDate date) {
		return LocalDateTime.of(date, end);
	}

	@Override
	public LocalTimeInterval linkedTo(LocalTimeInterval previous, LocalTimeInterval next) {
		return new LocalTimeInterval(previous, this, next);
	}

	@Override
	public boolean spansMultipleDays() {
		return false;
	}

	@Override
	public boolean isDateSupported() {
		return isLinked();
	}

	@Override
	public LocalTimeInterval addTo(LocalTimeInterval interval) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public LocalTimeInterval subtractFrom(LocalTimeInterval interval) {
		if (interval.intersectsThisOnLeft(anotherInterval)) {
			return interval.addTo(this);
		} else if (interval.intersectsThisOnRight(anotherInterval)) {
			return interval.addTo(anotherInterval);
		} else if (interval.getStart().equals(anotherInterval.getStart()) 
					&& interval.getEnd().equals(anotherInterval.getEnd())){
			return interval;
		} 
		
		// else
		throw new UnsupportedOperationException(interval + " cannot be combined with " + anotherInterval);
	}

	@Override
	public List<TemporalUnit> getUnits() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public static void main(String[] args) {
		LocalTimeInterval test = LocalTimeInterval.from(Availability_Status.AVAILABLE, LocalTime.MIN, LocalTime.MAX);
	}
	
}
