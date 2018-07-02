package Availability;

import java.util.*;
import java.util.Map.Entry;
import java.util.function.Predicate;
import java.util.logging.Logger;

import driver.Driver;

import java.io.Serializable;
import java.time.*;

import time.Availability_Status;
import time.Interval;
import time.LocalTimeInterval;

public class PersistantAvailabilityList implements Serializable {
	private static final long serialVersionUID = -8000329698205239191L;
	protected static Logger log = Driver.availabilityLog;
	
	Map<DayOfWeek, Map<Availability_Status, AvailabilityList>> weeklyAvailabilityMap;

	public PersistantAvailabilityList() {
		weeklyAvailabilityMap = new HashMap<>();
		Map<Availability_Status, AvailabilityList> tempMap;
		for (DayOfWeek dayOfWeek: DayOfWeek.values()) {
			tempMap = new HashMap<>();
			tempMap.put(Availability_Status.AVAILABLE, AvailabilityList.getAlways(Availability_Status.AVAILABLE));
			weeklyAvailabilityMap.put(dayOfWeek, tempMap);
			tempMap = new HashMap<>();
			tempMap.put(Availability_Status.OUTSIDE_AVAILABILITY, new AvailabilityList(Availability_Status.OUTSIDE_AVAILABILITY));
			weeklyAvailabilityMap.put(dayOfWeek, tempMap);
		}
		log.config("Generated new " + this.getClass().getName() + "\n" + weeklyAvailabilityMap);
	}
	
	public Map <LocalDate, Map<Availability_Status, AvailabilityList>> buildWeek(Set<LocalDate> availableDays, Set<LocalDate> daysOff){
		log.entering(this.getClass().getName(), "buildWeek()");
		Map <LocalDate, Map<Availability_Status, AvailabilityList>> toReturn = new HashMap<>();
		
		Map<Availability_Status, AvailabilityList> internalMap;
		for (LocalDate date: daysOff) {
			internalMap = new HashMap<>();
			internalMap.put(Availability_Status.OUTSIDE_AVAILABILITY, AvailabilityList.getAlways(Availability_Status.OUTSIDE_AVAILABILITY));
			toReturn.put(date, internalMap);
		}
		log.info("After Days off addition:\n  " + toReturn);
		
		for (LocalDate date: availableDays) {
			toReturn.put(date, weeklyAvailabilityMap.get(date.getDayOfWeek()));
		}
		log.info("Final result to return:\n" + toReturn);
		return toReturn;
	}
	
	private boolean ensureValidStatusFlag(String methodName, Availability_Status statusFlag) {
		if (!Interval.validateStatusFlag(this.getClass().getName() + "." + methodName, Availability_Status.OUTSIDE_AVAILABILITY, statusFlag, log)
					|| !Interval.validateStatusFlag(this.getClass().getName() + "." + methodName, Availability_Status.AVAILABLE, statusFlag, log)) {
			log.severe("RETURNING FALSE: expected " + Availability_Status.OUTSIDE_AVAILABILITY + " or " + Availability_Status.AVAILABLE 
					+ " found: " + statusFlag);
			return false;
		}
		return true;
	}
	
	private boolean addToAndRemoveFrom(String methodName, Availability_Status addTo, Availability_Status removeFrom, LocalTimeInterval interval, DayOfWeek dayOfWeek) {
		String qualifiedMethodName = methodName + "(" + dayOfWeek + ", " + interval + ")";
		log.entering(this.getClass().getName(), qualifiedMethodName);
		
		if (!ensureValidStatusFlag(methodName, interval.getStatusFlag())){
			return false;
		}
		
		if (!weeklyAvailabilityMap.get(dayOfWeek).get(addTo).add(interval)) {
			log.severe("RETURNING FALSE FROM" + qualifiedMethodName + " FAILED TO ADD TO LIST");
			return false;
		}
		if (!weeklyAvailabilityMap.get(dayOfWeek).get(removeFrom).remove(interval)) {
			log.severe("RETURNING FALSE FROM" + qualifiedMethodName + " FAILED TO REMOVE FROM LIST");
			return false;
		}
		
		log.fine("RETURN TRUE FROM: " + qualifiedMethodName + "\n\tPresently: " + weeklyAvailabilityMap);
		return true;
	}

	public boolean add(DayOfWeek dayOfWeek, LocalTimeInterval interval) {
		return addToAndRemoveFrom("add", interval.getStatusFlag(), Availability_Status.getInverse(interval.getStatusFlag()), interval, dayOfWeek);
		
	}

	public boolean remove(DayOfWeek dayOfWeek, LocalTimeInterval interval) {
		return addToAndRemoveFrom("remove", Availability_Status.getInverse(interval.getStatusFlag()), interval.getStatusFlag(), interval, dayOfWeek);
	}

	public boolean contains(DayOfWeek dayOfWeek, LocalTimeInterval interval) {
		log.entering(this.getClass().getName(), "contains(" + dayOfWeek + ", " + interval + ")");
		
		Availability_Status statusFlag = interval.getStatusFlag();
		if (!ensureValidStatusFlag("add", statusFlag)){
			return false;
		}
		
		AvailabilityList toSearch = weeklyAvailabilityMap.get(dayOfWeek).get(statusFlag);
		if (toSearch.containsConflicts(interval)) {
			log.info("RETURNING TRUE: " + toSearch + " contains " + interval);
			return true;
		}
		
		//else 
		log.info("RETURNING FALSE: " + toSearch + " does not contain " + interval);
		return false;
	}
	
	private boolean aggregateTestAndLog(Predicate<AvailabilityList> predicate, String methodName) {
		log.entering(this.getClass().getName(), methodName);
		boolean actionPerformed = false;
		int actions = 0;
		for (Entry<DayOfWeek, Map<Availability_Status, AvailabilityList>> topEntry : weeklyAvailabilityMap.entrySet()) {
			for (Entry<Availability_Status, AvailabilityList> entry: topEntry.getValue().entrySet()) {
				if (predicate.test(entry.getValue())) {
					actionPerformed = true;
					actions++;
					log.info(methodName + " performed on: " + topEntry.getKey() + " > " + entry.getKey());
				}
			}
		}
		log.finer("RETURNING FROM " + this.getClass().getName() + "." + methodName + " WITH VALUE " + actionPerformed + 
					(actionPerformed ? " OVER " + actions : ""));
		return actionPerformed;
	}
	
	public boolean condense() {
		return aggregateTestAndLog(availList -> availList.condense(), "condense()");
	}

	public boolean validate() {
		return aggregateTestAndLog(availList -> availList.validate(), "validate()");
	}

	public void archive() {
		// TODO Auto-generated method stub
		
	}

	public String toString() {
		return weeklyAvailabilityMap.toString();
	}
}
