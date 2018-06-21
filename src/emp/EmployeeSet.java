package emp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import driver.Driver;
import tools.CollectionTools;
import tools.NumberTools;
import tools.StringTools;

public class EmployeeSet<E extends Employee> {
	List<E> employeeSet;
	public final Class<? extends Employee> employeeType;
	
	public EmployeeSet(Class<? extends Employee> employeeType) {
		Driver.databaseLog.config("Intalizing ServerList Object");
		this.employeeType = employeeType;
		employeeSet = new ArrayList<>();
	}
	
	public boolean addEmployee(E employee) {
		Driver.databaseLog.info("Adding " + employee + " to ServerList");
		if (employeeSet.contains(employee)) {
			return false;
		}
		employeeSet.add(employee);
		return true;
	}
	
	public void addMultipleEmployees(Collection<? extends E> collection) {
		collection.stream().forEach(s -> {
			if(addEmployee(s)) {
				Driver.databaseLog.info(s + " passed adding from addingMultipleEmployee");
			} else {
				Driver.databaseLog.severe(s + " FAILED from addingMultipleEmployee");
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
		Driver.databaseLog.finer("In average fill");
		return employeeSet
			.stream()
			.mapToDouble(s -> s.getFillCount())
			.average()
			.getAsDouble();
	}
	
	void updatePriorities(double averageFill) {
		Driver.databaseLog.finer("In update priorites");
		employeeSet
			.stream()
			.peek(s -> s.updateEmployeePriority(averageFill));
	}
	
	public int count() {
		return employeeSet.size();
	}
	
	public String toCSV() {
		Driver.databaseLog.finer("In toCSV of EmployeeSet");
		StringBuffer buffer = new StringBuffer();
		
		employeeSet.stream()
			.forEach(server -> buffer.append(server.toCSV() + "\n"));
		
		return buffer.toString();
	}
	
	public static <E extends Employee> EmployeeSet<E> fromCSV(String[] csvString, Class<E> employeeType) {
		Driver.databaseLog.finer("In fromCSV of EmployeeSet");
		EmployeeSet<E> toReturn = new EmployeeSet<>(employeeType);
		Arrays.asList(csvString).stream()
			.forEach(str -> toReturn.addEmployee(Employee.fromCSV(employeeType)));
		return toReturn;
	}
	
	public Set<E> filter(Predicate<? super Employee> predicate){
		Driver.databaseLog.finer("In filter of EmployeeSet");
		return employeeSet.stream()
			.filter(predicate)
			.collect(Collectors.toSet());
	}
	
	public void viewServerPositions() {
		Driver.databaseLog.finer("In viewServerPositions of EmployeeSet");
		employeeSet.get(NumberTools.generateInt(
				"Which server should you like to view?"
					+ "\n" + CollectionTools.collectionPrinter(employeeSet, true),
				false,
				1,
				employeeSet.size()))
			.viewAssignedShifts();
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
