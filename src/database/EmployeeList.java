package database;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import driver.Driver;
import emp.Employee;
import tools.CollectionTools;
import tools.StringTools;

public class EmployeeList<E extends Employee> {
	Set<E> employeeSet;
	public final Class<? extends Employee> employeeType;
	
	public EmployeeList(Class<? extends Employee> employeeType) {
		if (Driver.debugging) {
			System.out.println("Intalizing ServerList Object");
		}
		this.employeeType = employeeType;
		employeeSet = new HashSet<>();
	}
	
	public boolean addEmployee(E employee) {
		if (Driver.debugging) System.out.println("  Adding " + employee + " to ServerList");
		return (employeeSet.add(employee));
	}
	
	public void addMultipleEmployees(Collection<? extends E> collection) {
		collection.stream().forEach(s -> {
			boolean pass = addEmployee(s);
			if (Driver.debugging) System.out.println(s + ((pass) ? " passed" : " failed"));
		});
	}
	
	E findEmployee(E toFind) {
		return CollectionTools.findFirst(employeeSet, s -> s.equals(toFind));
	}
	
	E findEmployee(String name) {
		return CollectionTools.findFirst(employeeSet, s -> s.NAME.equalsIgnoreCase(name));
	}
	
	E findEmployee(int ID) {
		return CollectionTools.findFirst(employeeSet, s -> s.ID == ID);
	}
	
	double getAverageFill() {
		return employeeSet
			.stream()
			.mapToDouble(s -> s.getFillCount())
			.average()
			.getAsDouble();
	}
	
	void updatePriorities(double averageFill) {
		employeeSet
			.stream()
			.peek(s -> s.updateEmployeePriority(averageFill));
	}
	
	public int count() {
		return employeeSet.size();
	}
	
	public String toCSV() {
		StringBuffer buffer = new StringBuffer();
		
		employeeSet.stream()
			.forEach(server -> buffer.append("[" + server.toCSV() + "]\n"));
		
		return buffer.toString();
	}
	
	public Set<E> filter(Predicate<? super Employee> predicate){
		return employeeSet.stream()
			.filter(predicate)
			.collect(Collectors.toSet());
	}
	
	public static EmployeeList<? extends Employee> fromCSV(String csvString) {
		return null; // TODO
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
