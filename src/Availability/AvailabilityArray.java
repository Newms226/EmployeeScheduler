package Availability;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import driver.Driver;
import tools.FileTools;
import tools.NumberTools;

@SuppressWarnings("unused")
public abstract class AvailabilityArray implements Serializable {

	/******************************************************************************
	 *                                                                            *
	 *                                                                            *
	 *                        Static Fields & Methods                             *
	 *                                                                            *
	 *                                                                            *
	 ******************************************************************************/
	
	private static final long serialVersionUID = 3350277295951138995L;
	protected static final Logger log = Driver.availabilityLog;
	
	public static final int MAX_INDEX_VALUE  = 10_080;
	
	public static final byte AVAILABLE         = 0,
			                 STRICTLY_AVAILABLE = 1,
			                 NOT_AVAILABLE      = 2,
			                 NEVER_AVAILABLE    = 3,
			                 MAX_AVAIL_CONSTANT = 3;
	
	public static boolean isStatusByteValid(byte status) {
		return 0 < status && status <= MAX_AVAIL_CONSTANT;
	}
	
	public static void assertValidStatusByte(byte status) throws IllegalArgumentException {
		if (!isStatusByteValid(status)) {
			throw new IllegalArgumentException(status + " is not a valid status byte. Range: [0, " 
					+ AvailabilityArray.MAX_AVAIL_CONSTANT + ")");
		}
	}
	
	public static String statusByteToString(int status) throws IllegalArgumentException {
		if (status == AVAILABLE) {
			return "Available";
		} else if (status == STRICTLY_AVAILABLE) {
			return "Strictly Available";
		} else if (status == NOT_AVAILABLE) {
			return "Not Presently Available";
		} else if (status == NEVER_AVAILABLE) {
			return "Outside Set Availability";
		} else {
			throw new IllegalArgumentException(status + " is not a valid status byte. Range: [0, " 
					+ AvailabilityArray.MAX_AVAIL_CONSTANT + ")");
		}
	}
	
	public static boolean isIndexValid(int index) {
		return 0 <= index && index < MAX_INDEX_VALUE;
	}
	
	public static <A extends AvailabilityArray> String getTimeChunkListString(List<? extends TimeChunk> list, String header) {
		StringBuffer buffer = new StringBuffer(header + FileTools.LINE_BREAK);
		
		list
			.stream()
			.forEach(c -> buffer.append(c.toString() + "\n"));
		
		return buffer.toString();
	}
	
	public static <A extends AvailabilityArray> List<LabledTimeChunk> getTimeChunks(A availability) {
		byte currentByte;
		int currentIndex = 0,
		    endIndex = MAX_INDEX_VALUE,
		    startIndex;
		ArrayList<LabledTimeChunk> list = new ArrayList<>();
		byte[] array = availability.availability;
		
		while(currentIndex < endIndex) {
			currentByte = array[currentIndex];
			startIndex = currentIndex;
			currentIndex++;
			while (array[currentIndex] == currentByte && currentIndex < endIndex) {
				currentIndex++;
			}
			list.add(LabledTimeChunk.fromIndex(startIndex, currentIndex, currentByte));
		}
		
		array = null;
		
		return list;
	}
	
	public static <A extends AvailabilityArray> List<LabledTimeChunk> getSpecificTimeChunks(A availability, byte statusByte) {
		if (!isStatusByteValid(statusByte)) {
			log.severe("FAILURE: " + statusByte + " is not a valid status byte.");
			return null;
		}
		
		// TODO: This is wastefull. Shouldn't build all the time chunks when it only needs a few!
		// else
		return getTimeChunks(availability)
			.stream()
			.filter(chunk -> chunk.getLabel() == statusByte)
			.collect(Collectors.toList());
	}
	
	/******************************************************************************
	 *                                                                            *
	 *                                                                            *
	 *                             Abstract Methods                               *
	 *                                                                            *
	 *                                                                            *
	 ******************************************************************************/
	
	abstract boolean toAvailable(TimeChunk chunk);
	
	abstract boolean toSTRICTLYAvailable(TimeChunk chunk);
	
	abstract boolean toNotAvailable(TimeChunk chunk);
	
	abstract boolean toNEVERAvailable(TimeChunk chunk);
	
	abstract char getID();
	
	/******************************************************************************
	 *                                                                            *
	 *                                                                            *
	 *                        Instance Fields & Methods                           *
	 *                                                                            *
	 *                                                                            *
	 ******************************************************************************/
	
	protected byte[] availability;
	
	AvailabilityArray() {
		availability = new byte[MAX_INDEX_VALUE];
	}
	
	protected boolean query(byte status, TimeChunk chunk) {
		try {
			assertValidStatusByte(status);
		} catch (IllegalArgumentException e) {
			log.log(Level.SEVERE, e.getMessage(), e);
			return false;
		}
		
		for (int i = chunk.indexStart; i < chunk.indexEnd; i++) {
			if (availability[i] != status) {
				log.info("FAILURE: " + chunk + " failed to be " + statusByteToString(status) + " at index " + NumberTools.format(i));
				return false;
			}
		}
		
		log.finer("SUCCESS: " + chunk + " contains enitrely " + statusByteToString(status));
		return true;
	}
	
	protected boolean query(ByteRejecter tester, TimeChunk chunk) {
		for (int i = chunk.indexStart; i < chunk.indexEnd; i++) {
			if (tester.reject(availability[i])) {
				log.info("FAILURE: " + chunk + " failed to pass the query at index " + i);
				return false;
			}
		}
		log.finer("SUCCESS: " + chunk);
		return true;
	}
	
	public boolean isAvailable(TimeChunk chunk) {
		log.entering(getClass().getName(), "isAvailable(" + chunk + ")");
		return query(b -> b != AVAILABLE, chunk);
	}
	
	public boolean isSTRICTLYAvailable(TimeChunk chunk) {
		log.entering(getClass().getName(), "isSTRICTLYAvailable(" + chunk + ")");
		return query(b -> b != AVAILABLE && b != STRICTLY_AVAILABLE, chunk);
	}
	
	public boolean isNotAvailable(TimeChunk chunk) {
		log.entering(getClass().getName(), "isNotAvailable(" + chunk + ")");
		return query(b -> b != NOT_AVAILABLE, chunk);
	}
	
	public boolean isNEVERAvailable(TimeChunk chunk) {
		log.entering(getClass().getName(), "isNEVERAvailable(" + chunk + ")");
		return query(b -> b != NEVER_AVAILABLE, chunk);
	}
	
	public String readAvailability(TimeChunk chunk) {
		StringBuffer buffer = new StringBuffer("Availability Status over " + chunk + "\n");
		
		byte cur;
		int index = chunk.indexStart;
		int end = chunk.indexEnd;
		int start;
		ArrayList<LabledTimeChunk> list = new ArrayList<>();
		
		while(index < end) {
			cur = availability[index];
			start = index;
			index++;
			while (availability[index] == cur && index < end) {
				index++;
			}
			list.add(LabledTimeChunk.fromIndex(start, index, cur));
		}
		
		list.stream().forEach(c -> buffer.append(c.toString() + "\n"));
		
		list = null;
		
		return buffer.toString();
	}
	
	public String toCSV() {
		StringBuffer buffer = new StringBuffer(getID() + "{");
		for (int i = 0; i < MAX_INDEX_VALUE - 2; i++) {
			buffer.append(availability[i] + ",");
		}
		buffer.append(availability[MAX_INDEX_VALUE - 1] + "}");
		return buffer.toString();
		
	}
	
	/******************************************************************************
	 *                                                                            *
	 *                                                                            *
	 *                              Override Methods                              *
	 *                                                                            *
	 *                                                                            *
	 ******************************************************************************/
	
	@Override
	public boolean equals(Object o) {
		if (o == null) return false;
		if (o == this) return true;
		if (!o.getClass().equals(getClass())) return false;
		
		AvailabilityArray that = (AvailabilityArray) o;
		if (!Arrays.equals(availability, that.availability)) return false;
		
		return true;
	}
	
//	public byte valueOf(String status) {
////		try {
////			return (byte) internalStatus.valueOf(status).ordinal();
////		} catch (IllegalArgumentException | NullPointerException e) {
////			log.log(Level.SEVERE, e.getMessage(), e);
////			return -1;
////		}
//		/*TODO
//		 * - When would this method even be used?
//		 * - With a 1D implementation, the index needs to be converted to a time for printing purposes
//		 * - But there is no need to be able to work backwards from that generated string... 
//		 */
//		return -1;
//	}
//	
//
//
////	String minutes = "|05 |10 | 15| 20| 25| 30| 35| 40| 45| 50| 55| 60";
//	
//	/*CALLED WHEN:
//	 * - Debugging: Make sure things are working correctly
//	 * >> Extracted from Main Schedule:
//	 *    - By Server to view their schedule
//	 *    - In the daily print out
//	 */
//	public static String toString(byte[][][] a, String arrayType) {
//		StringBuffer buffer = new StringBuffer(arrayType + "\n");
//		byte currentByte;
//		// TODO:
//		for (int i = 0; i < ARRAY_SIZE; i++) {
//			
//		}
//		return buffer.toString();
//	}
	
//	protected boolean query(byte status, TimeChunk chunk) {
//	try {
//		if (!AvailabilityStatus.isStatusByteValid(status)) {
//			throw new IllegalArgumentException(status + " is not a valid status byte. Range: [0, " 
//					+ AvailabilityStatus.MAX_AVAIL_CONSTANT + ")");
//		}
//	} catch(IllegalArgumentException e) {
//		log.log(Level.SEVERE, e.getMessage(), e);
//		return false;
//	}
//	
//	if (chunk.isLinked()) {
//		TimeChunk link = chunk.link;
//		log.fine(chunk + " is linked in " + AvailabilityStatus.toString(status));
//		return query(status, chunk.day, chunk.hourStart, chunk.minuteStart, chunk.hourEnd, chunk.minuteEnd)
//				&& query(status, link.day, link.hourStart, link.minuteStart, link.hourEnd, link.minuteEnd);
//	} else {
//		return query(status, chunk.day, chunk.hourStart, chunk.minuteStart, chunk.hourEnd, chunk.minuteEnd);
//	}
//	
//}
//
//private boolean query(byte status,
//                      int day,
//                      int hourStart, int minuteStart,
//                      int hourEnd, int minuteEnd)
//{
//	try {
//		int endEXCLUSIVE = getIndexFromDate(day, hourEnd, minuteEnd);
//		for (int i = getIndexFromDate(day, hourStart, minuteStart); i < endEXCLUSIVE; i++) {
//			if (availability[i] != status) {
//				log.info("FAILED: " + getDateFromIndex(i) + " is " + toString(availability[i]) +
//				" NOT " + toString(status));
//				return false;
//			} // test condition
//		} // for loop
//	} catch (IllegalArgumentException | ArrayIndexOutOfBoundsException e) {
//		log.log(Level.SEVERE, e.getMessage(), e);
//		return false;
//	}
//	log.fine("Succeded from " + AvailabilityStatus.toString(status));
//	return true;
//}
	
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
	
//	private enum internalStatus {
//	AVAILABILE {
//		public String toString() {return "Available";}
//	},
//    STRICTLY_AVAILABLE {
//		public String toString() {return "Strictly Available";}
//	},
//    NOT_AVAILABLE {
//		public String toString() {return "Not Available";}
//	},
//    NEVER_AVAILABLE {
//		public String toString() {return "NEVER Available";}
//	},
//}


//	protected boolean fillArray(byte status, TimeChunk chunk) {
//		if (!chunk.isLinked()) {
//			return fillArray(status, chunk.day, chunk.hourStart, chunk.minuteStart, chunk.hourEnd, chunk.minuteEnd);
//		} else {
//			TimeChunk linked = chunk.link;
//			return fillArray(status, chunk.day, chunk.hourStart, chunk.minuteStart, chunk.hourEnd, chunk.minuteEnd)
//					&& fillArray(status, linked.day, linked.hourStart, linked.minuteStart, linked.hourEnd, linked.minuteEnd);
//		}
//	}
	
//	protected boolean fillArray(byte status,
//            int day,
//            int hourStart, int minuteStart,
//            int hourEnd, int minuteEndEXCLUSIVE)
//{
//try {
//if (!isStatusByteValid(status)) {
//throw new IllegalArgumentException(status + " is not a valid status byte. Range: [0, " + MAX_AVAIL_CONSTANT + ")");
//}
//
//Arrays.fill(availability, getIndexFromDate(day, hourStart, minuteStart), getIndexFromDate(day, hourEnd, minuteEndEXCLUSIVE), status);
//} catch (IllegalArgumentException | ArrayIndexOutOfBoundsException e) {
//log.log(Level.SEVERE, e.getMessage(), e);
//return false;
//}
//
//return true;
//}
	
	public static String print(ByteRejecter rejecter, byte toTest) {
		return rejecter.reject(toTest) + "";
	}
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
		System.out.println(print(b -> b != AVAILABLE && b != STRICTLY_AVAILABLE, NEVER_AVAILABLE));
		
	}
}
