package Availability;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import emp.Employee;
import restaurant.PositionID;
import time.Day;
import time.LocalTimeInterval;
import time.Interval_SF;

public class Availability {
	public static final long AVOID_MINUTE_AMOUNT = 60; 
	
	Map <Day, Map<Interval_SF, AvailabilityList>> map;
	
	public Availability() {
		map = new HashMap<>();
		Map<Interval_SF, AvailabilityList> mapToAdd;
		for (Day day: Day.values()) {
			mapToAdd = new HashMap<>();
			mapToAdd.put(Interval_SF.AVAILABLE, new AvailabilityList(Interval_SF.AVAILABLE));
			map.put(day, mapToAdd);
		}
	}
	
	public boolean inAvailability(PositionID<? extends Employee> ID) {
		return map.get(ID.getDay())
			.get(Interval_SF.AVAILABLE)
				.contains(ID.getInterval());
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
	
	private void put(LocalTimeInterval chunk) {
		// TODO
	}
	
	private void put(LocalTimeInterval[] chunks) {
		for (LocalTimeInterval chunk: chunks) put(chunk);
	}
}