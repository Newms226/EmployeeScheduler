package assignment;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.logging.Logger;

import Availability.SchedulableTimeChunk;
import driver.Driver;
import emp.Employee;
import emp.EmployeeSet;
import emp.QualifiedEmployeeList;
import util.Averager;

public class AssignmentOperation implements Operation<SchedulableTimeChunk>,
                                            Serializable {
	
	public static void main(String[] args) {
		System.out.println(LocalDateTime.now().toString());
	}
	
	/******************************************************************************
	 *                                                                            *
	 *                               Static Fields                                *
	 *                                                                            *
	 ******************************************************************************/

	private static final long serialVersionUID = -582379435943850821L;
	private static final Logger log = Driver.deciderLog;
	
	/******************************************************************************
	 *                                                                            *
	 *                        Instance Fields & Methods                           *
	 *                                                                            *
	 ******************************************************************************/
	
	private SchedulableTimeChunk chunk;
	private LocalDateTime time;
	private Employee employee;
	private final QualifiedEmployeeList list;
	
	public AssignmentOperation(SchedulableTimeChunk chunk,
			                   QualifiedEmployeeList list) {
		log.finest("CONSTRUCTOR: Building new AssignmentOperation"
				+ "\n\t" + chunk.getInfoString()
				+ "\n\t" + list);

		
		this.chunk = chunk;
		this.list = list;
	}
	
	public SchedulableTimeChunk getTimeChunk() {
		return chunk;
	}
	
	Employee getEmployee() {
		return employee;
	}
	
	QualifiedEmployeeList getQualifiedEmployeeList() {
		return list;
	}

	/******************************************************************************
	 *                                                                            *
	 *                            Override Methods                                *
	 *                                                                            *
	 ******************************************************************************/
	
	@Override
	public SchedulableTimeChunk run() {
		log.entering(AssignmentOperation.class.getName(), "run");
		
		employee = list.getEmployee();
		employee.accept(chunk);
		chunk.setEmployee(employee);
		
		time = LocalDateTime.now();
		log.exiting(AssignmentOperation.class.getName(), "run");
		return chunk;
	}

	@Override
	public boolean rollback() {
//		Employee temp = ID.getEmplyee();
//		temp.markAvailable(ID);
//		ID.removeEmployee();
//		Driver.deciderLog.log(Level.FINE,
//				"{0} was rolled back. {1} is now available ({3})",
//				new Object[] {ID, temp, temp.currentlyAvailableFor(ID)});
//		return true;
		// TODO
		return false;
	}

	@Override
	public boolean isRan() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isRolledBack() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public LocalDateTime getTime() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public String toString() {
		return employee + " assigned to " + chunk + "\n  at " + time;
	}

	@Override
	public boolean equals(Object o) {
		if (o == this) return true;
		if (o == null) return false;
		
		if (!this.getClass().equals(o.getClass())) return false;
		
		AssignmentOperation that = (AssignmentOperation) o;
		
		if (!chunk.equals(that.chunk)) return false;
		if (!time.equals(that.time)) return false;
		if (!employee.equals(that.employee)) return false;
		if (!list.equals(that.list)) return false;
		
		return true;
	}
}

//@Override
//public String toCSV() {
//	Driver.deciderLog.finer("TO CSV: " + ID);
//	return getClass().getCanonicalName() + "," + time + "," + employee.ID 
//			+ ",{" + ID.toCSV() + "},{" + list.toCSV() + "}";
//}
//
//public static <E extends Employee> AssignmentOperation fromCSV(String str, EmployeeSet<E> list) {
//	String[] components = str.split(",");
//	AssignmentOperation toReturn = new AssignmentOperation(
//			PositionID.fromCSV(StringTools.trimBraces(components[3])),
//			QualifiedEmployeeList.fromCSV(StringTools.trimBraces(components[4])), 
//			null);
//	toReturn.employee = list.findEmployee(Integer.parseInt(components[2]));
//	toReturn.time = LocalDateTime.parse(components[1]);
//	return toReturn;
//}
