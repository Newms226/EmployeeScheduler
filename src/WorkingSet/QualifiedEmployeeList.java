
package WorkingSet;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;

import Availability.SchedulableTimeChunk;
import driver.Driver;
import emp.Employee;
import emp.EmployeeSet;
import emp.HouseShift;
import restaurant.PositionType;

public class QualifiedEmployeeList implements Serializable {
	
	/******************************************************************************
	 *                                                                            *
	 *                               Static Fields                                *
	 *                                                                            *
	 ******************************************************************************/
	private static final long serialVersionUID = 4736584683492745894L;
	private static final Logger log = Driver.deciderLog;
	
	/******************************************************************************
	 *                                                                            *
	 *                                                                            *
	 *                        Instance Fields & Methods                           *
	 *                                                                            *
	 *                                                                            *
	 ******************************************************************************/
	
	public final PositionType positionType;
	public final SchedulableTimeChunk chunk;
	
	private final List<Employee> workingList;
	
	private SF statusFlag;
	private boolean found;

	private enum SF {
		BELLOW_DESIRED, 
		BELLOW_PERSONAL_MAX, 
		BELLOW_GLOBAL_MAX, 
		HOUSE_ONLY
	}
	
	QualifiedEmployeeList(EmployeeSet list, SchedulableTimeChunk chunk) {
		log.log(Level.CONFIG, "Creating Qualified Employee list for {0}", chunk);
	
		this.chunk = chunk;
		this.positionType = chunk.positionType;
		statusFlag = SF.BELLOW_DESIRED;
		workingList = new ArrayList<Employee>(fill(list));
		if (workingList.size() > 0 ) found = true;
		// TODO: Note that this will cause if errors, if calling method does not immpediately
		// ask for the first employee
	}
	
	Set<Employee> fill(EmployeeSet list) {
		log.log(Level.FINER, "ENTERING: QualifiedEmployeeList.fill()");
		return list.filter(s -> s.bellowMinimumHours() && s.canWork(chunk));
	}

	@SuppressWarnings("static-access")
	Employee getEmployee() {
		log.log(Level.FINER, 
				"ENTERING: getEmployee for {0} presently {1}", 
				new Object[] {chunk, statusFlag});
		
		Optional<Employee> optionalEmployee;
		
		if (found) {
			optionalEmployee = Optional.of(workingList.get(0));
			log.fine("CONSTRUCTOR: Found: " + optionalEmployee.get() + " immediately");
		} else {
			optionalEmployee = Optional.empty();
		}
		
		if (statusFlag == SF.BELLOW_DESIRED) {
			optionalEmployee = conditionalFill(emp -> emp.bellowDesiredHours() 
					                           && emp.availableToWork(chunk));
			if (optionalEmployee.isPresent()) {
				found = true;
			} else {
				statusFlag = SF.BELLOW_PERSONAL_MAX;
				log.info("UPDATE SF: Bellow Desired > Bellow Personal Max\n"  + this);
			}
		}
		
		if (!found && statusFlag == SF.BELLOW_PERSONAL_MAX) {
			optionalEmployee = conditionalFill(emp -> emp.bellowPersonalMax()
					                           && emp.availableToWork(chunk));
			if (optionalEmployee.isPresent()) {
				found = true;
			} else {
				statusFlag = SF.BELLOW_GLOBAL_MAX;
				log.info("UPDATE SF: Bellow Personal Max > Bellow Global Max\n"  + this);
			}
		}
		
		if (!found && statusFlag == SF.BELLOW_GLOBAL_MAX) {
			optionalEmployee = conditionalFill(emp -> emp.bellowGlobalMax()
					                           && emp.availableToWork(chunk));
			if (optionalEmployee.isPresent()) {
				found = true;
			} else {
				statusFlag = (SF.HOUSE_ONLY);
				log.info("UPDATE SF: Bellow Global Max > HOUSE ONLY\n"  + this);
			}
		}
		
		if (!found && statusFlag == SF.HOUSE_ONLY) {
			optionalEmployee = Optional.of(new HouseShift());
			log.info("SCHEDULED: HOUSE to " + chunk);
		}
		
		found = false; // reset found;
		
//		if (optionalEmployee.isPresent()) {
			log.finer("RETURNING: getEmployee(" + chunk + ") with " + optionalEmployee.get());
			return optionalEmployee.get();
//		} else {
//			
//			return new HouseShift();
//		}
	}
	
	Optional<Employee> conditionalFill(Predicate<Employee> predicate) {
		return workingList.stream()
			.filter(predicate)
			.sorted(Employee.DESENDING_PRIORITY_ORDER)
			.findFirst();
	}
	
	/******************************************************************************
	 *                                                                            *
	 *                       Getter & Setter Methods                              *
	 *                                                                            *
	 ******************************************************************************/
	
	public SF getStatusFlag() {
		return statusFlag;
	}
	
	/******************************************************************************
	 *                                                                            *
	 *                              Override Methods                              *
	 *                                                                            *
	 ******************************************************************************/
	
	@Override
	public String toString() {
		return "Qualified Employee list for " + chunk 
				+ "\n  Size: " + workingList.size() + " StatusFlag: " + statusFlag;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o == this) return true;
		if (o == null) return false;
		
		if (!this.getClass().equals(o.getClass())) return false;
		
		QualifiedEmployeeList that = (QualifiedEmployeeList) o;
		
		if (statusFlag != that.statusFlag) return false;
		if (!chunk.equals(that.chunk)) return false;
		if (!workingList.containsAll(that.workingList)) return false;
		
		return true;
	}
}
//public boolean ofDay(PositionID<? extends Employee> ID) {
//return ID.getDay().dayOfWeek == this.ID.getDay().dayOfWeek;
//}
//void update(Averager avg) {
//final double averageFill = avg.average();
//super.stream()
//	.forEach(s -> s.employeePriority.getCurrentPriority(averageFill));
//}
//public String toCSV() {
//	StringBuffer buffer = new StringBuffer("(" + statusFlag.name() + "){");
//	workingList.stream()
//		.forEach(emp -> buffer.append("[" + emp.ID + "," + emp.getCurrentPrioirty() + "],"));
//	return StringTools.removeLastComma(buffer).concat("}");
//}
//
//public static <E extends Employee> QualifiedEmployeeList<E> fromCSV(String string){
//	// TODO
//	return null;
//}