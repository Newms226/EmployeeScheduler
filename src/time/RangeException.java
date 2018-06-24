package time;

import java.time.LocalDateTime;
import java.time.temporal.Temporal;

public class RangeException extends RuntimeException {
	private static final long serialVersionUID = -1340464084294775417L;
	private String message;
	
	public static enum SF {START_AFTER_END}
	
//	public static void assertValidRange(LocalDateTime start, LocalDateTime end) {
//		if (!testIfValidRange(start, end))
//			throw new RangeException(SF.START_AFTER_END, start, end);
//	}
//	
//	public static boolean testIfValidRange(LocalDateTime start, LocalDateTime end) {
//		return start.compareTo(end) < 0;
//	}
	
	public static void assertValidRange(TimeUnit start, TimeUnit end) {
		if (!testIfValidRange(start, end))
			throw new RangeException(SF.START_AFTER_END, start, end);
	}
	
	public static boolean testIfValidRange(TimeUnit start, TimeUnit end) {
		return start.compareTo(end) < 0;
	}
	
	public RangeException(String message) {
		this.message = message;
	}
	
//	public RangeException(SF cause, LocalDateTime start, LocalDateTime end) {
//		message = cause.name() + ": " + start + " > " + end;
//	}
	
	public RangeException(SF cause, TimeUnit start, TimeUnit end) {
		message = cause.name() + ": " + start + " > " + end;
	}
	
	@Override
	public String getMessage() {
		return message;
	}
}
