package Availability;

import java.time.temporal.Temporal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Logger;

import driver.Driver;
import racer.StopWatch;
import time.AbstractInterval;

public abstract class AbstractIntervalList<I extends AbstractInterval<I, U>, U extends Temporal & Comparable<U>> implements IntervalList<I> {
	private static final long serialVersionUID = -423861268517598889L;

	protected static Logger log = Driver.availabilityLog;
	
	protected List<I> list;
	protected final Comparator<I> comparator;
	
	public AbstractIntervalList(Comparator<I> comparator) {
		this.comparator = comparator;
		list = new ArrayList<>();
	}
	
	protected abstract void condense(I start, I end);

	@Override
	public boolean add(I interval) {
		log.entering(this.getClass().getName(), "add(" + interval + ")");
//		
//		if (contains(interval)) {
//			log.log(Level.SEVERE,
//					"Attempted to add {0} when it was already present\n\tCurrent list: {1}",
//					new Object[] {interval, list.toString()});
//			return false;
//		}
		
		int index = getConflictsWith(interval);
		if (index == -1) {
			log.info(interval + " does not conflict with " + this);
			list.add(interval);
			if (list.size() > 0) {
				condense();
			}
		} else {
			I conflictsWith = list.remove(index);
			log.info(interval + " conflicts with " + conflictsWith);
			list.add(AbstractInterval.combined(conflictsWith, interval));
		}
		
		
		
		long startTime = System.nanoTime();
		list.sort(comparator);
		long endTime = System.nanoTime();
		log.info("Sorted list in " + StopWatch.nanosecondsToString(endTime - startTime)
				+ "\n\tCurrently: " + this);
		
		log.exiting(this.getClass().getName(), "add(" + interval + ")");
		return true;
	}

	@Override
	public boolean remove(I interval) {
		log.entering(this.getClass().getName(), "remove(" + interval + ")");
		
		int indexToRemove = getConflictsWith(interval);
		if (indexToRemove == -1) {
			return false;
		}
		
		// else
		log.fine("Removed " + list.remove(indexToRemove) + " at index " + indexToRemove);
		return true;
	}

	@Override
	public boolean containsConflicts(I interval) {
		log.entering(this.getClass().getName(), "contains(" + interval + ")");
		return getConflictsWith(interval) != -1;
	}

	@Override
	public int size() {
		return list.size();
	}
	
	@Override
	public int getConflictsWithAfter(I interval, int searchInclusive) {
		log.entering(this.getClass().getName(), "getContainingAfter(" + interval + ", " + searchInclusive + ")");
		
		I temp;
		for (int i = searchInclusive; i < list.size(); i++) {
			temp = list.get(i);
			if (temp.conflictsWith(interval)) {
				log.info("Found " + temp + " at index " + i + " which contained " + interval);
				return i;
			} 
		}
		log.info("Failed to find " + interval);
		return -1;
	}
	
	@Override
	public void sort() {
		list.sort(comparator);
	}
	
	@Override
	public String toString() {
		return list.toString();
	}
}
