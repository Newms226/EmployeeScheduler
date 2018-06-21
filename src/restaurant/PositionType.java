package restaurant;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import emp.EmployeeType;
import tools.DriverTools;
import tools.NumberTools;

public enum PositionType implements Serializable {
	Bar       (1, 'b'),
	Cocktail  (2, 't'),
	Closer    (3, 'c'),
	Head_Wait (4, 'h'),
	Sales     (5, 's');
	
	public final int priority;
	public final char ABR;
	
	// TODO
	EmployeeType employeeType;
	
	public static final double POSITION_TYPE_COUNT = 5,
			                   MAX_POSITION_VALUE = 25; //TODO: Only applies with this setup

	private PositionType(int priority, char abreviation) {
		this.priority = priority;
		ABR = abreviation;
	}
	
	public int getPriority() {
		return priority;
	}
	
	public static PositionType build() {
		while (true) {
			try {
				System.out.println("Which position would you like to add?");
				for (int i = 0; i < values().length; i++) {
					System.out.println(i + " " + values()[i]);
				}
				return parse(NumberTools.generateInt(true, 0, values().length - 1));
			} catch (Exception e) {
				System.out.println(e.getMessage() + " Try Again.");
			}
		}
	}
	
	public static PositionType parse(int x) {
		switch (x) {
			case 0: return Bar;
			case 1: return Cocktail;
			case 2: return Closer;
			case 3: return Head_Wait;
			case 4: return Sales;
			default:
				throw new IllegalArgumentException("Invalid entry: " + x + " [0," + (values().length - 1) + "]");
		}
	}
	
	public static PositionType parse(char abreviation) {
		if (abreviation == 's') return Sales;
		if (abreviation == 'b') return Bar;
		if (abreviation == 'c') return Closer;
		if (abreviation == 't') return Cocktail;
		if (abreviation == 'h') return Head_Wait;
		
		throw new IllegalArgumentException("Invalid entry: " + abreviation + " [s, b, t, c, h]");
	}

	public static ArrayList<PositionType> getHouseQualList(){
		return new ArrayList<>(Arrays.asList(values()));
	}
	
	public static ArrayList<PositionType> getBarOnly() {
		ArrayList<PositionType> w = new ArrayList<>(5);
		w.add(PositionType.Bar);
		return w;
	}
	
	public static ArrayList<PositionType> getBarAndCocktailOnly() {
		ArrayList<PositionType> bar = new ArrayList<>(5);
		bar.add(PositionType.Bar);
		bar.add(PositionType.Cocktail);
		return bar;
	}
	
	public static ArrayList<PositionType> getKing() {
		ArrayList<PositionType> king = new ArrayList<>(5);
		king.add(PositionType.Closer);
		king.add(PositionType.Head_Wait);
		king.add(PositionType.Cocktail);
		king.add(PositionType.Sales);
		return king;
	}
	
	public static ArrayList<PositionType> getHandyMan() {
		ArrayList<PositionType> handyMan = new ArrayList<>(5);
		handyMan.add(PositionType.Bar);
		handyMan.add(PositionType.Closer);
		handyMan.add(PositionType.Head_Wait);
		handyMan.add(PositionType.Cocktail);
		handyMan.add(PositionType.Sales);
		return handyMan;
	}
	
	public static ArrayList<PositionType> getAllButCocktail() {
		ArrayList<PositionType> handyMan = new ArrayList<>(5);
		handyMan.add(PositionType.Bar);
		handyMan.add(PositionType.Closer);
		handyMan.add(PositionType.Head_Wait);
		handyMan.add(PositionType.Sales);
		return handyMan;
	}
	
	public static ArrayList<PositionType> getNewKid() {
		ArrayList<PositionType> newKid = new ArrayList<>(1);
		newKid.add(PositionType.Sales);
		return newKid;
	}
	
	public static ArrayList<PositionType> buildServerPositions() {
		ArrayList<PositionType> toReturn = new ArrayList<>((int)POSITION_TYPE_COUNT);
		if (DriverTools.validate("Can the server work in the bar?")) toReturn.add(Bar);
		if (DriverTools.validate("Can the server work in cocktail?")) toReturn.add(Cocktail);
		if (DriverTools.validate("Can the server close?")) toReturn.add(Closer);
		if (DriverTools.validate("Can the server head wait?")) toReturn.add(Head_Wait);
		if (DriverTools.validate("Does the server work sales shifts?")) toReturn.add(Sales);
		return toReturn;
	}
	
	public static double getQualifactionDouble(ArrayList<PositionType> qualifications) {
		Collections.sort(qualifications);
		return NumberTools.normalizeToRange(
			//value:
				((double) qualifications
							.stream()
							.mapToInt(PositionType::getPriority)
							.sum()) 
					* ((double) 1) / ((double) qualifications.size())
					* qualifications.get(qualifications.size() - 1).priority,
				
			// Min
				0,
			// Max
				MAX_POSITION_VALUE);
	}
	
	public static void main(String[] args) {
		PositionType one = PositionType.Bar;
		PositionType two = PositionType.Bar;
		System.out.println(two.getPriority());
	}
	
}