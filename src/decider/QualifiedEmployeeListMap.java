package decider;
import java.util.Map;
import java.util.TreeMap;

import database.EmployeeList;
import database.PositionID;
import database.PositionID.ShiftID;
import driver.Driver;
import emp.ClassNotEqualException;
import emp.Employee;

public class QualifiedEmployeeListMap<E extends Employee> {

	Map <ShiftID, QualifiedEmployeeList<E>> availabilityMap;
	EmployeeList<E> list;
	final int globalMax;
	
	QualifiedEmployeeListMap(EmployeeList<E> list, int globalMax) {
		this.list = list;
		availabilityMap = new TreeMap<ShiftID, QualifiedEmployeeList<E>>(PositionID.SHIFT_ID_ORDER);
		this.globalMax = globalMax;
	}
	
	// TODO: Thought: You could generate all the possible mappings at once with stream.map
	// Would it be faster to just work from a Map<PositionID, List> ???
	@SuppressWarnings("unchecked")
	QualifiedEmployeeList<? extends Employee> getList(PositionID<? extends Employee> ID) {
		ClassNotEqualException.assertEqual(list.employeeType, ID.employeeType);
		
		ShiftID tempID = ID.extractShiftID();
		if (Driver.debugging) System.out.println("  In getMap() for " + tempID);
		QualifiedEmployeeList<E> toReturn = availabilityMap.get(tempID);
		if (toReturn == null) {
			if (Driver.debugging) System.out.println("" + ID + " map is NOT present");
			toReturn = new QualifiedEmployeeList<E>(list, (PositionID<E>) ID, globalMax);
			availabilityMap.put(tempID, toReturn);
		} else {
			if (Driver.debugging) System.out.println("   " + ID + " map is present");
		}
		return toReturn;
	}
	
	public String toCSV() {
		if (availabilityMap.isEmpty()) return "EMPTY MAP\n";
		StringBuffer buffer = new StringBuffer();
		availabilityMap.entrySet().stream()
			.forEach(entry -> buffer.append(entry.getKey().toCSV() + entry.getValue().toCSV() + "\n" ));
		return buffer.toString();
	}
	
//	public void removeEmployeeFromDay(E employee, PositionID<? extends Employee> ID) {
//		availabilityMap.forEach( (k, m) -> {
//			if (m.ofDay(ID) && m.contains(employee)) m.remove(ID);
//		};
//	}
	
	public int getQueueCount() {
		return availabilityMap.size();
	}
}
