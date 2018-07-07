package Availability;

import java.util.logging.Level;
import java.util.logging.Logger;

import driver.Driver;
import racer.StopWatch;

public class TimeChunk {
	private static final Logger log = Driver.availabilityLog;
	
	public static TimeChunk from (int dayStart, int dayEnd,
                                  int hourStart, int minuteStart,
                                  int hourEnd, int minuteEnd) 
	{
		
		int difference = dayEnd - dayStart;
		
		if (difference == 0) {
			return new TimeChunk(dayStart, hourStart, hourEnd, minuteStart, minuteEnd, null);
		} else if (difference == 1) {
			TimeChunk start = null, end = null;
			start = new TimeChunk(dayStart, hourStart, 23, minuteStart, 59, end);
			end = new TimeChunk(dayEnd, 0, hourEnd, 0, minuteEnd, start);
			return start;
		} else {
			IllegalArgumentException e = new IllegalArgumentException("Invalid day range: " + dayStart + " - " + dayEnd);
			log.log(Level.SEVERE, e.getMessage(), e);
			throw e;
		}
	}
	
	public static TimeChunk from(int day,
			                     int hourStart, int minuteStart,
			                     int hourEnd, int minuteEnd) {
		return new TimeChunk(day, hourStart, hourEnd, minuteStart, minuteEnd, null);
	}
	
	public final int day,
	                 hourStart, hourEnd,
	                 minuteStart, minuteEnd;
	public final TimeChunk link;
	
	
	
	private TimeChunk(int day,
			                 int hourStart, int minuteStart,
			                 int hourEnd, int minuteEnd,
			                 TimeChunk link) {
		if (!(hourStart <= hourEnd)) {
			throwRangeException("hour", hourStart, hourEnd);
		}
		if (!(minuteStart < minuteEnd)) {
			throwRangeException("minute", minuteStart, minuteEnd);
		}
		
		assertNumericValidity("day", day, 0, AvailabilityStatus.TOTAL_DAYS);
		assertNumericValidity("hour start", hourStart, 0, AvailabilityStatus.TOTAL_HOURS);
		assertNumericValidity("hour end", hourEnd, 0, AvailabilityStatus.TOTAL_HOURS);
		assertNumericValidity("minute end", minuteEnd, 0, AvailabilityStatus.TOTAL_MINUTES);
		assertNumericValidity("minute start", minuteStart, 0, AvailabilityStatus.TOTAL_MINUTES);
		
		this.day = day;
		this.hourStart = hourStart;
		this.hourEnd = hourEnd;
		this.minuteEnd = minuteEnd;
		this.minuteStart = minuteStart;
		this.link = link;
	}
	
	public boolean isLinked() {
		return link != null;
	}
	
	public static void assertNumericValidity(String name, int toTest, int minInclusive, int maxExclusive) {
		if (!(minInclusive <= toTest && toTest < maxExclusive)) {
			IllegalArgumentException e = new IllegalArgumentException(name + " is out of range: " + toTest + " Range: "
					+ "[" + minInclusive + ", " + maxExclusive + ")" );
			log.log(Level.SEVERE, e.getMessage(), e);
			throw e;
		}
	}
	
	public static void throwRangeException(String name, int first, int second) {
		IllegalArgumentException e = new IllegalArgumentException("Invalid " + name + " order: "
				+ first + " - " + second + ". Must be: first <= second" );
		log.log(Level.SEVERE, e.getMessage(), e);
		throw e;
	}
	
	public static void main(String[] args) {
		long start, end;
		TimeChunk test;
		start = System.nanoTime();
		test = TimeChunk.from(6, 1, 1, 1, 2);
		end = System.nanoTime();
		System.out.println("Took: " + StopWatch.nanosecondsToString(end - start));
		
		start = System.nanoTime();
		test = TimeChunk.from(0, 1, 1, 1, 2);
		end = System.nanoTime();
		System.out.println("Took: " + StopWatch.nanosecondsToString(end - start));
	}
	                 
}
