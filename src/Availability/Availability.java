package Availability;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

import driver.Driver;
import time.LocalDateInterval;
import time.Week;

public class Availability {
	private static final Logger log = Driver.availabilityLog;
	
	PersistantWeeklyAvailability persistantAvail;
	TimeOffList timeOff;
	
	public Availability() {
		// TODO Auto-generated constructor stub
	}
	
	public WeekAvailability getWeekAvailability(Week week) {
		// TODO
		return null;
	}
	
	public boolean buildTimeOff() {
		// TODO
		return false;
	}
	
	public boolean setTimeOff(LocalDate date) {
		return timeOff.add(date);
	}
	
	public boolean setTimeOff(LocalDateInterval dateInterval) {
		return timeOff.addAll(dateInterval);
	}
	
	public boolean inTimeOff(LocalDate date) {
		return timeOff.contains(date);
	}
	
	public boolean setPersistantAvailability(TimeChunk chunk) {
		return persistantAvail.toAvailable(chunk);
	}
	
	public boolean isInsidePersistantAvailability(TimeChunk chunk) {
		return persistantAvail.isAvailable(chunk);
	}
	
	public String getPersistantTOTALAvailabilityAsString() {
		return AvailabilityArray
				.getTimeChunkListString(AvailabilityArray.getTimeChunks(persistantAvail),
						                "Total Persistant Availability");
	}
	
	public String getPersistantOUTSIDEAvailabilityString() {
		return AvailabilityArray
				.getTimeChunkListString(
						AvailabilityArray.getSpecificTimeChunks(persistantAvail, 
						                                        AvailabilityArray.NEVER_AVAILABLE),
						"Total Persistant Availability");
	}
	
	public String getPersistantINAvailablityString() {
		return AvailabilityArray
				.getTimeChunkListString(
						AvailabilityArray.getSpecificTimeChunks(persistantAvail, 
						                                        AvailabilityArray.AVAILABLE),
						"Total Persistant Availability");
	}
}
