package Availability;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.time.DateTimeException;
import java.time.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import driver.Driver;
import racer.Judge;
import racer.Racer;
import racer.Result;
import racer.StopWatch;
import tools.FileTools;

/**
 * 
 * @author Michael Newman
 * 
 *
 */
public class TimeChunk implements Serializable, Cloneable {
	
	public static void main(String[] args) throws FileNotFoundException {
		creationOptionsTimeTest();
	}
	
	/******************************************************************************
	 *                                                                            *
	 *                                                                            *
	 *                        Static Fields & Methods                             *
	 *                                                                            *
	 *                                                                            *
	 ******************************************************************************/
	
	/******************************************************************************
	 *                                                                            *
	 *                        Static Constructor Methods                          *
	 *                                                                            *
	 ******************************************************************************/
	
	public static TimeChunk build() {
		// TODO
		return null;
	}
	
	public static TimeChunk from(int dayStart, int hourStart, int minuteStart,
			                     int dayEnd, int hourEnd, int minuteEnd) {
		try {
			IntTimeRangeException.assertValidity("Day start", dayStart, 0, TOTAL_DAYS);
			IntTimeRangeException.assertValidity("Day end", dayEnd, 0, TOTAL_DAYS);
			return new TimeChunk(dayStart, getLocalTimeFromDateInts(hourStart, minuteStart),
					             dayEnd, getLocalTimeFromDateInts(hourEnd, minuteEnd));
		} catch (DateTimeException | IntTimeRangeException e) {
			log.log(Level.SEVERE, "FAILED TO CONSTRUCT, RETURNING NULL\n\t" + e.getMessage(), e);
			return null;
		}
	}
	
	public static TimeChunk fromIndex(int startINDEX, int endINDEX) {
		try {
			return new TimeChunk(getDayByteFromIndex(startINDEX), getLocalTimeFromIndex(startINDEX), 
					             getDayByteFromIndex(endINDEX), getLocalTimeFromIndex(endINDEX));
		} catch (DateTimeException | IntTimeRangeException e) {
			log.log(Level.SEVERE, "FAILED TO CONSTRUCT, RETURNING NULL\n\t" + e.getMessage(), e);
			return null;
		}
	}
	
	public static TimeChunk fromLocalTime(int dayStart, LocalTime start, int dayEnd, LocalTime end) {
		return new TimeChunk(dayStart, start, 
	                         dayEnd, end);
	}
	
	/******************************************************************************
	 *                                                                            *
	 *                               Static Fields                                *
	 *                                                                            *
	 ******************************************************************************/
	
	private static final long serialVersionUID = -6334533923887427675L;
	protected static final Logger log = Driver.availabilityLog;
	public static final DateTimeFormatter timeFormat12Hour = DateTimeFormatter.ofPattern("hh:mma");
	
	public static final Comparator<TimeChunk> INDEX_START_ORDER =
			(a, b) -> {
				if (a.indexStart < b.indexStart) return -1;
				if (a.indexStart > b.indexStart) return 1;
				return 0;
			};
	
	public static final int TOTAL_DAYS = 7,
			                TOTAL_HOURS = 24,
			                MINUTE_CHUNK = 1, // MUST BE AN EVEN DIVISOR OF 60!!
			                TOTAL_MINUTES = 60 / MINUTE_CHUNK, // REMAINDER MUST BE 0!!
			                AVOID_MINUTE_COUNT = 60,
			                DAY_OFFSET = 1440;

	
	/******************************************************************************
	 *                                                                            *
	 *                               Static Methods                               *
	 *                                                                            *
	 ******************************************************************************/
	
	/* Function began as (1440 * day) + (60 * hour) + minute
	 * simplified down to bellow function
	 */
	public static int getIndexFromDate(int day, int hour, int minute) {
		return day * DAY_OFFSET + 60 * hour + minute;
//		log.finest("ENTERING: getIndexFromDate(" + day + ", " + hour + ", " + minute + ")");
//		int toReturn;
//		if (day == 0) {
//			toReturn = (hour * 60) + minute;
//			log.finer("INDEX GENERATED: " + toReturn + " from "
//					+ "\n\tDay: " + day + " Hour: " + hour + " Minute: " + minute);
//		}
//		
//		// else
//		toReturn = (24 * day) + hour + (minute / 60);
//		log.finer("INDEX GENERATED: " + toReturn + " from "
//				+ "\n\tDay: " + day + " Hour: " + hour + " Minute: " + minute);
//		return toReturn;
	}
	
	public static int getIndexFromLocalTime(int day, LocalTime time) throws IntTimeRangeException {
		IntTimeRangeException.assertValidity("Day", day, 0, TOTAL_DAYS);
		return getIndexFromDate(day, time.getHour(), time.getMinute());
	}
	
	public static String getTimeStringFromIndex(int index) {
		int minutes = getMinuteByteFromIndex(index),
	        hour = getHourByteFromIndex(index, minutes);
		return new StringBuffer(dayByteToString(getDayByteFromIndex(index)) + ", " 
								+ hourByteToString(hour) + ":"
								+ getMinuteByteFromIndex(index) + getAorP(hour)).toString();
	}
	
	public static LocalTime getLocalTimeFromIndex(int index) throws DateTimeException, IntTimeRangeException {
		IntTimeRangeException.assertValidity("Index", index, 0, AvailabilityArray.MAX_INDEX_VALUE);
		int minutes = getMinuteByteFromIndex(index);
		return LocalTime.of(getHourByteFromIndex(index, minutes), minutes);
	}
	
	public static LocalTime getLocalTimeFromDateInts(int hour, int minute) throws DateTimeException {
		return LocalTime.of(hour, minute);
	}
	
	public static int getDayByteFromIndex(int index) {
		return index / DAY_OFFSET; 
	}
	
	@Deprecated
	public static int getHourByteFromIndex(int index) {
		return ((index % DAY_OFFSET) - getMinuteByteFromIndex(index)) / 60;
	}
	
	public static int getHourByteFromIndex(int index, int minutes) {
		return ((index % DAY_OFFSET) - minutes) / 60;
	}
	
	public static int getMinuteByteFromIndex(int index) {
		return index % 60;
	}

	public static String dayByteToString(int day) throws IllegalArgumentException {
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
	
	public static String hourByteToString(int hour) throws IllegalArgumentException {
		if (hour == 0) {
			return 12 + "";
		} else if (hour < 0) { 
			throw new IllegalArgumentException(hour + " is not a valid hour. Range: [0, " + (TOTAL_HOURS - 1) + "]");
		} else if (hour <= 12) {
			return hour + "";
		} else if (hour < 24) {
			return (hour - 12) + "";
		} else {
			throw new IllegalArgumentException(hour + " is not a valid hour. Range: [0, " + (TOTAL_HOURS - 1) + "]");
		}
	}
	
	public static String minuteBtyeToString(int minute) throws IllegalArgumentException{
		if (0 <= minute && minute < 10) {
			return "0" + minute;
		} else if (minute < 60) {
			return minute + "";
		}else {
			throw new IllegalArgumentException(minute + " is not a valid minute. Range: [0, 59]");
		}
	}
	
	public static char getAorP(int hour) {
		if (hour < 0) {
			throw new IllegalArgumentException(hour + " is not a valid hour. Range: [0, " + (TOTAL_HOURS - 1) + "]");
		} else if (hour < 12) {
			return 'a';
		} else if (hour < 24) {
			return 'p';
		} else { 
			throw new IllegalArgumentException(hour + " is not a valid hour. Range: [0, " + (TOTAL_HOURS - 1) + "]");
		}
	}
	
	/******************************************************************************
	 *                                                                            *
	 *                                                                            *
	 *                        Instance Fields & Methods                           *
	 *                                                                            *
	 *                                                                            *
	 ******************************************************************************/
	
	public final int indexStart, indexEnd;
	public final boolean isNegative, isZero;
	public final int dayStart, dayEnd;
	
	protected final LocalTime start, end;
	protected final String summary;
	
	protected TimeChunk(TimeChunk chunk) {
		this(chunk.dayStart, chunk.start, chunk.dayEnd, chunk.end);
	}
	
	private TimeChunk(int dayStart, LocalTime start,
			          int dayEnd, LocalTime end) {
		log.finest("ENTERING: Constructor(" + dayStart + ", " + start + ", " + dayEnd + ", " + end + ")");
		this.dayEnd = dayEnd; 
		this.dayStart = dayStart;
		this.start = start;
		this.end = end;
		
		this.indexStart = getIndexFromDate(dayStart, start.getHour(), start.getMinute());
		this.indexEnd = getIndexFromDate(dayEnd, end.getHour(), end.getMinute());
		
		this.summary = generateString();
		
		log.finest("Generated new TimeChunk:\n\t" + this);
		
		if (indexStart > indexEnd) {
			isNegative = true;
			log.info("Generated negative TimeChunk:\n\t" + this);
		} else {
			isNegative = false;
		}
		
		if (indexStart == indexEnd) {
			isZero = true;
			log.info("Generated zero value TimeChunk:\n\t" + this);
		} else {
			isZero = false;
		}
	}
	
	public int getMinuteStart() {
		return start.getMinute();
	}
	
	public int getMinuteEnd() {
		return end.getMinute();
	}
	
	public int getHourStart() {
		return start.getHour();
	}
	
	public int getHourEnd() {
		return end.getHour();
	}
	
	public int getDayStart() {
		return dayStart;
	}
	
	public int getDayEnd() {
		return dayEnd;
	}
	
	public LocalTime getStartAsLocalTime() {
		return start;
	}
	
	public LocalTime getEndAsLocalTime() {
		return end;
	}
	
	public Duration getAsDuration() {
		return Duration.between(start, end);
	}
	
	public int getMinutes() {
		if (isRightBoundary()) {
			return (AvailabilityArray.MAX_INDEX_VALUE - indexStart) + indexEnd;
		} else {
			return indexEnd - indexStart;
		}
	}

	public boolean isRightBoundary() {
		return dayStart == 6 && dayEnd == 1;
	}
	
	
	public String getInfoString() {
		return this.toString();
	}
	
	private String generateString() {
		return new StringBuffer(dayByteToString(dayStart) + " " + timeFormat12Hour.format(start) 
			+ " - " + dayByteToString(dayEnd) + " " + timeFormat12Hour.format(end)).toString();
	}
	
	/******************************************************************************
	 *                                                                            *
	 *                                                                            *
	 *                              Override Methods                              *
	 *                                                                            *
	 *                                                                            *
	 ******************************************************************************/
	
	@Override
	public String toString() {
		return summary;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o == null) return false;
		if (o == this) return true;
		if (!o.getClass().equals(getClass())) return false;
		
		TimeChunk that = (TimeChunk) o;
		if (indexStart != that.indexStart) return false;
		if (indexEnd != that.indexEnd) return false;
		
		return true;
	}
	
	@Override
	public TimeChunk clone() {
		return TimeChunk.fromIndex(indexStart, indexEnd);
	}
		
	/******************************************************************************
	 *                                                                            *
	 *                                                                            *
	 *                               Tester methods                               *
	 *                                                                            *
	 *                                                                            *
	 ******************************************************************************/
	
	private static void creationTimer() {
		long startLap, endLap;
		TimeChunk test = TimeChunk.from(0, 0, 0, 0, 5, 0);
		Random random = new Random();
		int runFor = (7*2*50), randomEnd;
		StopWatch timer = new StopWatch("Creation test", runFor);
		for (int i = 0; i < runFor; i++) {
			randomEnd = random.nextInt(24);
			
			startLap = System.nanoTime();
			test = TimeChunk.from(0, 0, 0, 0, randomEnd, 0);
			endLap = System.nanoTime();
			timer.lap(startLap, endLap);
		}
		timer.end();
		
		System.out.println(timer.resultsAsString() + "\n" + timer);	
	}
	
	private static void creationOptionsTimeTest() throws FileNotFoundException {
		TimeChunk test;
		int runFor = 1000;
		long startLap, endLap, elapsed3D, elapsedSt;
		int win3D = 0, winSt = 0, randomIndex, randomHour, ties = 0, maxValue = 5;
		Random random = new Random();
//		StopWatch straight = new StopWatch("Stright", runFor);
//		StopWatch d3 = new StopWatch("3D", runFor);
		Racer allInts = new Racer("From all ints", runFor);
		Racer fromIndex = new Racer("FromIndex", runFor);
		Racer fromLocalTime = new Racer("FromLocalTime", runFor);
		ArrayList<Racer> racers = new ArrayList<>(3);
		racers.add(allInts);
		racers.add(fromIndex);
		racers.add(fromLocalTime);
		Judge judge = new Judge("Write Test", racers, runFor, true);
		Result[] results = new Result[3];
		LocalTime localTimeStart = LocalTime.of(0, 0);
		for (int i = 0; i < runFor; i++) {
			test = TimeChunk.fromIndex(0, 1);
			randomHour = random.nextInt(maxValue) + 15;
			
			log.fine("START LAP: ALL INTS");
			startLap = System.nanoTime();
			test = TimeChunk.from(0, 0, 0, 0, randomHour, 0);
			endLap = System.nanoTime();
			results[0] = new Result(0, allInts.timer.lap(startLap, endLap));
			allInts.setResult(endLap - startLap);
//			System.out.println(test);
			
			log.fine("START LAP: FROM INDEX");
			randomIndex = TimeChunk.getIndexFromDate(0, randomHour, 0);
			startLap = System.nanoTime();
			test = TimeChunk.fromIndex(0, randomIndex);
			endLap = System.nanoTime();
			results[1] = new Result(1, fromIndex.timer.lap(startLap, endLap));
			fromIndex.setResult(endLap - startLap);
//			System.out.println(test + "\n\t" + test.indexEnd);
			
			log.fine("START LAP: FROM LOCAL TIME");
			
			startLap = System.nanoTime();
			LocalTime end = TimeChunk.getLocalTimeFromIndex(randomIndex);
			test = TimeChunk.fromLocalTime(0, localTimeStart, 0, end);
			endLap = System.nanoTime();
			fromLocalTime.setResult(endLap - startLap);
			results[2] = new Result(2, fromLocalTime.timer.lap(startLap, endLap));
//			System.out.println(test);
			
			judge.decideLapWinner(results);
		}
		
		judge.end();
		
		System.out.println(judge);
		FileTools.writeToFile(new PrintWriter(FileTools.DEFAULT_IO_DIRECTORY + "CreationRace0.txt"), judge.getWinnerByLap());
	}
	
	
//	
//	static {
//		if (TimeChunk.MINUTE_CHUNK < 1) {
//			throw new IllegalArgumentException("60 / MINUTE_CHUNK (" + TimeChunk.MINUTE_CHUNK + ") != 0");
//		}
//		int toTest = 60 % TimeChunk.MINUTE_CHUNK;
//		if (toTest != 0) {
//			throw new IllegalArgumentException("60 / MINUTE_CHUNK (" + TimeChunk.MINUTE_CHUNK + ") != 0");
//		}
//	}
	
//	private TimeChunk(int dayStart, int hourStart, int minuteStart,
//    int dayEnd,int hourEnd, int minuteEnd) {
//this.dayStart = dayStart;
//this.dayEnd = dayEnd;
//this.hourStart = hourStart;
//this.hourEnd = hourEnd;
//this.minuteEnd = minuteEnd;
//this.minuteStart = minuteStart;
//
//indexStart = getIndexFromDate(dayStart, hourStart, minuteStart);
//indexEnd = getIndexFromDate(dayEnd, hourEnd, minuteEnd);
//
//start = LocalTime.of(hourStart, minuteStart);
//log.finest("Generated LocalTime start as " + start + "\n\tFROM: " + hourStart + ":" + minuteStart);
//end = LocalTime.of(hourEnd, minuteEnd);
//log.finest("Generated LocalTime start as " + end + "\n\tFROM: " + hourEnd + ":" + minuteEnd);
//
//summary = generateString();
//
//
//log.finest("Generated new TimeChunk:\n\t" + this);
//if (isNegative()) {
//log.info("Generated negative TimeChunk:\n\t" + this);
//} else if (isZero()) {
//log.info("Generated zero value TimeChunk:\n\t" + this);
//}
//}
	
//	public static void throwRangeException(String name, int first, int second) {
//	IllegalArgumentException e = new IllegalArgumentException("Invalid " + name + " order: "
//			+ first + " - " + second + ". Must be: first <= second" );
//	log.log(Level.SEVERE, e.getMessage(), e);
//	throw e;
//}
}
