package Availability;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import driver.Driver;
import emp.Employee;
import restaurant.PositionID;
//import time.Day;
import time.TimeInterval;
import time.Interval_SF;

public class Availability {
	public static final long AVOID_MINUTE_AMOUNT = 60;
	public static final Duration AVOID_DURATION = Duration.of(AVOID_MINUTE_AMOUNT, ChronoUnit.MINUTES);
	private static final Logger log = Driver.availabilityLog;
	
	Map <DayOfWeek, Map<Interval_SF, AvailabilityList>> map;
	
	public Availability() {
		map = new HashMap<>();
		Map<Interval_SF, AvailabilityList> mapToAdd;
		for (DayOfWeek day: DayOfWeek.values()) {
			for (Interval_SF SF: Interval_SF.values()) {
				mapToAdd = new HashMap<>();
				mapToAdd.put(SF, new AvailabilityList(SF));
				map.put(day, mapToAdd);
			}
		}
	}
	
	public boolean inAvailability(PositionID<? extends Employee> ID) {
		log.entering("Availability", "inAvailability(" + ID + ")");
		return query(Interval_SF.AVAILABLE, ID.getInterval());
	}
	
	public boolean query(Interval_SF statusFlag, TimeInterval interval) {
//		if (interval.spansOvernight()) {
//			return queryMultiple(statusFlag, interval.splitByDay());
//		}
		
		return map.get(interval.getDayOfWeek())
					.get(statusFlag)
						.contains(interval);
	}
	
	public boolean queryMultiple(Interval_SF statusFlag, TimeInterval[] intervals) {
		for(TimeInterval interval: intervals) {
			if (!query(statusFlag, interval)) return false;
		}
		return true;
	}
	
	public boolean queryAllStatusFlags(TimeInterval
	
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
	
	private void put(TimeInterval chunk) {
		// TODO
	}
	
	private void put(TimeInterval[] chunks) {
		for (TimeInterval chunk: chunks) put(chunk);
	}
}
