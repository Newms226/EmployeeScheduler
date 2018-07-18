package daKingMaker;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import emp.Employee;
import emp.EmployeeSet;
import restaurant.PositionID;
import restaurant.PositionType;

public class WorkingAvailabilityMap<E extends Employee> {
	Map<E, byte[]> serverAvailability;
	Map<E, List<PositionType>> qualificationsMap;
	
	
	public static WorkingAvailabilityMap<E> buildFrom(EmployeeSet<E> employees) {
		WorkingAvailabilityMap toReturn = new WorkingAvailabilityMap();
//		toReturn.qualificationsMap = employees.mapWithID(Employee::getQualifications);
		// TODO
		return toReturn;
	}
	
	private WorkingAvailabilityMap() {
		// TODO Auto-generated constructor stub
		serverAvailability = new HashMap<>();
		qualificationsMap = new HashMap<>();
	}
	
	Integer getEmployee(PositionID<? extends Employee> ID) {
		/*TODO
		 * Return highest priority servers, working from availableServers[]
		 * > Fill Order: >> SORTED BY PRIORITY
		 *   1. Under Desired Hours
		 *   2. Under PERSONAL Max hours
		 *      >> Note that some servers are allowed overtime, and should have a personal > global max
		 *   3. Under Global Max hours
		 *   4. Return House Shift
		 *   
		 *Fill Process:
		 * - NEED:
		 *   > From the list of  
		 */
		
		return null;
	}
	
	private void fillAvailableServers() {
		
	}
	
	
	
	
}
