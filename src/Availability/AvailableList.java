package time;

class AvailableList extends AbstractAvailabilityList {

	AvailableList() {
		super();
		list.add(LocalTimeInterval.getAlwaysAvailable());
	}
	
	AvailableList(AvailableList listToAdd) {
		super(listToAdd.list);
	}
	

	LocalTimeInterval[] toScheduled(LocalTimeInterval chunk) {
		// TODO
		return null;
	}
	
	LocalTimeInterval[] toOutsideAvailability(LocalTimeInterval chunk) {
		// TODO
		return null;
	}
	
	LocalTimeInterval[] toTimeOff(LocalTimeInterval chunk) {
		// TODO
		return null;
	}

	@Override
	LocalTimeInterval[] toAvailable(LocalTimeInterval chunk) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	boolean contains(LocalTimeInterval chunk) {
		// TODO Auto-generated method stub
		return false;
	}
}
