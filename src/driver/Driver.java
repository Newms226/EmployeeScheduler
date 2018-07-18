package driver;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import Availability.SchedulableTimeChunk;
import Availability.TimeChunk;
import WorkingSet.Schedule;
import WorkingSet.ScheduleSetUp;
import driver.FileManager.SF;
import emp.Employee;
import emp.EmployeeSet;
import menu.ConsoleMenu;
import menu.RunnableOption;
import restaurant.PositionType;
import restaurant.Restaurant;

public class Driver {
	//public static final boolean debugging = true; 
	public static Logger driverLog, deciderLog, setUpLog, fileManagerLog, masterLog, availabilityLog, timeLog, errorLog;
	static {
		masterLog = Logger.getLogger("");
		Handler[] handlers = masterLog.getHandlers();
		if (handlers[0] instanceof ConsoleHandler) {
			masterLog.removeHandler(handlers[0]);
		}
		
		File workingDir = new File(
				FileManager.logFolder + "/" +
				FileManager.fileFormat.format(
						new Date()));
		if (!workingDir.mkdirs()) {
			throw new Error("COULD NOT MAKE DIRECTORIES AT:\n\t" + workingDir);
		}
		
		Handler console = new ConsoleHandler();
		FileHandler driverFile = null,
			    deciderFile = null,
			    setUpFile = null,
			    fileManagerFile = null,
			    masterFile = null,
			    availabilityFile = null,
			    timeFile = null;
		try {
			masterFile = new FileHandler(workingDir + "/%u.MASTER_LOG.txt");
			driverFile = new FileHandler(workingDir + "/%u.driverLog.txt");
			deciderFile = new FileHandler(workingDir + "/%u.schedulerLog.txt");
			setUpFile = new FileHandler(workingDir + "/%u.setUpLog.txt");
			fileManagerFile = new FileHandler(workingDir + "/%u.fileManagerLog.txt");
			availabilityFile = new FileHandler(workingDir + "/%u.availabilityLog.txt");
			timeFile = new FileHandler(workingDir + "/%u.timeLog.txt");
		} catch (SecurityException | IOException e) {
			throw new Error(e.getMessage() + " @ " + e.getStackTrace()[0]);
		}
		
		SimpleFormatter formater = new SimpleFormatter();
		console.setFormatter(formater);
		console.setLevel(Level.WARNING);
		
		masterFile.setFormatter(formater);
		masterFile.setLevel(Level.ALL);
		masterLog.addHandler(masterFile);
		masterLog.addHandler(console);
		masterLog.setLevel(Level.ALL);
		
		driverFile.setFormatter(formater);
		driverFile.setLevel(Level.ALL);
		driverLog = Logger.getLogger("driver");
		driverLog.setUseParentHandlers(false);
		driverLog.addHandler(driverFile);
		driverLog.addHandler(masterFile);
		driverLog.addHandler(console);
		driverLog.setLevel(Level.ALL);
		
		deciderFile.setFormatter(formater);
		deciderFile.setLevel(Level.ALL);
		deciderLog = Logger.getLogger("decider");
		deciderLog.setUseParentHandlers(false);
		deciderLog.addHandler(deciderFile);
		deciderLog.addHandler(masterFile);
		deciderLog.addHandler(console);
		deciderLog.setLevel(Level.ALL);
		
		setUpFile.setFormatter(formater);
		setUpFile.setLevel(Level.ALL);
		setUpLog = Logger.getLogger("setUp");
		setUpLog.setUseParentHandlers(false);
		setUpLog.addHandler(setUpFile);
		setUpLog.addHandler(masterFile);
		setUpLog.addHandler(console);
		setUpLog.setLevel(Level.ALL);
		
		fileManagerFile.setFormatter(formater);
		fileManagerFile.setLevel(Level.ALL);
		fileManagerLog = Logger.getLogger("fileManger");
		fileManagerLog.setUseParentHandlers(false);
		fileManagerLog.addHandler(fileManagerFile);
		fileManagerLog.addHandler(masterFile);
		fileManagerLog.addHandler(console);
		fileManagerLog.setLevel(Level.ALL);
		
		availabilityFile.setFormatter(formater);
		availabilityFile.setLevel(Level.ALL);
		availabilityLog = Logger.getLogger("availability");
		availabilityLog.setUseParentHandlers(false);
		availabilityLog.addHandler(availabilityFile);
		availabilityLog.addHandler(masterFile);
		availabilityLog.addHandler(console);
		availabilityLog.setLevel(Level.ALL);
		
		timeFile.setFormatter(formater);
		timeFile.setLevel(Level.ALL);
		timeLog = Logger.getLogger("time");
		timeLog.setUseParentHandlers(false);
		timeLog.addHandler(timeFile);
		timeLog.addHandler(masterFile);
		timeLog.addHandler(console);
		timeLog.setLevel(Level.ALL);
	}
	
	private String intro;
	private ConsoleMenu beginningInput, mainMenu, scheduleMenu, scheduleViewer;
	
	Restaurant restaurant;
	Scheduler DA_KING_MAKER;
	EmployeeSet employeeSet;
	ScheduleSetUp setUp;
	Schedule schedule;
	
	public Driver() throws SecurityException, IOException {
		intro =  "+~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~+"
		     + "\n|                                                |"
		 	 + "\n|                                                |"
			 + "\n|              Restaurant Scheduler              |"
			 + "\n|                                                |"
			 + "\n|                                 Michael Newman |"
			 + "\n+~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~+"
			 + "\n\n";
		
		beginningInput = new ConsoleMenu("Input Menu", 
				() -> mainMenu.selection()); // Hand off control to mainMenu after run
		beginningInput.add(new RunnableOption("Build from testing material",
				() -> buildTestingMaterial()));
		beginningInput.add(new RunnableOption("Load previous", null)); // TODO
		beginningInput.add(new RunnableOption("Build a new Restaurant & set-up", null)); // TODO
		
		mainMenu = new ConsoleMenu("Main Menu",
				null); // by default, do nothing after selection
		mainMenu.add(new RunnableOption("Schedule", 
				() -> schedule(), // TODO: Tests for needed components
				() -> mainMenu.selection()));
		mainMenu.add(new RunnableOption("View generated schedules", 
				() -> System.out.println(getSchedule()), // TODO: View previous schedules
				() -> mainMenu.selection()));
//		mainMenu.add(new RunnableOption("Employee menu", 
//				() -> EmployeeModifier.mainMenu.selection())); // TODO
		mainMenu.add(new RunnableOption("Schedule set-up", 
				() -> scheduleMenu.selection()));
		mainMenu.add(new RunnableOption("Exit program",
				() -> {
					FileManager.onExit(setUp, employeeSet, schedule);
					System.out.println("GOODBYE ^.^");
				}));
		
		scheduleViewer = new ConsoleMenu("Schedule Viewer");
		scheduleViewer.add(new RunnableOption("View whole schedule",
				() -> System.out.println(getSchedule())));
//		scheduleViewer.add("View indvidual Employees shifts", 
//				() -> {
//	
//				});
//		
		scheduleMenu = new ConsoleMenu("Schedule Set-Up");
//		scheduleMenu.add(new RunnableOption("Set-up days/shifts/priority", 
//				() -> workingSet.setUp.dayMenu()));
		scheduleMenu.add(new RunnableOption("Add a pre-defined schedule", null)); // TODO
		scheduleMenu.add(new RunnableOption("Return to main menu",
				() -> mainMenu.selection(),
				null)); // do nothing after this option
		
		
//		endMenu = new Menu("Ending program...", 
//				() -> System.exit(0));
//		endMenu.add("Save the set-up", null); // TODO
//		endMenu.add("Save the schedule", null); // TODO
//		endMenu.add("Save the Employee list", null); // TODO
//		endMenu.add("Save the restuarant", null); // TODO
//		endMenu.add("Save everything", null); // TODO
//		endMenu.add("Exit", () -> System.exit(0));
		//logSetUp();
	}
	
	void begin() {
		driverLog.finest("Began program, in begin()");
		System.out.println(intro);
		beginningInput.selection();
	}
	
	void buildTestingMaterial() {
		driverLog.config("Building testing material from Driver.buildTestingMaterial()");
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
		
		employeeSet = new EmployeeSet();
		employeeSet.addMultipleEmployees(Employees);
		
		setUp = new ScheduleSetUp();
		// Monday Lunch
		setUp.addTimeChunk(SchedulableTimeChunk.from(TimeChunk.from(1, 9, 0, 1, 15, 0), 5, PositionType.Bar));
		setUp.addTimeChunk(SchedulableTimeChunk.from(TimeChunk.from(1, 10, 0, 1, 15, 0), 5, PositionType.Closer));
		setUp.addTimeChunk(SchedulableTimeChunk.from(TimeChunk.from(1, 10, 0, 1, 15, 0), 5, PositionType.Closer));
		setUp.addTimeChunk(SchedulableTimeChunk.from(TimeChunk.from(1, 10, 0, 1, 15, 0), 5, PositionType.Head_Wait));
		setUp.addTimeChunk(SchedulableTimeChunk.from(TimeChunk.from(1, 11, 0, 1, 14, 0), 3, PositionType.Sales));
		setUp.addTimeChunk(SchedulableTimeChunk.from(TimeChunk.from(1, 11, 0, 1, 14, 0), 2, PositionType.Sales));
		setUp.addTimeChunk(SchedulableTimeChunk.from(TimeChunk.from(1, 11, 0, 1, 14, 0), 1, PositionType.Sales));
		//Tuesday Dinner
		setUp.addTimeChunk(SchedulableTimeChunk.from(TimeChunk.from(4, 15, 0, 4, 22, 0), 8, PositionType.Cocktail));
		setUp.addTimeChunk(SchedulableTimeChunk.from(TimeChunk.from(4, 15, 0, 4, 22, 0), 7, PositionType.Cocktail));
		setUp.addTimeChunk(SchedulableTimeChunk.from(TimeChunk.from(4, 15, 0, 4, 22, 0), 8, PositionType.Closer));
		setUp.addTimeChunk(SchedulableTimeChunk.from(TimeChunk.from(4, 15, 0, 4, 22, 0), 7, PositionType.Closer));
		setUp.addTimeChunk(SchedulableTimeChunk.from(TimeChunk.from(4, 15, 0, 4, 22, 0), 8, PositionType.Head_Wait));
		setUp.addTimeChunk(SchedulableTimeChunk.from(TimeChunk.from(4, 15, 0, 4, 22, 0), 7, PositionType.Head_Wait));
		setUp.addTimeChunk(SchedulableTimeChunk.from(TimeChunk.from(4, 16, 15, 4, 21, 0), 6, PositionType.Sales));
		setUp.addTimeChunk(SchedulableTimeChunk.from(TimeChunk.from(4, 16, 15, 4, 20, 2), 6, PositionType.Sales));
		setUp.addTimeChunk(SchedulableTimeChunk.from(TimeChunk.from(4, 17, 59, 4, 21, 0), 5, PositionType.Sales));
		setUp.addTimeChunk(SchedulableTimeChunk.from(TimeChunk.from(4, 17, 59, 4, 21, 0), 4, PositionType.Sales));
		setUp.addTimeChunk(SchedulableTimeChunk.from(TimeChunk.from(4, 17, 59, 4, 21, 0), 3, PositionType.Sales));
		// FridayDinner
		setUp.addTimeChunk(SchedulableTimeChunk.from(TimeChunk.from(5, 15, 0, 5, 20, 0), 10, PositionType.Bar));
		setUp.addTimeChunk(SchedulableTimeChunk.from(TimeChunk.from(5, 16, 0, 5, 21, 0), 10, PositionType.Bar));
		setUp.addTimeChunk(SchedulableTimeChunk.from(TimeChunk.from(5, 17, 0, 5, 23, 0), 9, PositionType.Bar));
		setUp.addTimeChunk(SchedulableTimeChunk.from(TimeChunk.from(5, 15, 0, 5, 20, 30), 10, PositionType.Cocktail));
		setUp.addTimeChunk(SchedulableTimeChunk.from(TimeChunk.from(5, 16, 30, 5, 23, 0), 10, PositionType.Cocktail));
		setUp.addTimeChunk(SchedulableTimeChunk.from(TimeChunk.from(5, 15, 0, 5, 23, 0), 10, PositionType.Closer));
		setUp.addTimeChunk(SchedulableTimeChunk.from(TimeChunk.from(5, 15, 0, 5, 23, 0), 10, PositionType.Closer));
		setUp.addTimeChunk(SchedulableTimeChunk.from(TimeChunk.from(5, 15, 0, 5, 23, 0), 10, PositionType.Head_Wait));
		setUp.addTimeChunk(SchedulableTimeChunk.from(TimeChunk.from(5, 15, 0, 5, 23, 0), 9, PositionType.Head_Wait));
		setUp.addTimeChunk(SchedulableTimeChunk.from(TimeChunk.from(5, 15, 0, 5, 22, 0), 6, PositionType.Sales));
		setUp.addTimeChunk(SchedulableTimeChunk.from(TimeChunk.from(5, 15, 0, 5, 21, 0), 5, PositionType.Sales));
		setUp.addTimeChunk(SchedulableTimeChunk.from(TimeChunk.from(5, 16, 0, 5, 21, 0), 6, PositionType.Sales));
		setUp.addTimeChunk(SchedulableTimeChunk.from(TimeChunk.from(5, 16, 0, 5, 21, 0), 6, PositionType.Sales));
		setUp.addTimeChunk(SchedulableTimeChunk.from(TimeChunk.from(5, 17, 0, 5, 21, 0), 6, PositionType.Sales));
		setUp.addTimeChunk(SchedulableTimeChunk.from(TimeChunk.from(5, 17, 0, 5, 21, 0), 6, PositionType.Sales));
		setUp.addTimeChunk(SchedulableTimeChunk.from(TimeChunk.from(5, 17, 0, 5, 20, 0), 6, PositionType.Sales));
		
		driverLog.config("Successfully built testing material from Driver.buildTestingMaterial()");
	}

	void schedule() {
//		driverLog.info("Began scheduling process from driver");
//		if (workingSet.employeeList.count() == 0) {
//			driverLog.severe("Employee list is empty");
//		}
//		if (workingSet.setUp.positionIDCount() == 0) {
//			driverLog.severe("Schedule is not set-up");
////			workingSet.setUp.dayMenu();
//			throw new Error();
//		}
//			
		driverLog.log(Level.INFO, "HANDING OFF TO SCHEDULER");
		DA_KING_MAKER = new Scheduler(employeeSet, setUp);
		DA_KING_MAKER.run();
		driverLog.log(Level.INFO, "RETURNED TO DRIVER");
		
	}
	
	String getSchedule() {
		return schedule.viewAll();
	}
	
	public static void main(String[] args) throws SecurityException, IOException {
//		Driver work = new Driver();
//		work.begin();
	}

}
