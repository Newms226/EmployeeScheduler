package WorkingSet;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.logging.Level;

import driver.Driver;
import emp.ClassNotEqualException;
import emp.Employee;
import emp.EmployeeSet;
import emp.HouseShift;
import restaurant.PositionID;
import tools.StringTools;
import util.Averager;

public class AssignmentOperation implements Operation<PositionID<? extends Employee>> {
	private PositionID<? extends Employee> ID;
	private LocalDateTime time;
	private Employee employee;
	private final QualifiedEmployeeList<? extends Employee> list;
	private final Averager averager;
	public final double currentAverage;
	
	public AssignmentOperation(PositionID<?> ID,
			            QualifiedEmployeeList<?> list,
			            Averager avg) {
		Driver.deciderLog.log(Level.FINER,
				"Building new AssignmentOperation", 
				new Object[] {ID, list, avg});
		try {
			ClassNotEqualException.assertEqual(ID.employeeType, list.employeeType());
		} catch (ClassNotEqualException e) {
			Driver.deciderLog.log(Level.SEVERE, e.getMessage(), e);
			throw e;
		}
		
		this.ID = ID;
		this.list = list;
		this.averager = avg;
		currentAverage = averager.average(); // TODO: Does this work right when the average is 0?
	}

	@Override
	public PositionID<? extends Employee> run() {
		Driver.deciderLog.entering(AssignmentOperation.class.getName(), "run");
		employee = list.getEmployee();
		if (employee == null) {
			ID = new PositionID<HouseShift>(HouseShift.class,
					                        ID.getDay(), 
					                        ID.getPositionType(), 
					                        ID.getShiftType(), 
					                        ID.getPriority());
			employee = new HouseShift();
			ID.assignEmployee(employee);
			Driver.deciderLog.log(Level.WARNING, "HOUSE ASSINGMENT: {0}", ID);
		} else {
			Driver.deciderLog.log(Level.FINE, "Regular assignment: {0}", ID);
			employee.accept(ID);
			averager.update(employee.updateEmployeePriority(currentAverage));
		}
		time = LocalDateTime.now();
		Driver.deciderLog.exiting(AssignmentOperation.class.getName(), "run");
		return ID;
	}

	@Override
	public boolean rollback() {
		Employee temp = ID.getEmplyee();
		temp.markAvailable(ID);
		ID.removeEmployee();
		Driver.deciderLog.log(Level.FINE,
				"{0} was rolled back. {1} is now available ({3})",
				new Object[] {ID, temp, temp.currentlyAvailableFor(ID)});
		return true;
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
		return employee + " assigned to " + ID + "\n  at " + time;
	}

	@Override
	public boolean equals(Object o) {
		if (o == this) return true;
		if (o == null) return false;
		
		if (!this.getClass().equals(o.getClass())) return false;
		
		AssignmentOperation that = (AssignmentOperation) o;
		
		if (!ID.equals(that.ID)) return false;
		if (!time.equals(that.time)) return false;
		if (!employee.equals(that.employee)) return false;
		if (!list.equals(that.list)) return false;
		if (!averager.equals(that.averager)) return false; // TODO: Do I really want to compare these?
		if (currentAverage != that.currentAverage) return false;
		
		return true;
	}

	@Override
	public int getIdentifier() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String toCSV() {
		Driver.deciderLog.finer("TO CSV: " + ID);
		return getClass().getCanonicalName() + "," + time + "," + employee.ID 
				+ ",{" + ID.toCSV() + "},{" + list.toCSV() + "}";
	}
	
	public static <E extends Employee> AssignmentOperation fromCSV(String str, EmployeeSet<E> list) {
		String[] components = str.split(",");
		AssignmentOperation toReturn = new AssignmentOperation(
				PositionID.fromCSV(StringTools.trimBraces(components[3])),
				QualifiedEmployeeList.fromCSV(StringTools.trimBraces(components[4])), 
				null);
		toReturn.employee = list.findEmployee(Integer.parseInt(components[2]));
		toReturn.time = LocalDateTime.parse(components[1]);
		return toReturn;
	}
	
	public PositionID<? extends Employee> getPositionID(){
		return ID;
	}
	
	public static void main(String[] args) {
		System.out.println(LocalDateTime.now().toString());
	}
}