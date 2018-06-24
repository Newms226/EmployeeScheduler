package time;

import java.util.*;

abstract class AbstractAvailabilityList {
	List<TimeChunk> list;
	Time_Chunk_SF statusFlag;
	
	AbstractAvailabilityList() {
		// TODO Auto-generated constructor stub
	}
	
	abstract TimeChunk[] toAvailable(TimeChunk chunk);
	
	abstract boolean contains(TimeChunk chunk);
}
