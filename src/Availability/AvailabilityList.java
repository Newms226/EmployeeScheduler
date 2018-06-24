package time;

import java.util.*;

abstract class AbstractAvailabilityList {
	protected List<LocalTimeInterval> list;
	protected Time_Chunk_SF statusFlag;
	
	AbstractAvailabilityList() {
		list = new ArrayList<>();
	}
	
	AbstractAvailabilityList(Collection<LocalTimeInterval> toAdd) {
		list = new ArrayList<>(toAdd);
	}
	
	abstract LocalTimeInterval[] toAvailable(LocalTimeInterval chunk);
	
	abstract boolean contains(LocalTimeInterval chunk);
}
