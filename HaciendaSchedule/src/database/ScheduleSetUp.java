package database;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import Menu.Menu;
import MyTime.Day;
import emp.Employee;
import emp.Server;
import tools.FileTools;
import tools.NumberTools;
import tools.StringTools;

public class ScheduleSetUp <E extends Employee> implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4051599033147551363L;

	private final String TRAINING_FILE_STR = "testingData/s_1";
	private final File TRAINING_FILE;
	
	private static int DEFAULT_GLOBAL_MAX_HOURS = 40;
	private int GLOBAL_MAX_HOURS;
	private List<PositionID<? extends Employee>> positionIDs;
	Menu setupMenu, dayMenu;
	
	public ScheduleSetUp() {
		this(DEFAULT_GLOBAL_MAX_HOURS);
	}
	
	public ScheduleSetUp(int maxHours) {
		if (0 >= maxHours)
			throw new IllegalArgumentException("Max hours must be at least 1:" + maxHours);
		this.GLOBAL_MAX_HOURS = maxHours;
		
		TRAINING_FILE = new File(
				this.getClass()
				.getClassLoader()
				.getResource(TRAINING_FILE_STR)
				.getFile());
				
		
		
		
		dayMenu = new Menu("Which day would you like to view/modify?");
		dayMenu.add("All days (View Only)",
				() -> System.out.println(viewAll()));
		dayMenu.add("Sunday",
				() -> modifyDay(Day.sunday()));
		dayMenu.add("Monday",
				() -> modifyDay(Day.monday()));
		dayMenu.add("Tuesday",
				() -> modifyDay(Day.tuesday()));
		dayMenu.add("Wednesday",
				() -> modifyDay(Day.wednesday()));
		dayMenu.add("Thursday",
				() -> modifyDay(Day.thursday()));
		dayMenu.add("Friday",
				() -> modifyDay(Day.friday()));
		dayMenu.add("Sunday",
				() -> modifyDay(Day.sunday()));
		
		positionIDs = new ArrayList<>();
	}
	
	public void dayMenu() {
		dayMenu.selection();
	}
	
	String viewDay(Day day) {
		StringBuffer buffer = new StringBuffer(day.toString() + "\n");
		List<PositionID<? extends Employee>> dayList = positionIDs
				.stream()
				.sorted(PositionID.DAY_ORDER)
				.filter(i -> i.getDay().equals(day)).collect(Collectors.toList());
		if (dayList.size() == 0) {
			buffer.append("No shifts are set up for " + day);
		} else {
			for (int i = 0; i < dayList.size(); i++) {
				System.out.print(i + ": " + dayList.get(i) + "\n");
			}
		}
		return buffer.toString();
	}
	
	void modifyDay(Day day) {
		setupMenu = new Menu("Schedule Modifier for " + day);
		setupMenu.add("Add a new shift", 
			() -> {
				PositionID<E> temp = PositionID.build();
				positionIDs.add(temp);
				System.out.println(temp.toString());
			});
		setupMenu.add("Delete shift",
			() -> {
				System.out.println(viewDay(day));
				positionIDs.remove(
						NumberTools.generateInt("Which index would you like to remove?",
													true,
													0,
													positionIDs.size()));
			});
		setupMenu.add("Modify shift", 
			() -> {
				System.out.println(viewDay(day));
				positionIDs.get(NumberTools.generateInt("Which index would you like to modify?",
						true,
						0,
						positionIDs.size()))
					.modify();
			});
		setupMenu.add("Return", () -> {return;}, null);
		setupMenu.selection();
	}

	
	public String viewAll() {
		StringBuffer buffer = new StringBuffer();
		positionIDs
			.stream()
			.sorted(PositionID.DAY_ORDER)
			.forEach(s -> buffer.append(s + "\n"));
		return buffer.toString();
	}
	
	void setMaxHours(int maxHours) {
		if (0 >= maxHours)
			throw new IllegalArgumentException("Max hours must be at least 1:" + maxHours);
		this.GLOBAL_MAX_HOURS = maxHours;
	}
	
	int getMaxHours() {
		return GLOBAL_MAX_HOURS;
	}
	
	public void trainingData() {
		parseCSV(TRAINING_FILE);
	}
	
	public void parseCSV(File importDoc) {
		try (BufferedReader reader = new BufferedReader(new FileReader(importDoc))){
//			GLOBAL_MAX_HOURS = Integer.parseInt(reader.readLine());
			positionIDs = reader
				.lines()
				.map(PositionID::fromCSV)
				.filter(p -> p != null)
				.collect(Collectors.toList());
		} catch (ArrayIndexOutOfBoundsException | IOException e) {
			// TODO Auto-generated catch block: Missing file not found exception
			e.printStackTrace();
			throw new Error();
		}
	}
	
	void save() {
		save(TRAINING_FILE);
	}
	
	public void save(File file) {
		try {
			FileTools.writeToFile(new PrintWriter(file), toCSV(), false);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	String toCSV() {
		StringBuffer buffer = new StringBuffer("{");
		positionIDs.stream()
			.forEach(ID -> buffer.append("[" + ID.toCSV() + "],"));
		return StringTools.removeLastComma(buffer).concat("}\n");
	}
	
	List<PositionID<? extends Employee>> getPositionIDsMUTATIVE() {
		return positionIDs;
	}
	
	
	public List<PositionID<? extends Employee>> getPositionIDsCOPY() {
		if (positionIDs.size() == 0) {
			throw new NullPointerException("PositionIDs have not been filled");
		}
		return positionIDs.stream()
					.map(PositionID::clone)
					.collect(Collectors.toList());
	}
	
	public int positionIDCount() {
		return positionIDs.size();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public boolean equals(Object that) {
		if (that == null) return false;
		if (this == that) return true;
		if (!this.getClass().equals(that.getClass())) return false;
		
		ScheduleSetUp<E> temp;
		try {
			temp = (ScheduleSetUp<E>) that;
		} catch (ClassCastException e) {
			return false;
		}
		
		if (GLOBAL_MAX_HOURS != temp.GLOBAL_MAX_HOURS) return false;
		if (!positionIDs.containsAll(temp.positionIDs)) return false;
		
		
		return true;
	}
	
	@Override
	public ScheduleSetUp<E> clone() {
		ScheduleSetUp<E> clone = new ScheduleSetUp<E>();
		clone.GLOBAL_MAX_HOURS = GLOBAL_MAX_HOURS;
		clone.positionIDs = getPositionIDsCOPY();
		return clone;
	}
	
	private static void testClone() {
		ScheduleSetUp<Server> test = new ScheduleSetUp<Server>();
		test.trainingData();
		ScheduleSetUp<?> two = test.clone();
		System.out.println(test.equals(two));
	}
	
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
	
	@SuppressWarnings("unused")
	private static void testMenu() {
		ScheduleSetUp<Server> test = new ScheduleSetUp<>();
		test.trainingData();
		test.dayMenu.selection();
	}
	
	@SuppressWarnings("unused")
	private void resetTrainingFileToCSV() {
		try (PrintWriter write = new PrintWriter(TRAINING_FILE)){
			write.write("1\n"
					+ "1,1,b,4.9\n" + 
					"1,1,b,4.9\n" + 
					"1,1,t,4.6\n" + 
					"1,1,t,4.6\n" + 
					"1,1,c,4.6\n" + 
					"1,1,c,4.6\n" + 
					"1,1,h,4.3\n" + 
					"1,1,h,4.3\n" + 
					"1,1,s,4.0\n" + 
					"1,1,s,4.0\n" + 
					"1,1,s,3.7\n" + 
					"1,1,s,3.4\n" + 
					"1,1,s,3.4\n" + 
					"1,1,s,3.1\n" + 
					"4,1,b,4.6\n" + 
					"4,1,b,4.6\n" + 
					"4,1,t,4.3\n" + 
					"4,1,t,4.3\n" + 
					"4,1,c,4.3\n" + 
					"4,1,c,4.3\n" + 
					"4,1,h,4.3\n" + 
					"4,1,h,4.3\n" + 
					"4,1,s,3.4\n" + 
					"4,1,s,3.4\n" + 
					"4,1,s,3.4\n" + 
					"4,1,s,2.8\n" + 
					"4,1,s,2.8\n" + 
					"4,1,s,2.5\n" + 
					"4,1,s,2.2\n" + 
					"4,1,s,2.2\n" + 
					"5,1,b,4.3\n" + 
					"5,1,b,4.0\n" + 
					"5,1,b,3.7\n" + 
					"5,1,t,4.3\n" + 
					"5,1,t,4.3\n" + 
					"5,1,c,4.0\n" + 
					"5,1,c,4.0\n" + 
					"5,1,h,3.7\n" + 
					"5,1,h,3.7\n" + 
					"5,1,s,3.7\n" + 
					"5,1,s,3.7\n" + 
					"5,1,s,3.7\n" + 
					"5,1,s,3.7\n" + 
					"5,1,s,3.7\n" + 
					"5,1,s,3.7\n" + 
					"5,1,s,3.7\n" + 
					"5,1,s,3.7\n" + 
					"");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		testClone();
	}
}
