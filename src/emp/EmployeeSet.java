package emp;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import Availability.TimeChunk;
import driver.Driver;
import racer.StopWatch;
import restaurant.PositionType;
import tools.CollectionTools;
import tools.FileTools;
import tools.NumberTools;
import tools.SerialTools;

public class EmployeeSet implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3440900186372080836L;
	List<Employee> employeeSet;
	
	public EmployeeSet() {
		Driver.setUpLog.config("Intalizing EmployeeSet Object");
		employeeSet = new ArrayList<>();
	}
	
	public boolean addEmployee(Employee employee) {
		Driver.setUpLog.info("Adding " + employee + " to EmployeeSet");
		if (employeeSet.contains(employee)) {
			Driver.setUpLog.log(Level.WARNING,
					            "Set already contains {0}",
					            employee);
			return false;
		}
		employeeSet.add(employee);
		Driver.setUpLog.log(Level.FINE,
	                        "Successfully added {0}",
	                        employee);
		return true;
	}
	
	public void addMultipleEmployees(Collection<Employee> collection) {
		collection.stream().forEach(s -> {
			if(addEmployee(s)) {
				Driver.setUpLog.info(s + " passed adding from addingMultipleEmployee");
			} else {
				Driver.setUpLog.severe(s + " FAILED from addingMultipleEmployee");
			}
		});
	}
	
	public Employee findEmployee(Employee toFind) {
		return CollectionTools.findFirst(employeeSet, s -> s.equals(toFind));
	}
	
	public Employee findEmployee(String name) {
		return CollectionTools.findFirst(employeeSet, s -> s.NAME.equalsIgnoreCase(name));
	}
	
	public Employee findEmployee(int ID) {
		return CollectionTools.findFirst(employeeSet, s -> s.ID == ID);
	}
	
	public Collection<Employee> getEmployeesByType(EmployeeType type){
		return employeeSet
				.stream()
				.filter(e -> e.employeeType == type)
				.collect(Collectors.toList());
	}
	
	double getAverageFill() {
		Driver.setUpLog.finer("In average fill");
		return employeeSet
			.stream()
			.mapToDouble(s -> s.currentMinutes)
			.average()
			.getAsDouble();
	}
	
	void updatePriorities() {
		Driver.setUpLog.finer("In update priorites");
		employeeSet
			.stream()
			.peek(s -> s.getCurrentPrioirty());
	}
	
	public int count() {
		return employeeSet.size();
	}
	
	public Stream<Employee> stream() {
		return employeeSet.stream();
	}
	
	public Set<Employee> filter(Predicate<Employee> predicate){
		Driver.fileManagerLog.finer("In filter of EmployeeSet");
		return employeeSet.stream()
			.filter(predicate)
//			.sorted(Employee.DESENDING_PRIORITY_ORDER)
			.collect(Collectors.toSet());
	}
	
	public <V> Map<Integer, V> mapWithID(Function<Employee, V> valueMapper){
		return employeeSet.stream()
				.collect(
					Collectors.toMap(e -> e.ID, 
							         e -> valueMapper.apply(e)));
	}
	
	public void viewServerPositions() {
		Driver.fileManagerLog.finer("In viewServerPositions of EmployeeSet");
		employeeSet.get(NumberTools.generateInt(
				"Which server should you like to view?"
					+ "\n" + CollectionTools.collectionPrinter(employeeSet, true),
				false,
				1,
				employeeSet.size()))
			.viewAssignedShifts();
	}
	
	public <BUFFER extends CharSequence & Appendable & Serializable> void 
				appendProcessString(BUFFER buffer) throws IOException {
		employeeSet.sort(Employee.DESENDING_PRIORITY_ORDER);
		for (Employee e: employeeSet) {
			e.appendCurrentStatusString(buffer);
		}
	}

	@Override
	public boolean equals(Object o) {
		if (o == this) return true;
		if (o == null) return false;
		
		if (!this.getClass().equals(o.getClass())) return false;
		
		if (!employeeSet.containsAll(((EmployeeSet) o).employeeSet)) return false;
		
		return true;
	}
	
//	void addServer() {
//	boolean valid = false;
//	do {
//		try {
//			valid = serverSet.add(Server.build());
//			if (!valid) System.out.println("Server is already present");
//		} catch (Exception e) {
//			System.out.println(e.getMessage() + "\nTry Again.");
//		}
//	} while (!valid);
//}

//void addMultipleServers() {
//	int fillCount = NumberTools.generateInt("How many servers would you like to input?", 1, 100);
//	for (int i = 0; i < fillCount; i++) {
//		addServer();
//	}
//}
	
//	void prepare() {
//		if (Driver.debugging) {
//			System.out.println("Intalizing QualificationHashMap");
//		}
//		qualifiedMap = new QualificationHashMap(serverMap.values());
//		if (Driver.debugging) {
//			System.out.println("  Complete.");
//		}
//	}
	
//	// [day][LorD][Position Type][server]
//	void getPossibleServers(){
//		for (Server s: serverMap.values()) {
//			for (Day day: Day.values) {
//				for (int i = 0; i < 2; i++) {
//					if (s.canWork(day.dayOfWeek, i)) {
//						// TODO: Add this server to all of their qualified arrays.
//					} // end if
//				} // loop through lunch or dinner
//			} // loop through each day
//		} // loop through each server
//	}
//	public String toCSV() {
//		Driver.fileManagerLog.finer("In toCSV of EmployeeSet");
//		StringBuffer buffer = new StringBuffer();
//		
//		employeeSet.stream()
//			.forEach(server -> buffer.append(server.toCSV() + "\n"));
//		
//		return buffer.toString();
//	}
//	
//	public static <E extends Employee> EmployeeSet<E> fromCSV(String[] csvString, Class<E> employeeType) {
//		Driver.fileManagerLog.finer("In fromCSV of EmployeeSet");
//		EmployeeSet<E> toReturn = new EmployeeSet<>(employeeType);
//		Arrays.asList(csvString).stream()
//			.forEach(str -> toReturn.addEmployee(Employee.fromCSV(employeeType)));
//		return toReturn;
//	}
	
	public static void main(String[] args) {
		Employee kim = new Employee("Kim",1,  LocalDate.of(2017, 1, 1), PositionType.getKing());
		Employee wade = new Employee("wade",2,  LocalDate.of(2000, 1, 1), PositionType.getBarOnly());
		Employee AshleyO = new Employee("Olson",3,  LocalDate.of(2005, 1, 1), PositionType.getBarOnly());
		Employee Alecia = new Employee("alecia",14,  LocalDate.of(2007, 1, 1), PositionType.getBarOnly());
		Employee chris = new Employee("Chris",4,  LocalDate.of(2010, 1, 1), PositionType.getHandyMan());
		Employee dena = new Employee("Dena",5, LocalDate.of(2013, 1, 1), PositionType.getKing());
		Employee blake = new Employee("blake",6, LocalDate.of(2014, 1, 1), PositionType.getHandyMan());
		Employee chels = new Employee("chels",7, LocalDate.of(2010, 1, 1), PositionType.getKing());
		Employee brandon = new Employee("brandon",8, LocalDate.of(2018, 1, 1), PositionType.getKing());
		Employee me = new Employee("me",9, LocalDate.of(2017, 1, 1), PositionType.getKing());
		Employee Marcy = new Employee("Marcy",10, LocalDate.of(2018, 1, 1), PositionType.getNewKid());
		Employee Paul2 = new Employee("Paul 2",11, LocalDate.of(2017, 6, 1), PositionType.getAllButCocktail());
		Employee paul3 = new Employee("Paul 3",12, LocalDate.of(2018, 1, 1), PositionType.getNewKid());
		Employee gregs = new Employee("Gregs",13, LocalDate.of(2015, 6, 1), PositionType.getKing());
		kim.highPromote();
		dena.highPromote();
		wade.highPromote();
		AshleyO.promote();
		me.promote();
		gregs.promote();
		brandon.promote();
		wade.availability.setToPersistantlyUNAVAILABLE(TimeChunk.from(5, 0, 0, 5, 23, 59));
		List<Employee> Employees = new ArrayList<>();
		Employees.add(kim);
		Employees.add(wade);
		Employees.add(chris);
		Employees.add(blake);
		Employees.add(chels);
		Employees.add(brandon);
		Employees.add(dena);
		Employees.add(AshleyO);
		Employees.add(me);
		Employees.add(Alecia);
		Employees.add(Marcy);
		Employees.add(paul3);
		Employees.add(Paul2);
		Employees.add(gregs);
		
		EmployeeSet set = new EmployeeSet();
		set.addMultipleEmployees(Employees);
		
		File file = new File(FileTools.DEFAULT_IO_DIRECTORY + "SET.txt");
		long startTime, endTime;
		startTime = System.nanoTime();
		SerialTools.seralizeObject(set, file);
		endTime = System.nanoTime();
		System.out.println("SERIALIZED IN " + StopWatch.nanosecondsToString(endTime - startTime));
	}
}
