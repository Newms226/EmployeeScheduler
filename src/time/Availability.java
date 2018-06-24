package time;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import emp.Employee;
import restaurant.PositionID;

public class Availability {
	Map <Day, List<? extends TimeChunk>> map;
	
	public Availability() {
		map = new HashMap<>();
	}
	
	public boolean inAvailability(PositionID<? extends Employee> ID) {
		if (!map.containsKey(ID.getDay())) return true;
		// TODO
		return false;
	}
	
	public void setAvailability(Day day, TimeChunk chunk, TimeChunk.SF statusFlag) {
		
	}
	
	public void setAvailability(PositionID<? extends Employee> ID, TimeChunk chunk, TimeChunk.SF statusFlag) {
		setAvailability(ID.getDay(), chunk, statusFlag);
	}
	
	public int condense() {
		// TODO
		return 0;
	}
}
