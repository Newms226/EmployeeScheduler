package emp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.stream.Collectors;

import driver.Driver;
import tools.CollectionTools;
import tools.NumberTools;
import tools.StringTools;

public class EmployeeSet<E extends Employee> {
	List<E> employeeSet;
	public final Class<? extends Employee> employeeType;
	
	public EmployeeSet(Class<? extends Employee> employeeType) {
		Driver.setUpLog.config("Intalizing ServerList Object");
		this.employeeType = employeeType;
		employeeSet = new ArrayList<>();
	}
	
	public boolean addEmployee(E employee) {
		Driver.setUpLog.info("Adding " + employee + " to ServerList");
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
	
	public void addMultipleEmployees(Collection<? extends E> collection) {
		collection.stream().forEach(s -> {
			if(addEmployee(s)) {
				Driver.setUpLog.info(s + " passed adding from addingMultipleEmployee");
			} else {
				Driver.setUpLog.severe(s + " FAILED from addingMultipleEmployee");
			}
		});
	}
	
	public E findEmployee(E toFind) {
		return CollectionTools.findFirst(employeeSet, s -> s.equals(toFind));
	}
	
	public E findEmployee(String name) {
		return CollectionTools.findFirst(employeeSet, s -> s.NAME.equalsIgnoreCase(name));
	}
	
	public E findEmployee(int ID) {
		return CollectionTools.findFirst(employeeSet, s -> s.ID == ID);
	}
	
	double getAverageFill() {
		Driver.setUpLog.finer("In average fill");
		return employeeSet
			.stream()
			.mapToDouble(s -> s.getFillCount())
			.average()
			.getAsDouble();
	}
	
	void updatePriorities(double averageFill) {
		Driver.setUpLog.finer("In update priorites");
		employeeSet
			.stream()
			.peek(s -> s.updateEmployeePriority(averageFill));
	}
	
	public int count() {
		return employeeSet.size();
	}
	
	public String toCSV() {
		Driver.fileManagerLog.finer("In toCSV of EmployeeSet");
		StringBuffer buffer = new StringBuffer();
		
		employeeSet.stream()
			.forEach(server -> buffer.append(server.toCSV() + "\n"));
		
		return buffer.toString();
	}
	
	public static <E extends Employee> EmployeeSet<E> fromCSV(String[] csvString, Class<E> employeeType) {
		Driver.fileManagerLog.finer("In fromCSV of EmployeeSet");
		EmployeeSet<E> toReturn = new EmployeeSet<>(employeeType);
		Arrays.asList(csvString).stream()
			.forEach(str -> toReturn.addEmployee(Employee.fromCSV(employeeType)));
		return toReturn;
	}
	
	public Set<E> filter(Predicate<? super Employee> predicate){
		Driver.fileManagerLog.finer("In filter of EmployeeSet");
		return employeeSet.stream()
			.filter(predicate)
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
	
	@SuppressWarnings("unchecked")
	@Override
	public boolean equals(Object o) {
		if (o == this) return true;
		if (o == null) return false;
		
		if (!this.getClass().equals(o.getClass())) return false;
		
		try {
			if (!employeeSet.containsAll(((EmployeeSet<E>) o).employeeSet)) return false;
		} catch (ClassCastException e) {
			return false;
		}
		
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
	
	public static void main(String[] args) {
	}
}
