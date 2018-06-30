package driver;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Date;
import java.util.logging.*;

import WorkingSet.WorkingSet;
import emp.Server;
import menu.ConsoleMenu;
import menu.RunnableOption;
import restaurant.Restaurant;
import tools.CollectionTools;

public class Driver {
	//public static final boolean debugging = true; 
	public static Logger driverLog, deciderLog, setUpLog, fileManagerLog, masterLog, availabilityLog, timeLog;
	public static final String LOG_DIRECTORY = "/Users/Michael/gitHub/EmployeeScheduler/logs/";
	static {
		masterLog = Logger.getLogger("");
		Handler[] handlers = masterLog.getHandlers();
		if (handlers[0] instanceof ConsoleHandler) {
			masterLog.removeHandler(handlers[0]);
		}
		
		File workingDir = new File(LOG_DIRECTORY + FileManager.fileFormat.format(new Date()));
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
	private ConsoleMenu beginningInput, mainMenu, scheduleMenu, employeeMenu, scheduleViewer;
	
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
		mainMenu.add(new RunnableOption("Employee menu", 
				() -> employeeMenu.selection()));
		mainMenu.add(new RunnableOption("Schedule set-up", 
				() -> scheduleMenu.selection()));
		mainMenu.add(new RunnableOption("Exit program",
				() -> {
					if (workingSet != null) workingSet.save(FileManager.SF.ON_EXIT);
					System.exit(0);
				}));
		
		scheduleViewer = new ConsoleMenu("Schedule Viewer");
		scheduleViewer.add(new RunnableOption("View whole schedule",
				() -> System.out.println(getSchedule()));
		scheduleViewer.add("View indvidual servers shifts", 
				() -> {
	
				});
		
		scheduleMenu = new CMenu("Schedule Set-Up");
		scheduleMenu.add("Set-up days/shifts/priority", 
				() -> workingSet.setUp.dayMenu());
		scheduleMenu.add("Add a pre-defined schedule", null); // TODO
		scheduleMenu.add("Return to main menu",
				() -> mainMenu.selection(),
				null); // do nothing after this option
		
		employeeMenu = new CMenu("Employee Menu");
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
//		Driver work = new Driver();
//		work.begin();
		LocalTime one = LocalTime.of(5, 17);
		LocalTime two = LocalTime.of(5, 16);
		System.out.println(one.isAfter(two));
	}

}
