package time;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class LocalDateInterval extends AbstractDateBasedInterval<LocalDateInterval, LocalDate> {
	private static final long serialVersionUID = -7651643355466848652L;
	
	public static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, MMMM d, uuuu");
	public static Comparator<LocalDateInterval> NATURAL_ORDER = (a, b) -> a.compareTo(b);
	
	public static LocalDateInterval ofSingleton(Availability_Status statusFlag, LocalDate startAndEnd) {
		return new LocalDateInterval(statusFlag, startAndEnd, startAndEnd);
	}
	
	private static final List<TemporalUnit> supportedUnits = new ArrayList<>();
	static {
		supportedUnits.add(ChronoUnit.DAYS);
		supportedUnits.add(ChronoUnit.WEEKS);
		supportedUnits.add(ChronoUnit.MONTHS);
		supportedUnits.add(ChronoUnit.YEARS);
		supportedUnits.add(ChronoUnit.DECADES);
		supportedUnits.add(ChronoUnit.CENTURIES);
		supportedUnits.add(ChronoUnit.MILLENNIA);
		supportedUnits.add(ChronoUnit.ERAS);
	}

	public LocalDateInterval(Availability_Status statusFlag, LocalDate start, LocalDate end) {
		super(statusFlag, start, end);
	}
	
	public List<LocalDate> extractLocalDates(){
		log.entering(this.getClass().getName(), "extractLocalDates");
		List<LocalDate> toReturn = new ArrayList<>();
		LocalDate current = start;
		do {
			toReturn.add(current);
			current = current.plusDays(1);
		} while (current.compareTo(end) <= 0);
		log.finer("RETURN: extractLocalDates:\n\t" + toReturn);
		return toReturn;
	}

	@Override
	public boolean spansMultipleDays() {
		return true;
	}

	@Override
	public boolean isTimeSupported() {
		return false;
	}

	@Override
	public DateTimeFormatter getFormatter() {
		return formatter;
	}

	@Override
	public List<TemporalUnit> getUnits() {
		return supportedUnits;
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

	@Override
	public Period getPeriod() {
		// TODO Auto-generated method stub
		return null;
	}

}
