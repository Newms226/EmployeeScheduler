package Availability;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import driver.Driver;
import emp.Employee;
import restaurant.PositionID;
//import time.Day;
import time.Interval;
import time.Interval_SF;
import time.LocalTimeInterval;
import time.TimeBasedInterval;

public class Availability {
	public static final long AVOID_MINUTE_AMOUNT = 60;
	public static final Duration AVOID_DURATION = Duration.of(AVOID_MINUTE_AMOUNT, ChronoUnit.MINUTES);
	private static final Logger log = Driver.availabilityLog;
	
	Map <LocalDate, Map<Interval_SF, AvailabilityList>> map;
	
	public Availability() {
		map = new HashMap<>();
	}
	
	public boolean inAvailability(PositionID<? extends Employee> ID) {
		log.entering("Availability", "inAvailability(" + ID + ")");
		return query(Interval_SF.AVAILABLE, ID.getInterval(), ID.getDay());
	}
	
	private boolean internalQuery(Interval_SF statusFlag, LocalTimeInterval interval, LocalDate date) {
		return map.computeIfAbsent(date, d -> new HashMap<Interval_SF, AvailabilityList>())
					.computeIfAbsent(statusFlag, sf -> new AvailabilityList(statusFlag))
						.contains(interval);
	}
	
	public boolean query(Interval_SF statusFlag, LocalTimeInterval interval, LocalDate date) {
		return internalQuery(statusFlag, interval, date) 
				&& interval.isLinked() ? internalQuery(statusFlag, interval.getNext(), date) : true;
	}
	
	public boolean markAvailable(PositionID<? extends Employee> ID) {
		// TODO
		return false;
	}
	
	public boolean markUNAvailable(PositionID<? extends Employee> ID) {
		// TODO
		return false;
	}
	
	public int condense() {
		// TODO
		return 0;
	}
	
	public void validateNoOverlap() {
		
	}
	
	public void buildFrom(AvailabilityList buildFrom) {
		// TODO
	}
}
