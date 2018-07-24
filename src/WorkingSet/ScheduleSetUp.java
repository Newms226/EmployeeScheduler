package WorkingSet;
import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import Availability.SchedulableTimeChunk;
import driver.Driver;
import menu.ConsoleMenu;

public class ScheduleSetUp implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4051599033147551363L;
	private static final Logger log = Driver.setUpLog;

	private final String TRAINING_FILE_STR = "testingData/s_1";
	public final File TRAINING_FILE;

	private List<SchedulableTimeChunk> timeChunks;
	ConsoleMenu setupMenu, dayMenu;

	
	public ScheduleSetUp() {
		
		TRAINING_FILE = new File(
				this.getClass()
				.getClassLoader()
				.getResource(TRAINING_FILE_STR)
				.getFile()
		);
				
		
		
		
//		dayMenu = new ConsoleMenu("Which day would you like to view/modify?");
//		dayMenu.add(new RunnableOption("All days (View Only)",
//				() -> System.out.println(viewAll())));
//		dayMenu.add(new RunnableOption("Sunday",
//				() -> modifyDay(Day.SUNDAY)));
//		dayMenu.add(new RunnableOption("Monday",
//				() -> modifyDay(Day.MONDAY)));
//		dayMenu.add("Tuesday",
//				() -> modifyDay(Day.TUESDAY));
//		dayMenu.add("Wednesday",
//				() -> modifyDay(Day.WEDNESDAY));
//		dayMenu.add("Thursday",
//				() -> modifyDay(Day.THURSDAY));
//		dayMenu.add("Friday",
//				() -> modifyDay(Day.FRIDAY));
//		dayMenu.add("Sunday",
//				() -> modifyDay(Day.SUNDAY));
		
		timeChunks = new ArrayList<>();
	}
	
	public void dayMenu() {
		dayMenu.selection();
	}
	
	public void addTimeChunk(SchedulableTimeChunk ID) {
		log.log(Level.FINER, 
				"Adding SchedulableTimeChunk from ScheduleSetUp.addTimeChunk", 
				ID);
		timeChunks.add(ID);
	}
	
//	String viewDay(Day day) {
//		Driver.masterLog.log(Level.FINEST, "Viewing day", day);
//		StringBuffer buffer = new StringBuffer(day.toString() + "\n");
//		List<PositionID<? extends Employee>> dayList = positionIDs
//				.stream()
//				.sorted(PositionID.DAY_ORDER)
//				.filter(i -> i.getDay().equals(day)).collect(Collectors.toList());
//		if (dayList.size() == 0) {
//			Driver.masterLog.log(Level.WARNING, "No shifts are currently set up", day);
//			buffer.append("No shifts are set up for " + day);
//		} else {
//			for (int i = 0; i < dayList.size(); i++) {
//				System.out.print(i + ": " + dayList.get(i) + "\n");
//			}
//		}
//		return buffer.toString();
//	}
	
//	void modifyDay(Day day) {
//		Driver.setUpLog.log(Level.FINEST, "Modifying day", day);
//		setupMenu = new CMenu("Schedule Modifier for " + day);
//		setupMenu.add("Add a new shift", 
//			() -> {
//				PositionID<E> temp = PositionID.build();
//				positionIDs.add(temp);
//				System.out.println(temp.toString());
//			});
//		setupMenu.add("Delete shift",
//			() -> {
//				System.out.println(viewDay(day));
//				positionIDs.remove(
//						NumberTools.generateInt("Which index would you like to remove?",
//													true,
//													0,
//													positionIDs.size()));
//			});
//		setupMenu.add("Modify shift", 
//			() -> {
//				System.out.println(viewDay(day));
//				positionIDs.get(NumberTools.generateInt("Which index would you like to modify?",
//						true,
//						0,
//						positionIDs.size()))
//					.modify();
//			});
//		setupMenu.add("Return", () -> {return;}, null);
//		setupMenu.selection();
//	}

	
	public String viewAll() {
		StringBuffer buffer = new StringBuffer();
		timeChunks
			.stream()
			.sorted(SchedulableTimeChunk.VIEW_ORDER)
			.forEach(s -> buffer.append(s + "\n"));
		return buffer.toString();
	}
	
//	void setMaxHours(int maxHours) {
//		Driver.setUpLog.log(Level.FINEST, "Setting max hours", maxHours);
//		if (0 >= maxHours) {
//			IllegalArgumentException e = new IllegalArgumentException("Max hours must be at least 1:" + maxHours);
//			Driver.setUpLog.log(Level.SEVERE, "Max hours must be at least one", e);
//			throw e;
//		}
//		this.GLOBAL_MAX_HOURS = maxHours;
//	}
	
//	int getMaxHours() {
//		return GLOBAL_MAX_HOURS;
//	}
	

	
	
	
	List<SchedulableTimeChunk> getPositionIDsMUTATIVE() {
		return timeChunks;
	}
	
	
	public List<SchedulableTimeChunk> getPositionIDsCOPY() {
		if (timeChunks.size() == 0) {
			log.log(Level.SEVERE, 
					"FAILURE: Returning Null > TimeChunks were null", 
					new NullPointerException());
			return null;
		}
		return timeChunks.stream()
					.map(SchedulableTimeChunk::clone)
					.collect(Collectors.toList());
	}
	
	public int size() {
		return timeChunks.size();
	}
	

	@Override
	public boolean equals(Object that) {
		if (that == null) return false;
		if (this == that) return true;
		if (!this.getClass().equals(that.getClass())) return false;
	
		if (!timeChunks.containsAll(((ScheduleSetUp)that).timeChunks)) return false;
		
		return true;
	}
	
	@Override
	public ScheduleSetUp clone() {
		Driver.masterLog.finest("ENTERING: " + ScheduleSetUp.class.getName() + ".clone()");
		ScheduleSetUp clone = new ScheduleSetUp();
		clone.timeChunks = getPositionIDsCOPY();
		return clone;
	}
	
//	private static void testClone() {
//		ScheduleSetUp<Server> test = new ScheduleSetUp<Server>();
//		test.trainingData();
//		ScheduleSetUp<?> two = test.clone();
//		System.out.println(test.equals(two));
//	}
//	
//	public void trainingData() {
//		fromCSV(TRAINING_FILE); // TODO!
//	}
//	
//	public void fromCSV(File importDoc) {
//		try (BufferedReader reader = new BufferedReader(new FileReader(importDoc))){
////			GLOBAL_MAX_HOURS = Integer.parseInt(reader.readLine());
//			positionIDs = reader
//				.lines()
//				.map(PositionID::fromCSV)
//				.filter(p -> p != null)
//				.collect(Collectors.toList());
//		} catch (ArrayIndexOutOfBoundsException | IOException e) {
//			// TODO Auto-generated catch block: Missing file not found exception
//			e.printStackTrace();
//			throw new Error();
//		}
//	}
//	
//	public String toCSV() {
//		StringBuffer buffer = new StringBuffer();
//		positionIDs.stream()
//			.forEach(ID -> buffer.append(ID.toCSV() + "\n"));
//		return buffer.toString();
//	}
//	
//	public static <E extends Employee> ScheduleSetUp<E> fromCSV(String[] lines, int expectedLines, int maxHours) {
//		Driver.fileManagerLog.entering(ScheduleSetUp.class.getName(), "fromCSV()");
//		
//		ScheduleSetUp<E> toReturn = new ScheduleSetUp<>(maxHours);
//		for (String line: lines) {
//			toReturn.addPositonID(PositionID.fromCSV(line));
//		}
//		
//		if(expectedLines != toReturn.positionIDCount())
//			Driver.fileManagerLog.log(Level.SEVERE, 
//					"ScheduleSetUp.fromCSV() did not find the right amount of lines. Expected: {0} Found: {1}",
//					new Object[] {expectedLines, toReturn.positionIDCount()});
//		
//		Driver.fileManagerLog.exiting(ScheduleSetUp.class.getName(), "fromCSV()");
//		return toReturn;
//	}
//	
//	void save() {
//		save(TRAINING_FILE);
//	}
//	
//	public void save(File file) {
//		try {
//			FileTools.writeToFile(new PrintWriter(file), toCSV(), false);
//		} catch (FileNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
	
//	@SuppressWarnings("unchecked")
//	private boolean atemptDeserialization(File file) {
//		try (FileInputStream stream = new FileInputStream(file);
//				ObjectInputStream reader = new ObjectInputStream(stream);){
//			positionIDs = (List<PositionID>) reader.readObject();
//			return true;
//		} catch (IOException | ClassCastException | ClassNotFoundException e) {
//			if (Driver.debugging) e.printStackTrace();
//			return false;
//		}
//	}
//	
//	// TODO: Is this basically finalize?
//	void save() {
//		//save(new File(TRAINING_FILE));
//		save(new File(this.getClass().getResource(TRAINING_FILE).getFile()));
//	}
//	
//	void save(File file) {
//		try (FileOutputStream stream = new FileOutputStream(file);
//				ObjectOutputStream writer = new ObjectOutputStream(stream);){
//			
//			writer.writeObject(this);
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			if (Driver.debugging) {
//				e.printStackTrace();
//				throw new Error();
//			}
//		}
//	}
//	
//	static ScheduleSetUp trainingRead() {
//		return read(new File(ScheduleSetUp.class.getResource(TRAINING_FILE).getFile()));
//	}
//	
//	static ScheduleSetUp read(File file) {
//		try (FileInputStream stream = new FileInputStream(file);
//				ObjectInputStream read = new ObjectInputStream(stream);){
//			return (ScheduleSetUp) read.readObject();
//		} catch (IOException | ClassNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//			throw new Error();
//		} 
//	}
//	
//	private static void testSerial() {
//		ScheduleSetUp test = ScheduleSetUp.trainingRead();
//		System.out.println("Testing Data: " + test.positionIDs.size());
//		for (int i = 0; i < test.positionIDs.size(); i++) {
//			System.out.println(test.positionIDs.get(i));
//		}
//	}
//	
//	private static void testParsing() {
//		ScheduleSetUp test = new ScheduleSetUp();
//		test.trainingData();
//		test.save();
//		
//		ScheduleSetUp parsed = ScheduleSetUp.trainingRead();
//		
//		//System.out.println(test.positionIDs.containsAll(parsed.positionIDs));
//		System.out.println("Testing Data: " + test.positionIDs.size() + " Parsed Data: " + parsed.positionIDs.size());
//		for (int i = 0; i < test.positionIDs.size(); i++) {
//			System.out.println(test.positionIDs.get(i) + " > " + parsed.positionIDs.get(i));
//		}
//	}
	
//	@SuppressWarnings("unused")
//	private static void testMenu() {
//		ScheduleSetUp<Server> test = new ScheduleSetUp<>();
//		test.trainingData();
//		test.dayMenu.selection();
//	}
	
//	@SuppressWarnings("unused")
//	private void resetTrainingFileToCSV() {
//		Driver.databaseLog.config("Reseting Schedule);
//		try (PrintWriter write = new PrintWriter(TRAINING_FILE)){
//			write.write("1\n"
//					+ "1,1,b,4.9\n" + 
//					"1,1,b,4.9\n" + 
//					"1,1,t,4.6\n" + 
//					"1,1,t,4.6\n" + 
//					"1,1,c,4.6\n" + 
//					"1,1,c,4.6\n" + 
//					"1,1,h,4.3\n" + 
//					"1,1,h,4.3\n" + 
//					"1,1,s,4.0\n" + 
//					"1,1,s,4.0\n" + 
//					"1,1,s,3.7\n" + 
//					"1,1,s,3.4\n" + 
//					"1,1,s,3.4\n" + 
//					"1,1,s,3.1\n" + 
//					"4,1,b,4.6\n" + 
//					"4,1,b,4.6\n" + 
//					"4,1,t,4.3\n" + 
//					"4,1,t,4.3\n" + 
//					"4,1,c,4.3\n" + 
//					"4,1,c,4.3\n" + 
//					"4,1,h,4.3\n" + 
//					"4,1,h,4.3\n" + 
//					"4,1,s,3.4\n" + 
//					"4,1,s,3.4\n" + 
//					"4,1,s,3.4\n" + 
//					"4,1,s,2.8\n" + 
//					"4,1,s,2.8\n" + 
//					"4,1,s,2.5\n" + 
//					"4,1,s,2.2\n" + 
//					"4,1,s,2.2\n" + 
//					"5,1,b,4.3\n" + 
//					"5,1,b,4.0\n" + 
//					"5,1,b,3.7\n" + 
//					"5,1,t,4.3\n" + 
//					"5,1,t,4.3\n" + 
//					"5,1,c,4.0\n" + 
//					"5,1,c,4.0\n" + 
//					"5,1,h,3.7\n" + 
//					"5,1,h,3.7\n" + 
//					"5,1,s,3.7\n" + 
//					"5,1,s,3.7\n" + 
//					"5,1,s,3.7\n" + 
//					"5,1,s,3.7\n" + 
//					"5,1,s,3.7\n" + 
//					"5,1,s,3.7\n" + 
//					"5,1,s,3.7\n" + 
//					"5,1,s,3.7\n" + 
//					"");
//		} catch (FileNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
	
	public static void main(String[] args) {
		String toSplit = "#s 47";
		System.out.println(Integer.parseInt(toSplit.split(" ")[1]));
	}
}
