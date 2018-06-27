package time;

import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalField;
import java.time.temporal.TemporalUnit;

public abstract class AbstractTimeBasedInterval <INTERVAL extends TimeBasedInterval<INTERVAL, UNIT>, UNIT extends Temporal & Comparable<UNIT>>
				extends AbstractInterval<INTERVAL, UNIT> implements TimeBasedInterval<INTERVAL, UNIT> {
	private static final long serialVersionUID = 5711826628888513978L;

	public static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mma");
	
	final INTERVAL previous, next;
	
	protected AbstractTimeBasedInterval(Interval_SF statusFlag, UNIT start, UNIT end) {
		this(null, statusFlag, start, end, null);
	}
	
	protected AbstractTimeBasedInterval(INTERVAL previous, Interval_SF statusFlag, UNIT start, UNIT end, INTERVAL next) {
		super(statusFlag, start, end);
		this.previous = previous;
		this.next = next;
	}
	
	protected AbstractTimeBasedInterval(INTERVAL previous, INTERVAL current, INTERVAL next) {
		this(previous, current.getStatusFlag(), current.getStart(), current.getEnd(), next);
	}

	@Override
	public boolean isLinked() {
		return next != null || previous != null;
	}

	@Override
	public boolean isSupported(TemporalUnit unit) {
		return start.isSupported(unit);
	}

	@Override
	public boolean isSupported(TemporalField field) {
		return start.isSupported(field);
	}

	@Override
	public INTERVAL getNext() {
		return next;
	}

	@Override
	public INTERVAL getPrevious() {
		return previous;
	}
	
	@Override
	public long getMinutes() {
		return super.get(ChronoUnit.MINUTES);
	}
	
	@Override
	public DateTimeFormatter getFormatter() {
		return formatter;
	}
	
	@Override
	public String toString() {
		return formatter.format(start) + " - " + formatter.format(end);
	}
}
