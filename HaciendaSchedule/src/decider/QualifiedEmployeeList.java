
package decider;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

import database.EmployeeList;
import database.PositionID;
import driver.Driver;
import emp.Employee;
import tools.StringTools;

public class QualifiedEmployeeList<E extends Employee> {
	private static final long serialVersionUID = 4736584683492745894L;
	
	private SF statusFlag;
	private final Set<E> workingList;
	public final PositionID<E> ID;
	final int globalMax;
	public final Class<? extends Employee> employeeType;
	
	public enum SF {BELLOW_DESIRED, BELLOW_PERSONAL_MAX, BELLOW_GLOBAL_MAX, HOUSE_ONLY}
	
	QualifiedEmployeeList(EmployeeList<E> list, PositionID<E> ID, int globalMax) {
		if (Driver.debugging) new RuntimeException(" CREATING LIST FOR: " + ID).printStackTrace();
		statusFlag = SF.BELLOW_DESIRED;
		this.ID = ID;
		this.employeeType = list.employeeType;
		workingList = new HashSet<E>(fill(list));
		this.globalMax = globalMax;
	}
	
	Set<E> fill(EmployeeList<E> list) {
		return list.filter(s -> s.canWork(ID));
	}

	Employee getEmployee() {
		Optional<E> optionalEmployee;
		if (statusFlag.equals(SF.BELLOW_DESIRED)) {
			optionalEmployee = conditionalFill(emp -> emp.currentlyAvailableFor(ID));
			if (optionalEmployee.isPresent()) {
				return optionalEmployee.get();
			} else {
				statusFlag = SF.BELLOW_PERSONAL_MAX;
			}
		}
		if (statusFlag.equals(SF.BELLOW_PERSONAL_MAX)) {
			optionalEmployee = conditionalFill(emp -> emp.currentlyAvailableFor(ID) && emp.bellowPersonalMax());
			if (optionalEmployee.isPresent()) {
				return optionalEmployee.get();
			} else {
				statusFlag = SF.BELLOW_GLOBAL_MAX;
			}
		}
		if (statusFlag.equals(SF.BELLOW_GLOBAL_MAX)) {
			optionalEmployee = conditionalFill(emp -> emp.currentlyAvailableFor(ID) && emp.bellowGlobalMax(globalMax));
			if (optionalEmployee.isPresent()) {
				return optionalEmployee.get();
			} else {
				statusFlag = SF.BELLOW_GLOBAL_MAX;
			}
		}
		
		return null;
	}
	
	Optional<E> conditionalFill(Predicate<E> predicate) {
		return workingList.stream()
			.filter(predicate)
			.sorted(Employee.DESENDING_PRIORITY_ORDER)
			.findFirst();
	}
	
	public SF getStatusFlag() {
		return statusFlag;
	}
	
	public String toCSV() {
		StringBuffer buffer = new StringBuffer("(" + statusFlag.name() + ") {");
		workingList.stream()
			.mapToInt(emp -> emp.ID)
			.forEach(i -> buffer.append("[" + i + "],"));
		return StringTools.removeLastComma(buffer).concat("}\n");
	}
	
	public static <E extends Employee> QualifiedEmployeeList<E> fromCSV(String csvEncoded){
		// TODO
		return null;
	}
	
//	public boolean ofDay(PositionID<? extends Employee> ID) {
//		return ID.getDay().dayOfWeek == this.ID.getDay().dayOfWeek;
//	}
	
	public Class<? extends Employee> employeeType(){
		return employeeType;
	}
	
//	void update(Averager avg) {
//		final double averageFill = avg.average();
//		super.stream()
//			.forEach(s -> s.employeePriority.getCurrentPriority(averageFill));
//	}
}
