package Availability;

import java.util.logging.Level;

import driver.Driver;

public class NotEqualException extends RuntimeException {
	private static final long serialVersionUID = 6449766697919869659L;
	
	private final String message;
	
	public static boolean test(Object one, Object two) {
		return one.equals(two);
	}
	
	public static void assertEqual(Object one, Object two) {
		if (!test(one, two)) {
			NotEqualException e = new NotEqualException(one, two);
			Driver.masterLog.log(Level.SEVERE, e.getMessage(), e);
			throw e;
		}
	}
	
	public NotEqualException(String message) {
		this.message = message;
	}
	
	public NotEqualException(Object one, Object two) {
		message = "NOT EQUAL: " + one + " != " + two;
	}
	
	@Override
	public String getMessage() {
		return message;
	}

}
