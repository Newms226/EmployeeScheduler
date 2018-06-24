package time;

import java.time.LocalDateTime;
import java.time.temporal.Temporal;
import java.util.logging.Level;

import driver.Driver;

public class RangeException extends RuntimeException {
	private static final long serialVersionUID = -1340464084294775417L;
	private String message;

	
	public RangeException(String message) {
		this.message = message;
	}
	
	public static <T extends Temporal & Comparable<T>> boolean test(T start, T end) {
		return start.compareTo(end) < 0;
	}
	
	public static <T extends Temporal & Comparable<T>> void assertValidRange(T start, T end) {
		if (!test(start, end)) {
			RangeException e = new RangeException(start, end);
			Driver.masterLog.log(Level.SEVERE, e.getMessage(), e);
			throw e;
		}
	}
	
	public RangeException(Object start, Object end) {
		message = "INVALID RANGE. Must be start < end. CURRENTLY:" + start + " > " + end;
	}
	
	@Override
	public String getMessage() {
		return message;
	}
	
//	public RangeException(SF cause, LocalDateTime start, LocalDateTime end) {
//	message = cause.name() + ": " + start + " > " + end;
//}
//	
//	public static enum SF {START_AFTER_END}
	
//	public static void assertValidRange(LocalDateTime start, LocalDateTime end) {
//		if (!testIfValidRange(start, end))
//			throw new RangeException(SF.START_AFTER_END, start, end);
//	}
//	
//	public static boolean testIfValidRange(LocalDateTime start, LocalDateTime end) {
//		return start.compareTo(end) < 0;
//	}
	
//	public static void assertValidRange(TimeUnit start, TimeUnit end) {
//		if (!testIfValidRange(start, end))
//			throw new RangeException(SF.START_AFTER_END, start, end);
//	}
//	
//	public static boolean testIfValidRange(TimeUnit start, TimeUnit end) {
//		return start.compareTo(end) < 0;
//	}
}
