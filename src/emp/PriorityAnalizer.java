package emp;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import menu.ConsumerOption;
import menu.ListModificationMenu;
import restaurant.PositionType;
import tools.NumberTools;

public class PriorityAnalizer extends ListModificationMenu<Employee> {

	public PriorityAnalizer(Collection<Employee> toAnalize) {
		super("Priority Analyzier", toAnalize);
		add(new ConsumerOption<Employee>("Add to current hours", 
				e -> e.addToCurrentHours(NumberTools.generateDouble("How many hours would you like to add?",
						                                            true, 1, 24))));
		add(new ConsumerOption<Employee>("High Promote", e -> e.highPromote()));
		add(new ConsumerOption<Employee>("Mid Promote", e -> e.promote()));
		add(new ConsumerOption<Employee>("Low Promote", e -> e.lowPromote()));
		add(new ConsumerOption<Employee>("Change Grace", 
				e -> e.employeePriority.setGrace(NumberTools.generateDouble("What would you like to change the grace to?",
						                                                     true, 1, 10))));
	}
	
	public void evaluate() {
		selection();
	}
	
	@Override
	public void selection() {
//		if (returnEnabled) {
//			if (DriverTools.validate("\nReturn?")) return;
//		}
		double avgFill = listToModify.stream().mapToDouble(e -> e.currentHours).average().getAsDouble();
		listToModify.stream()
			.forEach(e -> e.updateEmployeePriority(avgFill));
		
		listToModify.sort(Employee.DESENDING_PRIORITY_ORDER);
		int i = 0;
		
		System.out.println("AVG FILL: " + avgFill);
		for (Employee e: listToModify) {
			i++;
			System.out.println(i + ": " + e + " " + NumberTools.format(e.currentHours) + " hours " 
					 + e.employeePriority.getSimplePriorityString(avgFill));
		}
		Employee toWorkOn = listToModify.get(NumberTools.generateInt("What would you like to work with?", false,
				1, listToModify.size()) - 1);
		int menuChoice = NumberTools.generateInt("Choose: " + toWorkOn + "\nWhat would you like to do?\n" + this,
				true,
				1, 
				size()) - 1;
		returnEnabled = true;
		options.get(menuChoice).setObjectToConsume(toWorkOn).run();
	}
	
	public static void main(String[] args) {
		Employee kim = new Employee("Kim",1,  LocalDate.of(2017, 1, 1), PositionType.getKing());
		Employee wade = new Employee("wade",2,  LocalDate.of(2000, 1, 1), PositionType.getBarOnly());
		Employee AshleyO = new Employee("Olson",3,  LocalDate.of(2005, 1, 1), PositionType.getBarOnly());
		Employee Alecia = new Employee("alecia",14,  LocalDate.of(2007, 1, 1), PositionType.getBarOnly());
		Employee chris = new Employee("Chris",4,  LocalDate.of(2010, 1, 1), PositionType.getHandyMan());
		Employee dena = new Employee("Dena",5, LocalDate.of(2013, 1, 1), PositionType.getKing());
		Employee blake = new Employee("blake",6, LocalDate.of(2014, 1, 1), PositionType.getHandyMan());
		Employee chels = new Employee("chels",7, LocalDate.of(2010, 1, 1), PositionType.getKing());
		Employee brandon = new Employee("brandon",8, LocalDate.of(2018, 1, 1), PositionType.getKing());
		Employee me = new Employee("me",9, LocalDate.of(2017, 1, 1), PositionType.getKing());
		Employee Marcy = new Employee("Marcy",10, LocalDate.of(2018, 1, 1), PositionType.getNewKid());
		Employee Paul2 = new Employee("Paul 2",11, LocalDate.of(2017, 6, 1), PositionType.getAllButCocktail());
		Employee paul3 = new Employee("Paul 3",12, LocalDate.of(2018, 1, 1), PositionType.getNewKid());
		Employee gregs = new Employee("Gregs",13, LocalDate.of(2015, 6, 1), PositionType.getKing());
		List<Employee> Employees = new ArrayList<>();
		kim.highPromote();
		Employees.add(kim);
		wade.highPromote();
		Employees.add(wade);
		Employees.add(chris);
		Employees.add(blake);
		Employees.add(chels);
		brandon.lowPromote();
		Employees.add(brandon);
		dena.highPromote();
		Employees.add(dena);
		Employees.add(AshleyO);
		Employees.add(me);
		Employees.add(Alecia);
		Employees.add(Marcy);
		Employees.add(paul3);
		Employees.add(Paul2);
		Employees.add(gregs);
		Employees.sort(Employee.DESENDING_PRIORITY_ORDER);
		PriorityAnalizer work = new PriorityAnalizer(Employees);
		work.evaluate();
	}

}
