package driver;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.DirectoryStream;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import WorkingSet.OperationStack;
import WorkingSet.Schedule;
import WorkingSet.ScheduleSetUp;
import driver.FileManager.SF;
import emp.EmployeeSet;
import racer.StopWatch;

public class FileManager {
	
	public static void main(String[] args) throws IOException {
		cleanUpLogs();
	}
	
	private FileManager() {}
	
	/******************************************************************************
	 *                                                                            *
	 *                                                                            *
	 *                                Static Fields                               *
	 *                                                                            *
	 *                                                                            *
	 ******************************************************************************/
	
	public final static SimpleDateFormat fileFormat = new SimpleDateFormat("MMddyy.kkmm.ss.SSSS"); 
	private static final Logger log = Driver.fileManagerLog;
	
	public static final String WORKING_FOLDER = "/Users/Michael/gitHub/EmployeeScheduler/files/";
	static final String logFolder = WORKING_FOLDER + "logs";
	
	static final int DAYS_TO_DELETE_LOGS_AFTER = 10;
	static final ChronoUnit DELETE_UNIT = ChronoUnit.DAYS;
	
	static final int DAYS_TO_ARCHIVE_AFTER = 30;
	static final ChronoUnit ARCHIVE_UNIT = ChronoUnit.DAYS;
	
	public static enum SF {
		BEFORE_SCHEDULE, 
		AFTER_SCHEDULE, 
		AFTER_VERIFY, 
		BEFORE_ROLLBACK, 
		ROLLED_BACK,
		MODIFIED_SCHEDULE,
		ON_EXIT;
	}
	
	/******************************************************************************
	 *                                                                            *
	 *                                                                            *
	 *                          Serialization Methods                             *
	 *                                                                            *
	 *                                                                            *
	 ******************************************************************************/
	
	public static boolean serializeScheduleSetUp(SF statusFlag, ScheduleSetUp toSerialize) {
		// TODO
		return false;
	}
	
	public static boolean serializeEmployeeSet(SF statusFlag, EmployeeSet toSerialize) {
		// TODO
		return false;
	}
	
	public static boolean serializeSchedule(SF statusFlag, Schedule toSerialize) {
		// TODO
		return false;
	}
	
	public static boolean serializeOperationsStack(SF statusFlag, OperationStack opStack) {
		// TODO 
		return false;
	}
	
	private static boolean serialize(SF statusFlag, Serializable toSerialize, File location) {
		// TODO
		return false;
	}
	
	public static void onExit(ScheduleSetUp setUp, EmployeeSet employeeSet, Schedule schedule) {
		if (setUp != null) {
			serializeScheduleSetUp(SF.ON_EXIT, setUp);
		}
		
		if (schedule != null) {
			serializeSchedule(SF.ON_EXIT, schedule);
		}
		
		if (employeeSet != null) {
			serializeEmployeeSet(SF.ON_EXIT, employeeSet);
		}
	}

	/******************************************************************************
	 *                                                                            *
	 *                                                                            *
	 *                           Log Deletion Methods                             *
	 *                                                                            *
	 *                                                                            *
	 ******************************************************************************/
	
	private static class DeleteFileVisitor extends SimpleFileVisitor<Path> {
		@Override
		public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
			log.finest("ATTEMPT: Deleting > " + file);
			if (!file.toFile().delete()) {
				log.warning("FAILURE: Failed to delete file > " + file);
			} 
			return FileVisitResult.CONTINUE;
		}
		
		@Override
		public FileVisitResult postVisitDirectory(Path dir, IOException e) throws IOException {
			if (e == null) {
				log.finest("ATTEMPT: Deleting dir > " + dir);
				if (!dir.toFile().delete()) {
					log.warning("FAILURE: Failed to delete directory > " + dir);
				}
			} else {
				log.log(Level.SEVERE, "FAILURE: " + e.getMessage(), e);
			}
			return FileVisitResult.CONTINUE;
		}
	}
	
	private static class ReadyToDeleteFilter implements DirectoryStream.Filter<Path>{
		Instant deleteAfter;
		
		private ReadyToDeleteFilter(Instant deleteAfter) {
			this.deleteAfter = deleteAfter;
		}
		
		@Override
		public boolean accept(Path file) throws IOException {
			return deleteAfter.compareTo(Files.readAttributes(file, BasicFileAttributes.class)
								.creationTime().toInstant()) >= 0;
		}
	}
	
	public static void cleanUpLogs() {
		log.entering(FileManager.class.getName(), "cleanUpLogs()");
		long startTime = System.nanoTime();
		try (DirectoryStream<Path> paths = Files.newDirectoryStream(new File(logFolder).toPath(), 
				new ReadyToDeleteFilter(Instant.now().minus(DAYS_TO_DELETE_LOGS_AFTER, DELETE_UNIT))))
		{
			DeleteFileVisitor deleator = new DeleteFileVisitor();
			paths.forEach(p -> {
				try {
					Files.walkFileTree(p, deleator);
				} catch (IOException e) {
					log.log(Level.SEVERE, "FAILURE: " + e.getMessage(), e);
				}
			});
		} catch (IOException e) {
			log.log(Level.SEVERE, "FAILURE: " + e.getMessage(), e);
		}
		long endTime = System.nanoTime();
		log.finer("RETURNING: SUCCESS > cleanUpLogs() in " + StopWatch.nanosecondsToString(endTime - startTime));
	}
	
//	
//	public static final String ARRAY_ELEMENT_REGREX = "(\\[)([^\\s]+)(,*)([^\\s]*)(\\])";
//	public static final Pattern ARRAY_ELEMENT_REGREX_MATCHER = Pattern.compile(ARRAY_ELEMENT_REGREX);
//	
//	public static final String ARRAY_REGREX = "\\{(\\[)([^\\s]+)(,*)([^\\s]*)(\\])\\}";
//	public static final Pattern ARRAY_REGREX_MATCHER = Pattern.compile(ARRAY_ELEMENT_REGREX);
//	
//	
//	private static File workingFile;
//	private static PrintWriter writer; // TODO
//	
//	public static enum SF {
//		BEFORE_SCHEDULE(01), 
//		AFTER_SCHEDULE(02), 
//		AFTER_VERIFY(03), 
//		BEFORE_ROLLBACK(04), 
//		ROLLED_BACK(05),
//		MODIFIED_SCHEDULE(06),
//		ON_EXIT(07);
//		
//		public final int ID_CODE;
//		
//		private SF(int ID) {
//			ID_CODE = ID;
//		}
//	}
//	
//	
//	static final String workingSetChar = "#w",
//			               opStackChar = "#o",
//			                  listChar = "#l",
//			                 setUpChar = "#su",
//			              queueMapChar = "#m",
//			              scheduleChar = "#sc";
//	
//	private static void setUp(SF statusFlag, String setID) {
//		workingFile = new File(workingSetFolder + "/" + statusFlag.ID_CODE + "." + setID + ".txt");
//		try {
//			writer = new PrintWriter(workingFile);
//			Driver.fileManagerLog.fine("Successfully intiated PrintWriter in FileManager.setUp");
//		} catch (FileNotFoundException e ) {
//			Driver.fileManagerLog.log(Level.SEVERE, "FAILED TO INITIATE PRINTWRITER AT " + workingFile, e);
//			throw new Error();
//		}
//	}
//
//	public static <E extends Employee> void saveWorkingSet(WorkingSet<E> set, SF statusFlag) {
//		setUp(statusFlag, set.getSetID());
//		                                                          // Line numbers & Array Index
//		writer.write(set.getSetID()                               // 0
//				+ "\n" + statusFlag.name()                        // 1
//				+ "\n" + set.getEmployeeType().getCanonicalName() // 2
//				+ "\n" + set.restaurant.toCSV()                   // 3
//				+ "\n" + set.resultsArePresent() + "\n");         // 4
//		
//		saveEmployeeList(set.employeeList);
//		saveSetUp(set.setUp);
//		
//		if (set.resultsArePresent()) {
//			WorkingSet<E>.ResultSet results = set.getResultSet();
//			saveQueueMap(results.qualMap);
//			saveOperationsStack(results.opStack);
//			saveSchedule(results.schedule);
//		}
//		
//		writer.close();
//	}
//	
//	private static <E extends Employee> void saveEmployeeList(EmployeeSet<E> list) {
//		Driver.fileManagerLog.info("Writing Employee List");
//		writer.append(listChar + " " + list.count() + "\n" + list.toCSV());
//		Driver.fileManagerLog.info("Finished writing Employee List");
//	}
//	
//	private static <E extends Employee> void saveSetUp(ScheduleSetUp<E> setUp) {
//		Driver.fileManagerLog.info("Writing set-up");
//		writer.append(setUpChar + " " + setUp.positionIDCount() + "\n" + setUp.toCSV());
//		Driver.fileManagerLog.info("Finished writing set-up");
//	}
//	
//	private static <E extends Employee> void saveOperationsStack(OperationStack opStack) {
//		Driver.fileManagerLog.info("Writing Operation stack");
//		writer.append(opStackChar + " " + opStack.count() + "\n" + opStack.toCSV());
//		Driver.fileManagerLog.info("Finished writing Operation stack");
//		// TODO
//	}
//	
//	private static <E extends Employee> void saveQueueMap(QualifiedEmployeeListMap<E> queueMap) {
//		Driver.fileManagerLog.info("Writing queue-map");
//		writer.append(queueMapChar + " " + queueMap.getQueueCount() + "\n" + queueMap.toCSV());
//		Driver.fileManagerLog.info("Finished writing queue-map");
//	}
//	
//	private static <E extends Employee> void saveSchedule(Schedule schedule) {
//		Driver.fileManagerLog.info("Writing Schedule");
//		writer.append(scheduleChar + " " + schedule.getPositionIDCount() + "\n" + schedule.toCSV());
//		Driver.fileManagerLog.info("Finished writing schedule");
//	}
//	
//	public static WorkingSet<? extends Employee> readWorkingSet(File file) {
//		String fileText = generateStringFromFile(file);
//		String[] components = fileText.split("#");
//		String employeeTypeString = components[0].split(LINE_BREAK_CHAR)[2];
//		if (employeeTypeString.equals(Server.class.getCanonicalName())) {
//			return readServerWorkingSet(components);
//		}
//		
//		throw new Error("Cound not match \"" + employeeTypeString + "\"");
//	}
	
//	public static String generateStringFromFile(File file) {
//		try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
//			StringBuffer buffer = new StringBuffer();
//			reader.lines().forEach(str -> buffer.append(str + LINE_BREAK_CHAR));
//			return buffer.toString();
//		} catch (FileNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
//		return null;
//	}
//	
//	private static WorkingSet<Server> readServerWorkingSet(String[] componentArray) {
//		EmployeeSet<Server> employeeList = readServerSet(componentArray[1]);
//		return new WorkingSet<>(
//				Server.class,
//				employeeList,
//				FileManager.<Server>readScheduleSetUp(componentArray[2]),
//				FileManager.<Server>readOpStack(componentArray[4], employeeList),
//				FileManager.<Server>readQueueMap(componentArray[3], employeeList));
//	}
//	
//	private static EmployeeSet<Server> readServerSet(String str){
//		String[] components = str.split(LINE_BREAK_CHAR);
//		if (!components[0].contains(listChar)) {
//			throw new Error("Invalid call to readEmployeeSet: " + components[0]);
//		}
//		
//		EmployeeSet<Server> list = new EmployeeSet<Server>(Server.class);
//		for (int i = 2; i < components.length; i++) {
//			list.addEmployee(Server.fromCSV(StringTools.trimBraces(components[i])));
//		}
//		
//		if (Integer.parseInt(components[0].substring(3, components[0].length() - 1)) != list.count()) {
//			throw new Error("Did not read in the valid amount of employees. "
//				+ "Read: " + list.count() + " Expected: " + Integer.parseInt(components[0].substring(3, components[0].length() - 1)));
//		}
//		
//		return list;
//	}
//	
//	private static <E extends Employee> OperationStack readOpStack(String str, EmployeeSet<E> list) {
//		// TODO
//		return null;
//	}
//	
//	private static <E extends Employee> ScheduleSetUp<E> readScheduleSetUp(String str) {
//		// TODO
////		if (lines[0].contains(FileManager.setUpChar)) {
////			Driver.databaseLog.fine("Correct call to ScheduleSetUp.fromCSV()");
////		} else {
////			IllegalArgumentException e = new IllegalArgumentException("Invalid call to ScheduleSetUp.fromCSV(): " + lines[0]);
////			Driver.databaseLog.log(Level.SEVERE, e.getMessage(), e);
////			throw e;
////		}
////		
////		int maxHours = 0, numberOfLines = 0;
////		try {
////			maxHours = Integer.parseInt(lines[1]);
////			Driver.databaseLog.log(Level.FINEST, "Validly parsed max hours", maxHours);
////			numberOfLines = Integer.parseInt(lines[0].split(" ")[1]);
////			Driver.databaseLog.log(Level.FINEST, "Validly parsed number of lines", numberOfLines);
////		} catch (NumberFormatException e) {
////			Driver.databaseLog.log(Level.SEVERE, e.getMessage(), e);
////			throw e;
////		}
//		return null;
//	}
//	
//	private static <E extends Employee> QualifiedEmployeeListMap<E> readQueueMap(String str, EmployeeSet<E> list) {
//		// TODO
//		return null;
//	}
//	
//	private static Schedule readSchedule(String str) {
//		// TODO
//		return null;
//	}
//	
//	public static WorkingSet<? extends Employee> selectWorkingSetFromSaved(){
//		/*TODO
//		 * > Read in saved files to a File[]
//		 * > Add to a *SET* until 
//		 *   a. out of files
//		 *   b. past limit (say 10)
//		 * > Print them as numbered with an amount of info
//		 * > Return the users selection
//		 */
//		return null;
//	}
//	
//	public static void numberFileLines(File toNumber, File writeTo) {
//		try (BufferedReader reader = new BufferedReader(new FileReader(toNumber));
//		     PrintWriter writer = new PrintWriter(writeTo))
//		{
//			String str;
//			int i = 0;
//			while( (str = reader.readLine()) != null) {
//				writer.write(i + ": " + str + "\n");
//				i++;
//			}
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}
//	

}
