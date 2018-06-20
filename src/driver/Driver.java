package driver;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Date;
import java.util.logging.*;

import Menu.Menu;
import database.FileManager;
import decider.Scheduler;
import decider.WorkingSet;
import emp.Server;
import tools.CollectionTools;

public class Driver {
	//public static final boolean debugging = true; 
	public static Logger driverLog, empLog, deciderLog, databaseLog;
	static {
		driverLog = Logger.getLogger("");
		Handler[] handlers = driverLog.getHandlers();
		if (handlers[0] instanceof ConsoleHandler) {
			driverLog.removeHandler(handlers[0]);
		}
		Handler console = new ConsoleHandler();
		console.setLevel(Level.SEVERE);
		SimpleFormatter formater = new SimpleFormatter();
		console.setFormatter(formater);
		FileHandler file = null;
		try {
			file = new FileHandler("/Users/Michael/gitHub/EmployeeScheduler/logs/"
					+ FileManager.fileFormat.format(new Date()) + ".txt");
		} catch (SecurityException | IOException e) {
			throw new Error();
		}
		file.setFormatter(formater);
		driverLog.setLevel(Level.ALL);
		driverLog.addHandler(console);
		driverLog.addHandler(file);
		
		deciderLog = Logger.getLogger("decider");
		deciderLog.setUseParentHandlers(true);
		
		empLog = Logger.getLogger("emp");
		empLog.setUseParentHandlers(true);
		
		databaseLog = Logger.getLogger("database");
		databaseLog.setUseParentHandlers(true);
	}
	
	private String intro;
	private Menu beginningInput, mainMenu, scheduleMenu, employeeMenu, scheduleViewer;
	
	Restaurant restaurant;
	Scheduler<Server> DA_KING_MAKER;
	WorkingSet<Server> workingSet;
	
	public Driver() throws SecurityException, IOException {
		intro =  "+~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~+"
		     + "\n|                                                |"
		 	 + "\n|                                                |"
			 + "\n|              Restaurant Scheduler              |"
			 + "\n|                                                |"
			 + "\n|                                 Michael Newman |"
			 + "\n+~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~+"
			 + "\n\n";
		
		beginningInput = new Menu("Input Menu", () -> mainMenu.selection());
		beginningInput.add("Build from testing material",
				() -> buildTestingMaterial());
		beginningInput.add("Build from previous Set-Up", null); // TODO
		beginningInput.add("Build a new Restaurant & set-up", null); // TODO
		
		mainMenu = new Menu ("Main Menu",
				null); // by default, do nothing after selection
		mainMenu.add("Schedule", 
				() -> schedule(), // TODO: Tests for needed components
				() -> mainMenu.selection());
		mainMenu.add("View generated schedules", 
				() -> System.out.println(getSchedule()), // TODO: View previous schedules
				() -> mainMenu.selection());
		mainMenu.add("Employee menu", 
				() -> employeeMenu.selection());
		mainMenu.add("Schedule set-up", 
				() -> scheduleMenu.selection());
		mainMenu.add("Exit program",
				() -> {
					if (workingSet != null) workingSet.save(FileManager.SF.ON_EXIT);
					System.exit(0);
				});
		
		scheduleViewer = new Menu("Schedule Viewer");
		scheduleViewer.add("View whole schedule",
				() -> System.out.println(getSchedule()));
		scheduleViewer.add("View indvidual servers shifts", 
				() -> {
	
				});
		
		scheduleMenu = new Menu("Schedule Set-Up");
		scheduleMenu.add("Set-up days/shifts/priority", 
				() -> workingSet.setUp.dayMenu());
		scheduleMenu.add("Add a pre-defined schedule", null); // TODO
		scheduleMenu.add("Return to main menu",
				() -> mainMenu.selection(),
				null); // do nothing after this option
		
		employeeMenu = new Menu("Employee Menu");
//		employeeMenu.add("Add a new employee", 
//				() -> serverList.addServer()); // TODO
		employeeMenu.add("Modify employee", null); // TODO
		employeeMenu.add("Change grace of employee", null); // TODO
		employeeMenu.add("Remove an employee", null); // TODO
		employeeMenu.add("Return to the main menu", 
				() -> mainMenu.selection(),
				null); // Do nothing after this.
		
//		endMenu = new Menu("Ending program...", 
//				() -> System.exit(0));
//		endMenu.add("Save the set-up", null); // TODO
//		endMenu.add("Save the schedule", null); // TODO
//		endMenu.add("Save the server list", null); // TODO
//		endMenu.add("Save the restuarant", null); // TODO
//		endMenu.add("Save everything", null); // TODO
//		endMenu.add("Exit", () -> System.exit(0));
		//logSetUp();
	}
	
	void logSetUp() throws SecurityException, IOException {
		
	}
	
	void begin() {
		driverLog.finest("Began program, in begin()");
		System.out.println(intro);
		beginningInput.selection();
	}
	
	void buildTestingMaterial() {
		driverLog.config("Building testing material from Driver.buildTestingMaterial()");
		workingSet = WorkingSet.serverTrainingData();
		driverLog.config("Successfully built testing material from Driver.buildTestingMaterial()");
	}

	void schedule() {
		driverLog.info("Began scheduling process from driver");
		if (workingSet.employeeList.count() == 0) {
			driverLog.severe("Employee list is empty");
		}
		if (workingSet.setUp.positionIDCount() == 0) {
			driverLog.severe("Schedule is not set-up");
//			workingSet.setUp.dayMenu();
			throw new Error();
		}
		
		driverLog.log(Level.INFO, "HANDING OFF TO SCHEDULER");
		DA_KING_MAKER = new Scheduler<>(workingSet);
		DA_KING_MAKER.run();
		driverLog.log(Level.INFO, "RETURNED TO DRIVER");
		
	}
	
	String getSchedule() {
		return workingSet.getSchedule().viewAll();
	}
	
	public static void main(String[] args) throws SecurityException, IOException {
		Driver work = new Driver();
		work.begin();
	}

}
