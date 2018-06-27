package Availability;

import java.time.Duration;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import driver.Driver;
import racer.StopWatch;
import time.LocalTimeInterval;
import time.Interval_SF;

class AvailabilityList {
	protected List<LocalTimeInterval> list;
	protected final Interval_SF statusFlag;
	private static Logger log = Driver.availabilityLog;
	
	
	AvailabilityList(Interval_SF statusFlag) {
		list = new ArrayList<>();
		this.statusFlag = statusFlag;
		
	}
	
	// Note: This performs a shallow clone
	AvailabilityList(Collection<LocalTimeInterval> toAdd, Interval_SF statusFlag) {
		list = new ArrayList<>();
		this.statusFlag = statusFlag;
	}
	
	void addGeneric(LocalDate date) {
		if (statusFlag == Interval_SF.AVAILABLE) {
			list.add(LocalTimeInterval.getAlwaysAvailable(date));
		}
	}
	
	boolean add(LocalTimeInterval interval) {
		log.entering(this.getClass().getName(), "add: SF = " + statusFlag.name());
		NotEqualException.assertEqual(statusFlag, interval.getStatusFlag());
		
		if (contains(interval)) {
			log.log(Level.SEVERE,
					"Attempted to add {0} when it was already present",
					interval);
			return false;
		}
		
		// else
		log.log(Level.FINE,
				"Added {0}",
				interval);
		list.add(interval);
		
		long startTime = System.nanoTime();
		list.sort(LocalTimeInterval.NATURAL_ORDER);
		long endTime = System.nanoTime();
		log.info("Sorted list in " + StopWatch.nanosecondsToString(endTime - startTime));
		
		return true;
	}
	
	Interval_SF getStatusFlag() {
		return statusFlag;
	}
	
	boolean contains(LocalTimeInterval searchFor) {
		for (LocalTimeInterval interval: list) {
			if (interval.contains(searchFor)) return true;
		}
		return false;
	}
}
