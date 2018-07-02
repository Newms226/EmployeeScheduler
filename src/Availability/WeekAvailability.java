package Availability;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import driver.Driver;
import time.Availability_Status;
import time.LocalDateTimeInterval;
import time.LocalTimeInterval;
import time.Week;

public class WeekAvailability {
	private static final Logger log = Driver.availabilityLog;
	private Map <LocalDate, Map<Availability_Status, AvailabilityList>> map;
	final Week week;
	
	public static WeekAvailability from(PersistantAvailabilityList persistantAvailability, TimeOffList timeOffList, Week week) {
		log.entering(WeekAvailability.class.getName(), "STATIC: from()");
		Set<LocalDate> availableDays = timeOffList.getAvailableDays(week);
		Set<LocalDate> daysOff = timeOffList.getDaysOff(week);
		log.finer("BEFORE TRANSFER TO" + PersistantAvailabilityList.class.getName() 
				+ "\n\tDaysOff: " + daysOff 
				+ "\n\tAvailableDays: " + availableDays);
		return new WeekAvailability(week, persistantAvailability.buildWeek(availableDays, daysOff));
	}
	
	public WeekAvailability(Week week, Map <LocalDate, Map<Availability_Status, AvailabilityList>> map) {
		map = new HashMap<>();
		this.week = week;
		this.map = map;
	}
	
	boolean availableFor(LocalDate date, LocalTimeInterval interval) {
		return getStatusFor(date, interval) == Availability_Status.AVAILABLE;
	}
	
	boolean setStatus(Availability_Status newStatus, LocalDate date, LocalTimeInterval interval) {
		// TODO
		return false;
	}
	
	boolean setToScheduled(LocalDate date, LocalTimeInterval interval) {
		return setStatus(Availability_Status.SCHEDULED, date, interval);
	}
	
	Availability_Status getStatusFor(LocalDate date, LocalTimeInterval interval) {
		// TODO
		return null;
	}
	
	boolean validate() {
		// TODO
		return false;
	}
}
