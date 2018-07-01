package Availability;

import time.LocalTimeInterval;

import java.util.logging.Level;

import time.Availability_Status;

final class AvailabilityList extends AbstractIntervalList<LocalTimeInterval> {
	private static final long serialVersionUID = -5533798423626365230L;
	
	private final Availability_Status statusFlag;
	
	AvailabilityList(Availability_Status statusFlag) {
		super(LocalTimeInterval.NATURAL_ORDER);
		this.statusFlag = statusFlag;
		addGeneric();
	}
	
	void addGeneric() {
		if (statusFlag == Availability_Status.AVAILABLE) {
			list.add(LocalTimeInterval.getAlwaysAvailabile());
		}
	}
	
	Availability_Status getStatusFlag() {
		return statusFlag;
	}

	@Override
	public boolean condense() {
		log.entering(this.getClass().getName(), "condense");
		LocalTimeInterval first, second;
		boolean changes = false;
		for (int i = 0; i < list.size() - 1; i++) {
			first = list.get(i);
			second = list.get(i + 1);
			if (first.getEnd().compareTo(second.getStart()) == 0){
				log.info(first + " is directly adjecent to " + second);
				condense(first, second);
				changes = true;
			}
		}
		log.log(Level.FINER, "RETURNING after " + changes + " modifications");
		return changes;
	}
	
	private void condense(LocalTimeInterval start, LocalTimeInterval end) {
		log.finer("Internal condese of " + start + " & " + end);
		list.remove(end);
		list.remove(start);
		list.add(LocalTimeInterval.from(statusFlag, start.getStart(), end.getEnd()));
		log.exiting(this.getClass().getName(), "private condense");
	}

	@Override
	public boolean validate() {
		log.entering(this.getClass().getName(), "validate");
		LocalTimeInterval toTest;
		int overlap;
		for (int i = 0; i < list.size() - 1; i++) {
			toTest = list.get(i);
			if ((overlap = getContainingAfter(toTest, i + 1)) != -1) {
				log.warning("OVERLAP: " + toTest + " overlaps with " + list.get(overlap) + " at index " + overlap);
				return false;
			}
		}
		log.fine("Passed validate");
		return true;
	}

	@Override
	public void archive() {
		// TODO Auto-generated method stub
		
	}

}
