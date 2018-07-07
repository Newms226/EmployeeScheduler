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
		availability = new byte[AvailabilityStatus.WEEK_DAY_COUNT]
				               [AvailabilityStatus.TOTAL_HOURS]
				            	   [AvailabilityStatus.TOTAL_MINUTES];
	}
	
	private boolean query(byte status, TimeChunk chunk) {
		if (chunk.isLinked()) {
			TimeChunk link = chunk.link;
			return query(status, chunk.day, chunk.hourStart, chunk.hourEnd, chunk.minuteStart, chunk.minuteEnd)
					&& query(status, link.day, link.hourStart, link.hourEnd, link.minuteStart, link.minuteEnd);
		} else {
			return query(status, chunk.day, chunk.hourStart, chunk.hourEnd, chunk.minuteStart, chunk.minuteEnd);
		}
		
	}
	
	private boolean query(byte status,
                          int dayStart,
                          int hourStart, int hourEnd,
                          int minuteStart, int minuteEnd)
	{
		try {
			if (!AvailabilityStatus.isStatusByteValid(status)) {
				throw new IllegalArgumentException(status + " is not a valid status byte. Range: [0, " + AvailabilityStatus.MAX_AVAIL_CONSTANT + ")");
			}
			
			for ( ; hourStart <= hourEnd; hourStart++) {
				for ( ; minuteStart <= minuteEnd; minuteStart++) {
					if (availability[dayStart][hourStart][minuteStart] != status) {
						return false;
					} // test condition
				} // minutes
			} // hours
		} catch (IllegalArgumentException | ArrayIndexOutOfBoundsException e) {
			log.log(Level.SEVERE, e.getMessage(), e);
			return false;
		}
		
		return true;
	}
	
	public boolean inAvailability(TimeChunk chunk) {
		return query(AvailabilityStatus.AVAILABILE, chunk);
	}
	
	public boolean inSTRICTAvailability(TimeChunk chunk) {
		return query(AvailabilityStatus.STRICTLY_AVAILABLE, chunk);
	}
	
	public boolean inUNAvailability(TimeChunk chunk) {
		return query(AvailabilityStatus.NOT_AVAILABLE, chunk);
	}
	
	private boolean set(String methodName, byte toSetTo, byte[] errorStatuses, TimeChunk chunk) {
		if (chunk.isLinked()) {
			TimeChunk link = chunk.link;
			return set(methodName, toSetTo, errorStatuses, chunk.day, chunk.hourStart, chunk.minuteStart, chunk.hourEnd, chunk.minuteEnd)
					&& set(methodName, toSetTo, errorStatuses, link.day, link.hourStart, link.minuteStart, link.hourEnd, link.minuteEnd);
		} else {
			return set(methodName, toSetTo, errorStatuses, chunk.day, chunk.hourStart, chunk.minuteStart, chunk.hourEnd, chunk.minuteEnd);
		}
	}
	
	private boolean set(String methodName,
			            byte statusToSetTo, byte[] errorStatuses,
			            int day,
			            int hourStart, int minuteStart,
			            int hourEnd, int minuteEnd) 
	{
		for ( ; hourStart <= hourEnd; hourStart++) {
			for ( ; minuteStart <= minuteEnd; minuteStart++) {
				for (int i = 0; i < errorStatuses.length; i++) {
					if (availability[day][hourStart][minuteStart] == errorStatuses[i]) {
						log.severe(methodName + " ALERT: availability[" + day + "][" + hourStart + "][" + minuteStart + "]"
								+ " was marked as " + AvailabilityStatus.toString(errorStatuses[i]));
						return false;
					} // error Tester
				} // errors loop
				
				// else
				availability[day][hourStart][minuteStart] = statusToSetTo;
			} // minutes
		} // hours
		
		return true;
	}
	
	public boolean toAvailable(TimeChunk chunk) {
		/*TODO
		 * > Work through whole chunk
		 * > Test >> if (byte != NEVER_AVAIL)
		 *   > set to Avail
		 *   > else >> log Level.SEVERE & do not set
		 */
		return set("toAvailable(" + chunk + ")", AvailabilityStatus.AVAILABILE, new byte[] {AvailabilityStatus.NEVER_AVAILABLE}, chunk);
		
		
//		if (chunk.isLinked()) {
//			TimeChunk link = chunk.link;
//			return toAvailable(chunk.day, chunk.hourStart, chunk.minuteStart, chunk.hourEnd, chunk.minuteEnd)
//					&& toAvailable(link.day, link.hourStart, link.minuteStart, link.hourEnd, link.minuteEnd);
//		} else {
//			return toAvailable(chunk.day, chunk.hourStart, chunk.minuteStart, chunk.hourEnd, chunk.minuteEnd);
//		}
	}
	
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
		return false;
	}
	
	public boolean toSTRICTAvailabilityBACKWARDS(TimeChunk setToUnavail) {
		/*TODO
		 * Backwards & Forwards methods
		 * > while (i = start; i >= end; i--)
		 *   > if (i == Available) >> SET TO Strict
		 *   > else break >> log a failure to set whole limit >>> Level.warning
		 */
		return false;
	}
	
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
		if (!(toSTRICTAvailabilityBACKWARDS(chunk) | toSTRICTAvailabilityFORWARDS(chunk))) {
			return false;
		}
		
		return true;
	}
	
	public boolean toUNavailableIGNORE_STRICT(TimeChunk chunk) {
		if (!set("toUNAvailable(" + chunk + ")", AvailabilityStatus.NOT_AVAILABLE, new byte[] {AvailabilityStatus.NOT_AVAILABLE}, chunk)) {
			return false;
		}
		
		// else
		if (!(toSTRICTAvailabilityBACKWARDS(chunk) | toSTRICTAvailabilityFORWARDS(chunk))) {
			return false;
		}
		
		return true;
	}
	
	public static void main(String[] args) {
		WeekAvailability testString = new WeekAvailability();
		System.out.println(AvailabilityStatus.toString(testString.availability, "Week Avail"));
	}
	
}
