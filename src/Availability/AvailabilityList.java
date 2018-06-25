package Availability;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import driver.Driver;
import time.LocalTimeInterval;
import time.TimeInterval;
import time.Interval_SF;

class AvailabilityList {
	protected List<TimeInterval> list;
	protected final Interval_SF statusFlag;
	private static Logger log = Driver.availabilityLog;
	
	
	AvailabilityList(Interval_SF statusFlag) {
		list = new ArrayList<>();
		this.statusFlag = statusFlag;
		if (statusFlag.equals(Interval_SF.AVAILABLE)) {
			list.add(LocalTimeInterval.getAlwaysAvailable());
		}
	}
	
	// Note: This performs a shallow clone
	AvailabilityList(Collection<TimeInterval> toAdd, Interval_SF statusFlag) {
		list = new ArrayList<>();
		this.statusFlag = statusFlag;
	}
	
	boolean add(TimeInterval interval) {
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
		return true;
	}
	
	Interval_SF getStatusFlag() {
		return statusFlag;
	}
	
	boolean contains(TimeInterval chunk) {
		// TODO
		return false;
	}
}
