package Availability;

import java.util.*;

import time.LocalTimeInterval;
import time.Interval_SF;

class AvailabilityList {
	protected List<LocalTimeInterval> list;
	protected final Interval_SF statusFlag;
	
	AvailabilityList(Interval_SF statusFlag) {
		list = new ArrayList<>();
		this.statusFlag = statusFlag;
		if (statusFlag.equals(Interval_SF.AVAILABLE)) {
			list.add(LocalTimeInterval.getAlwaysAvailable());
		}
	}
	
	// Note: This performs a shallow clone
	AvailabilityList(Collection<LocalTimeInterval> toAdd, Interval_SF statusFlag) {
		list = new ArrayList<>(toAdd);
		this.statusFlag = statusFlag;
	}
	
	Interval_SF getStatusFlag() {
		return statusFlag;
	}
	
	LocalTimeInterval[] toAvailable(LocalTimeInterval chunk) {
		// TODO
		return null;
	}
	
	boolean contains(LocalTimeInterval chunk) {
		// TODO
		return false;
	}
}
