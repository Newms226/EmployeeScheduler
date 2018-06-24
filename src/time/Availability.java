package time;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import emp.Employee;
import restaurant.PositionID;

public class Availability {
	Map <Day, Map<SF, ? extends AbstractAvailabilityList>> map;
	TimeOffList timeOff;
	OutsideAvailabilityList neverAvailableList;
	
	public Availability() {
		map = new HashMap<>();
		Map<SF, ? extends AbstractAvailabilityList> mapToAdd;
		for (Day day: Day.values()) {
			mapToAdd = new HashMap<>();
			mapToAdd.put(SF.AVAILABLE, value)
			map.put(day, ;
		}
	}
	
	public boolean inAvailability(PositionID<? extends Employee> ID) {
		map.get(ID.getDay())
			.get(SF.AVAILABLE)
				.contains(ID.getTimeChunk());
		
		return false;
	}
	
	public void setAvailability(Day day, TimeChunk chunk, SF statusFlag) {
		
	}
	
	public void setAvailability(PositionID<? extends Employee> ID, TimeChunk chunk, SF statusFlag) {
		setAvailability(ID.getDay(), chunk, statusFlag);
	}
	
	public int condense() {
		// TODO
		return 0;
	}
	
	public void buildFrom(OutsideAvailabilityList outsideAvailList,
			              TimeOffList timeOffList) {
		// TODO
	}
	
	private void put(TimeChunk chunk) {
		// TODO
	}
	
	private void put(TimeChunk[] chunks) {
		for (TimeChunk chunk: chunks) put(chunk);
	}
}
