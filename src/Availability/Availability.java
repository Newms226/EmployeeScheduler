package Availability;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;
import java.util.logging.Logger;

import driver.Driver;
import time.Week;

public class Availability {
	private static final Logger log = Driver.availabilityLog;
	
	PersistantWeeklyAvailability persistantAvail;
	TimeOffList timeOff;
	
	public Availability() {
		persistantAvail = new PersistantWeeklyAvailability();
		timeOff = new TimeOffList();
	}
	
	public WorkingAvailability getWorkingAvailability(Week week) {
		log.finer("CONSTRUCTOR: generating new WorkingAvailabilty for " + week);
		
		WorkingAvailability toReturn = new WorkingAvailability();
		for (LocalDate date: timeOff.getDaysOff(week)) {
			int day = TimeOffset.getIndexFromDayOfWeek(date.getDayOfWeek()) / TimeChunk.DAY_OFFSET;
			toReturn.toNotAvailable(TimeChunk.fromLocalTime(day, LocalTime.MIN, day, LocalTime.MAX));
		}
		for (LocalDate date: timeOff.getAvailableDays(week)) {
			int startPosition = TimeOffset.getIndexFromDayOfWeek(date.getDayOfWeek());
			System.arraycopy(persistantAvail.availability, 
					         startPosition,
					         toReturn.availability,
					         startPosition,
					         TimeChunk.DAY_OFFSET);
		}
		return toReturn;
	}
	
	public boolean buildTimeOff() {
		// TODO
		return false;
	}
	
	public boolean setTimeOff(LocalDate date) {
		return timeOff.add(date);
	}
	
	public boolean inTimeOff(LocalDate date) {
		return timeOff.contains(date);
	}
	
	public boolean setToPersistantlyAvailable(TimeChunk chunk) {
		return persistantAvail.toAvailable(chunk);
	}
	
	public boolean setToPersistantlyUNAVAILABLE(TimeChunk chunk) {
		return persistantAvail.toNEVERAvailable(chunk);
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
