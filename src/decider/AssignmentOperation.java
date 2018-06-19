package decider;

import java.time.LocalDate;
import java.time.LocalDateTime;

import database.EmployeeSet;
import database.PositionID;
import driver.Driver;
import emp.ClassNotEqualException;
import emp.Employee;
import emp.HouseShift;
import tools.StringTools;
import util.Averager;

public class AssignmentOperation implements Operation<PositionID<? extends Employee>> {
	private PositionID<? extends Employee> ID;
	private LocalDateTime time;
	private Employee employee;
	private final QualifiedEmployeeList<? extends Employee> list;
	private final Averager averager;
	public final double currentAverage;
	
	AssignmentOperation(PositionID<?> ID,
			            QualifiedEmployeeList<?> list,
			            Averager avg) {
		
		ClassNotEqualException.assertEqual(ID.employeeType, list.employeeType());
		
		this.ID = ID;
		this.list = list;
		this.averager = avg;
		currentAverage = averager.average(); // TODO: Does this work right when the average is 0?
	}

	@Override
	public PositionID<? extends Employee> run() {
		employee = list.getEmployee();
		if (employee == null) {
			ID = new PositionID<HouseShift>(HouseShift.class,
					                        ID.getDay(), 
					                        ID.getPositionType(), 
					                        ID.getShiftType(), 
					                        ID.getPriority());
			employee = new HouseShift();
			ID.assignEmployee(employee);
			if (Driver.debugging) System.out.println("HOUSE ASSINGMENT: " + ID);
		} else {
			employee.accept(ID);
			averager.update(employee.updateEmployeePriority(currentAverage));
		}
		time = LocalDateTime.now();
		return ID;
	}

	@Override
	public boolean rollback() {
		Employee temp = ID.getEmplyee();
		temp.markAvailable(ID);
		ID.removeEmployee();
		if (Driver.debugging) System.out.println("  " + ID + " was rolled back."
				+ "\n    " + temp + " is now available (" + temp.currentlyAvailableFor(ID) + ")" );
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
	public boolean equals(Object that) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getIdentifier() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String toCSV() {
//		if (Driver.debugging) System.out.println("TO CSV: " + ID);
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
