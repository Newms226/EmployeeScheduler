package database;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;

import decider.OperationStack;
import decider.QualifiedEmployeeListMap;
import decider.WorkingSet;
import driver.Driver;
import driver.Schedule;
import emp.Employee;
import emp.Server;

public class FileManager {
	public static final String ARRAY_ELEMENT_REGREX = "(\\[)([^\\s]+)(,*)([^\\s]*)(\\])";
	public static final Pattern ARRAY_ELEMENT_REGREX_MATCHER = Pattern.compile(ARRAY_ELEMENT_REGREX);
	
	public static final String ARRAY_REGREX = "\\{(\\[)([^\\s]+)(,*)([^\\s]*)(\\])\\}";
	public static final Pattern ARRAY_REGREX_MATCHER = Pattern.compile(ARRAY_ELEMENT_REGREX);
	
	static final String resourceFolder = "/Users/Michael/gitHub/EmployeeScheduler/WorkingSets"; 
	
	public static final String LINE_BREAK_CHAR = "_\\n_";
			            
	static String setID;
	private static File workingFile;
	private static PrintWriter writer; // TODO
	
	public static enum SF {
		BEFORE_SCHEDULE(01), 
		AFTER_SCHEDULE(02), 
		AFTER_VERIFY(03), 
		BEFORE_ROLLBACK(04), 
		ROLLED_BACK(05),
		MODIFIED_SCHEDULE(06),
		ON_EXIT(07);
		
		public final int ID_CODE;
		
		private SF(int ID) {
			ID_CODE = ID;
		}
	}

	
	public final static SimpleDateFormat fileFormat = new SimpleDateFormat("MMddyy.kkmm.ss.SSSS"); 
	static final String workingSetChar = "#w",
			             opStackChar = "#o",
			                listChar = "#l",
			               setUpChar = "#s",
			            queueMapChar = "#m",
			            scheduleChar = "#r";

	public static <E extends Employee> void saveAll(WorkingSet<E> set, SF statusFlag) {
		setID = fileFormat.format(new Date());
		workingFile = new File(resourceFolder + "/" + statusFlag.ID_CODE + "." + setID + ".txt");
//		if (!workingFile.mkdirs()) {
//			if (Driver.debugging)System.out.println("FAILED TO MAKE DIRECTORIES:\n  " + workingFile);
//			throw new Error();
//		}
		
		try {
			writer = new PrintWriter(workingFile);
		} catch (FileNotFoundException e ) {
			if (Driver.debugging) System.out.println("FAILED TO INITIATE PRINTWRITER");
			e.printStackTrace();
			throw new Error();
		}
		
		writer.write(setID + "\n" + statusFlag.name() + "\n" + set.getEmployeeType().getCanonicalName() + "\n");
		
		saveEmployeeList(set.employeeList);
		saveSetUp(set.setUp);
		saveQueueMap(set.queueMap);
		saveOperationsStack(set.opStack);
		saveSchedule(set.getSchedule());
		
		writer.close();
	}
	
	private static <E extends Employee> void saveEmployeeList(EmployeeList<E> list) {
		if (Driver.debugging) System.out.println("  Writing Employee List");
		writer.append(listChar + " " + list.count() + "\n" + list.toCSV());
		if (Driver.debugging) System.out.println("    DONE");
	}
	
	private static <E extends Employee> void saveOperationsStack(OperationStack opStack) {
		if (Driver.debugging) System.out.println("  Writing OperationStack");
		writer.append(opStackChar + " " + opStack.size() + "\n" + opStack.toCSV());
		if (Driver.debugging) System.out.println("    DONE");
		// TODO
	}
	
	private static <E extends Employee> void saveSetUp(ScheduleSetUp<E> setUp) {
		if (Driver.debugging) System.out.println("  Writing set-up");
		writer.append(setUpChar + " " + setUp.positionIDCount() + "\n" + setUp.getMaxHours() + "\n" + setUp.toCSV());
		if (Driver.debugging) System.out.println("    DONE");
	}
	
	private static <E extends Employee> void saveQueueMap(QualifiedEmployeeListMap<E> queueMap) {
		if (Driver.debugging) System.out.println("  Writing queue-map");
		writer.append(queueMapChar + " " + queueMap.getQueueCount() + "\n" + queueMap.toCSV());
		if (Driver.debugging) System.out.println("    DONE");
	}
	
	private static <E extends Employee> void saveSchedule(Schedule schedule) {
		if (Driver.debugging) System.out.println("  Writing Schedule");
		if (schedule == null) {
			if (Driver.debugging) System.out.println("   SCHEDULE NULL");
			return;
		}
		writer.append(scheduleChar + " " + schedule.getPositionIDCount() + "\n" + schedule.toCSV());
		if (Driver.debugging) System.out.println("    DONE");
	}
	
	public static WorkingSet<? extends Employee> readWorkingSet(File file) {
		String fileText = generateStringFromFile(file);
		String[] components = fileText.split("#");
		String employeeTypeString = components[0].split(LINE_BREAK_CHAR)[2];
		if (employeeTypeString.equals(Server.class.getCanonicalName())) {
			return readServerWorkingSet(components);
		}
		
		throw new Error("Cound not match \"" + employeeTypeString + "\"");
	}
	
	public static String generateStringFromFile(File file) {
		try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
			StringBuffer buffer = new StringBuffer();
			reader.lines().forEach(str -> buffer.append(str + LINE_BREAK_CHAR));
			return buffer.toString();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
	
	private static WorkingSet<Server> readServerWorkingSet(String[] componentArray) {
		EmployeeList<Server> employeeList = readEmployeeSet(componentArray[1]);
		return new WorkingSet<>(
				Server.class,
				employeeList,
				readScheduleSetUp(componentArray[2]),
				readOpStack(componentArray[4]),
				readQueueMap(componentArray[3], employeeList));
	}
	
	private static <E extends Employee> EmployeeList<E> readEmployeeSet(String str){
		// TODO
		return null;
	}
	
	private static OperationStack readOpStack(String str) {
		// TODO
		return null;
	}
	
	private static <E extends Employee> ScheduleSetUp<E> readScheduleSetUp(String str) {
		// TODO
		return null;
	}
	
	private static <E extends Employee> QualifiedEmployeeListMap<E> readQueueMap(String str, EmployeeList<E> list) {
		// TODO
		return null;
	}
	
	private static Schedule readSchedule(String str) {
		// TODO
		return null;
	}
	
	public static void main(String[] args) {
		System.out.println(generateStringFromFile(new File("/Users/Michael/gitHub/EmployeeScheduler/WorkingSets/061718.0103.54.0371.txt")));
	}
}
