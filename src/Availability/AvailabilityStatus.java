package Availability;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import driver.Driver;
import racer.StopWatch;

@SuppressWarnings("unused")
public class AvailabilityStatus {
	private AvailabilityStatus() {} // PREVENT INSTANCE CREATION
	
	private static final Logger log = Driver.availabilityLog;
	
	private enum internalStatus {
		AVAILABILE {
			public String toString() {return "Available";}
		},
        STRICTLY_AVAILABLE {
			public String toString() {return "Strictly Available";}
		},
        NOT_AVAILABLE {
			public String toString() {return "Not Available";}
		},
        NEVER_AVAILABLE {
			public String toString() {return "NEVER Available";}
		},
	}

	public static final byte AVAILABILE = (byte) internalStatus.AVAILABILE.ordinal(),
			                 STRICTLY_AVAILABLE = (byte) internalStatus.STRICTLY_AVAILABLE.ordinal(),
			                 NOT_AVAILABLE = (byte) internalStatus.NOT_AVAILABLE.ordinal(),
			                 NEVER_AVAILABLE = (byte) internalStatus.NEVER_AVAILABLE.ordinal(), 
			                 MAX_AVAIL_CONSTANT = (byte) internalStatus.values().length;
	
	public static final int TOTAL_DAYS = 7,
//			                WEEK_DAY_COUNT = 8, // Include the day after the last day of the week
			                TOTAL_HOURS = 24,
			                MINUTE_CHUNK = 1, // MUST BE AN EVEN DIVISOR OF 60!!
			                TOTAL_MINUTES = 60 / MINUTE_CHUNK, // REMAINDER MUST BE 0!!
			                AVOID_MINUTE_COUNT = 60;
	
	
	static {
		if (MINUTE_CHUNK < 1) {
			throw new IllegalArgumentException("60 / MINUTE_CHUNK (" + MINUTE_CHUNK + ") != 0");
		}
		int toTest = 60 % MINUTE_CHUNK;
		if (toTest != 0) {
			throw new IllegalArgumentException("60 / MINUTE_CHUNK (" + MINUTE_CHUNK + ") != 0");
		}
	}
	
	static boolean fillArray(byte[][][] a, byte status, TimeChunk chunk) {
		if (!chunk.isLinked()) {
			return fillArray(a, status, chunk.day, chunk.hourStart, chunk.hourEnd, chunk.minuteStart, chunk.minuteEnd);
		} else {
			TimeChunk linked = chunk.link;
			return fillArray(a, status, chunk.day, chunk.hourStart, chunk.hourEnd, chunk.minuteStart, chunk.minuteEnd)
					&& fillArray(a, status, linked.day, linked.hourStart, linked.hourEnd, linked.minuteStart, linked.minuteEnd);
		}
	}
	
	private static boolean fillArray(byte[][][] a, byte status,
			                         int dayStart,
			                         int hourStart, int hourEnd,
			                         int minuteStart, int minuteEnd)
	{
		try {
			if (!isStatusByteValid(status)) {
				throw new IllegalArgumentException(status + " is not a valid status byte. Range: [0, " + MAX_AVAIL_CONSTANT + ")");
			}
			
			for ( ; hourStart <= hourEnd; hourStart++) {
				Arrays.fill(a[dayStart][hourStart], minuteStart, minuteEnd + 1, status);
			}
		} catch (IllegalArgumentException | ArrayIndexOutOfBoundsException e) {
			log.log(Level.SEVERE, e.getMessage(), e);
			return false;
		}
		
		return true;
	}
	
	public static boolean isStatusByteValid(byte status) {
		return 0 < status && status <= MAX_AVAIL_CONSTANT;
	}
	
	public static byte valueOf(String status) {
		try {
			return (byte) internalStatus.valueOf(status).ordinal();
		} catch (IllegalArgumentException | NullPointerException e) {
			log.log(Level.SEVERE, e.getMessage(), e);
			return -1;
		}
	}
	
	public static String toString(int status) {
		try {
			return internalStatus.values()[status].toString();
		} catch (ArrayIndexOutOfBoundsException e) {
			log.log(Level.SEVERE, e.getMessage(), e);
			return "NOT A VALID BYTE: " + status;
		}
	}

//	String minutes = "|05 |10 | 15| 20| 25| 30| 35| 40| 45| 50| 55| 60";
	
	/*CALLED WHEN:
	 * - Debugging: Make sure things are working correctly
	 * >> Extracted from Main Schedule:
	 *    - By Server to view their schedule
	 *    - In the daily print out
	 */
	public static String toString(byte[][][] a, String arrayType) {
		StringBuffer buffer = new StringBuffer(arrayType + "\n");
		byte currentByte;
		for (int d = 0; d < TOTAL_DAYS; d++) {
			buffer.append(dayByteToString(d) + "\n");
			for (int h = 0; h < TOTAL_HOURS; h++) {
				buffer.append(" " + hourByteToString(h) + "\n  ");
				for (int m = 0; m < TOTAL_MINUTES; m++) {
					buffer.append(a[d][h][m] + " ");
				} // Minutes
				buffer.append("\n");
			} // Hours
		} // Days
		return buffer.toString();
	}
	
	public static String dayByteToString(int day) {
		switch(day) {
		case 0: return "Sunday";
		case 1: return "Monday";
		case 2: return "Tuesday";
		case 3: return "Wednesday";
		case 4: return "Thursday";
		case 5: return "Friday";
		case 6: return "Saturday";
		default:
			throw new IllegalArgumentException(day + " is not a valid day. Range: [0, " + (TOTAL_DAYS - 1) + "]");
		}
	}
	
	public static String hourByteToString(int hour) {
		switch(hour) {
		case 0: return "12:00a";
		case 1: return "1:00a";
		case 2: return "2:00a";
		case 3: return "3:00a";
		case 4: return "4:00a";
		case 5: return "5:00a";
		case 6: return "6:00a";
		case 7: return "7:00a";
		case 8: return "8:00a";
		case 9: return "9:00a";
		case 10: return "10:00a";
		case 11: return "11:00a";
		case 12: return "12:00p";
		case 13: return "1:00p";
		case 14: return "2:00p";
		case 15: return "3:00p";
		case 16: return "4:00p";
		case 17: return "5:00p";
		case 18: return "6:00p";
		case 19: return "7:00p";
		case 20: return "8:00p";
		case 21: return "9:00p";
		case 22: return "10:00p";
		case 23: return "11:00p";
		default:
			throw new IllegalArgumentException(hour + " is not a valid hour. Range: [0, " + (TOTAL_HOURS - 1) + "]");
		}
	}
	
//	/*TODO
//	 * > Ensure day byte is valid
//	 * > Work through day
//	 * > Find first index of Avail
//	 *   > Mark first byte as beginning location
//	 *   > Once found, continue through array until byte != 3
//	 *     > Mark this byte as the end
//	 *     > Add to the List
//	 * > Continue until at the end of the day
//	 *   > If nothing found, return empty list
//	 *   ? What about scheduling over night? >> what if last byte is a 3?
//	 * > Return list
//	 */
//	public static List<TimeChunk> getScheduledTimes(byte[][][] a, int day){
//		TimeChunk.assertValidity("Day", day, 0, TOTAL_DAYS);
//		List<TimeChunk> toReturn = new ArrayList<>();
//		int minuteStart, minuteEnd, hourStart, hourEnd;
//		for (int h = 0; h < TOTAL_HOURS; h++) {
//			for (int m = 0; m < TOTAL_MINUTES; m++) {
//				if (a[day][h][m] == SCHEDULED) {
//					hourStart = h;
//					minuteStart = m;
//					do {
//						m++;
//					} while (m < TOTAL_MINUTES && a[day][h][m] == SCHEDULED);
//					if (m == TOTAL_MINUTES && h != 23 && a[day][h+1][0] != AVAILABILE) {
//						hourEnd
//					}
//				}
//			}
//		}
//	}
//	
//	public static String toString2(int status) {
//		if (status == 0) {
//			return "Available";
//		} else if (status == 1) {
//			return "Strictly Available";
//		} else if (status == 2) {
//			return "Not Available";
//		} else {
//			throw new IllegalArgumentException();
//		}
//	}
	
	public static void main(String[] args) {
//		long start, end;
//		byte toTest = 2;
//		start = System.nanoTime();
//		AvailabilityStatus.toString(toTest);
//		end = System.nanoTime();
//		System.out.println(StopWatch.nanosecondsToString(end - start));
//		start = System.nanoTime();
//		AvailabilityStatus.toString2(toTest);
//		end = System.nanoTime();
//		System.out.println(StopWatch.nanosecondsToString(end - start));
		
		
	}
}
