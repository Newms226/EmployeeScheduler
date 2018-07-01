package Availability;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import driver.Driver;
import racer.StopWatch;
import time.Interval;

public abstract class AbstractIntervalList<I extends Interval<I>> implements IntervalList<I> {
	private static final long serialVersionUID = -423861268517598889L;

	protected static Logger log = Driver.availabilityLog;
	
	protected List<I> list;
	protected final Comparator<I> comparator;
	
	public AbstractIntervalList(Comparator<I> comparator) {
		this.comparator = comparator;
		list = new ArrayList<>();
	}

	@Override
	public boolean add(I interval) {
		log.entering(this.getClass().getName(), "add(" + interval + ")");
		
		if (contains(interval)) {
			log.log(Level.SEVERE,
					"Attempted to add {0} when it was already present\n\tCurrent list: {1}",
					new Object[] {interval, list.toString()});
			return false;
		}
		
		//TODO: Auto condense
		
		// else
		log.log(Level.FINE,
				"Added {0}",
				interval);
		list.add(interval);
		
		long startTime = System.nanoTime();
		list.sort(comparator);
		long endTime = System.nanoTime();
		log.info("Sorted list in " + StopWatch.nanosecondsToString(endTime - startTime));
		
		log.exiting(this.getClass().getName(), "add(" + interval + ")");
		return true;
	}

	@Override
	public boolean remove(I interval) {
		log.entering(this.getClass().getName(), "remove(" + interval + ")");
		
		int indexToRemove = getContaining(interval);
		if (indexToRemove == -1) {
			return false;
		}
		
		// else
		log.fine("Removed " + list.remove(indexToRemove) + " at index " + indexToRemove);
		return true;
	}

	@Override
	public boolean contains(I interval) {
		log.entering(this.getClass().getName(), "contains(" + interval + ")");
		return getContaining(interval) != -1;
	}

	@Override
	public int size() {
		return list.size();
	}
	
	@Override
	public int getContainingAfter(I interval, int searchInclusive) {
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
}
