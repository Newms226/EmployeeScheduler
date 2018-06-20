
package decider;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.logging.Level;

import database.EmployeeSet;
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
	
	QualifiedEmployeeList(EmployeeSet<E> list, PositionID<E> ID, int globalMax) {
		Driver.deciderLog.log(Level.CONFIG, "Creating Qualified Employee list for {0}", ID);
		statusFlag = SF.BELLOW_DESIRED;
		this.ID = ID;
		this.employeeType = list.employeeType;
		workingList = new HashSet<E>(fill(list));
		this.globalMax = globalMax;
	}
	
	Set<E> fill(EmployeeSet<E> list) {
		Driver.deciderLog.log(Level.FINER, "ENTERING: QualifiedEmployeeList.fill()");
		return list.filter(s -> s.canWork(ID));
	}

	Employee getEmployee() {
		Driver.deciderLog.log(Level.FINER, 
				"ENTERING: getEmployee for {0} presently {1}", 
				new Object[] {ID, statusFlag});
		Optional<E> optionalEmployee = Optional.empty();
		if (statusFlag.equals(SF.BELLOW_DESIRED)) {
			optionalEmployee = conditionalFill(emp -> emp.currentlyAvailableFor(ID));
			if (!optionalEmployee.isPresent()) {
				statusFlag = SF.BELLOW_PERSONAL_MAX;
			}
		}
		if (statusFlag.equals(SF.BELLOW_PERSONAL_MAX)) {
			optionalEmployee = conditionalFill(emp -> emp.currentlyAvailableFor(ID) && emp.bellowPersonalMax());
			if (!optionalEmployee.isPresent()) {
				statusFlag = SF.BELLOW_GLOBAL_MAX;
			}
		}
		if (statusFlag.equals(SF.BELLOW_GLOBAL_MAX)) {
			optionalEmployee = conditionalFill(emp -> emp.currentlyAvailableFor(ID) && emp.bellowGlobalMax(globalMax));
			if (!optionalEmployee.isPresent()) {
				statusFlag = (SF.HOUSE_ONLY);
			}
		}
		
		Driver.deciderLog.log(Level.FINER, 
				"Leaving: getEmployee for {0} presently {1} found {2}", 
				new Object[] {ID, 
						statusFlag, 
						optionalEmployee.isPresent()});
		
		if (optionalEmployee.isPresent()) {
			return optionalEmployee.get();
		} else {
			return null;
		}
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
		StringBuffer buffer = new StringBuffer("(" + statusFlag.name() + "){");
		workingList.stream()
			.forEach(emp -> buffer.append("[" + emp.ID + "," + emp.getCurrentPrioirty() + "],"));
		return StringTools.removeLastComma(buffer).concat("}");
	}
	
	public static <E extends Employee> QualifiedEmployeeList<E> fromCSV(String string){
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
