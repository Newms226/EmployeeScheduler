package Availability;

import java.io.Serializable;

import time.Interval;

public interface IntervalList <INTERVAL extends Interval<INTERVAL>> extends Serializable {
	
	public boolean add(INTERVAL interval);
	
	public boolean remove(INTERVAL interval);
	
	public boolean containsConflicts(INTERVAL interval);
	
	public default int getConflictsWith(INTERVAL interval) {
		return getConflictsWithAfter(interval, 0);
	}
	
	public int getConflictsWithAfter(INTERVAL interval, int searchInclusive);
	
	public boolean condense();
	
	public boolean validate();
	
	public int size();
	
	public default boolean isEmpty() {
		return size() == 0;
	}
	
	public void sort();
	
	public void archive();
	
}
