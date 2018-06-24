package restaurant;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.logging.Level;

import Menu.Menu;
import driver.Driver;
import emp.ClassNotEqualException;
import emp.Employee;
import emp.EmployeeType;
import emp.Server;
import tools.NumberTools;

public class PositionID<E extends Employee> implements Comparable<PositionID<? extends Employee>>,
	                                                   Serializable,
	                                                   Cloneable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3602410610461602123L;
	public static final int MAX_PRIORITY = 30;
	public static final Comparator<PositionID<?>> REVERSE_ORDER = (a, b) -> -a.compareTo(b);
	public static final Comparator<PositionID<?>> DESCENDING_PRIORITY_ORDER = 
		(a, b) -> {
			if (a.compareTo(b) < 0) return 1;
			if (a.compareTo(b) > 0) return -1;
			if (a.positionType.compareTo(b.positionType) < 0) return -1;
			if (a.positionType.compareTo(b.positionType) > 0) return 1;
			return 0;
		};
	public static final Comparator<PositionID<?>> NATURAL_ORDER = (a, b) -> a.compareTo(b);
	public static final Comparator<PositionID<?>> DAY_ORDER = 
		(a, b) -> { 
			if (a.day.compareTo(b.day) > 0) return -1;
			if (a.day.compareTo(b.day) < 0) return 1;
			if (a.shiftType.compareTo(b.shiftType) < 0) return -1;
			if (a.shiftType.compareTo(b.shiftType) > 0) return 1;
			if (a.positionType.compareTo(b.positionType) < 0) return -1;
			if (a.positionType.compareTo(b.positionType) > 0) return 1;
			if (a.priority > b.priority) return -1;
			if (a.priority < b.priority) return 1;
			return 0;
		};
	public static final Comparator<PositionID<? extends Employee>> EXTRACTED_SHIFT_ID_ORDER = 
			(a,b) -> a.extractShiftID().compareTo(b.extractShiftID());
	public static final Comparator<ShiftID> SHIFT_ID_ORDER =
			(a,b) -> a.compareTo(b);
	
			
	protected Day day;
	protected PositionType positionType;
	protected ShiftType shiftType;
	
	double priority;
	Employee employee;
	transient Menu modify;
	private boolean debugging;
	public final Class<? extends Employee> employeeType;
	
	public PositionID(Class<? extends Employee> employeeType, Day day, PositionType positionType, ShiftType shiftType, double shiftPriority) {
		Driver.setUpLog.log(Level.FINEST, "Created new PositionID", new Object[] {employeeType, day, positionType, shiftType, shiftPriority});
		this.day = day;
		this.positionType = positionType;
		this.shiftType = shiftType;
		this.priority = shiftPriority;
		this.employeeType = employeeType;
//		calculatePriority(shiftPriority);
		
		modify = new Menu(this + "\nWhat would you like to change?", 
				() -> System.out.println("Now: " + this));
		modify.add("Position Type (Closer, Cocktail, etc.)", () -> {
			this.setPositionType(PositionType.build());
			modify.selection();
		});
		modify.add("Shift Type (Lunch, Dinner, etc.)", () -> {
			this.setShiftType(ShiftType.buildShiftType());
			modify.selection();
		});
		modify.add("Day", () -> {
			this.setDay(Day.build());
			modify.selection();
		});
		modify.add("Priority", 
			() -> {
				NumberTools.generateDouble("What do you want to change the priority to?",
						  true, 
						  1, 
						  10);
				modify.selection();
			});
		modify.add("Change server", null); // TODO
		modify.add("Return", () -> System.out.println(), null);
	}
	
	private PositionID(String day, String shiftType, String positionType, String shiftPriority) {
		this (Server.class, 
				Day.parse(Integer.parseInt(day)), 
				PositionType.parse(positionType.toCharArray()[0]), 
				ShiftType.getFromInt(Integer.parseInt(shiftType)), 
				Double.parseDouble(shiftPriority));
		Driver.setUpLog.log(Level.FINEST, "Created new positionID from String constructor", new Object[] {day, shiftType, positionType, shiftPriority});;
	}
	
	public static <E extends Employee> PositionID<E> build() {
		return build(EmployeeType.build().classType, Day.build());
	}
	
	static <E extends Employee> PositionID<E> build(Class<? extends Employee> employeeType, Day day) { 
		Driver.setUpLog.log(Level.FINEST, "Building new PositionID with day & employee type", new Object[] {employeeType, day});
		return new PositionID<E>(
				employeeType,
				day,
				PositionType.build(),
				ShiftType.buildShiftType(),
				NumberTools.generateDouble("What is the priority of this shift?",
						  true, 
						  1, 
						  10));
	}
	
	public PositionType getPositionType() {
		return positionType;
	}

	public void setPositionType(PositionType positionType) {
		Driver.setUpLog.log(Level.FINE, "setPositionType", positionType);
		this.positionType = positionType;
	}

//	@Override
//	public boolean equals(Object o) {
//		if (o == this) {
//			System.out.println("Same object, TRUE");
//			return true;
//		}
//		if (!(this.getClass().equals(o.getClass()) 
//				|| this.getClass().equals(o.getClass().getSuperclass())) ) {
////					System.out.println("FALSE: Does not extend the write class"
////							+ "\n this: " + this.getClass().getSimpleName() + 
////							"\no:" + o.getClass().getSimpleName() + " super:" + o.getClass().getSuperclass().getSimpleName() );
//			return false;
//		}
//		
//		final ShiftID that = (ShiftID) o;
//		if (!this.getDay().equals(that.getDay())) {
//			//System.out.println("FALSE: day wrong");
//			return false;
//		}
//		if (!this.getPositionType().equals(that.getPositionType())) {
////					System.out.println("FALSE: positionType wrong");
//			return false;
//		}
//		if (!this.getShiftType().equals(that.getShiftType())) {
////					System.out.println("FALSE: shift type wronge");
//			return false;
//		}
////				System.out.println("TRUE!");
//		return true;
//	}


	public ShiftType getShiftType() {
		return shiftType;
	}

	public void setShiftType(ShiftType shiftType) {
		Driver.setUpLog.log(Level.FINE, "set shiftType", shiftType);
		this.shiftType = shiftType;
	}

	public Day getDay() {
		return day;
	}

	public void setDay(Day day) {
		Driver.setUpLog.log(Level.FINE, "set day", day);
		this.day = day;
	}

	
	@SuppressWarnings("unchecked")
	public <e extends Employee> void assignEmployee(e employee) {
		if (employee == null) throw new IllegalArgumentException("Must provide non-null server");
		ClassNotEqualException.assertEqual(employeeType, employee.getClass());
		
		if (this.employee != null) {
			Driver.deciderLog.severe("WARNING: Assigned " + employee + " to " 
					+ toString() + " when " + this.employee + " was already assigned");
		}
		
		Driver.deciderLog.info("Assigning server to ShiftID FROM SHIFT ID");
		this.employee = (E) employee;
	}
	
	public Employee getEmplyee() {
		return employee;
	}
	
	public void removeEmployee() {
		Driver.deciderLog.log(Level.INFO, "Removing employee", employee);
		employee = null;
	}
	
//	void calculatePriority(double shiftPriority) {
//		if (debugging) System.out.println("PRIORITY: Working on " + NumberTools.format(shiftPriority));
//		calculatedPriority = NumberTools.normalizeToRange(
//				getDay().getPriority() + calculatedPriority + shiftPriority, // Value
//				0,                                                             // Min Value
//				MAX_PRIORITY);                                                 // Max Value
//		if (debugging) System.out.println("PRIORITY: Calculated " + NumberTools.format(calculatedPriority));
//	}
	
	@SuppressWarnings("unchecked")
	@Override
	public boolean equals(Object that) {
		Driver.masterLog.log(Level.FINEST, "ENTERING: positionID.equals", that);
		if (that == null) return false;
		if (this == that) {
			Driver.masterLog.finest("TRUE: PositionID.equals(that) compared the same object");
			return true;
		}
		if (!this.getClass().equals(that.getClass())) {
			Driver.masterLog.finest("FALSE: Class doesnt match");
			return false;
		}
		
		PositionID<E> temp = null;
		try {
			temp = (PositionID<E>) that;
		} catch (ClassCastException e) {
			Driver.masterLog.finest("FALSE: ClassCastException");
			return false;
		}
		
		if (this.priority != temp.priority) {
			Driver.masterLog.finest("FALSE: Priority doesnt match. "
					+ "This: " + this.priority + " that: " + temp.priority);
			return false;
		}
		
		if (!this.day.equals(temp.day)) {
			Driver.masterLog.log(Level.FINEST, 
					            "FALSE: this.day({0}) != that.day({1})",
					            new Object[] {this.day, temp.day});
			return false;
		}
		
		if (!this.positionType.equals(temp.positionType)) {
			Driver.masterLog.log(Level.FINEST, 
		            "FALSE: this.positionType({0}) != that.positionType({1})",
		            new Object[] {this.positionType, temp.positionType});
			return false;
		}
		
		if(!this.shiftType.equals(temp.shiftType)) {
			Driver.masterLog.log(Level.FINEST, 
		            "FALSE: this.shiftType({0}) != that.shiftType({1})",
		            new Object[] {this.shiftType, temp.shiftType});
		}
		
		
		if (this.employee == null && temp.employee != null) {
			Driver.masterLog.finest("FALSE: this.employee == null && temp.employee != null");
			return false;
		}
		if (this.employee != null) {
			Driver.masterLog.finest("In this.employee != null");
			if (temp.employee == null) {
				Driver.masterLog.finest("FALSE: this.employee != null & temp.employee == null");
				return false;
			}
			if (!this.employee.equals(temp.employee)) {
				Driver.masterLog.finest("FALSE: " + this.employee + " != " + temp.employee);
				return false;
			}
		}
		Driver.masterLog.finest("TRUE");
		return true;
	}
	
//	@Override
//	public int compareTo(PositionID<? extends Employee> that) { // TODO
//		if (this.priority < that.priority) return -1;
//		if (this.priority > that.priority) return 1;
//		
//		return 0;
//	}
	
	public String toString() {
		return day + " " + shiftType.name() + " " + positionType.name() 
			+ " " + priority + ((employee == null) ? "" : "\n  " + employee.toString());
	}
	
	public void modify() {
		modify.selection();
	}
	
	public String toCSV() {
		return getDay().ordinal()  + "," + getShiftType().ordinal() + "," + getPositionType().ABR + "," + priority;
	}
	
	public String toCSVWithEmployee() {
		return toCSV() + ":" + employee.ID;
	}
	
	public static <E extends Employee> PositionID<E> fromCSV(String str) {
		return fromCSV(str.split(","));
	}
	
	static <E extends Employee> PositionID<E> fromCSV(String[] split) {
		Driver.fileManagerLog.finer("parsing: " + Arrays.toString(split));
		PositionID<E> temp = new PositionID<E>(split[0], split[1], split[2], split[3]);
		Driver.fileManagerLog.finer("parsed to " + temp);
		return temp;
	}
	
	@Override
	public PositionID<E> clone() {
		PositionID<E> clone =  new PositionID<E>(employeeType,
				                                 day,
				                                 positionType,
				                                 shiftType,
				                                 priority);
		if (employee != null) clone.assignEmployee(employee);
		return clone;
	}
	
	public ShiftID extractShiftID() {
		return new ShiftID(day, shiftType, positionType);
	}
	
	public void setTo(PositionID<E> setTo) {
		Driver.masterLog.log(Level.FINER, "Setting this positionID to", setTo);
		this.day = setTo.day;
		this.positionType = setTo.positionType;
		this.shiftType = setTo.shiftType;
		this.employee = setTo.employee;
		this.priority = setTo.priority;
	}
	
	public static PositionID<Server> getExampleServerBased(){
		return new PositionID<Server>(Server.class, Day.MONDAY, PositionType.Bar, ShiftType.DINNER, 10);
	}
	
	public double getPriority() {
		return priority;
	}

	@Override
	public int compareTo(PositionID<? extends Employee> o) {
		if (priority < o.priority) return -1;
		if (priority > o.priority) return 1;
		return 0;
	}
	
	public static class ShiftID implements Comparable<Object> {
		final int dayOfWeek, shiftTypeOrdinal, positionTypeOrdinal;
		
		ShiftID(int dayOfWeek, int shiftTypeOrdinal, int positionTypeOrdinal){
			Driver.deciderLog.finest("Created ShiftID");
			this.dayOfWeek = dayOfWeek;
			this.shiftTypeOrdinal = shiftTypeOrdinal;
			this.positionTypeOrdinal = positionTypeOrdinal;
		}

		ShiftID(Day clone, ShiftType shiftType, PositionType positionType) {
			this (clone.ordinal(), shiftType.ordinal(), positionType.ordinal());
		}
		
		public String toCSV() {
			return dayOfWeek + "," + shiftTypeOrdinal + "," + positionTypeOrdinal;
		}

		@Override
		public int compareTo(Object o) {
			if (this == o) return 0;
			
			final ShiftID id;
			if (o.getClass().equals(PositionID.class)) {
				final PositionID<?> temp = (PositionID<?>) o;
				id = temp.extractShiftID();
			} else {
				id = (ShiftID) o;
			}
			
			if (dayOfWeek < id.dayOfWeek) return -1;
			if (dayOfWeek > id.dayOfWeek) return 1;
			if (shiftTypeOrdinal < id.shiftTypeOrdinal) return -1;
			if (shiftTypeOrdinal > id.shiftTypeOrdinal) return 1;
			if (positionTypeOrdinal < id.positionTypeOrdinal) return -1;
			if (positionTypeOrdinal > id.positionTypeOrdinal) return 1;
			return 0;
		}
		
		@Override
		public String toString() {
			return Day.parse(dayOfWeek) + " " 
					+ (shiftTypeOrdinal == 0 ? "LUNCH" : "DINNER") + " "
					+ PositionType.parse(positionTypeOrdinal);
		}
	}
	
	@SuppressWarnings("unused")
	private static void testEqualsWithExtract() {
		PositionID<Server> one = new PositionID<>(Server.class, Day.MONDAY, PositionType.Bar, ShiftType.DINNER, 10);
		PositionID<Server> two = new PositionID<>(Server.class, Day.MONDAY, PositionType.Bar, ShiftType.DINNER, 10);
		System.out.println(one.extractShiftID().equals(two.extractShiftID()));
	}
	
	@SuppressWarnings("unused")
	private static void testEquals() {
		PositionID<Server> one = new PositionID<>(Server.class, Day.MONDAY, PositionType.Bar, ShiftType.DINNER, 10);
		PositionID<Server> two = new PositionID<>(Server.class, Day.MONDAY, PositionType.Bar, ShiftType.DINNER, 10);
		ArrayList<PositionType> king = new ArrayList<>(5);
		king.add(PositionType.Closer);
		king.add(PositionType.Head_Wait);
		king.add(PositionType.Cocktail);
		king.add(PositionType.Sales);
		two.employee = new Server("Kim",1,  LocalDate.of(2017, 1, 1), king);
		one.employee = new Server("Kim",1,  LocalDate.of(2017, 1, 1), king);
		System.out.println(one.equals(two));
	}
	
	@SuppressWarnings("unused")
	private static void testClone() {
		PositionID<Server> one = new PositionID<>(Server.class, Day.MONDAY, PositionType.Bar, ShiftType.DINNER, 10);
		System.out.println(one.priority);
		PositionID<Server> two = one.clone();
		ArrayList<PositionType> king = new ArrayList<>(5);
		king.add(PositionType.Closer);
		king.add(PositionType.Head_Wait);
		king.add(PositionType.Cocktail);
		king.add(PositionType.Sales);
		one.employee = new Server("Kim",1,  LocalDate.of(2017, 1, 1), king);
		System.out.println(two.employee);
	}
	
	public static void testExtractShiftID() {
		PositionID<Server> pos = new PositionID<>(Server.class, Day.MONDAY, PositionType.Bar, ShiftType.DINNER, 4.9);
		ShiftID extracted = pos.extractShiftID();
		System.out.println(extracted.toString());
	}
	
	public static void main(String[] args) {
		testExtractShiftID();
	}
}
