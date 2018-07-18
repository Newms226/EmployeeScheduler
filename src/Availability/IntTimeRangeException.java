package Availability;

import java.util.logging.Level;
import java.util.logging.Logger;

import driver.Driver;

public class IntTimeRangeException extends RuntimeException {
	
	/******************************************************************************
	 *                                                                            *
	 *                                                                            *
	 *                        Static Fields & Methods                             *
	 *                                                                            *
	 *                                                                            *
	 ******************************************************************************/

	private static final long serialVersionUID = -3403159304586660194L;
	private static final Logger log = Driver.errorLog;
	
	public static boolean test(int toTest, int minInclusive, int maxExclusive) {
		if (minInclusive <= toTest && toTest < maxExclusive) return true;
		
		// else
		return false;
	}
	
	public static void assertValidity(String name, int toTest, int minInclusive, int maxExclusive) {
		if (!test(toTest, minInclusive, maxExclusive)) {
			IntTimeRangeException e = new IntTimeRangeException(name, toTest, minInclusive, maxExclusive);
			log.log(Level.SEVERE, e.getMessage(), e);
			throw e;
		}
	}

	/******************************************************************************
	 *                                                                            *
	 *                                                                            *
	 *                        Instance Fields & Methods                           *
	 *                                                                            *
	 *                                                                            *
	 ******************************************************************************/
	
	public final String message;
	
	public IntTimeRangeException(String name, int toTest, int minInclusive, int maxExclusive) {
		this (name + " is out of range: " + toTest + " Range: [" + minInclusive + ", " + maxExclusive + ")");
	}
	
	public IntTimeRangeException(String message) {
		this.message = message;
	}
	
	@Override
	public String getMessage() {
		return message;
	}
}
