package emp;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.logging.Level;

import Menu.Menu;
import driver.Driver;
import restaurant.Day;
import restaurant.PositionID;
import restaurant.PositionType;
import restaurant.Restaurant;
import tools.ArrayTools;
import tools.CollectionTools;

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
		Driver.setUpLog.log(Level.CONFIG, "Generated generic employee {0}", name);
	}
	
	Employee(String name, int ID, LocalDate startDate, boolean[][] possibleHours,
			double pay, int maxHours, int desiredHours, int minHours,
			ArrayList<PositionType> possibleShifts)
	{
		this.NAME = Character.toUpperCase(name.charAt(0)) + name.substring(1, name.length());
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
		serverMod.add("View assgined shifts", () -> viewAssignedShifts());
		// TODO
		serverMod.add("Return", null, null, null);
		
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
		Driver.setUpLog.fine("Low promote: " + NAME);
		employeePriority.internalSetGrace(EmployeePriority.LOW_PROMOTE_GRACE);
	}
	
	public void promote() {
		Driver.setUpLog.fine("Promote: " + NAME);
		employeePriority.internalSetGrace(EmployeePriority.MID_PROMOTE_GRACE);
	}
	
	public void fastPromote() {
		Driver.setUpLog.fine("Fast promote: " + NAME);
		employeePriority.internalSetGrace(EmployeePriority.HIGH_PROMOTE_GRACE);
	}
	
	public String toString() {
		return NAME;
	}
	
	public String toCSV() {
		Driver.fileManagerLog.finest("toCSV for " + NAME);
		return ID + "," + NAME + "," + START_DATE + ","  + PAY + ","
				+ ArrayTools.print2D(POSSIBLE_HOURS, false) + ","
				+ currentHours + "," + MAX_HOURS + "," + DESIRED_HOURS + "," + MIN_HOURS + ","
				+ qualifiedFor.toString().replaceAll(" ", "");
	}
	
	public static <E extends Employee> E fromCSV(Class<E> employeeType) {
		if (employeeType.equals(Server.class)) {
			return Server.fromCSV(employeeType);
		}
		Error e =  new Error("Bottomed out in Employee.fromCSV()");
		Driver.fileManagerLog.log(Level.SEVERE, e.getMessage(), e);
		throw e;
	}
	
	public boolean everAvailableFor(PositionID<? extends Employee> ID) {
		Driver.deciderLog.finer("Testing: " + NAME + " EVER available for " + ID);
		boolean test = false;
		if (POSSIBLE_HOURS[ID.getDay().ordinal()][ID.getShiftType().ordinal()]) {
			test = true;
		}
		Driver.deciderLog.finer(test + "");
		return test;
	}
	
	public boolean currentlyAvailableFor(PositionID<? extends Employee> ID) {
		Driver.deciderLog.finer("Testing: " + NAME + " currenlty available for " + ID);
		boolean test = false;
		if (availableHours[ID.getDay().ordinal()][ID.getShiftType().ordinal()]) {
			test = true;
		}
		Driver.deciderLog.finer(test + "");
		return test;
	}
	
	public boolean qualifiedFor(PositionID<? extends Employee> ID) {
		Driver.deciderLog.finer("Testing: " + NAME + " can work a " + ID.getPositionType() + " shift");
		boolean test = false;
		if (qualifiedFor.contains(ID.getPositionType())) {
			test = true;
		}
		Driver.deciderLog.finer(test + "");
		return test;
	}
	
	public boolean canWork(PositionID<? extends Employee> ID) {
		Driver.deciderLog.fine("Testing " + NAME + " on ID: " + ID);
	
		// TESTS: TODO: If you ask can Server x work a Cook shift, will that fail?
		if (!qualifiedFor(ID)) return false;
		if (!currentlyAvailableFor(ID)) return false;
		
		// PASSED:
		Driver.deciderLog.fine("FULL PASS:" + NAME + " can work " + ID /*+ " Presently: " +
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
		StringBuffer buffer = new StringBuffer();
		qualifiedFor.stream().forEach(s -> buffer.append(s));
		Driver.setUpLog.config("Changing " + NAME + " qualifications. Present:\n\t" + buffer.toString());
		
		qualifiedFor = PositionType.buildServerPositions();
		
		StringBuffer buffer2 = new StringBuffer();
		qualifiedFor.stream().forEach(s-> buffer2.append(s));
		Driver.setUpLog.config("Changied " + NAME + " qualifications to\n\t" + buffer.toString());
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
	
	public void menu() {
		serverMod.selection();
	}
	
	public void viewAssignedShifts() {
		CollectionTools.collectionPrinter(assignedShifts, false);
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


