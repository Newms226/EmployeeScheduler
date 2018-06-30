package time;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalUnit;
import java.util.ArrayList;
import java.util.List;

public class LocalDateInterval extends AbstractDateBasedInterval<LocalDateInterval, LocalDate> {
	private static final long serialVersionUID = -7651643355466848652L;
	
	public static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, MMMM d, uuuu");
	
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

	public LocalDateInterval(Interval_SF statusFlag, LocalDate start, LocalDate end) {
		super(statusFlag, start, end);
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

}
