
package emp;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import Availability.SchedulableTimeChunk;
import assignment.AssignmentStatusFlag;
import driver.Driver;
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
	
	private List<Employee> workingList;
	
	private AssignmentStatusFlag statusFlag;
	private boolean found;
	
	QualifiedEmployeeList(EmployeeSet list, SchedulableTimeChunk chunk) {
		log.log(Level.CONFIG, "Creating Qualified Employee list for {0}", chunk);
	
		this.chunk = chunk;
		this.positionType = chunk.positionType;
		statusFlag = AssignmentStatusFlag.BELLOW_PERSONAL_MIN;
		workingList = new ArrayList<Employee>(fill(list));
		log.fine("RESULT: Created a list of size " + workingList.size() + " from an employee set of size " + list.count());
//		if (workingList.size() > 0 ) found = true;
		// TODO: Note that this will cause if errors, if calling method does not immpediately
		// ask for the first employee
	}
	
	Set<Employee> fill(EmployeeSet list) {
		log.log(Level.FINER, "ENTERING: QualifiedEmployeeList.fill()");
		return list.filter(s -> /*s.bellowMinimumHours() && */ s.canWork(chunk));
	}
	
	public Employee getEmployee() {
		return getEmployee(statusFlag);
	}
	
	private Optional<Employee> possibleEmployee;
	
	private Employee getEmployee(AssignmentStatusFlag statusFlag) {
		log.entering(getClass().getName(), "getEmployee(" + statusFlag + ")");
		if (statusFlag == AssignmentStatusFlag.HOUSE_ONLY) return new HouseShift();
		
//		updateList();
		if (workingList.isEmpty()) {
			log.warning("EMPTY: Qualified Employee list for " + chunk.getInfoString() + 
					" is now empty. Returning House");
			statusFlag = AssignmentStatusFlag.HOUSE_ONLY;
			return new HouseShift();
		}
		
		final AssignmentStatusFlag finalSF = statusFlag;
		possibleEmployee = workingList.stream()
			.filter(emp -> emp.queryMinutes(chunk, finalSF) && emp.availableToWork(chunk))
			.sorted(Employee.DESENDING_PRIORITY_ORDER)
			.findFirst();
		
		if (possibleEmployee.isPresent()) {
			log.fine("SUCCESS: Found " + possibleEmployee.get());
			return possibleEmployee.get();
		}
		
		// else
		log.fine("FAILURE: getEmployee(" + statusFlag + ")");
		this.statusFlag = this.statusFlag.downgrade();
		return getEmployee(this.statusFlag);
	}
	
	// TODO: add any removed employees to the op stack for future roll back!
	private void updateList() {
		workingList = workingList.stream()
				.filter(emp -> emp.availableToWork(chunk))
				.collect(Collectors.toList());
	}

//	Employee getEmployee() {
//		log.log(Level.FINER, 
//				"ENTERING: getEmployee for {0} presently {1}", 
//				new Object[] {chunk.getInfoString(), statusFlag});
//		
//		Optional<Employee> optionalEmployee = Optional.empty();
//	
//		
////		if (found) {
////			optionalEmployee = Optional.of(workingList.get(0));
////			log.fine("CONSTRUCTOR: Found: " + optionalEmployee.get() + " immediately");
////		} else {
////			optionalEmployee = Optional.empty();
////		}
//		
//		if (statusFlag == AssignmentStatusFlag.BELLOW_PERSONAL_MIN) {
//			optionalEmployee = conditionalFind(emp -> emp.bellowMinimumHours()
//					                           && emp.availableToWork(chunk));
//			if (optionalEmployee.isPresent()) {
//				found = true;
//			} else {
//				statusFlag = SF.BELLOW_DESIRED;
//				log.info("UPDATE SF: Bellow Personal Min > Bellow Desired\n"  + this);
//			}
//		}
//		
//		if (!found && statusFlag == SF.BELLOW_DESIRED) {
//			optionalEmployee = conditionalFind(emp -> emp.bellowDesiredHours() 
//					                           && emp.availableToWork(chunk));
//			if (optionalEmployee.isPresent()) {
//				found = true;
//			} else {
//				statusFlag = SF.BELLOW_PERSONAL_MAX;
//				log.info("UPDATE SF: Bellow Desired > Bellow Personal Max\n"  + this);
//			}
//		}
//		
//		if (!found && statusFlag == SF.BELLOW_PERSONAL_MAX) {
//			optionalEmployee = conditionalFind(emp -> emp.bellowPersonalMax()
//					                           && emp.availableToWork(chunk));
//			if (optionalEmployee.isPresent()) {
//				found = true;
//			} else {
//				statusFlag = SF.BELLOW_GLOBAL_MAX;
//				log.info("UPDATE SF: Bellow Personal Max > Bellow Global Max\n"  + this);
//			}
//		}
//		
//		if (!found && statusFlag == SF.BELLOW_GLOBAL_MAX) {
//			optionalEmployee = conditionalFind(emp -> emp.bellowGlobalMax()
//					                           && emp.availableToWork(chunk));
//			if (optionalEmployee.isPresent()) {
//				found = true;
//			} else {
//				statusFlag = (SF.HOUSE_ONLY);
//				log.info("UPDATE SF: Bellow Global Max > HOUSE ONLY\n"  + this);
//			}
//		}
//		
//		if (!found && statusFlag == SF.HOUSE_ONLY) {
//			optionalEmployee = Optional.of(new HouseShift());
//			log.info("SCHEDULED: HOUSE to " + chunk.getInfoString());
//		}
//		
//		found = false; // reset found;
//		
////		if (optionalEmployee.isPresent()) {
//			log.finer("RETURNING: getEmployee(" + chunk.getInfoString() + ") with " + optionalEmployee.get());
//			return optionalEmployee.get();
////		} else {
////			
////			return new HouseShift();
////		}
//	}
//	
//	Optional<Employee> conditionalFind(Predicate<Employee> predicate) {
//		return workingList.stream()
//			.filter(emp -> emp.capableOfWorking(chunk, statusFlag))
//			.sorted(Employee.DESENDING_PRIORITY_ORDER)
//			.findFirst();
//	}
	
	/******************************************************************************
	 *                                                                            *
	 *                       Getter & Setter Methods                              *
	 *                                                                            *
	 ******************************************************************************/
	
	public AssignmentStatusFlag getStatusFlag() {
		return statusFlag;
	}
	
	public <BUFFER extends CharSequence & Appendable & Serializable> void 
				appendProcessString(BUFFER buffer) throws IOException {
		workingList.sort(Employee.DESENDING_PRIORITY_ORDER);
		for (Employee e: workingList) {
			e.appendCurrentStatusString(buffer);
		}
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