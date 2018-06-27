package time;

import java.util.logging.Level;
import java.util.logging.Logger;

import driver.Driver;

public class InvalidSubtypeException extends RuntimeException {
	private static final long serialVersionUID = 7677488451849194147L;
	private String message;
	
	public static void logAndThrow(String message) {
		logAndThrow(message, Driver.timeLog);
	}
	
	public static void logAndThrow(String message, Logger log) throws InvalidSubtypeException {
		InvalidSubtypeException e = new InvalidSubtypeException(message);
		log.log(Level.SEVERE, e.getMessage(), e);
		throw e;
	}
	
	public InvalidSubtypeException(String message) {
		this.message = message;
	}
	
	@Override
	public String getMessage() {
		return message;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
