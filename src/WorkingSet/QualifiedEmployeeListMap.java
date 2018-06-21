package WorkingSet;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;

import driver.Driver;
import emp.ClassNotEqualException;
import emp.Employee;
import emp.EmployeeSet;
import restaurant.PositionID;
import restaurant.PositionID.ShiftID;

public class QualifiedEmployeeListMap<E extends Employee> {

	Map <ShiftID, QualifiedEmployeeList<E>> availabilityMap;
	EmployeeSet<E> list;
	final int globalMax;
	
	public QualifiedEmployeeListMap(EmployeeSet<E> list, int globalMax) {
		this.list = list;
		availabilityMap = new TreeMap<ShiftID, QualifiedEmployeeList<E>>(PositionID.SHIFT_ID_ORDER);
		this.globalMax = globalMax;
	}
	
	// TODO: Thought: You could generate all the possible mappings at once with stream.map
	// Would it be faster to just work from a Map<PositionID, List> ???
	@SuppressWarnings("unchecked")
	public QualifiedEmployeeList<? extends Employee> getList(PositionID<? extends Employee> ID) {
		try {
			ClassNotEqualException.assertEqual(list.employeeType, ID.employeeType);
		} catch (ClassNotEqualException e) {
			Driver.deciderLog.log(Level.SEVERE, e.getMessage(), e);
			throw e;
		}
		
		ShiftID tempID = ID.extractShiftID();
		Driver.deciderLog.log(Level.FINER, "ENTERNG: QualifiedEmployeeListMap.getList({0})", ID);
		QualifiedEmployeeList<E> toReturn = availabilityMap.get(tempID);
		if (toReturn == null) {
			Driver.deciderLog.log(Level.WARNING, "Mapping for {0} is not present", tempID);
			toReturn = new QualifiedEmployeeList<E>(list, (PositionID<E>) ID, globalMax);
			availabilityMap.put(tempID, toReturn);
		} else {
			Driver.deciderLog.log(Level.FINE, "{0} map is present", tempID);
		}
		Driver.deciderLog.log(Level.FINER, "RETURNING: QualifiedEmployeeListMap.getList({0})", ID);
		return toReturn;
	}
	
	public String toCSV() {
		Driver.deciderLog.entering(QualifiedEmployeeListMap.class.getName(), "toCSV");
		if (availabilityMap.isEmpty()) {
			Driver.deciderLog.warning("EMPTY MAP!");
			return "EMPTY MAP\n";
		}
		StringBuffer buffer = new StringBuffer();
		availabilityMap.entrySet().stream()
			.forEach(entry -> buffer.append(entry.getKey().toCSV() + entry.getValue().toCSV() + "\n" ));
		Driver.deciderLog.exiting(QualifiedEmployeeListMap.class.getName(), "toCSV");
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
