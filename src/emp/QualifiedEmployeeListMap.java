package emp;
import java.io.Serializable;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import Availability.SchedulableTimeChunk;
import driver.Driver;

public class QualifiedEmployeeListMap implements Serializable {
	
	/******************************************************************************
	 *                                                                            *
	 *                              Static Fields                                 *
	 *                                                                            *
	 ******************************************************************************/
	
	private static final long serialVersionUID = 6970937561074612393L;
	private static final Logger log = Driver.deciderLog;
	
	/******************************************************************************
	 *                                                                            *
	 *                                                                            *
	 *                        Instance Fields & Methods                           *
	 *                                                                            *
	 *                                                                            *
	 ******************************************************************************/
	
	private Map <SchedulableTimeChunk, QualifiedEmployeeList> availabilityMap;
	private EmployeeSet list;
	
	public QualifiedEmployeeListMap(EmployeeSet list) {
		this.list = list;
		availabilityMap = new TreeMap<SchedulableTimeChunk, 
				                      QualifiedEmployeeList>(SchedulableTimeChunk.SHIFT_ID_ORDER);
	}
	
	public int getQueueCount() {
		return availabilityMap.size();
	}
	
	// TODO: Thought: You could generate all the possible mappings at once with stream.map
	// Would it be faster to just work from a Map<PositionID, List> ???
	public QualifiedEmployeeList getList(SchedulableTimeChunk chunk) {
		log.log(Level.FINER, "ENTERNG: QualifiedEmployeeListMap.getList({0})", chunk);
		
		QualifiedEmployeeList toReturn = availabilityMap.get(chunk);
		if (toReturn == null) {
			log.log(Level.INFO, "Mapping for {0} is not present", chunk);
			toReturn = new QualifiedEmployeeList(list, chunk);
			availabilityMap.put(chunk, toReturn);
		} else {
			log.log(Level.FINE, "{0} map is present", chunk);
		}
		
		log.log(Level.FINER, "RETURNING: QualifiedEmployeeListMap.getList({0})", chunk);
		return toReturn;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o == this) return true;
		if (o == null) return false;
		
		if (!this.getClass().equals(o.getClass())) return false;
		
		QualifiedEmployeeListMap that = (QualifiedEmployeeListMap) o;
		
		if (!availabilityMap.equals(that.availabilityMap)) return false;
		if (!list.equals(that.list)) return false;
		
		return true;
	}
}
//
//public String toCSV() {
//	Driver.deciderLog.entering(QualifiedEmployeeListMap.class.getName(), "toCSV");
//	if (availabilityMap.isEmpty()) {
//		Driver.deciderLog.warning("EMPTY MAP!");
//		return "EMPTY MAP\n";
//	}
//	StringBuffer buffer = new StringBuffer();
//	availabilityMap.entrySet().stream()
//		.forEach(entry -> buffer.append(entry.getKey().toCSV() + entry.getValue().toCSV() + "\n" ));
//	Driver.deciderLog.exiting(QualifiedEmployeeListMap.class.getName(), "toCSV");
//	return buffer.toString();
//}

//public void removeEmployeeFromDay(E employee, PositionID<? extends Employee> ID) {
//	availabilityMap.forEach( (k, m) -> {
//		if (m.ofDay(ID) && m.contains(employee)) m.remove(ID);
//	};
//}
