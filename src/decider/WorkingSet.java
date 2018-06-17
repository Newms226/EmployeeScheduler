package decider;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import database.EmployeeList;
import database.FileManager;
import database.PositionID;
import database.ScheduleSetUp;
import driver.Driver;
import driver.Schedule;
import emp.Employee;
import emp.PositionType;
import emp.Server;

public class WorkingSet <E extends Employee> {
	public final EmployeeList<E> employeeList;
	public final ScheduleSetUp<E> setUp;
	public final OperationStack opStack;
	public final QualifiedEmployeeListMap<E> queueMap;
	public final Class<E> employeeType;
	
	private Schedule schedule;
	
	public WorkingSet(Class<E> E_TYPE, int globalMax) {
		employeeType = E_TYPE;
		employeeList = new EmployeeList<E>(E_TYPE);
		setUp = new ScheduleSetUp<E>();
		opStack = new OperationStack();
		queueMap = new QualifiedEmployeeListMap<E>(employeeList, globalMax);
	}
	
	WorkingSet(Class<E> employeeType,
			   EmployeeList<E> employeeList,
			   ScheduleSetUp<E> setUp, 
			   OperationStack opStack,
			   QualifiedEmployeeListMap<E> queueMap){
		this.employeeList = employeeList;
		this.setUp = setUp;
		this.opStack = opStack;
		this.queueMap = queueMap;
		this.employeeType = employeeType;
		// TODO writeToFile();
	}
	
	public void save(FileManager.SF statusFlag) {
		FileManager.saveAll(this, statusFlag);
	}
	
	public static <E extends Employee> WorkingSet<E> readFromFile(File input) {
		// TODO
		return null;
	}
	
	
	@Override
	public boolean equals(Object o) {
		// TODO
		if (this == o) return true;
		if (!this.getClass().equals(o.getClass())) return false;
		
		
		return true;
	}
	
	public Class<?> getEmployeeType() {
		return employeeType;
	}
	
	public void setSchedule(Collection<PositionID<? extends Employee>> completedIDs) {
		setSchedule(new Schedule(completedIDs));
	}
	
	public void setSchedule(Schedule schedule) {
		this.schedule = schedule;
	}
	
	public Schedule getSchedule() {
		if (schedule == null && Driver.debugging) {
			System.out.println("WARNING: Attempted to get schedule when object is null");
//			throw new Error();
		}
		return schedule;
	}

	public static WorkingSet<Server> serverTrainingData(){
		
		Server kim = new Server("Kim",1,  LocalDate.of(2017, 1, 1), PositionType.getKing());
		Server wade = new Server("wade",2,  LocalDate.of(2000, 1, 1), PositionType.getBarOnly());
		Server AshleyO = new Server("Olson",3,  LocalDate.of(2005, 1, 1), PositionType.getBarOnly());
		Server Alecia = new Server("alecia",14,  LocalDate.of(2007, 1, 1), PositionType.getBarOnly());
		Server chris = new Server("Chris",4,  LocalDate.of(2010, 1, 1), PositionType.getHandyMan());
		Server dena = new Server("Dena",5, LocalDate.of(2013, 1, 1), PositionType.getKing());
		Server blake = new Server("blake",6, LocalDate.of(2014, 1, 1), PositionType.getHandyMan());
		Server chels = new Server("chels",7, LocalDate.of(2010, 1, 1), PositionType.getKing());
		Server brandon = new Server("brandon",8, LocalDate.of(2018, 1, 1), PositionType.getKing());
		Server me = new Server("me",9, LocalDate.of(2017, 1, 1), PositionType.getKing());
		Server Marcy = new Server("Marcy",10, LocalDate.of(2018, 1, 1), PositionType.getNewKid());
		Server Paul2 = new Server("Paul 2",11, LocalDate.of(2017, 6, 1), PositionType.getAllButCocktail());
		Server paul3 = new Server("Paul 3",12, LocalDate.of(2018, 1, 1), PositionType.getNewKid());
		Server gregs = new Server("Gregs",13, LocalDate.of(2015, 6, 1), PositionType.getKing());
		kim.fastPromote();
		dena.fastPromote();
		me.promote();
		gregs.promote();
		brandon.promote();
		List<Server> servers = new ArrayList<>();
		servers.add(kim);
		servers.add(wade);
		servers.add(chris);
		servers.add(blake);
		servers.add(chels);
		servers.add(brandon);
		servers.add(dena);
		servers.add(AshleyO);
		servers.add(me);
		servers.add(Alecia);
		servers.add(Marcy);
		servers.add(paul3);
		servers.add(Paul2);
		servers.add(gregs);
		
		WorkingSet<Server> workingSet = new WorkingSet<>(Server.class, 60);
		workingSet.employeeList.addMultipleEmployees(servers);
		workingSet.setUp.trainingData();
		
		return workingSet;
	}
	
	public static void main(String[] args) throws IOException {
	}

}
