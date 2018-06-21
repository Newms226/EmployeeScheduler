package WorkingSet;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import driver.Driver;
import driver.FileManager;
import emp.Employee;
import emp.EmployeeSet;
import emp.Server;
import restaurant.PositionID;
import restaurant.PositionType;
import restaurant.Restaurant;

public class WorkingSet <E extends Employee> {
	public final EmployeeSet<E> employeeList;
	public final ScheduleSetUp<E> setUp;
	public final Restaurant restaurant;
	public final Class<E> employeeType;
	private String workingSetID;
	private ResultSet results;
	private boolean accurate;
	
	public WorkingSet(Restaurant restaurant, Class<E> E_TYPE, int globalMax) {
		Driver.deciderLog.config("Generated empty working set from constructor of type "
				+ E_TYPE.getName() + " with " + globalMax + " global max hours");
		employeeType = E_TYPE;
		employeeList = new EmployeeSet<E>(E_TYPE);
		setUp = new ScheduleSetUp<E>();
		this.restaurant = restaurant;
		workingSetID = FileManager.fileFormat.format(new Date());
	}
	
	public WorkingSet(Restaurant restaurant,
			          Class<E> employeeType,
			          EmployeeSet<E> employeeList,
			          ScheduleSetUp<E> setUp) {
		Driver.deciderLog.config("Generated working set from specified paramters");
		this.employeeList = employeeList;
		this.setUp = setUp;
		this.employeeType = employeeType;
		this.restaurant = restaurant;
		// TODO writeToFile();
	}
	
	public void save(FileManager.SF statusFlag) {
		Driver.deciderLog.finest("WorkingSet.save(" + statusFlag.name() + ")");
		FileManager.saveWorkingSet(this, statusFlag);
		accurate = true;
	}
	
	public static <E extends Employee> WorkingSet<E> readFromFile(File input) {
		// TODO
		return null;
	}
	
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null) return false;
		if (!this.getClass().equals(o.getClass())) return false;
		
		
		return true;
	}
	
	public Class<?> getEmployeeType() {
		return employeeType;
	}
	
	boolean setIDisValid() {
		// TODO
		return false;
	}
	
	public String getSetID() {
		if (!setIDisValid()) {
			workingSetID = FileManager.fileFormat.format(new Date());
		}
		return workingSetID;
	}
	
	public void setResultSet(ResultSet results) {
		this.results = results;
		accurate = false;
	}
	
	public void setResultSet(OperationStack opStack, 
			                 QualifiedEmployeeListMap<E> qualMap,
			                 Collection<PositionID<? extends Employee>> completedIDs) {
		setResultSet(new ResultSet(opStack, qualMap, completedIDs));
	}
	
	public ResultSet getResultSet() {
		if (results == null) {
			Driver.deciderLog.severe("Tried to get result set when it was null. Returned null");
			return null;
		}
		accurate = false;
		return results;
	}
	
	public Schedule getSchedule() {
		if (results == null) {
			Driver.deciderLog.severe("Tried to get scheudle when it was null. Returned null");
			return null;
		}
		accurate = false;
		return results.schedule;
	}
	
	public boolean resultsArePresent() {
		return results != null;
	}
	
	public class ResultSet {
		public final QualifiedEmployeeListMap<E> qualMap;
		public final OperationStack opStack;
		public final Schedule schedule;
		
		ResultSet(OperationStack opStack,
				  QualifiedEmployeeListMap<E> qualMap,
				  Schedule schedule) {
			this.opStack = opStack;
			this.qualMap = qualMap;
			this.schedule = schedule;
		}
		
		ResultSet(OperationStack opStack,
		          QualifiedEmployeeListMap<E> qualMap,
		          Collection<PositionID<? extends Employee>> completedIDs) {
			this(opStack, qualMap, new Schedule(completedIDs));
		}
	}

	public static WorkingSet<Server> serverTrainingData(){
		Driver.deciderLog.config("Generating serverTrainingData from WorkingSet");
		
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
		
		WorkingSet<Server> workingSet = new WorkingSet<>(Restaurant.HACIENDA, Server.class, 60);
		workingSet.employeeList.addMultipleEmployees(servers);
		workingSet.setUp.trainingData();
		
		servers = null;
		
		Driver.deciderLog.config("RETURNING: WorkingSet.serverTrainingData");
		return workingSet;
	}
	
	public static void main(String[] args) throws IOException {
	}

}
