package Availability;

import java.util.Arrays;
import java.util.function.BooleanSupplier;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;

import driver.Driver;
import sun.security.x509.AVA;

public class WeekAvailability {
	private static final Logger log = Driver.availabilityLog;
	
	private byte[][][] availability;
	
	public static WeekAvailability getWeekAvailability() {
		// TODO 
		return null;
	}
	
	private WeekAvailability() {
		availability = new byte[AvailabilityStatus.TOTAL_DAYS]
				               [AvailabilityStatus.TOTAL_HOURS]
				            	   [AvailabilityStatus.TOTAL_MINUTES];
	}
	
	
	
	public boolean inAvailability(TimeChunk chunk) {
		return query(AvailabilityStatus.AVAILABILE, chunk);
	}
	
	public boolean inUNAvailability(TimeChunk chunk) {
		return query(AvailabilityStatus.NOT_AVAILABLE, chunk);
	}
	
//	public boolean inSTRICTAvailability(TimeChunk chunk) {
//		return query(AvailabilityStatus.STRICTLY_AVAILABLE, chunk);
//	}
	
	public boolean inSTRICTAvailability(TimeChunk chunk) {
		if (chunk.isLinked()) {
			TimeChunk link = chunk.link;
			log.fine(chunk + " is linked from inSTRICTAvailability");
			return inSTRICTAvailability(chunk.day, chunk.hourStart, chunk.hourEnd, chunk.minuteStart, chunk.minuteEnd)
					&& inSTRICTAvailability(link.day, link.hourStart, link.hourEnd, link.minuteStart, link.minuteEnd);
		} else {
			return inSTRICTAvailability(chunk.day, chunk.hourStart, chunk.hourEnd, chunk.minuteStart, chunk.minuteEnd);
		}
		
	}
	
	public boolean inSTRICTAvailability(int dayStart,
                          int hourStart, int hourEnd,
                          int minuteStart, int minuteEnd)
	{
		byte toTest;
		
		try {
			for ( ; hourStart <= hourEnd; hourStart++) {
				for ( ; minuteStart <= minuteEnd; minuteStart++) {
					toTest = availability[dayStart][hourStart][minuteStart];
					if (toTest != AvailabilityStatus.AVAILABILE || toTest != AvailabilityStatus.STRICTLY_AVAILABLE) {
						log.info("FAILED inSTRICTAvailability from" + AvailabilityStatus.toString(toTest) 
								+ " at [" + dayStart + "][" + hourStart + "][" + minuteStart + "]");
						return false;
					} // test condition
				} // minutes
			} // hours
		} catch (IllegalArgumentException | ArrayIndexOutOfBoundsException e) {
			log.log(Level.SEVERE, e.getMessage(), e);
			return false;
		}
		log.fine("Succeded from inSTRICTAvailability));
		return true;
	}
	
	private boolean set(String methodName, byte toSetTo, byte errorStatuses, TimeChunk chunk) {
		if (chunk.isLinked()) {
			TimeChunk link = chunk.link;
			log.finer(chunk + " is linked. Calling linked set...");
			return set(methodName, toSetTo, errorStatuses, chunk.day, chunk.hourStart, chunk.minuteStart, chunk.hourEnd, chunk.minuteEnd)
					&& set(methodName, toSetTo, errorStatuses, link.day, link.hourStart, link.minuteStart, link.hourEnd, link.minuteEnd);
		} else {
			return set(methodName, toSetTo, errorStatuses, chunk.day, chunk.hourStart, chunk.minuteStart, chunk.hourEnd, chunk.minuteEnd);
		}
	}
	
	private boolean set(String methodName,
			            byte statusToSetTo, byte errorStatus,
			            int day,
			            int hourStart, int minuteStart,
			            int hourEnd, int minuteEnd) 
	{
		for ( ; hourStart <= hourEnd; hourStart++) {
			for ( ; minuteStart <= minuteEnd; minuteStart++) {
				if (availability[day][hourStart][minuteStart] == errorStatus) {
					log.severe(methodName + " ALERT: availability[" + day + "][" + hourStart + "][" + minuteStart + "]"
							+ " was marked as " + AvailabilityStatus.toString(errorStatus));
					return false;
				} // error Tester
				
				// else
				availability[day][hourStart][minuteStart] = statusToSetTo;
			} // minutes
		} // hours
		log.finer("Succeded from " + methodName);
		return true;
	}
	
	public boolean toAvailable(TimeChunk chunk) {
		/*TODO
		 * > Work through whole chunk
		 * > Test >> if (byte != NEVER_AVAIL)
		 *   > set to Avail
		 *   > else >> log Level.SEVERE & do not set
		 */
//		return set("toAvailable(" + chunk + ")", AvailabilityStatus.AVAILABILE, AvailabilityStatus.NEVER_AVAILABLE, chunk);
		int d = chunk.day;
		for (int h = chunk.hourStart ; h <= chunk.hourEnd; h++) {
			for (int m = chunk.minuteStart; m <= chunk.minuteEnd; m++) {
				if (availability[d][h][m] == AvailabilityStatus.NEVER_AVAILABLE) {
					log.severe("ALERT: availability[" + d + "][" + h + "][" + m + "] was marked as NEVER Available");
					return false;
				} // error Tester
				
				// else
				availability[d][h][m] = AvailabilityStatus.AVAILABILE;
			} // minutes
		} // hours
		log.finer("Succeded from toAvailable");
		return true;
	}
		
//		if (chunk.isLinked()) {
//			TimeChunk link = chunk.link;
//			return toAvailable(chunk.day, chunk.hourStart, chunk.minuteStart, chunk.hourEnd, chunk.minuteEnd)
//					&& toAvailable(link.day, link.hourStart, link.minuteStart, link.hourEnd, link.minuteEnd);
//		} else {
//			return toAvailable(chunk.day, chunk.hourStart, chunk.minuteStart, chunk.hourEnd, chunk.minuteEnd);
//		}
	
//	private boolean toAvailable(int day,
//			                    int hourStart, int minuteStart,
//			                    int hourEnd, int minuteEnd) 
//	{
//		return set(
//			() -> {
//				if (availability[day][hourStart][minuteStart] == AvailabilityStatus.NEVER_AVAILABLE) return false;
//			    else return true;
//			}, "toAvailable", AvailabilityStatus.AVAILABILE, day, hourStart, minuteStart, hourEnd, minuteEnd);
//	}
	
	public boolean toSTRICTAvailabilityFORWARDS(TimeChunk setToUnavail) {
		/*TODO
		 * Backwards & Forwards methods
		 * > while (i = start; i >= end; i--)
		 *   > if (i == Available) >> SET TO Strict
		 *   > else break >> log a failure to set whole limit
		 */
		int d = setToUnavail.day,
		    h = setToUnavail.hourStart,
		    m = setToUnavail.minuteStart,
		    minutesSet = 0;
		while (minutesSet < AvailabilityStatus.AVOID_MINUTE_COUNT && availability[d][h][m] == AvailabilityStatus.AVAILABILE) {
			availability[d][h][m] = AvailabilityStatus.STRICTLY_AVAILABLE;
			m++;
			
			if (m == AvailabilityStatus.TOTAL_MINUTES) {
				m = 0;
				
				h++;
				if (h == AvailabilityStatus.TOTAL_HOURS) {
					log.warning("Went past the end of the day when trying to set " + setToUnavail + " to strictly available");
					break;
				} // if "reached the end of the day
			} // if "m needs to be reset"
		} // while loop
		    
		if (minutesSet == AvailabilityStatus.AVOID_MINUTE_COUNT) {
			log.fine("Successfully set " + AvailabilityStatus.AVOID_MINUTE_COUNT + " minutes to strictly available"); 
			return true;
		} else {
			log.warning("Failed to set " + AvailabilityStatus.AVOID_MINUTE_COUNT + " minutes to strictly available. Set: " + minutesSet);
			return false;
		}
	}
	
	/**
	 * Method to 
	 * @param chunk The period of time to set to unavailable
	 * @return
	 */
	public boolean toUNavailable(TimeChunk chunk) {
		/*TODO
		 * > Test whole chunk to make sure presently Available || Strict
		 *   > NOT DONE IN THIS METHOD >> Assumed to be done by the calling method. This method just runs 
		 * > set whole chunk to un-available.
		 *   > if (byte = strict) >> log Level.config
		 *   > if (byte = unavailable) >> log Level.severe!
		 *     ? Maybe ask the user if they want to continue?
		 *     ? throw an error?
		 * > Call toStrictAvailability on both sides
		 */
//		return false;
		if (!set("toUNAvailable(" + chunk + ")", AvailabilityStatus.NOT_AVAILABLE, new byte[] {AvailabilityStatus.STRICTLY_AVAILABLE, AvailabilityStatus.NOT_AVAILABLE}, chunk)) {
			return false;
		}
		
		// else
		if (!toSTRICTAvailabilityFORWARDS(chunk)) {
			return false;
		}
		
		return true;
	}
	
	public boolean toUNavailableIGNORE_STRICT(TimeChunk chunk) {
		if (!set("toUNAvailable(" + chunk + ")", AvailabilityStatus.NOT_AVAILABLE, AvailabilityStatus.NOT_AVAILABLE, chunk)) {
			return false;
		}
		
		// else
		if (!toSTRICTAvailabilityFORWARDS(chunk)) {
			return false;
		}
		
		return true;
	}
	
	public static void main(String[] args) {
		WeekAvailability testString = new WeekAvailability();
		System.out.println(AvailabilityStatus.toString(testString.availability, "Week Avail"));
	}
	
//	private boolean query(byte status, TimeChunk chunk) {
//		if (chunk.isLinked()) {
//			TimeChunk link = chunk.link;
//			log.fine(chunk + " is linked in " + AvailabilityStatus.toString(status));
//			return query(status, chunk.day, chunk.hourStart, chunk.hourEnd, chunk.minuteStart, chunk.minuteEnd)
//					&& query(status, link.day, link.hourStart, link.hourEnd, link.minuteStart, link.minuteEnd);
//		} else {
//			return query(status, chunk.day, chunk.hourStart, chunk.hourEnd, chunk.minuteStart, chunk.minuteEnd);
//		}
//		
//	}
//	
//	private boolean query(byte status,
//                          int dayStart,
//                          int hourStart, int hourEnd,
//                          int minuteStart, int minuteEnd)
//	{
//		try {
//			if (!AvailabilityStatus.isStatusByteValid(status)) {
//				throw new IllegalArgumentException(status + " is not a valid status byte. Range: [0, " 
//						+ AvailabilityStatus.MAX_AVAIL_CONSTANT + ")");
//			}
//			
//			for ( ; hourStart <= hourEnd; hourStart++) {
//				for ( ; minuteStart <= minuteEnd; minuteStart++) {
//					if (availability[dayStart][hourStart][minuteStart] != status) {
//						log.info("FAILED query of " + AvailabilityStatus.toString(status) + " from" 
//								+ AvailabilityStatus.toString(availability[dayStart][hourStart][minuteStart]) 
//								+ " at [" + dayStart + "][" + hourStart + "][" + minuteStart + "]");
//						return false;
//					} // test condition
//				} // minutes
//			} // hours
//		} catch (IllegalArgumentException | ArrayIndexOutOfBoundsException e) {
//			log.log(Level.SEVERE, e.getMessage(), e);
//			return false;
//		}
//		log.fine("Succeded from " + AvailabilityStatus.toString(status));
//		return true;
//	}
	
}
