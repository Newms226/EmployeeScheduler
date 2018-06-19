package driver;
import Menu.Menu;
import database.FileManager;
import decider.Scheduler;
import decider.WorkingSet;
import emp.Server;
import tools.CollectionTools;

public class Driver {
	public static final boolean debugging = true; 
	
	private String intro;
	private Menu beginningInput, mainMenu, scheduleMenu, employeeMenu, scheduleViewer;
	
	Restaurant restaurant;
	Scheduler<Server> DA_KING_MAKER;
	WorkingSet<Server> workingSet;
	
	public Driver() {
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
	}
	
	void begin() {
		System.out.println(intro);
		beginningInput.selection();
	}
	
	void buildTestingMaterial() {
		workingSet = WorkingSet.serverTrainingData();
	}

	void schedule() {
		if (workingSet.employeeList.count() == 0) {
			if (Driver.debugging) System.out.println("Employee list is empty");
			throw new Error(); // TODO
		}
		if (workingSet.setUp.positionIDCount() == 0) {
			if (Driver.debugging) System.out.println("Schedule is not set-up");
			workingSet.setUp.dayMenu();
		}
		
		if (Driver.debugging) System.out.println("HANDING OFF TO SCHEDULER...\n\n");
		DA_KING_MAKER = new Scheduler<>(workingSet);
		DA_KING_MAKER.run();
		if(Driver.debugging) System.out.println("\nRETURNED TO DRIVER\n");
		
	}
	
	String getSchedule() {
		return workingSet.getSchedule().viewAll();
	}
	
	public static void main(String[] args) {
		Driver work = new Driver();
		work.begin();
	}

}
