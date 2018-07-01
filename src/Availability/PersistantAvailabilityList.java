package Availability;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import driver.Driver;

import java.io.Serializable;
import java.time.*;

import time.Availability_Status;
import time.LocalTimeInterval;
import time.Week;

public class PersistantAvailabilityList implements Serializable {
	private static final long serialVersionUID = -8000329698205239191L;
	protected static Logger log = Driver.availabilityLog;
	
	Map<DayOfWeek, AvailabilityList> weeklyAvailabilityMap;

	public PersistantAvailabilityList() {
		weeklyAvailabilityMap = new HashMap<>();
	}
	
	public Map <LocalDate, Map<Availability_Status, AvailabilityList>> buildWeek(Week weekToBuild){
		// TODO
		return null;
	}

	public boolean add(DayOfWeek dayOfWeek, LocalTimeInterval interval) {
		log.entering(this.getClass().getName(), "add(" + dayOfWeek + ", " + interval + ")");
		
		if (interval.getStatusFlag() != Availability_Status.OUTSIDE_AVAILABILITY) {
			log.severe("Attempted to add an interval which was not " + Availability_Status.OUTSIDE_AVAILABILITY.name()
							+ ".\n\tWas: " + interval.getStatusFlag().name() + "\n\t Returning FALSE");
			return false;
		}
		
		// else:
		AvailabilityList toAddTo = weeklyAvailabilityMap.get(dayOfWeek);
		if (toAddTo == null) {
			log.log(Level.INFO, "Map for " + dayOfWeek + " was NOT already available.");
			toAddTo = new AvailabilityList(Availability_Status.OUTSIDE_AVAILABILITY);
			toAddTo.add(interval);
			weeklyAvailabilityMap.put(dayOfWeek, toAddTo);
		} else {
			log.log(Level.INFO, "Map for " + dayOfWeek + " was already available.");
			toAddTo.add(interval);
		}
		
		log.fine("Successfully added " + interval + " to " + dayOfWeek + " mapping.\n\tPresently: " + toAddTo);
		return true;
		
	}

	public boolean remove(DayOfWeek dayOfWeek, LocalTimeInterval interval) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean contains(DayOfWeek dayOfWeek, LocalTimeInterval interval) {
		// TODO Auto-generated method stub
		return false;
	}
	
	public boolean condense() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean validate() {
		// TODO Auto-generated method stub
		return false;
	}

	public void archive() {
		// TODO Auto-generated method stub
		
	}

	public String toString() {
		return weeklyAvailabilityMap.toString();
	}
}
