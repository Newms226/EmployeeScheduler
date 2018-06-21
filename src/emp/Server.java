package emp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import driver.Driver;
import restaurant.PositionID;
import restaurant.PositionType;
import tools.DriverTools;
import tools.NumberTools;

public class Server extends Employee {

	private static final long serialVersionUID = -4175692530707891160L;

	public Server(String name, int ID, LocalDate startDate, ArrayList<PositionType> possibleShifts) {
		super(name, ID, startDate, getDefaultAvail(), 0, 40, 35, 0, possibleShifts);
		employeeType = EmployeeType.Server;
	}
	
	public Server(String name, int ID, LocalDate startDate, boolean[][] possibleHours, double pay, int maxHours,
			int desiredHours, int minHours, ArrayList<PositionType> possibleShifts) {
		super(name, ID, startDate, possibleHours, pay, maxHours, desiredHours, minHours, possibleShifts);
		employeeType = EmployeeType.Server;
	}
	
	public static String printServersByStaticPriority(List<Server> servers) {
		StringBuffer buffer = new StringBuffer();
		servers
			.stream()
			.sorted(DESENDING_PRIORITY_ORDER)
			.forEach(s -> buffer.append(NumberTools.format(s.employeePriority.calculateStaticPriority())
					+ ": " + s.toString() + "(filled: " + s.filledShifts + ")\n"));
		
		return buffer.toString();
	}
	
	
	public static boolean[][] parseHours() {
		//TODO
		return null;
	}
	
	/* 0 = -: 5, 7-23
	 * 
	 */
	public static boolean[][] parseHours(String str){
		//TODO
		return null;
	}
	
	@Override
	public void markUnavailable(PositionID<? extends Employee> ID) {
		ClassNotEqualException.assertEqual(Server.class, ID.employeeType);
		
		if (this.availableHours[ID.getDay().dayOfWeek][ID.getShiftType().ordinal()] == false) {
			IllegalArgumentException e = new IllegalArgumentException("EROR: " + NAME + " is marked as unavailable for " + ID + "\n" + printAvailability());
			Driver.empLog.log(Level.SEVERE, e.getMessage(), e);
			throw e;
		}
		this.availableHours[ID.getDay().dayOfWeek][ID.getShiftType().ordinal()] = false;
	}
	
	@Override
	public void markAvailable(PositionID<? extends Employee> ID) {
		ClassNotEqualException.assertEqual(Server.class, ID.employeeType);
		// TODO Auto-generated method stub	
	}
	
	@Override
	public void accept(PositionID<? extends Employee> ID) {
//		new Exception("  IN ACCEPT: " + NAME + " to " + ID).printStackTrace();
		ClassNotEqualException.assertEqual(Server.class, ID.employeeType);
		
		filledShifts++; // TODO: Change for hours
		ID.assignEmployee(this);
		Driver.empLog.info("Accepted " + NAME + " to " + ID 
			/*+ "\n" + printAvailability()*/);
		markUnavailable(ID);
		assignedShifts.add(ID);
	}
	
	@Override
	public void rollBack(PositionID<? extends Employee> ID) {
		// TODO Auto-generated method stub
		
	}
	
	@SuppressWarnings("unlikely-arg-type")
	public boolean ofType(Class<?> type) {
		return employeeType.equals(type);
	}
	
	public static Server build() {
		return new Server(DriverTools.generateString("What is the servers name?"),
			NumberTools.generateInt("What is the servers ID?", false, 0, 9999),
			LocalDate.of(
					NumberTools.generateInt("What year did the server start?", false, 1970, LocalDate.now().getYear()),
					NumberTools.generateInt("What month did the server start?", false, 1, 12),
					NumberTools.generateInt("What day did the server start?", false, 1, 31)),
			parseHours(),
			NumberTools.generateDouble("How much does the server make?",false, 0, 30),
			NumberTools.generateInt("What are the servers max hours?", false, 0, 60),
			NumberTools.generateInt("What are the servers desired hours?", false, 0, 60),
			NumberTools.generateInt("What are the servers min hours?", false, 0, 60),
				PositionType.buildServerPositions());
	}
	
	public static Server fromCSV(String str) {
		// TOD
		return null;
	}
	
	public static void testPriorityCalc() {
		ArrayList<PositionType> w = new ArrayList<>(5);
		w.add(PositionType.Bar);
		ArrayList<PositionType> bar = new ArrayList<>(5);
		bar.add(PositionType.Bar);
		bar.add(PositionType.Cocktail);
		ArrayList<PositionType> king = new ArrayList<>(5);
		king.add(PositionType.Closer);
		king.add(PositionType.Head_Wait);
		king.add(PositionType.Cocktail);
		king.add(PositionType.Sales);
		ArrayList<PositionType> handyMan = new ArrayList<>(5);
		handyMan.add(PositionType.Bar);
		handyMan.add(PositionType.Closer);
		handyMan.add(PositionType.Head_Wait);
		handyMan.add(PositionType.Cocktail);
		handyMan.add(PositionType.Sales);
		Server kim = new Server("Kim",1,  LocalDate.of(2017, 1, 1), king);
		Server wade = new Server("wade",2,  LocalDate.of(2000, 1, 1), w);
		Server ash = new Server("ash",3,  LocalDate.of(2005, 1, 1), w);
		Server chris = new Server("Chris",4,  LocalDate.of(2010, 1, 1), handyMan);
		Server dena = new Server("Dena",5, LocalDate.of(2013, 1, 1), king);
		Server blake = new Server("blake",6, LocalDate.of(2014, 1, 1), handyMan);
		Server chels = new Server("chels",7, LocalDate.of(2010, 1, 1), king);
		Server brandon = new Server("brandon",8, LocalDate.of(2018, 1, 1), king);
		Server me = new Server("me",9, LocalDate.of(2017, 1, 1), king);
		//			chris.fastPromote();
		//			wade.setGrace(1);
		kim.fastPromote();
		blake.promote();
		wade.fastPromote();
		dena.fastPromote();
		ash.fastPromote();
		me.promote();
		//chris.setGrace(6);
		brandon.lowPromote();
		wade.filledShifts++;
		wade.employeePriority.getCurrentPriority(0);
		//			wade.filledShifts++;
		//			wade.serverPriority.getCurrentPriority(0);
		ash.filledShifts++;
		ash.employeePriority.getCurrentPriority(0);
		wade.filledShifts++;
		wade.employeePriority.getCurrentPriority(1);
		ash.filledShifts++;
		ash.employeePriority.getCurrentPriority(1);
		ArrayList<Server> servers = new ArrayList<Server>();
		servers.add(kim);
		servers.add(wade);
		servers.add(chris);
		servers.add(blake);
		servers.add(chels);
		servers.add(brandon);
		servers.add(dena);
		servers.add(ash);
		servers.add(me);
		System.out.println(printServersByStaticPriority(servers));
	}
	
	public static void main(String[] args) {
		String parsed = LocalDate.now().toString();
		System.out.println(parsed + " > " + LocalDate.parse(parsed));
	}



	
}
