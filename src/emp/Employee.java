package emp;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;

import Menu.Menu;
import MyTime.Day;
import database.PositionID;
import driver.Driver;
import driver.Restaurant;
import tools.ArrayTools;

public abstract class Employee implements Comparable<Employee>, Serializable {
	
	private static final long serialVersionUID = 9156517117805277061L;
	public static final Comparator<Employee> DESENDING_PRIORITY_ORDER = (a, b) -> -a.compareTo(b);
	public static final Comparator<Employee> NAME_ORDER = (a, b) -> a.NAME.compareToIgnoreCase(b.NAME);
	public static final Comparator<Employee> PRIORITY_ORDER = (a, b) -> a.compareTo(b);
	
	public final int ID;
	public final String NAME;
	public final LocalDate START_DATE;
	
	private double PAY;
	
	private boolean[][] POSSIBLE_HOURS;
	protected boolean[][] availableHours;
	
	public transient EmployeePriority employeePriority; // TODO: Transient?
	
	
	private int MAX_HOURS,
	            DESIRED_HOURS,
	            MIN_HOURS;
	private double currentHours;
	
	int filledShifts;
	public final Restaurant restaurant;
	
	Menu serverMod;
	
	ArrayList<PositionType> qualifiedFor;
	ArrayList<PositionID<? extends Employee>> assignedShifts;
	
	protected EmployeeType employeeType;
	protected Class<? extends Employee> genericType;
	
	static boolean[][] getDefaultAvail(){
		boolean[][] toReturn =  {{true, true},
	        {true, true},
	        {true, true},
	        {true, true},
	        {true, true},
	        {true, true},
	        {true, true}};
		return toReturn;
	}
	
	Employee(String name, int ID, LocalDate startDate, ArrayList<PositionType> possibleShifts) {
		this (name, ID, startDate, getDefaultAvail(), 0, 40, 35, 0, possibleShifts);
	}
	
	Employee(String name, int ID, LocalDate startDate, boolean[][] possibleHours,
			double pay, int maxHours, int desiredHours, int minHours,
			ArrayList<PositionType> possibleShifts)
	{
		this.NAME = name;
		this.ID = ID;
		this.POSSIBLE_HOURS = possibleHours;
		this.availableHours = ArrayTools.copy2D(possibleHours);
		this.PAY = pay;
		this.MAX_HOURS = maxHours;
		this.DESIRED_HOURS = desiredHours;
		this.MIN_HOURS = minHours;
		this.qualifiedFor = possibleShifts;
		this.START_DATE = startDate;
		this.restaurant = Restaurant.HACIENDA;
		employeePriority = new EmployeePriority(this);
		
		serverMod = new Menu(NAME + " modification menu. What would you like to change?");
		serverMod.add("Change available hours", null);
		// TODO
		
		assignedShifts = new ArrayList<>();
	}
	
//	public static Employee getHouseShift() {
//		Employee house = new Employee("House Shift", -1, LocalDate.now(), PositionType.getHouseQualList());
//		house.employeeType = EmployeeType.House;
//		return house;
//	}
	
	public double getFillCount() {
		return filledShifts;
	}
	
	public void lowPromote() {
		employeePriority.internalSetGrace(EmployeePriority.LOW_PROMOTE_GRACE);
	}
	
	public void promote() {
		employeePriority.internalSetGrace(EmployeePriority.MID_PROMOTE_GRACE);
	}
	
	public void fastPromote() {
		employeePriority.internalSetGrace(EmployeePriority.HIGH_PROMOTE_GRACE);
	}
	
	public String toString() {
		return NAME;
	}
	
	public String toCSV() {
		return ID + "," + NAME + "," + START_DATE + ","  + PAY + ","
				+ ArrayTools.print2D(POSSIBLE_HOURS, false) + ","
				+ currentHours + "," + MAX_HOURS + "," + DESIRED_HOURS + "," + MIN_HOURS + ","
				+ qualifiedFor.toString().replaceAll(" ", "");
	}
	
	public static <E extends Employee> E fromCSV(Class<E> employeeType) {
		if (employeeType.equals(Server.class)) {
			return Server.fromCSV(employeeType);
		}
		throw new Error("Bottomed out in Employee.fromCSV()");
	}
	
	public boolean everAvailableFor(PositionID<? extends Employee> ID) {
		if (Driver.debugging) System.out.println("   Testing: " + NAME + " ever available for " + ID);
		if (POSSIBLE_HOURS[ID.getDay().dayOfWeek][ID.getShiftType().ordinal()]) {
			if (Driver.debugging) System.out.println("    PASS");
			return true;
		}
		// else
		if (Driver.debugging) System.out.println("    FAIL");
		return false;
	}
	
	public boolean currentlyAvailableFor(PositionID<? extends Employee> ID) {
		if (Driver.debugging) System.out.println("   Testing: " + NAME + " currenlty available for " + ID);
		if (availableHours[ID.getDay().dayOfWeek][ID.getShiftType().ordinal()]) {
			if (Driver.debugging) System.out.println("    PASS");
			return true;
		}
		// else
		if (Driver.debugging) System.out.println("    FAIL");
		return false;
	}
	
	public boolean qualifiedFor(PositionID<? extends Employee> ID) {
		if (Driver.debugging) System.out.println("   Testing: " + NAME + " can work a " + ID.getPositionType() + " shift");
		if (qualifiedFor.contains(ID.getPositionType())) {
			if (Driver.debugging) System.out.println("    PASS");
			return true;
		}
		// else
		if (Driver.debugging) System.out.println("    FAIL");
		return false;
	}
	
	public boolean canWork(PositionID<? extends Employee> ID) {
		if (Driver.debugging) System.out.println("  Testing " + NAME + " on ID: " + ID);
	
		// TESTS: TODO: If you ask can Server x work a Cook shift, will that fail?
		if (!qualifiedFor(ID)) return false;
		if (!currentlyAvailableFor(ID)) return false;
		
		// PASSED:
		if (Driver.debugging) System.out.println("  FULL PASS:" + NAME + " can work " + ID /*+ " Presently: " +
					this.availableHours[ID.getDay().dayOfWeek][ID.getShiftType().ordinal()]*/);
		return true;
	}
	
	public boolean bellowDesiredHours() {
		return currentHours < DESIRED_HOURS;
	}
	
	public boolean bellowPersonalMax() {
		return currentHours < MAX_HOURS;
	}
	
	public boolean bellowGlobalMax(int globalMax) {
		return currentHours < globalMax;
	}
	
	public abstract void accept(PositionID<? extends Employee> ID);
	
	public abstract void rollBack(PositionID<? extends Employee> ID);
	
	public abstract void markAvailable(PositionID<? extends Employee> ID);
	
	public abstract void markUnavailable(PositionID<? extends Employee> ID);
	
	public void changeQualifications() {
		if (Driver.debugging) {
			System.out.println("Changing " + NAME + " qualifications. Present:\n  ");
			qualifiedFor.stream().forEach(s-> System.out.print(s.toString()));
			System.out.println();
		}
		
		qualifiedFor = PositionType.buildServerPositions();
		
		if (Driver.debugging) {
			System.out.println("Changied " + NAME + " qualifications to\n  ");
				qualifiedFor.stream().forEach(s-> System.out.print(s.toString()));
				System.out.println();
		}
	}
	
	@Override
	public int compareTo(Employee that) {
		return employeePriority.compareTo(that.employeePriority);
	}
	
	@Override
	public boolean equals(Object that) {
		if (that == null) return false;
		if (this == that) return true;
		if (!this.getClass().equals(that.getClass())) return false;
		
		
		if (this.ID != ((Employee)that).ID) return false;
		return true;
	}
	
	String printAvailability() {
		StringBuffer buffer = new StringBuffer(NAME + "\n       L    D");
		for (int d = 0; d < 7; d++) {
			buffer.append("\n" + Day.getAbreviationFromInt(d) + " : ");
			for (int s = 0; s < 2; s++) {
				buffer.append(availableHours[d][s] + " ");
			}
		}
		return buffer.toString();
	}
	
	public Class<?> getEmployeeType() {
		if (employeeType == null) {
			throw new Error("  must call getEmployeeType from sub-class");
		}
		return employeeType.classType;
	}
	
	public abstract boolean ofType(Class<?> type);
	
	public double updateEmployeePriority(double averageFill) {
		return employeePriority.getCurrentPriority(averageFill);
	}
	
	public double getCurrentPrioirty() {
		return employeePriority.currentPriority;
	}
		
	public static void main(String[] args) {
		
		ArrayList<PositionType> king = new ArrayList<>(5);
		king.add(PositionType.Closer);
		king.add(PositionType.Head_Wait);
		king.add(PositionType.Cocktail);
		king.add(PositionType.Sales);
		Server kim = new Server("Kim",1,  LocalDate.of(2017, 1, 1), king);
		System.out.println(king.toString().replaceAll(" ", ""));
	}
}


