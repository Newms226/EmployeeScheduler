package database;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;

import decider.OperationStack;
import decider.QualifiedEmployeeListMap;
import decider.WorkingSet;
import driver.Driver;
import emp.Employee;

public class FileManager {
	public static final String ARRAY_ELEMENT_REGREX = "(\\[)([^\\s]+)(,*)([^\\s]*)(\\])";
	public static final Pattern ARRAY_ELEMENT_REGREX_MATCHER = Pattern.compile(ARRAY_ELEMENT_REGREX);
	
	public static final String ARRAY_REGREX = "\\{(\\[)([^\\s]+)(,*)([^\\s]*)(\\])\\}";
	public static final Pattern ARRAY_REGREX_MATCHER = Pattern.compile(ARRAY_ELEMENT_REGREX);
	
	static final String resourceFolder = "/Users/Michael/eclipse-workspace/HaciendaSchedule/WorkingSets";
			            
	static String setID;
	private static File workingFile;
	private static PrintWriter writer; // TODO
	
	public static enum SF {
		BEFORE_SCHEDULE, 
		AFTER_SCHEDULE, 
		AFTER_VERIFY, 
		BEFORE_ROLLBACK, 
		ROLLED_BACK,
		MODIFIED_SCHEDULE;
	}

	
	public final static SimpleDateFormat fileFormat = new SimpleDateFormat("MMddyy.kkmm.ss.SSSS"); 
	static final String workingSetChar = "w",
			             opStackChar = "o",
			                listChar = "l",
			               setUpChar = "s",
			            queueMapChar = "m";

	public static <E extends Employee> void saveAll(WorkingSet<E> set) {
		setID = fileFormat.format(new Date());
		workingFile = new File(resourceFolder + "/" + setID + ".txt");
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
		
		writer.write(setID + "\n" + set.getEmployeeType().getSimpleName() + "\n");
		
		saveEmployeeList(set.employeeList);
		saveSetUp(set.setUp);
		saveQueueMap(set.queueMap);
		saveOperationsStack(set.opStack);
		
		writer.close();
	}
	
	private static <E extends Employee> void saveEmployeeList(EmployeeList<E> list) {
		if (Driver.debugging) System.out.println("  Writing Employee List");
		writer.append(listChar + list.count() + "\n" + list.toCSV() + "\n");
		if (Driver.debugging) System.out.println("    DONE");
	}
	
	private static <E extends Employee> void saveOperationsStack(OperationStack opStack) {
		if (Driver.debugging) System.out.println("  Writing OperationStack");
		writer.append(opStackChar + opStack.size() + "\n" + opStack.toCSV()+ "\n");
		if (Driver.debugging) System.out.println("    DONE");
		// TODO
	}
	
	private static <E extends Employee> void saveSetUp(ScheduleSetUp<E> setUp) {
		if (Driver.debugging) System.out.println("  Writing set-up");
		writer.append(setUpChar + setUp.positionIDCount() + "\n" + setUp.getMaxHours() + setUp.toCSV()+ "\n");
		if (Driver.debugging) System.out.println("    DONE");
	}
	
	private static <E extends Employee> void saveQueueMap(QualifiedEmployeeListMap<E> queueMap) {
		if (Driver.debugging) System.out.println("  Writing queue-map");
		writer.append(queueMapChar + queueMap.getQueueCount() + "\n" + queueMap.toCSV()+ "\n");
		if (Driver.debugging) System.out.println("    DONE");
	}

	
//	public static <E extends Employee>
	
//	private static void testRegrex() {
//		String queueTest = "[ME,9.2]";
//		Matcher tester;
//		tester = ARRAY_ELEMENT_REGREX_MATCHER.matcher(queueTest);
//		System.out.println(tester.matches());
//	}
	
	public static void main(String[] args) {
//		ScheduleSetUp<Server> test = new ScheduleSetUp<>();
//		test.trainingData();
//		FileManager.saveSetUp(test);
//		System.out.println(Operation.OperationType.valueOf("a").name() + " " + Operation.OperationType.valueOf("a").toString());
	}
}
